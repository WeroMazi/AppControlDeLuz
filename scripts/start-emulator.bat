@echo off
REM Starts the Android emulator for the given AVD name (default: Medium_Phone_API_36.1)
set AVD_NAME=%1
if "%AVD_NAME%"=="" set AVD_NAME=Medium_Phone_API_36.1
set EMULATOR=%LOCALAPPDATA%\Android\sdk\emulator\emulator.exe
if not exist "%EMULATOR%" (
  echo Emulator not found at %EMULATOR%
  exit /b 1
)
start "" "%EMULATOR%" -avd %AVD_NAME%
