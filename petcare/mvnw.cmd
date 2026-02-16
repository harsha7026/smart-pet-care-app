@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup script for Windows, version 3.3.2
@REM ----------------------------------------------------------------------------

@IF "%JAVA_HOME%"=="" GOTO noJavaHome
@SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
@IF EXIST "%JAVA_EXE%" GOTO javaHomeFound

echo JAVA_HOME is set to an invalid directory: %JAVA_HOME%
GOTO exit

:noJavaHome
@SET JAVA_EXE=java.exe
@java -version >NUL 2>&1
@if %ERRORLEVEL% EQU 0 GOTO javaHomeFound

echo Java not found. Please set JAVA_HOME.
GOTO exit

:javaHomeFound

@SET BASE_DIR=%~dp0
@if "%MAVEN_BASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%BASE_DIR%
@if not "%MAVEN_BASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%

@REM Trim trailing backslash to avoid escaping the closing quote in JVM args
@set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper-3.3.2.jar
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@if exist "%WRAPPER_JAR%" GOTO foundWrapper

echo [mvnw] Downloading Maven Wrapper from Maven Central ...
@if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"
@powershell -Command "Try { (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar', '%WRAPPER_JAR%') } Catch { exit 1 }"
@if %ERRORLEVEL% NEQ 0 goto errorDownload

:foundWrapper

@SET MAVEN_JAVA_EXE=%JAVA_EXE%
@SET MAVEN_OPTS=%MAVEN_OPTS% %MAVEN_DEBUG_OPTS%

"%MAVEN_JAVA_EXE%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -classpath "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*
GOTO exit

:errorDownload
echo [mvnw] Failed to download Maven Wrapper.
GOTO exit

:exit
@ENDLOCAL
