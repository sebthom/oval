@echo off
set /p passphrase=Enter GPG Password:

call %MVN% -e release:prepare -Darguments="-DskipTests -Dgpg.passphrase=%passphrase%" && call %MVN% -e release:perform -Darguments="-DskipTests -Dgpg.passphrase=%passphrase%"