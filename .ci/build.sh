#!/usr/bin/env bash
#
# Copyright 2015-2019 by Vegard IT GmbH, Germany, https://vegardit.com
# SPDX-License-Identifier: Apache-2.0
#
# @author Sebastian Thomschke, Vegard IT GmbH
# @author Patrick Spielmann, Vegard IT GmbH

set -e # abort script at first error
set -o pipefail # causes a pipeline to return the exit status of the last command in the pipe that returned a non-zero return value

if [[ -f .ci/release-trigger.sh ]]; then
    echo "Sourcing [.ci/release-trigger.sh]..."
    source .ci/release-trigger.sh
fi

MAVEN_VERSION=3.6.2
MAVEN_HELP_PLUGIN_VERSION=3.2.0
if [[ ! -e $HOME/.m2/bin/apache-maven-$MAVEN_VERSION ]]; then
    echo "Installing Maven version $MAVEN_VERSION..."
    mkdir -p $HOME/.m2/bin/
    wget --quiet https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz -O /tmp/maven.tar.gz
    tar xfz /tmp/maven.tar.gz -C $HOME/.m2/bin/
fi
export M2_HOME=$HOME/.m2/bin/apache-maven-$MAVEN_VERSION
export PATH=$M2_HOME/bin:$PATH

# https://stackoverflow.com/questions/3545292/how-to-get-maven-project-version-to-the-bash-command-line
echo "Determining current Maven project version..."
projectVersion="$(mvn -s .ci/maven_settings.xml org.apache.maven.plugins:maven-help-plugin:$MAVEN_HELP_PLUGIN_VERSION:evaluate -Dexpression=project.version -q -DforceStdout)"
echo "  -> Current Version: $projectVersion"

MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1" # https://zeroturnaround.com/rebellabs/your-maven-build-is-slow-speed-it-up/
export MAVEN_OPTS="${MAVEN_OPTS} -Xmx1024m -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"

#
# decide whether to build/deploy a snapshot version or perform a release build
#
if [[ ${projectVersion:-foo} == ${POM_CURRENT_VERSION:-bar} ]]; then
    # https://stackoverflow.com/questions/8653126/how-to-increment-version-number-in-a-shell-script/21493080#21493080
    nextDevelopmentVersion="$(echo ${POM_RELEASE_VERSION} | perl -pe 's/^((\d+\.)*)(\d+)(.*)$/$1.($3+1).$4/e')-SNAPSHOT"

    echo "###################################################"
    echo "# Creating Maven Release...                       #"
    echo "###################################################"
    echo "  ->          Release Version: ${POM_RELEASE_VERSION}"
    echo "  -> Next Development Version: ${nextDevelopmentVersion}"
    echo "  ->           Skipping Tests: ${SKIP_TESTS}"
    echo "  ->               Is Dry-Run: ${DRY_RUN}"

    # workaround for "No toolchain found with specification [version:1.8, vendor:default]" during release builds
    cp -f .ci/maven_settings.xml $HOME/.m2/settings.xml
    cp -f .ci/maven_toolchains.xml $HOME/.m2/toolchains.xml

    # workaround for "Git fatal: ref HEAD is not a symbolic ref" during release
    git checkout ${TRAVIS_BRANCH}

    mvn -e -U --batch-mode --show-version \
        -s .ci/maven_settings.xml -t .ci/maven_toolchains.xml \
        -DskipTests=${SKIP_TESTS} -DskipITs=${SKIP_TESTS} \
        -DdryRun=${DRY_RUN} -Dresume=false "-Darguments=-DskipTests=${SKIP_TESTS} -DskipITs=${SKIP_TESTS}" -DreleaseVersion=${POM_RELEASE_VERSION} -DdevelopmentVersion=${nextDevelopmentVersion} \
        help:active-profiles clean release:clean release:prepare release:perform \
        | grep -v -e "\[INFO\]  .* \[0.0[0-9][0-9]s\]" # the grep command suppresses all lines from maven-buildtime-extension that report plugins with execution time <=99ms
else
    echo "###################################################"
    echo "# Building Maven Project...                       #"
    echo "###################################################"
    if [[ ${TRAVIS_BRANCH} == "master" ]]; then
        mavenGoal="deploy"
    else
        mavenGoal="verify"
    fi
    mvn -e -U --batch-mode --show-version \
        -s .ci/maven_settings.xml -t .ci/maven_toolchains.xml \
        help:active-profiles clean $mavenGoal \
        | grep -v -e "\[INFO\]  .* \[0.0[0-9][0-9]s\]" # the grep command suppresses all lines from maven-buildtime-extension that report plugins with execution time <=99ms
fi
