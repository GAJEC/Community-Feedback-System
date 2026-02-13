@echo off
setlocal EnableExtensions

cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
  echo javac not found. Install JDK and add it to PATH.
  exit /b 1
)

set "SRC=src"
set "OUT=out\classes"
set "SOURCES=%OUT%\sources.txt"

if not exist "%OUT%" mkdir "%OUT%" >nul 2>nul
del /q "%SOURCES%" >nul 2>nul

for /r "%SRC%" %%F in (*.java) do echo %%F>>"%SOURCES%"

echo Compiling...
javac -encoding UTF-8 -cp ".;lib\*" -d "%OUT%" @"%SOURCES%"
if errorlevel 1 (
  echo.
  echo Compilation failed.
  exit /b 1
)

REM Lagay ka ng static assets sa classpath output (para gumana yung getResource("/assets/..." ) sa client)
if exist "res\assets" (
  if not exist "%OUT%\assets" mkdir "%OUT%\assets" >nul 2>nul
  xcopy /E /I /Y "res\assets" "%OUT%\assets" >nul
)

set "COUNT=%~1"
if "%COUNT%"=="" set "COUNT=1"

REM Kung ilang window yung gusto mong open. Default is 1, pero pwede mo palitan ng mas marami para testing ng broadcast.
if "%COUNT%"=="1" (
  echo.
  echo Starting client...
  java -cp "%OUT%;.;lib\*" client.Client
  exit /b %errorlevel%
)

echo.
echo Starting %COUNT% clients...
for /l %%I in (1,1,%COUNT%) do (
  start "Client %%I" /D "%~dp0" cmd /k "java -cp ""%OUT%;.;lib\*"" client.Client"
)
