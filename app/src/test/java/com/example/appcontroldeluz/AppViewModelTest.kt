package com.example.appcontroldeluz

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.appcontroldeluz.data.model.HealthResponse
import com.example.appcontroldeluz.data.model.Esp32CommandResponse
import com.example.appcontroldeluz.data.model.LightControlRequest
import com.example.appcontroldeluz.data.model.LightControlResponse
import com.example.appcontroldeluz.data.model.LightsResponse
import com.example.appcontroldeluz.data.model.SensorStatus
import com.example.appcontroldeluz.data.model.VoiceCommandResponse
import com.example.appcontroldeluz.data.remote.VoiceApiService
import com.example.appcontroldeluz.data.repository.LightsRepository
import com.example.appcontroldeluz.ui.viewmodel.AppViewModel
import com.example.appcontroldeluz.ui.viewmodel.MemoryLightCacheStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun controlRoom_rollsBack_whenApiFails() = runTest {
        val repository = LightsRepository(FailingControlApi())
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = AppViewModel(app, repository, cacheStore = MemoryLightCacheStore())

        advanceUntilIdle() // init

        vm.controlRoom("sala", true)
        // optimistic update
        assertEquals(true, vm.lights.value["sala"])

        advanceUntilIdle()
        // rollback after backend error
        assertEquals(false, vm.lights.value["sala"])
    }

    @Test
    fun controlAll_setsAllLights_whenApiSucceeds() = runTest {
        val repository = LightsRepository(SuccessApi())
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = AppViewModel(app, repository, cacheStore = MemoryLightCacheStore())

        advanceUntilIdle() // init
        vm.controlAll(true)
        advanceUntilIdle()

        assertEquals(true, vm.lights.value["sala"])
        assertEquals(true, vm.lights.value["cocina"])
        assertEquals(true, vm.lights.value["dormitorio"])
    }
}

private class SuccessApi : VoiceApiService {
    override suspend fun getHealth(): HealthResponse = HealthResponse("healthy", true, "eastus", 0, 0)

    override suspend fun getLights(): LightsResponse = LightsResponse(
        status = "success",
        lights = mapOf(
            "sala" to false,
            "cocina" to false,
            "dormitorio" to false,
            "baño" to false,
            "jardin" to false,
            "garage" to false
        )
    )

    override suspend fun controlLight(request: LightControlRequest): LightControlResponse {
        val allOn = request.room == "todas" && request.state
        val map = if (allOn) {
            mapOf("sala" to true, "cocina" to true, "dormitorio" to true, "baño" to true, "jardin" to true, "garage" to true)
        } else {
            mapOf("sala" to request.state)
        }
        return LightControlResponse("success", "ok", map)
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

private class FailingControlApi : VoiceApiService {
    override suspend fun getHealth(): HealthResponse = HealthResponse("healthy", true, "eastus", 0, 0)

    override suspend fun getLights(): LightsResponse = LightsResponse(
        status = "success",
        lights = mapOf(
            "sala" to false,
            "cocina" to false,
            "dormitorio" to false,
            "baño" to false,
            "jardin" to false,
            "garage" to false
        )
    )

    override suspend fun controlLight(request: LightControlRequest): LightControlResponse {
        throw IllegalStateException("backend error")
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
