package com.example.appcontroldeluz.data.repository

import com.example.appcontroldeluz.data.config.HomeAutomationConfig
import com.example.appcontroldeluz.data.model.LightControlRequest
import com.example.appcontroldeluz.data.model.VoiceCommandResponse
import com.example.appcontroldeluz.data.remote.HomeAutomationApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class LightsRepository(private val api: HomeAutomationApiService) {
    suspend fun healthCheck(): Result<Boolean> = runCatching {
        api.getHealth().status.equals("healthy", ignoreCase = true)
    }

    suspend fun getLights(): Result<Map<String, Boolean>> = runCatching {
        api.getLights().lights
    }

    suspend fun controlLight(room: String, state: Boolean): Result<Map<String, Boolean>> = runCatching {
        api.controlLight(LightControlRequest(room = room, state = state)).lights
    }

    suspend fun sendEsp32LightCommand(light: String, action: String): Result<String> = runCatching {
        val normalizedLight = light.trim().lowercase()
        val normalizedAction = action.trim().lowercase()

        require(normalizedLight == "all" || normalizedLight.toIntOrNull() in 1..8) {
            "LED invalido. Usa un numero del 1 al 8, o 'all'."
        }
        require(normalizedAction == "on" || normalizedAction == "off") {
            "Accion invalida. Solo se permite 'on' u 'off'."
        }

        val response = api.sendEsp32LightCommand(normalizedLight, normalizedAction)
        response.message.ifBlank {
            "Comando ${response.command ?: "$normalizedLight:$normalizedAction"} enviado"
        }
    }

    suspend fun processVoiceCommand(audioFile: File): Result<VoiceCommandResponse> = runCatching {
        val requestBody = audioFile.asRequestBody("audio/wav".toMediaType())
        val part = MultipartBody.Part.createFormData("file", audioFile.name, requestBody)
        api.processVoiceCommand(part)
    }

    fun toCanonicalRoom(input: String): String {
        return HomeAutomationConfig.canonicalizeRoom(input)
    }

    fun ensureAllRooms(lights: Map<String, Boolean>): Map<String, Boolean> {
        return HomeAutomationConfig.normalizeLights(lights)
    }
}
