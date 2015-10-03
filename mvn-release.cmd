@echo off
set /p passphrase=Enter GPG Password:

%MVN% -e release:prepare -Darguments="-Dgpg.passphrase=%passphrase%" && mvn -e release:perform -Darguments="-Dgpg.passphrase=%passphrase%"