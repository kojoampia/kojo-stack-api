@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM
@REM Maven Wrapper (Windows)
@REM https://maven.apache.org/wrapper/
@REM
@REM Wrapper batch script to download and run Maven without requiring global installation
@REM Usage: mvnw [maven goals/options]
@REM Example: mvnw clean package
@REM          mvnw spring-boot:run
@REM          mvnw test -DskipTests

@echo off
setlocal enabledelayedexpansion

set "MAVEN_WRAPPER_DIR=%~dp0"
if "%MAVEN_PROJECTBASEDIR%"=="" (
  set "MAVEN_PROJECTBASEDIR=%MAVEN_WRAPPER_DIR%"
)

set "MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

REM Read Maven Wrapper properties
for /f "delims=" %%a in ('type "%MAVEN_WRAPPER_PROPERTIES%"') do (
  if "%%a" neq "" if not "%%a:~0,1%%" equ "#" (
    set "%%a"
  )
)

if "%MAVEN_WRAPPER_JAR%"=="" (
  set "MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
)

if "%MAVEN_WRAPPER_JAR_URL%"=="" (
  set "MAVEN_WRAPPER_JAR_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
)

if "%MAVEN_REPO_URL%"=="" (
  set "MAVEN_REPO_URL=https://repo.maven.apache.org/maven2"
)

if "%MAVEN_VERSION%"=="" (
  set "MAVEN_VERSION=3.9.6"
)

REM Download Maven Wrapper JAR if not present
if not exist "%MAVEN_WRAPPER_JAR%" (
  echo [INFO] Downloading maven-wrapper.jar from %MAVEN_WRAPPER_JAR_URL%...
  if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"
  powershell -Command "& {[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; (new-object System.Net.WebClient).DownloadFile('%MAVEN_WRAPPER_JAR_URL%', '%MAVEN_WRAPPER_JAR%')}"
  if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to download maven-wrapper.jar
    exit /b 1
  )
)

REM Check if JAVA_HOME is set
if "%JAVA_HOME%"=="" (
  for /f "tokens=*" %%i in ('where java') do set "JAVA_EXE=%%i"
  if "!JAVA_EXE!"=="" (
    echo [ERROR] JAVA_HOME is not set and java is not in PATH
    exit /b 1
  )
) else (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

REM Check if Maven Wrapper JAR exists
if not exist "%MAVEN_WRAPPER_JAR%" (
  echo [ERROR] Maven Wrapper JAR not found at %MAVEN_WRAPPER_JAR%
  exit /b 1
)

REM Run Maven via the wrapper JAR
echo [INFO] Running Maven %MAVEN_VERSION%...
"%JAVA_EXE%" ^
  -classpath "%MAVEN_WRAPPER_JAR%" ^
  org.apache.maven.wrapper.MavenWrapperMain ^
  %*

endlocal
