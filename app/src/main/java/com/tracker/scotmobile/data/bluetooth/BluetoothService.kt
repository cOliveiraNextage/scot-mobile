package com.tracker.scotmobile.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/**
 * Gerencia a conexão Bluetooth via perfil SPP (Serial Port Profile).
 * Migrado de BluetoothService.java — substitui Handler/Thread por coroutines e Channel.
 */
class BluetoothService(private val context: Context) {

    enum class State { NONE, CONNECTING, CONNECTED }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _state = MutableStateFlow(State.NONE)
    val state: StateFlow<State> = _state.asStateFlow()

    /** Canal de frames recebidos. Cada elemento é um frame terminado em 0x0D. */
    val frameChannel = Channel<String>(Channel.BUFFERED)

    private var socket: BluetoothSocket? = null
    private var connectedThread: ConnectedThread? = null

    companion object {
        private const val TAG = "BluetoothService"
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getBondedDevices(): Set<BluetoothDevice> {
        if (!hasConnectPermission()) return emptySet()
        return bluetoothAdapter?.bondedDevices ?: emptySet()
    }

    /** Conecta ao dispositivo via RFCOMM. Deve ser chamado em IO dispatcher. */
    @SuppressLint("MissingPermission")
    suspend fun connect(device: BluetoothDevice): Result<Unit> = withContext(Dispatchers.IO) {
        disconnect()
        _state.value = State.CONNECTING

        try {
            val btSocket = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                device.createRfcommSocketToServiceRecord(SPP_UUID)
            } else {
                device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
            }

            bluetoothAdapter?.cancelDiscovery()
            btSocket.connect()

            socket = btSocket
            connectedThread = ConnectedThread(btSocket).also { it.start() }

            _state.value = State.CONNECTED
            Result.success(Unit)
        } catch (e: IOException) {
            Log.e(TAG, "Falha na conexão Bluetooth", e)
            _state.value = State.NONE
            Result.failure(e)
        }
    }

    /** Envia bytes para o dispositivo conectado. */
    fun write(bytes: ByteArray) {
        connectedThread?.write(bytes)
    }

    /** Encerra a conexão e reseta o estado. */
    fun disconnect() {
        connectedThread?.cancel()
        connectedThread = null
        try { socket?.close() } catch (_: IOException) {}
        socket = null
        _state.value = State.NONE
    }

    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    /**
     * Thread de leitura contínua do socket.
     * Acumula bytes no buffer até receber 0x0D (delimitador de frame).
     */
    private inner class ConnectedThread(private val btSocket: BluetoothSocket) : Thread() {

        private val buffer = CharArray(65535)
        private var index = 0

        @Volatile
        private var running = true

        override fun run() {
            val inputStream = btSocket.inputStream
            val readBuffer = ByteArray(1024)

            while (running) {
                try {
                    sleep(50) // aguarda dados prontos (mesmo intervalo do original)
                    val bytes = inputStream.read(readBuffer)
                    if (bytes > 0) {
                        for (i in 0 until bytes) {
                            buffer[index++] = readBuffer[i].toInt().toChar()
                            if (readBuffer[i] == 0x0D.toByte()) {
                                val frame = String(buffer, 0, index)
                                frameChannel.trySend(frame)
                                index = 0
                            }
                        }
                    }
                } catch (e: IOException) {
                    if (running) {
                        Log.e(TAG, "Conexão perdida", e)
                        running = false
//                        _state.value = State.WAITING
                    }
                    break
                } catch (_: InterruptedException) {
                    break
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                btSocket.outputStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Erro ao escrever", e)
            }
        }

        fun cancel() {
            running = false
            try { btSocket.close() } catch (_: IOException) {}
        }
    }
}
