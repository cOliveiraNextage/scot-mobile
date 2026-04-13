package com.tracker.scotmobile.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tracker.scotmobile.data.bluetooth.BluetoothScanRepository
import com.tracker.scotmobile.ui.theme.ScotMobileTheme
import com.tracker.scotmobile.ui.viewmodel.BluetoothUiState

/**
 * Tela de seleção de dispositivo Bluetooth.
 * Exibe pareados e permite scan de novos dispositivos.
 * Migrado de DeviceListActivity.java para Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    uiState: BluetoothUiState,
    onDeviceSelected: (address: String) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isScanning) "Buscando dispositivos…"
                        else "Selecionar dispositivo"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Botão de scan
            Button(
                onClick = { if (uiState.isScanning) onStopScan() else onStartScan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isScanning) Icons.Default.Bluetooth
                    else Icons.AutoMirrored.Filled.BluetoothSearching,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(if (uiState.isScanning) "Parar busca" else "Buscar dispositivos")
            }

            if (uiState.isScanning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Dispositivos pareados
                if (uiState.pairedDevices.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Dispositivos pareados")
                    }
                    items(uiState.pairedDevices) { device ->
                        DeviceItem(device = device, onClick = { onDeviceSelected(device.address) })
                    }
                }

                // Dispositivos descobertos
                if (uiState.discoveredDevices.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Outros dispositivos")
                    }
                    items(uiState.discoveredDevices) { device ->
                        DeviceItem(device = device, onClick = { onDeviceSelected(device.address) })
                    }
                }

                if (uiState.pairedDevices.isEmpty() && uiState.discoveredDevices.isEmpty() && !uiState.isScanning) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum dispositivo encontrado.\nPressione 'Buscar dispositivos'.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---- Previews ----

private val previewPairedDevices = listOf(
    BluetoothScanRepository.ScannedDevice("PI-TRACKER-001", "00:11:22:33:44:55", paired = true),
    BluetoothScanRepository.ScannedDevice("PI-TRACKER-002", "AA:BB:CC:DD:EE:FF", paired = true)
)

private val previewDiscoveredDevices = listOf(
    BluetoothScanRepository.ScannedDevice("Dispositivo Novo", "11:22:33:44:55:66", paired = false)
)

@Preview(showBackground = true, showSystemUi = true, name = "Lista de dispositivos")
@Composable
private fun DeviceListScreenPreview() {
    ScotMobileTheme {
        DeviceListScreen(
            uiState = BluetoothUiState(
                pairedDevices = previewPairedDevices,
                discoveredDevices = previewDiscoveredDevices,
                isScanning = false
            ),
            onDeviceSelected = {},
            onStartScan = {},
            onStopScan = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Scanning")
@Composable
private fun DeviceListScreenScanningPreview() {
    ScotMobileTheme {
        DeviceListScreen(
            uiState = BluetoothUiState(
                pairedDevices = previewPairedDevices,
                discoveredDevices = emptyList(),
                isScanning = true
            ),
            onDeviceSelected = {},
            onStartScan = {},
            onStopScan = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Vazio")
@Composable
private fun DeviceListScreenEmptyPreview() {
    ScotMobileTheme {
        DeviceListScreen(
            uiState = BluetoothUiState(),
            onDeviceSelected = {},
            onStartScan = {},
            onStopScan = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeviceListScreenDarkPreview() {
    ScotMobileTheme {
        DeviceListScreen(
            uiState = BluetoothUiState(
                pairedDevices = previewPairedDevices,
                discoveredDevices = previewDiscoveredDevices
            ),
            onDeviceSelected = {},
            onStartScan = {},
            onStopScan = {},
            onNavigateBack = {}
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    HorizontalDivider()
}

@Composable
private fun DeviceItem(
    device: BluetoothScanRepository.ScannedDevice,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(device.name) },
        supportingContent = { Text(device.address) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = null,
                tint = if (device.paired) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}
