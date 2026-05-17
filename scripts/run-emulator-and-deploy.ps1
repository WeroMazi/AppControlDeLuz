param([string]$AvdName = "Medium_Phone_API_36.1")
$androidSdk = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$adb = Join-Path $androidSdk "platform-tools\adb.exe"
$emu = Join-Path $androidSdk "emulator\emulator.exe"
if (-not $env:AUTOMATION_API_BASE_URL) {
    $env:AUTOMATION_API_BASE_URL = "http://10.0.2.2:8000/"
}
if (-not (Test-Path $adb)) {
    Write-Error "adb not found at $adb. Install Android SDK Platform-Tools from Android Studio SDK Manager."
    exit 1
}
if (-not (Test-Path $emu)) {
    Write-Error "emulator not found at $emu. Install Android Emulator from Android Studio SDK Manager."
    exit 1
}

$avds = & $emu -list-avds
if ($avds -notcontains $AvdName) {
    Write-Error "AVD '$AvdName' was not found. Create one in Android Studio: Tools > Device Manager. Available AVDs: $($avds -join ', ')"
    exit 1
}

$devicesText = & $adb devices
if ($devicesText -notmatch "emulator-\d+\s+device") {
    Write-Host "Starting emulator $AvdName..."
    Start-Process -FilePath $emu -ArgumentList ("-avd", "$AvdName")
    Start-Sleep -Seconds 8
}
Write-Host "Waiting for device..."
& $adb wait-for-device
Write-Host "Waiting for Android boot..."
for ($i = 0; $i -lt 30; $i++) {
    $bootCompleted = (& $adb shell getprop sys.boot_completed).Trim()
    if ($bootCompleted -eq "1") { break }
    Start-Sleep -Seconds 2
}
Write-Host "Building APK..."
& .\gradlew.bat assembleDebug -x test --console=plain
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Installing APK..."
& $adb install -r ".\app\build\outputs\apk\debug\app-debug.apk"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Launching app..."
& $adb shell monkey -p com.example.appcontroldeluz -c android.intent.category.LAUNCHER 1
