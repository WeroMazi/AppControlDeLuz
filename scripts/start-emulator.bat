@echo off
setlocal
REM Starts the Android emulator for the given AVD name (default: Medium_Phone_API_36.1)
set AVD_NAME=%1
if "%AVD_NAME%"=="" set AVD_NAME=Medium_Phone_API_36.1
set ANDROID_SDK=%LOCALAPPDATA%\Android\Sdk
set EMULATOR=%ANDROID_SDK%\emulator\emulator.exe
if not exist "%EMULATOR%" (
  echo Emulator not found at %EMULATOR%
  echo Install Android Emulator from Android Studio SDK Manager.
  exit /b 1
)
"%EMULATOR%" -list-avds | findstr /X /C:"%AVD_NAME%" >nul
if errorlevel 1 (
  echo AVD "%AVD_NAME%" was not found.
  echo Create one in Android Studio: Tools ^> Device Manager.
  echo Available AVDs:
  "%EMULATOR%" -list-avds
  exit /b 1
)
start "" "%EMULATOR%" -avd %AVD_NAME%
