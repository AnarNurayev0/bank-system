@echo off
cls
echo ========================================
echo  Bank System - Auto Start
echo ========================================
echo.
echo [1/2] Cleaning port 8080...

REM Try to kill process on port 8080 using netstat and taskkill
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo      Killing process %%a...
    taskkill /F /PID %%a >nul 2>&1
)

echo      Port 8080 is ready!
echo.
echo [2/2] Starting Spring Boot...
echo.

call mvnw.cmd spring-boot:run
