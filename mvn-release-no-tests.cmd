@echo off
set /p passphrase=Enter GPG Password:

mvn -e release:prepare -Darguments="-DskipTests -Dgpg.passphrase=%passphrase%" && mvn -e release:perform -Darguments="-DskipTests -Dgpg.passphrase=%passphrase%"