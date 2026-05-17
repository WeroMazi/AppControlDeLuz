@echo off
setlocal
REM Starts emulator (if needed), builds debug APK, installs and launches the app on the emulator.
set AVD_NAME=%1
if "%AVD_NAME%"=="" set AVD_NAME=Medium_Phone_API_36.1
set ANDROID_SDK=%LOCALAPPDATA%\Android\Sdk
set SDK_PLATFORM_TOOLS=%ANDROID_SDK%\platform-tools
set ADB=%SDK_PLATFORM_TOOLS%\adb.exe
set EMULATOR=%ANDROID_SDK%\emulator\emulator.exe
if "%AUTOMATION_API_BASE_URL%"=="" set AUTOMATION_API_BASE_URL=http://10.0.2.2:8000/

if not exist "%ADB%" (
  echo adb not found at %ADB%
  echo Install Android SDK Platform-Tools from Android Studio SDK Manager.
  exit /b 1
)
if not exist "%EMULATOR%" (
  echo emulator not found at %EMULATOR%
  echo Install Android Emulator from Android Studio SDK Manager.
  exit /b 1
)

REM Check that the requested AVD exists before trying to start it.
"%EMULATOR%" -list-avds | findstr /X /C:"%AVD_NAME%" >nul
if errorlevel 1 (
  echo AVD "%AVD_NAME%" was not found.
  echo Create one in Android Studio: Tools ^> Device Manager.
  echo Available AVDs:
  "%EMULATOR%" -list-avds
  exit /b 1
)

REM Check for running emulator with device state "device".
"%ADB%" devices | findstr /C:"emulator-" | findstr /C:"device" >nul
if errorlevel 1 (
  echo Starting emulator %AVD_NAME%...
  start "" "%EMULATOR%" -avd %AVD_NAME%
  timeout /t 8 /nobreak >nul
)
echo Waiting for device...
"%ADB%" wait-for-device

echo Waiting for Android boot...
"%ADB%" shell getprop sys.boot_completed | findstr /C:"1" >nul
if errorlevel 1 timeout /t 10 /nobreak >nul

echo Building APK...
call gradlew.bat assembleDebug -x test --console=plain
if errorlevel 1 exit /b 1

echo Installing APK...
"%ADB%" install -r ".\app\build\outputs\apk\debug\app-debug.apk"
if errorlevel 1 exit /b 1

echo Launching app...
"%ADB%" shell monkey -p com.example.appcontroldeluz -c android.intent.category.LAUNCHER 1
