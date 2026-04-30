@echo off
chcp 65001 >nul
cd /d "e:\StreamFlow"
echo Building StreamFlow...
mvn clean compile
if %ERRORLEVEL% EQU 0 (
    echo Starting StreamFlow...
    java -cp "target/classes;%USERPROFILE%\.m2\repository\javazoom\jlayer\1.0.1\jlayer-1.0.1.jar" com.odyssey.Main
) else (
    echo Build failed!
    pause
)
