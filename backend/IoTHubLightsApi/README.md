# IoTHubLightsApi

API intermedia para enviar comandos cloud-to-device a Azure IoT Hub sin exponer secretos en la app Android.

## Configuracion

Define la cadena de conexion del IoT Hub como variable de entorno:

```powershell
$env:IOTHUB_CONNECTION_STRING = "HostName=Luces-inteligentes.azure-devices.net;SharedAccessKeyName=...;SharedAccessKey=..."
```

El `DeviceId` usado por esta API esta documentado en `Program.cs`:

```csharp
const string deviceId = "esp32";
```

La app Android usa `AUTOMATION_API_BASE_URL` para saber donde esta esta API. En emulador Android, normalmente:

```powershell
$env:AUTOMATION_API_BASE_URL = "http://10.0.2.2:8000/"
```

## Ejecutar

```powershell
cd backend\IoTHubLightsApi
dotnet run
```

## Endpoints

- `POST /api/lights/1/on` envia `led1:on`
- `POST /api/lights/1/off` envia `led1:off`
- `POST /api/lights/2/on` envia `led2:on`
- `POST /api/lights/2/off` envia `led2:off`
- Repite el mismo patron del LED 1 al LED 8.
- `POST /api/lights/all/on` envia `all:on`
- `POST /api/lights/all/off` envia `all:off`

## Prueba rapida

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8000/api/lights/1/on"
```
