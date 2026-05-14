package com.example.appcontroldeluz.data.repository

import com.example.appcontroldeluz.data.config.HomeAutomationConfig
import com.example.appcontroldeluz.data.model.SensorStatus

class SensorsRepository(private val api: com.example.appcontroldeluz.data.remote.HomeAutomationApiService? = null) {
    // Local mock state used as fallback when remote endpoints are unavailable.
    private var sensorStatus = SensorStatus(
        enabled = true,
        linkedLights = HomeAutomationConfig.defaultSensorLinkedLights
    )

    suspend fun getSensorStatus(): Result<SensorStatus> = runCatching {
        if (api != null) {
            try {
                api.getSensorStatus()
            } catch (e: Exception) {
                // Fallback to local mock
                sensorStatus
            }
        } else {
            sensorStatus
        }
    }

    suspend fun setSensorEnabled(enabled: Boolean): Result<SensorStatus> = runCatching {
        sensorStatus = sensorStatus.copy(enabled = enabled)
        if (api != null) {
            try {
                api.setSensorEnabled(mapOf("enabled" to enabled))
            } catch (e: Exception) {
                // Keep local change as fallback
                sensorStatus
            }
        } else {
            sensorStatus
        }
    }

    suspend fun removeLinkedLight(lightId: String): Result<SensorStatus> = runCatching {
        sensorStatus = sensorStatus.copy(
            linkedLights = sensorStatus.linkedLights.filterNot { it.id == lightId }
        )
        if (api != null) {
            try {
                api.unlinkLight(mapOf("light_id" to lightId))
            } catch (e: Exception) {
                // fallback
                sensorStatus
            }
        } else {
            sensorStatus
        }
    }
}
