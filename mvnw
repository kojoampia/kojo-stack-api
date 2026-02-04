#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# Maven Wrapper
# https://maven.apache.org/wrapper/

# Get the directory where this script is located
PRG="$0"
while [ -h "$PRG" ]; do
  ls=$(ls -ld "$PRG")
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=$(dirname "$PRG")/"$link"
  fi
done
DIRNAME=$(dirname "$PRG")
MAVEN_PROJECTBASEDIR=$(cd "$DIRNAME" && pwd -P)

# Set defaults
MAVEN_WRAPPER_PROPERTIES="${MAVEN_PROJECTBASEDIR}/.mvn/wrapper/maven-wrapper.properties"
MAVEN_WRAPPER_JAR="${MAVEN_PROJECTBASEDIR}/.mvn/wrapper/maven-wrapper.jar"

# Check if properties file exists
if [ ! -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
  echo "[ERROR] Unable to locate maven-wrapper.properties at $MAVEN_WRAPPER_PROPERTIES" >&2
  exit 1
fi

# Set default values
mavenVersion="3.9.6"
mavenWrapperUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

# Parse properties file
while IFS='=' read -r key value; do
  case "$key" in
    \#*|"") continue ;;
  esac
  # Trim whitespace
  value=$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
  case "$key" in
    mavenVersion) mavenVersion="$value" ;;
    mavenWrapperUrl) mavenWrapperUrl="$value" ;;
  esac
done < "$MAVEN_WRAPPER_PROPERTIES"

# Download Maven Wrapper JAR if not present
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  echo "[INFO] Downloading Maven Wrapper JAR from ${mavenWrapperUrl}..."
  mkdir -p "$(dirname "$MAVEN_WRAPPER_JAR")"
  
  if command -v wget > /dev/null 2>&1; then
    wget -q "$mavenWrapperUrl" -O "$MAVEN_WRAPPER_JAR" || {
      echo "[ERROR] Failed to download Maven Wrapper JAR" >&2
      exit 1
    }
  elif command -v curl > /dev/null 2>&1; then
    curl -sS "$mavenWrapperUrl" -o "$MAVEN_WRAPPER_JAR" || {
      echo "[ERROR] Failed to download Maven Wrapper JAR" >&2
      exit 1
    }
  else
    echo "[ERROR] wget or curl is required to download Maven Wrapper JAR" >&2
    exit 1
  fi
fi

# Determine Java executable
if [ -n "$JAVA_HOME" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="$(command -v java 2>/dev/null)"
fi

if [ -z "$JAVA_EXE" ]; then
  echo "[ERROR] JAVA_HOME is not set and java is not in PATH" >&2
  exit 1
fi

# Verify Maven Wrapper JAR exists
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  echo "[ERROR] Maven Wrapper JAR not found at $MAVEN_WRAPPER_JAR" >&2
  exit 1
fi

# Run Maven via the wrapper JAR
exec "$JAVA_EXE" \
  -Dbasedir="$MAVEN_PROJECTBASEDIR" \
  -Dfile.encoding=UTF-8 \
  -classpath "$MAVEN_WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain \
  -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
  "$@"
