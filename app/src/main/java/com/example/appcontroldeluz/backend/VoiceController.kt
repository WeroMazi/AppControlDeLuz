package com.example.appcontroldeluz.backend

import android.content.Context

/**
 * Base del modulo de voz.
 *
 * Esta clase queda como esqueleto para integrar reconocimiento, parser de comandos
 * y llamadas al backend en una siguiente etapa.
 */
class VoiceController(private val context: Context, private val onResult: (String) -> Unit) {
    fun isAvailable(): Boolean {
        return false
    }

    fun startListening() {
    }

    fun stopListening() {
    }

    fun destroy() {
    }
}
