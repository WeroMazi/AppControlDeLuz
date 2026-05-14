package com.example.appcontroldeluz.data.model

import com.google.gson.annotations.SerializedName

data class LightsResponse(
    val status: String,
    val lights: Map<String, Boolean>
)

data class LightControlRequest(
    val room: String,
    val state: Boolean
)

data class LightControlResponse(
    val status: String,
    val message: String,
    val lights: Map<String, Boolean>
)

data class ParsedVoiceCommand(
    val action: String?,
    val room: String?,
    @SerializedName("original_text")
    val originalText: String?
)

data class VoiceCommandResponse(
    val status: String,
    val message: String,
    val transcription: String,
    val confidence: Double,
    @SerializedName("command_executed")
    val commandExecuted: Boolean,
    val parsed: ParsedVoiceCommand?,
    val lights: Map<String, Boolean>?
)

data class HealthResponse(
    val status: String,
    @SerializedName("azure_speech_configured")
    val azureSpeechConfigured: Boolean,
    @SerializedName("azure_speech_region")
    val azureSpeechRegion: String,
    @SerializedName("total_lights")
    val totalLights: Int,
    @SerializedName("lights_on")
    val lightsOn: Int
)
