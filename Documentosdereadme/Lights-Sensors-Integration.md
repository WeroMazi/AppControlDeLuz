**Lights & Sensors — Integración rápida**

Objetivo
- Documentar qué hay que implementar en el backend (Azure) y dónde editar la app para que las luces y los sensores queden conectados y fáciles de mantener.

**Resumen de endpoints (esperados en Azure / HomeAutomation API)**
- Luces
  - GET /api/lights → devuelve `LightsResponse` ({ status, lights: Map<String, Boolean> })
  - POST /api/lights/control → recibe `{ room: String, state: Boolean }`, devuelve `LightControlResponse` (incluye `lights` actualizado)

- Voz
  - POST /api/voice/command (multipart file) → devuelve `VoiceCommandResponse` con `lights` opcional

- Sensores (recomendado)
  - GET /api/sensors/status → devuelve `SensorStatus` (enabled:Boolean, lastDetectionLabel:String, linkedLights: List<LinkedLightDevice>)
  - POST /api/sensors/enable → recibe `{ enabled: Boolean }`, devuelve `SensorStatus`
  - POST /api/sensors/unlink → recibe `{ light_id: String }`, devuelve `SensorStatus`

Si tu backend usa otros nombres/rutas, actualiza `app/build.gradle.kts` (variable `AUTOMATION_API_BASE_URL`) y `app/src/main/java/com/example/appcontroldeluz/data/remote/HomeAutomationApiService.kt`.

**Dónde editar en la app (quick links)**
- Cliente Retrofit y URL base:
  - `app/src/main/java/com/example/appcontroldeluz/data/remote/ApiClient.kt`
  - `app/build.gradle.kts` (campo `AUTOMATION_API_BASE_URL`)

- Contrato de endpoints (añadir o cambiar rutas/modelos):
  - `app/src/main/java/com/example/appcontroldeluz/data/remote/HomeAutomationApiService.kt`

- Luces (lógica):
  - `app/src/main/java/com/example/appcontroldeluz/data/repository/LightsRepository.kt`  — llamas a `api.getLights()` y `api.controlLight(...)`
  - `app/src/main/java/com/example/appcontroldeluz/ui/viewmodel/AppViewModel.kt` — `controlRoom()`, `controlAll()` usan `LightsRepository`
  - UI: `app/src/main/java/com/example/appcontroldeluz/ui/screens/DashboardScreen.kt`

- Sensores (lógica y UI):
  - `app/src/main/java/com/example/appcontroldeluz/data/repository/SensorsRepository.kt` — actualmente hace fallback mock; reemplaza internals para llamar a `HomeAutomationApiService` si tu Azure implementa los endpoints.
  - Modelos: `app/src/main/java/com/example/appcontroldeluz/data/model/SensorModels.kt`
  - ViewModel: `app/src/main/java/com/example/appcontroldeluz/ui/viewmodel/AppViewModel.kt` — expone `sensorStatus` y acciones `setSensorEnabled()` / `removeLinkedLightFromSensor()`.
  - UI: `app/src/main/java/com/example/appcontroldeluz/ui/screens/SensorScreen.kt` — consume `sensorStatus` y llama a las acciones del ViewModel.

**Pasos para integrar Azure (recomendado)**
1. Implementar los endpoints en Azure con los nombres sugeridos arriba o avisar para adaptar la app a tus rutas.
2. Desplegar y obtener la URL pública (ej. `https://mi-servicio.azurewebsites.net/`).
3. Antes de compilar, exportar la variable de entorno para apuntar la app:

```powershell
$env:AUTOMATION_API_BASE_URL = 'https://mi-servicio.azurewebsites.net/'
.\gradlew.bat assembleDebug
```

4. Verificar que `HomeAutomationApiService` refleja exactamente los modelos JSON que devuelve Azure (ajusta `SensorStatus`, `LinkedLightDevice`, `LightsResponse`, etc.).
5. Quitar o adaptar los fallbacks del `SensorsRepository` si quieres forzar llamadas remotas en vez del mock local.

**Notas de implementación para la persona que venga a trabajar (checklist)**
- Revisa `data/model` y añade campos que Azure retorne (por ejemplo IDs de dispositivos, timestamps, estados detallados).
- Testea con Postman/Insomnia los endpoints antes de compilar la app.
- Si añades autenticación (Azure AD, JWT), actualiza `ApiClient` para inyectar el interceptor con el token.
- Para IoT Hub / Device Twins: implementa en el backend una capa que transforme el estado de los dispositivos a la forma que la app espera (mapa de habitaciones).

**Comprobaciones rápidas en la app**
- Ejecuta `scripts\run-emulator-and-deploy.ps1` para levantar emulador, compilar e instalar.
- Abre la app, ve a `Ajustes` → `APARIENCIA` para verificar tema; ve a `Sensor` para probar el flujo de sensores.

Si me pasas la especificación exacta de los endpoints Azure (payloads y rutas), actualizo `HomeAutomationApiService.kt`, los modelos en `data/model` y el `SensorsRepository` para llamar 100% a Azure sin fallback.

---
Archivo creado por el equipo de integración — mantén este doc actualizado cuando cambies rutas o modelos.
