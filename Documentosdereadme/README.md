## AppControlDeLuz

Proyecto Android en Kotlin + Jetpack Compose para controlar luces por habitaciones, automatizacion con sensor y comandos de voz.

## Mapa Rapido (Donde Editar)

### Conexion Azure / Backend
- app/build.gradle.kts
	- Variable de compilacion: AUTOMATION_API_BASE_URL
	- Para Azure: exporta AUTOMATION_API_BASE_URL o edita el fallback local.
- app/src/main/java/com/example/appcontroldeluz/data/remote/ApiClient.kt
	- Cliente Retrofit principal.
- app/src/main/java/com/example/appcontroldeluz/data/remote/HomeAutomationApiService.kt
	- Contrato de endpoints (health, lights, voice).

### Logica de Luces
- app/src/main/java/com/example/appcontroldeluz/data/repository/LightsRepository.kt
	- Lectura y control de luces.
- app/src/main/java/com/example/appcontroldeluz/data/config/HomeAutomationConfig.kt
	- Alias de habitaciones y orden canonico.
- app/src/main/java/com/example/appcontroldeluz/ui/viewmodel/AppViewModel.kt
	- Estado, cache y acciones de UI para luces.

### Logica de Sensores
- app/src/main/java/com/example/appcontroldeluz/data/repository/SensorsRepository.kt
	- Fuente central de estado de sensor y luces vinculadas.
	- Lugar recomendado para conectar futuros endpoints Azure de sensores.
- app/src/main/java/com/example/appcontroldeluz/data/model/SensorModels.kt
	- Modelos de estado de sensor.
- app/src/main/java/com/example/appcontroldeluz/ui/screens/SensorScreen.kt
	- UI del sensor conectada al ViewModel.

### Pantallas Principales
- app/src/main/java/com/example/appcontroldeluz/MainActivity.kt
	- Navegacion principal e inyeccion de AppViewModel.
- app/src/main/java/com/example/appcontroldeluz/ui/screens/DashboardScreen.kt
	- Panel principal de control de habitaciones.
- app/src/main/java/com/example/appcontroldeluz/ui/screens/VoiceAssistantScreen.kt
	- Flujo de grabacion y procesamiento de voz.

## Notas de Integracion Azure

- Si tu backend ya corre en Azure App Service o Azure Container Apps, configura AUTOMATION_API_BASE_URL con la URL publica, por ejemplo:
	- https://tu-servicio.azurewebsites.net/
- La app consume endpoints relativos como:
	- api/health
	- api/lights
	- api/lights/control
	- api/voice/command

## Tecnologias

- Kotlin
- Jetpack Compose
- Material 3
- Retrofit + OkHttp
- Firebase Analytics / Crashlytics

## Arrancar el emulador y desplegar (rápido)

He incluido scripts para facilitar arrancar el emulador y desplegar la APK localmente.

- Windows batch (abrir con doble-clic o ejecutar desde PowerShell/cmd):
	- `scripts\start-emulator.bat [AVD_NAME]` — arranca el AVD (por defecto `Medium_Phone_API_36.1`).
	- `scripts\launch-and-deploy.bat [AVD_NAME]` — arranca el emulador si es necesario, compila `assembleDebug`, instala la APK y lanza la `MainActivity`.

- PowerShell (más detallado):
	- `scripts\run-emulator-and-deploy.ps1 [AVD_NAME]` — equivalente al `launch-and-deploy.bat` pero en PowerShell. Ejemplo:

```powershell
.\scripts\run-emulator-and-deploy.ps1 -AvdName "Medium_Phone_API_36.1"
```

Notas:
- Los scripts buscan `adb` y `emulator` en `%LOCALAPPDATA%\Android\sdk` (ubicación estándar). Si tu SDK está en otra ruta, exporta `LOCALAPPDATA` o modifica el script.
- `AUTOMATION_API_BASE_URL` se configura en `app/build.gradle.kts`. Para apuntar a Azure establece la variable de entorno `AUTOMATION_API_BASE_URL` antes de compilar, por ejemplo en PowerShell:

```powershell
$env:AUTOMATION_API_BASE_URL = 'https://tu-servicio-azure.azurewebsites.net/'
.\gradlew.bat assembleDebug
```

