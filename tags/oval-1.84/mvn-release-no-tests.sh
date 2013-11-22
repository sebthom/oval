#!/bin/sh
read -s -p "Enter GPG Password: " passphrase

mvn -e release:prepare -Darguments="-DskipTests -Dgpg.passphrase=${passphrase}" && mvn -e release:perform -Darguments="-DskipTests -Dgpg.passphrase=${passphrase}"