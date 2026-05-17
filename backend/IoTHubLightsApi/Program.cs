using System.Text;
using Microsoft.Azure.Devices;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();

var app = builder.Build();

// Base local para pruebas: http://localhost:8000
// En Android Emulator, configura AUTOMATION_API_BASE_URL=http://10.0.2.2:8000/
// La cadena de conexion NO debe ir en el codigo. Define IOTHUB_CONNECTION_STRING
// como variable de entorno o en un proveedor seguro de configuracion.
const string deviceId = "esp32"; // DeviceId registrado en Azure IoT Hub Luces-inteligentes.
var allowedActions = new HashSet<string>(StringComparer.OrdinalIgnoreCase) { "on", "off" };

app.MapGet("/api/health", () => Results.Ok(new
{
    status = "healthy",
    azure_iot_configured = !string.IsNullOrWhiteSpace(GetConnectionString(app.Configuration)),
    device_id = deviceId,
    azure_speech_configured = false,
    azure_speech_region = "",
    total_lights = 8,
    lights_on = 0
}));

app.MapGet("/api/lights", () => Results.Ok(new
{
    status = "success",
    lights = new Dictionary<string, bool>
    {
        ["sala"] = false,
        ["cocina"] = false,
        ["dormitorio"] = false,
        ["baño"] = false,
        ["jardin"] = false,
        ["garage"] = false
    }
}));

app.MapPost("/api/lights/control", async (
    LightControlRequest request,
    IConfiguration configuration,
    CancellationToken cancellationToken) =>
{
    var normalizedRoom = request.Room.Trim().ToLowerInvariant();
    var light = normalizedRoom switch
    {
        "sala" => "1",
        "cocina" => "2",
        "dormitorio" => "3",
        "baño" or "bano" => "4",
        "jardin" or "jardín" => "5",
        "garage" or "garaje" => "6",
        "todas" or "todos" or "all" => "all",
        _ => ""
    };

    if (string.IsNullOrWhiteSpace(light))
    {
        return Results.BadRequest(new
        {
            status = "error",
            message = "Habitacion invalida para el control ESP32."
        });
    }

    var action = request.State ? "on" : "off";
    var result = await SendCommandAsync(light, action, configuration, cancellationToken);
    if (!result.Success)
    {
        return result.Error!;
    }

    return Results.Ok(new
    {
        status = "success",
        message = "Comando enviado a Azure IoT Hub.",
        lights = new Dictionary<string, bool>
        {
            ["sala"] = light == "1" ? request.State : normalizedRoom is "todas" or "todos" or "all" && request.State,
            ["cocina"] = light == "2" ? request.State : normalizedRoom is "todas" or "todos" or "all" && request.State,
            ["dormitorio"] = light == "3" ? request.State : normalizedRoom is "todas" or "todos" or "all" && request.State,
            ["baño"] = light == "4" ? request.State : normalizedRoom is "todas" or "todos" or "all" && request.State,
            ["jardin"] = light == "5" ? request.State : normalizedRoom is "todas" or "todos" or "all" && request.State,
            ["garage"] = light == "6" ? request.State : normalizedRoom is "todas" or "todos" or "all" && request.State
        }
    });
});

app.MapPost("/api/lights/{light}/{action}", async (
    string light,
    string action,
    IConfiguration configuration,
    CancellationToken cancellationToken) =>
{
    var normalizedAction = action.Trim().ToLowerInvariant();
    if (!allowedActions.Contains(normalizedAction))
    {
        return Results.BadRequest(new
        {
            status = "error",
            message = "Accion invalida. Solo se permite 'on' u 'off'."
        });
    }

    var commandTarget = light.Trim().ToLowerInvariant();
    string command;

    if (commandTarget == "all")
    {
        command = $"all:{normalizedAction}";
    }
    else if (int.TryParse(commandTarget, out var ledNumber) && ledNumber is >= 1 and <= 8)
    {
        command = $"led{ledNumber}:{normalizedAction}";
    }
    else
    {
        return Results.BadRequest(new
        {
            status = "error",
            message = "LED invalido. Usa un numero del 1 al 8, o 'all'."
        });
    }

    var result = await SendCommandAsync(commandTarget, normalizedAction, configuration, cancellationToken);
    if (!result.Success)
    {
        return result.Error!;
    }

    return Results.Ok(new
    {
        status = "success",
        message = "Comando enviado a Azure IoT Hub.",
        deviceId,
        command
    });
});

app.Run("http://0.0.0.0:8000");

static async Task<CommandSendResult> SendCommandAsync(
    string light,
    string action,
    IConfiguration configuration,
    CancellationToken cancellationToken)
{
    var command = light == "all" ? $"all:{action}" : $"led{light}:{action}";
    var connectionString = GetConnectionString(configuration);
    if (string.IsNullOrWhiteSpace(connectionString))
    {
        return CommandSendResult.Failed(Results.Problem(
            title: "Configuracion de Azure incompleta",
            detail: "Falta la variable de entorno IOTHUB_CONNECTION_STRING.",
            statusCode: StatusCodes.Status500InternalServerError));
    }

    try
    {
        using var serviceClient = ServiceClient.CreateFromConnectionString(connectionString);
        using var message = new Message(Encoding.UTF8.GetBytes(command))
        {
            ContentType = "text/plain",
            ContentEncoding = "utf-8"
        };

        cancellationToken.ThrowIfCancellationRequested();
        await serviceClient.SendAsync(deviceId, message);

        return CommandSendResult.Sent();
    }
    catch (Exception ex)
    {
        return CommandSendResult.Failed(Results.Problem(
            title: "No se pudo enviar el comando a Azure IoT Hub",
            detail: ex.Message,
            statusCode: StatusCodes.Status502BadGateway));
    }
}

static string? GetConnectionString(IConfiguration configuration)
{
    return Environment.GetEnvironmentVariable("IOTHUB_CONNECTION_STRING")
        ?? configuration["IOTHUB_CONNECTION_STRING"];
}

sealed record CommandSendResult(bool Success, IResult? Error)
{
    public static CommandSendResult Sent() => new(true, null);
    public static CommandSendResult Failed(IResult error) => new(false, error);
}

sealed record LightControlRequest(string Room, bool State);
