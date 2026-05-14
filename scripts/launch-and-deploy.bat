@echo off
REM Starts emulator (if needed), builds debug APK, installs and launches the app on the emulator.
set AVD_NAME=%1
if "%AVD_NAME%"=="" set AVD_NAME=Medium_Phone_API_36.1
set SDK_PLATFORM_TOOLS=%LOCALAPPDATA%\Android\sdk\platform-tools
set ADB=%SDK_PLATFORM_TOOLS%\adb.exe
set EMULATOR=%LOCALAPPDATA%\Android\sdk\emulator\emulator.exe
if not exist "%ADB%" (
  echo adb not found at %ADB%
  exit /b 1
)
if not exist "%EMULATOR%" (
  echo emulator not found at %EMULATOR%
  exit /b 1
)

nREM Check for running emulator with device state "device"
"%ADB%" devices | findstr /R /C:"emulator-.*\sdevice" >nul
if errorlevel 1 (
  echo Starting emulator %AVD_NAME%...
  start "" "%EMULATOR%" -avd %AVD_NAME%
  timeout /t 8 /nobreak >nul
)
necho Waiting for device...
"%ADB%" wait-for-device
echo Building APK...
call gradlew.bat assembleDebug -x test --console=plain
echo Installing APK...
"%ADB%" install -r ".\app\build\outputs\apk\debug\app-debug.apk"
echo Launching app...
"%ADB%" shell am start -n com.example.appcontroldeluz/.MainActivity
