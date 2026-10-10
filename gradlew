#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd -P)"
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -Xmx1024m -Xms256m -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
