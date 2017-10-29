@echo off
REM to skip tests execute "mvn-release.cmd -DskipTests"
set /p passphrase=Enter GPG Password:

call mvn %MVN_OPTS% -e release:prepare -Darguments="%MVN_OPTS% -Dgpg.passphrase=%passphrase% %*" && call mvn %MVN_OPTS% -e release:perform -Darguments="%MVN_OPTS% -Dgpg.passphrase=%passphrase% %*"