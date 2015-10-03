@echo off
set /p passphrase=Enter GPG Password:

call %MVN% -e release:prepare -Darguments="-Dgpg.passphrase=%passphrase%" && call %MVN% -e release:perform -Darguments="-Dgpg.passphrase=%passphrase%"