@echo off
REM
REM Batch script to migrate the Deployit Server
REM

setlocal ENABLEDELAYEDEXPANSION

REM Get Java executable
if "%JAVA_HOME%"=="" (
  set JAVACMD=java
) else (
  set JAVACMD="%JAVA_HOME%\bin\java"
)

REM Get JVM options
if "%DEPLOYIT_SERVER_OPTS%"=="" (
  set DEPLOYIT_SERVER_OPTS=-Xmx1024m -XX:MaxPermSize=128m
)

REM Get logging-related options
if "%DEPLOYIT_SERVER_LOG_OPTS%"=="" (
  set DEPLOYIT_SERVER_LOG_OPTS=-Dlogback.configurationFile=conf\logback.xml -Dderby.stream.error.file=log\derby.log
)

REM Get Deployit server home dir
if "%DEPLOYIT_SERVER_HOME%"=="" (
  cd /d "%~dp0"
  cd ..
  set DEPLOYIT_SERVER_HOME=!CD!
)

cd /d "%DEPLOYIT_SERVER_HOME%"

REM Build Deployit server classpath
set DEPLOYIT_SERVER_CLASSPATH=conf;ext
for %%i in (hotfix\*.jar) do set DEPLOYIT_SERVER_CLASSPATH=!DEPLOYIT_SERVER_CLASSPATH!;%%i
for %%i in (lib\*.jar) do set DEPLOYIT_SERVER_CLASSPATH=!DEPLOYIT_SERVER_CLASSPATH!;%%i
for %%i in (plugins\*.jar) do set DEPLOYIT_SERVER_CLASSPATH=!DEPLOYIT_SERVER_CLASSPATH!;%%i
for /d %%i in (plugins\*) do set DEPLOYIT_SERVER_CLASSPATH=!DEPLOYIT_SERVER_CLASSPATH!;%%i

REM Run Deployit server
%JAVACMD% %DEPLOYIT_SERVER_OPTS% %DEPLOYIT_SERVER_LOG_OPTS% -cp "%DEPLOYIT_SERVER_CLASSPATH%" com.xebialabs.deployit.tools.RepositoryMigration %*

:end
endlocal