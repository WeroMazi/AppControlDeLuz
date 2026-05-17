package com.example.appcontroldeluz.data.remote

import com.example.appcontroldeluz.data.model.HealthResponse
import com.example.appcontroldeluz.data.model.Esp32CommandResponse
import com.example.appcontroldeluz.data.model.LightControlRequest
import com.example.appcontroldeluz.data.model.LightControlResponse
import com.example.appcontroldeluz.data.model.LightsResponse
import com.example.appcontroldeluz.data.model.VoiceCommandResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface HomeAutomationApiService {
    @GET("api/health")
    suspend fun getHealth(): HealthResponse

    @GET("api/lights")
    suspend fun getLights(): LightsResponse

    @POST("api/lights/control")
    suspend fun controlLight(@Body request: LightControlRequest): LightControlResponse

    // ESP32 direct control through the backend API.
    // Base URL goes in AUTOMATION_API_BASE_URL, normally http://10.0.2.2:8000/ for Android Emulator.
    @POST("api/lights/{light}/{action}")
    suspend fun sendEsp32LightCommand(
        @Path("light") light: String,
        @Path("action") action: String
    ): Esp32CommandResponse

    @Multipart
    @POST("api/voice/command")
    suspend fun processVoiceCommand(@Part file: MultipartBody.Part): VoiceCommandResponse

    // Sensor endpoints (Azure-backed). These are optional and have local mock fallback.
    @GET("api/sensors/status")
    suspend fun getSensorStatus(): com.example.appcontroldeluz.data.model.SensorStatus

    @POST("api/sensors/enable")
    suspend fun setSensorEnabled(@Body payload: Map<String, Boolean>): com.example.appcontroldeluz.data.model.SensorStatus

    @POST("api/sensors/unlink")
    suspend fun unlinkLight(@Body payload: Map<String, String>): com.example.appcontroldeluz.data.model.SensorStatus
}
