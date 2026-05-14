package com.example.appcontroldeluz.data.model

data class LinkedLightDevice(
    val id: String,
    val name: String,
    val status: String = "sincronizado"
)

data class SensorStatus(
    val enabled: Boolean = true,
    val lastDetectionLabel: String = "Hace 2 minutos • Pasillo Principal",
    val linkedLights: List<LinkedLightDevice> = emptyList()
)
