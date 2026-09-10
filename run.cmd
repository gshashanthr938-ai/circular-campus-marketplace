@echo off
setlocal enabledelayedexpansion
REM ============================================================
REM  Circular Campus Marketplace - one-click run (Windows)
REM  Starts the app on http://localhost:8080/
REM ============================================================

REM --- 1. Locate a JDK 17 (needed to compile & run) ---
if "%JAVA_HOME%"=="" (
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%D"
)
if "%JAVA_HOME%"=="" (
    for /d %%D in ("C:\Program Files\Java\jdk-17*") do set "JAVA_HOME=%%D"
)
if "%JAVA_HOME%"=="" (
    echo [ERROR] Could not find a JDK 17. Install Temurin JDK 17 or set JAVA_HOME.
    exit /b 1
)
echo Using JAVA_HOME=%JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo Starting Circular Campus Marketplace...
echo Open http://localhost:8080/ in your browser once you see the banner.
echo Press Ctrl+C in this window to stop.
echo.

REM --- 2. Run via the bundled Maven Wrapper (no Maven install needed) ---
call mvnw.cmd -q compile exec:java

endlocal
