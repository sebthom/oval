#!/bin/sh
mvn -e release:prepare -Darguments="-DskipTests" && mvn -e release:perform -Darguments="-DskipTests"