package com.example.appcontroldeluz.data.config

import com.example.appcontroldeluz.data.model.LinkedLightDevice

object HomeAutomationConfig {
    // Room names and aliases are centralized here for easier future edits.
    val canonicalRooms: List<String> = listOf(
        "sala",
        "cocina",
        "dormitorio",
        "baño",
        "jardin",
        "garage"
    )

    private val roomAliases: Map<String, String> = mapOf(
        "sala" to "sala",
        "cocina" to "cocina",
        "dormitorio" to "dormitorio",
        "habitacion" to "dormitorio",
        "habitación" to "dormitorio",
        "cuarto" to "dormitorio",
        "recamara" to "dormitorio",
        "recámara" to "dormitorio",
        "baño" to "baño",
        "bano" to "baño",
        "jardin" to "jardin",
        "jardín" to "jardin",
        "patio" to "jardin",
        "garage" to "garage",
        "garaje" to "garage",
        "cochera" to "garage",
        "todas" to "todas",
        "todos" to "todas"
    )

    val defaultSensorLinkedLights: List<LinkedLightDevice> = listOf(
        LinkedLightDevice(id = "pasillo_techo_1", name = "Techo Pasillo 1"),
        LinkedLightDevice(id = "lampara_decorativa", name = "Lámpara Decorativa"),
        LinkedLightDevice(id = "luz_cortesia", name = "Luz de Cortesía")
    )

    const val azureBaseUrlExample: String = "https://<tu-servicio-azure>.azurewebsites.net/"

    fun canonicalizeRoom(input: String): String {
        val normalizedInput = input.trim().lowercase()
        return roomAliases[normalizedInput] ?: normalizedInput
    }

    fun normalizeLights(lights: Map<String, Boolean>): Map<String, Boolean> {
        val normalized = mutableMapOf<String, Boolean>()
        lights.forEach { (key, value) ->
            normalized[canonicalizeRoom(key)] = value
        }

        return canonicalRooms.associateWith { room -> normalized[room] ?: false }
    }
}
