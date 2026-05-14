param([string]$AvdName = "Medium_Phone_API_36.1")
$adb = "$env:LOCALAPPDATA\Android\sdk\platform-tools\adb.exe"
$emu = "$env:LOCALAPPDATA\Android\sdk\emulator\emulator.exe"
if (-not (Test-Path $adb)) { Write-Error "adb not found at $adb"; exit 1 }
if (-not (Test-Path $emu)) { Write-Error "emulator not found at $emu"; exit 1 }

#$devices = & $adb devices
#$devices | Out-Host
$devicesText = & $adb devices
if ($devicesText -notmatch "emulator-\d+\s+device") {
    Write-Host "Starting emulator $AvdName..."
    Start-Process -FilePath $emu -ArgumentList ("-avd","$AvdName") -NoNewWindow
    Start-Sleep -Seconds 8
}
Write-Host "Waiting for device..."
& $adb wait-for-device
Write-Host "Building APK..."
& .\gradlew.bat assembleDebug -x test --console=plain
Write-Host "Installing APK..."
& $adb install -r ".\app\build\outputs\apk\debug\app-debug.apk"
Write-Host "Launching app..."
& $adb shell am start -n com.example.appcontroldeluz/.MainActivity
