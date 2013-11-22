#!/bin/sh
read -s -p "Enter GPG Password: " passphrase

mvn -e release:prepare -Darguments="-Dgpg.passphrase=${passphrase}" && mvn -e release:perform -Darguments="-Dgpg.passphrase=${passphrase}"