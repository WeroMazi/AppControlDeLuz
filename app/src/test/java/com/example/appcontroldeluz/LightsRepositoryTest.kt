package com.example.appcontroldeluz

import com.example.appcontroldeluz.data.model.HealthResponse
import com.example.appcontroldeluz.data.model.Esp32CommandResponse
import com.example.appcontroldeluz.data.model.LightControlRequest
import com.example.appcontroldeluz.data.model.LightControlResponse
import com.example.appcontroldeluz.data.model.LightsResponse
import com.example.appcontroldeluz.data.model.SensorStatus
import com.example.appcontroldeluz.data.model.VoiceCommandResponse
import com.example.appcontroldeluz.data.remote.VoiceApiService
import com.example.appcontroldeluz.data.repository.LightsRepository
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LightsRepositoryTest {

    private val repository = LightsRepository(FakeVoiceApi())

    @Test
    fun canonicalRoomMapsAliases() {
        assertEquals("dormitorio", repository.toCanonicalRoom("habitación"))
        assertEquals("jardin", repository.toCanonicalRoom("jardín"))
        assertEquals("garage", repository.toCanonicalRoom("garaje"))
        assertEquals("baño", repository.toCanonicalRoom("bano"))
    }

    @Test
    fun ensureAllRoomsReturnsStableShape() {
        val partial = mapOf("sala" to true, "cocina" to false)
        val normalized = repository.ensureAllRooms(partial)

        assertEquals(6, normalized.size)
        assertTrue(normalized.containsKey("dormitorio"))
        assertTrue(normalized.containsKey("baño"))
        assertTrue(normalized.containsKey("jardin"))
        assertTrue(normalized.containsKey("garage"))
        assertEquals(true, normalized["sala"])
        assertEquals(false, normalized["cocina"])
    }
}

private class FakeVoiceApi : VoiceApiService {
    override suspend fun getHealth(): HealthResponse {
        return HealthResponse("healthy", true, "eastus", 6, 0)
    }

    override suspend fun getLights(): LightsResponse {
        return LightsResponse("success", emptyMap())
    }

    override suspend fun controlLight(request: LightControlRequest): LightControlResponse {
        return LightControlResponse("success", "ok", emptyMap())
    }

    override suspend fun sendEsp32LightCommand(light: String, action: String): Esp32CommandResponse {
        return Esp32CommandResponse("success", "ok", "esp32", "$light:$action")
    }

    override suspend fun processVoiceCommand(file: MultipartBody.Part): VoiceCommandResponse {
        return VoiceCommandResponse("success", "ok", "", 0.0, false, null, null)
    }

    override suspend fun getSensorStatus(): SensorStatus = SensorStatus()

    override suspend fun setSensorEnabled(payload: Map<String, Boolean>): SensorStatus = SensorStatus(
        enabled = payload["enabled"] ?: true
    )

    override suspend fun unlinkLight(payload: Map<String, String>): SensorStatus = SensorStatus()
}
