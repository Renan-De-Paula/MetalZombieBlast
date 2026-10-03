@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
set "PATH=%JAVA_HOME%\bin;C:\tools\apache-maven-3.9.9\bin;%PATH%"
java -jar target\ultimo-abrigo-1.0.0.jar
