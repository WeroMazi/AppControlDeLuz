package com.example.appcontroldeluz.backend

import android.content.Context

/**
 * Base de conexion Bluetooth.
 *
 * Esta clase queda como esqueleto para integrar logica real en una siguiente etapa.
 */
class BluetoothController(private val context: Context) {
    fun isAvailable(): Boolean {
        return false
    }

    fun connect(address: String): Boolean {
        return false
    }

    fun sendCommand(command: String): Boolean {
        return false
    }

    fun disconnect() {
    }
}
