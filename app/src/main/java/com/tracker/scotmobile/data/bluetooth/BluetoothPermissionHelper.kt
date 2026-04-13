package com.tracker.scotmobile.data.bluetooth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Centraliza a lógica de verificação de permissões Bluetooth.
 * Chame [requiredPermissions] para obter a lista de permissões
 * que ainda precisam ser solicitadas.
 */
object BluetoothPermissionHelper {

    /**
     * Lista de permissões Bluetooth exigidas em tempo de execução,
     * de acordo com a versão do Android do dispositivo.
     */
    val requiredPermissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            // Android 6–11
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

    /** Retorna true se todas as permissões necessárias já foram concedidas. */
    fun hasAllPermissions(context: Context): Boolean =
        requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }

    /** Retorna apenas as permissões que ainda não foram concedidas. */
    fun missingPermissions(context: Context): Array<String> =
        requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
}
