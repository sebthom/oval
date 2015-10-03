@echo off
set /p passphrase=Enter GPG Password:

call mvn -e release:prepare -Darguments="-Dgpg.passphrase=%passphrase%" && call mvn -e release:perform -Darguments="-Dgpg.passphrase=%passphrase%"