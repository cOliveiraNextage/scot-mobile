package com.tracker.scotmobile.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Gerencia a descoberta de dispositivos Bluetooth e fornece a lista de pareados.
 * Migrado de DeviceListActivity.java.
 */
class BluetoothScanRepository(private val context: Context) {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun isBluetoothAvailable(): Boolean = adapter != null

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    /** Retorna dispositivos já pareados. */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getBondedDevices(): List<ScannedDevice> {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return emptyList()
        return adapter?.bondedDevices
            ?.map { ScannedDevice(it.name ?: "Desconhecido", it.address, paired = true) }
            ?: emptyList()
    }

    /**
     * Emite dispositivos descobertos via scan ativo.
     * O Flow fecha automaticamente quando a descoberta terminar ou o coletor cancelar.
     */
    @SuppressLint("MissingPermission")
    fun scanDevices(): Flow<ScanEvent> = callbackFlow {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            trySend(ScanEvent.Error("Permissão BLUETOOTH_SCAN não concedida"))
            close()
            return@callbackFlow
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getParcelableExtra(
                                    BluetoothDevice.EXTRA_DEVICE,
                                    BluetoothDevice::class.java
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        device?.let {
                            if (it.bondState != BluetoothDevice.BOND_BONDED) {
                                trySend(
                                    ScanEvent.DeviceFound(
                                        ScannedDevice(
                                            it.name ?: "Desconhecido",
                                            it.address,
                                            paired = false
                                        )
                                    )
                                )
                            }
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        trySend(ScanEvent.ScanFinished)
                        close()
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)

        if (adapter?.isDiscovering == true) adapter.cancelDiscovery()
        adapter?.startDiscovery()
        trySend(ScanEvent.ScanStarted)

        awaitClose {
            adapter?.cancelDiscovery()
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            adapter?.cancelDiscovery()
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    // ---- tipos ----

    data class ScannedDevice(
        val name: String,
        val address: String,
        val paired: Boolean
    )

    sealed class ScanEvent {
        object ScanStarted : ScanEvent()
        object ScanFinished : ScanEvent()
        data class DeviceFound(val device: ScannedDevice) : ScanEvent()
        data class Error(val message: String) : ScanEvent()
    }
}
