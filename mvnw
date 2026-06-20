#!/bin/sh
# Maven Wrapper Script
set -e
MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
MAVEN_OPTS="${MAVEN_OPTS:-}"
JAVA_CMD="${JAVA_HOME:+$JAVA_HOME/bin/}java"
exec "$JAVA_CMD" $MAVEN_OPTS -jar "$MAVEN_WRAPPER_JAR" "$@"
