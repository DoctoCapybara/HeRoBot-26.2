@echo off
set JAVA_HOME=C:\Users\imyou\AppData\Local\Temp\opencode\jdk25\jdk25
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "F:\ki\opencode projekts\heromod andere version\HeRoBot"
gradlew.bat --no-daemon %*
