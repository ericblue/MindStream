echo off
rem # MindStream by Eric Blue (http://eric-blue.com)
cls
title Mindstream

set JAR=dist\mindstream.jar


if "%JAVA_HOME%" == "" (
  echo Error:
  echo   JAVA_HOME environment variable is NOT set!
  echo .
  echo Use My Computer/Properties/Advanced/Environment variables
  echo to set it e.g to C:\Program Files\Java\jre6 Then don't forget to restart 
  echo the shell!
  echo .
  echo Fallback to PATH...  
  java -Djava.ext.dirs=lib -jar %JAR% %1 %2
) else (
  echo Java home set to: %JAVA_HOME%
  rem TODO Fix classpath load issue, rather than load java.ext.dirs
  "%JAVA_HOME%\bin\java" -Djava.ext.dirs=lib -jar %JAR% %1 %2
)




