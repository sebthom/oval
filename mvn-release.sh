#!/bin/bash
REM to skip tests execute "mvn-release.sh -DskipTests"

read -s -p "Enter GPG Password: " passphrase

mvn -e release:prepare -Darguments="-Dgpg.passphrase=${passphrase} $@" && mvn -e release:perform -Darguments="-Dgpg.passphrase=${passphrase} $@"