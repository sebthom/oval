@echo off
set /p passphrase=Enter GPG Password:

call mvn -e release:prepare -Darguments="-DskipTests -Dgpg.passphrase=%passphrase%" && call mvn -e release:perform -Darguments="-DskipTests -Dgpg.passphrase=%passphrase%"