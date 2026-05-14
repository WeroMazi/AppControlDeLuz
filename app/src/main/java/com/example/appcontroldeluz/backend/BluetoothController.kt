package com.example.appcontroldeluz.backend

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.IOException
import java.util.*

/**
 * Clase encargada de la conexión con el Arduino vía Bluetooth.
 * Los métodos están preparados pero desactivados para no interferir con el desarrollo actual.
 */
class BluetoothController(private val context: Context) {
    private val bluetoothManager: BluetoothManager? = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private var bluetoothSocket: BluetoothSocket? = null
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    /**
     * Conecta al dispositivo Bluetooth por su dirección MAC.
     * @SuppressLint("MissingPermission") se usa para evitar advertencias del IDE
     * hasta que implementemos la solicitud de permisos real.
     */
    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        // Comentado para evitar errores de ejecución durante el diseño de la UI
        /*
        try {
            val device = bluetoothAdapter?.getRemoteDevice(address) ?: return false
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            return true
        } catch (e: IOException) {
            return false
        }
        */
        return false
    }

    @SuppressLint("MissingPermission")
    fun sendCommand(command: String) {
        // Lógica de envío preparada para el futuro
    }

    fun disconnect() {
        // Lógica de desconexión preparada para el futuro
    }
}
