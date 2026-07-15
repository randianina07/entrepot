@echo off
setlocal

cd /d "%~dp0"

echo Verification du port 8080...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo Processus trouve sur le port 8080 : PID=%%a
    taskkill /F /PID %%a >nul 2>&1
    echo Processus %%a termine.
)

timeout /t 1 >nul

if exist "%~dp0mvnw.cmd" (
    call "%~dp0mvnw.cmd" spring-boot:run
) else (
    where mvn >nul 2>nul
    if errorlevel 1 (
        echo Maven wrapper not found and Maven is not available on PATH.
        exit /b 1
    )
    mvn spring-boot:run
)

endlocal
