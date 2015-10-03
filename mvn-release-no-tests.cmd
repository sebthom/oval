@echo off
set /p passphrase=Enter GPG Password:

call mvn %MVN_OPTS% -e release:prepare -Darguments="%MVN_OPTS% -DskipTests -Dgpg.passphrase=%passphrase%" && call mvn %MVN_OPTS% -e release:perform -Darguments="%MVN_OPTS% -DskipTests -Dgpg.passphrase=%passphrase%"