package com.example.appcontroldeluz.backend

import android.content.Context

/**
 * Base del modulo de conexion Wi-Fi.
 *
 * Archivo esqueleto para integrar escaneo, emparejamiento y comunicacion de red.
 */
class WifiController(private val context: Context) {
    fun isAvailable(): Boolean {
        return false
    }

    fun isConnected(): Boolean {
        return false
    }

    fun disconnect() {
    }
}
