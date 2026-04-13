package com.tracker.scotmobile.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tracker.scotmobile.data.bluetooth.BluetoothPermissionHelper
import com.tracker.scotmobile.data.bluetooth.BluetoothService
import com.tracker.scotmobile.ui.theme.ScotMobileTheme
import com.tracker.scotmobile.ui.viewmodel.BluetoothViewModel
import com.tracker.scotmobile.ui.viewmodel.RfTestResult

/**
 * Tela principal do teste de RF.
 * Equivalente à ITS_Android Activity + BlankFragment, reescrita em Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfTestScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDeviceList: () -> Unit,
    viewModel: BluetoothViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val logScrollState = rememberScrollState()
    var showLog by remember { mutableStateOf(false) }
    var fakeMode by remember { mutableStateOf(viewModel.useFakeMode) }
    val context = LocalContext.current

    // Solicita permissões Bluetooth ao entrar na tela (apenas modo real)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* permissões concedidas/negadas — o VM verificará internamente */ }

    LaunchedEffect(Unit) {
        if (!fakeMode) {
            val missing = BluetoothPermissionHelper.missingPermissions(context)
            if (missing.isNotEmpty()) permissionLauncher.launch(missing)
        }
    }

    // Auto-scroll do log
    LaunchedEffect(uiState.log) {
        logScrollState.animateScrollTo(logScrollState.maxValue)
    }

    // Snackbar para erros
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teste RF") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Ícone de status Bluetooth
                    IconButton(onClick = onNavigateToDeviceList) {
                        Icon(
                            imageVector = when (uiState.connectionState) {
                                BluetoothService.State.CONNECTED -> Icons.Default.Bluetooth
                                BluetoothService.State.CONNECTING -> Icons.AutoMirrored.Filled.BluetoothSearching
                                else -> Icons.Default.BluetoothDisabled
                            },
                            contentDescription = "Bluetooth",
                            tint = when (uiState.connectionState) {
                                BluetoothService.State.CONNECTED -> Color(0xFF2196F3)
                                BluetoothService.State.CONNECTING -> Color(0xFFFFC107)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Toggle modo fake (emulador)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (fakeMode)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = if (fakeMode) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Emulação",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (fakeMode) "Ativo — simula hardware RF" else "Desativado — usa Bluetooth real",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = fakeMode,
                        onCheckedChange = { enabled ->
                            fakeMode = enabled
                            viewModel.useFakeMode = enabled
                            viewModel.disconnect()
                        }
                    )
                }
            }

            // Status da conexão
            ConnectionStatusCard(
                state = uiState.connectionState,
                deviceName = uiState.connectedDeviceName,
                cpValue = uiState.cpValue,
                onConnectClick = onNavigateToDeviceList,
                onDisconnectClick = { viewModel.disconnect() }
            )

            // Campo de ID da unidade
            OutlinedTextField(
                value = uiState.idUnit,
                onValueChange = { viewModel.updateIdUnit(it) },
                label = { Text("ID da unidade RF") },
                placeholder = { Text("Ex: A000001") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.startRfTest() }
                ),
                trailingIcon = {
                    if (uiState.idUnit.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateIdUnit("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                }
            )

            // Botão de teste
            Button(
                onClick = {
                    if (uiState.isTesting) viewModel.cancelTesting()
                    else viewModel.startRfTest()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isTesting)
                        MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Cancelar (tentativa ${uiState.retryCount}/${com.tracker.scotmobile.data.bluetooth.RfProtocol.MAX_RETRIES})")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar Teste RF")
                }
            }

            // Resultado do teste
            uiState.testResult?.let { result ->
                RfResultCard(result = result)
            }

            // Log de comunicação (expansível)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    TextButton(
                        onClick = { showLog = !showLog },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (showLog) "Ocultar log ▲" else "Exibir log ▼")
                    }
                    if (showLog) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                                .verticalScroll(logScrollState)
                        ) {
                            Text(
                                text = uiState.log.ifEmpty { "Nenhuma atividade registrada." },
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ConnectionStatusCard(
    state: BluetoothService.State,
    deviceName: String,
    cpValue: String,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (state) {
                    BluetoothService.State.CONNECTED -> Icons.Default.Bluetooth
                    BluetoothService.State.CONNECTING -> Icons.AutoMirrored.Filled.BluetoothSearching
                    else -> Icons.Default.BluetoothDisabled
                },
                contentDescription = null,
                tint = when (state) {
                    BluetoothService.State.CONNECTED -> Color(0xFF2196F3)
                    BluetoothService.State.CONNECTING -> Color(0xFFFFC107)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (state) {
                        BluetoothService.State.CONNECTED -> deviceName.ifEmpty { "Conectado" }
                        BluetoothService.State.CONNECTING -> "Conectando…"
                        else -> "Desconectado"
                    },
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = cpValue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (state == BluetoothService.State.CONNECTED) {
                TextButton(onClick = onDisconnectClick) { Text("Desconectar") }
            } else {
                TextButton(onClick = onConnectClick) { Text("Conectar") }
            }
        }
    }
}

@Composable
private fun RfResultCard(result: RfTestResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resultado do Teste",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ResultRow("ID Recebido", result.idReceive)
            ResultRow("Bat. Externa", "%.1f V".format(result.externBattery))
            ResultRow("Bat. Backup", "%.1f V".format(result.backupBattery))
            ResultRow("Carga Bat.", "${result.percentageBb}%")
            ResultRow("Temperatura", "${result.temperature} °C")
            ResultRow("Modo", result.mode.toString())
            ResultRow("BBTX", result.bbtx.toString())
            ResultRow("Horas RX BB", result.hourRxBb.toString())
            ResultRow("Firmware", "v${result.firmwareVersion}")
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// ---- Previews ----

private val previewResult = RfTestResult(
    idReceive = "000001",
    externBattery = 14.0f,
    backupBattery = 12.5f,
    percentageBb = 100,
    temperature = 25,
    mode = 3,
    bbtx = 1,
    hourRxBb = 100,
    firmwareVersion = 261
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Desconectado")
@Composable
private fun RfTestScreenDisconnectedPreview() {
    ScotMobileTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Teste RF") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectionStatusCard(
                    state = BluetoothService.State.NONE,
                    deviceName = "",
                    cpValue = "PI 0000",
                    onConnectClick = {},
                    onDisconnectClick = {}
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("ID da unidade RF") },
                    placeholder = { Text("Ex: A000001") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar Teste RF")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Conectado com resultado")
@Composable
private fun RfTestScreenConnectedPreview() {
    ScotMobileTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Teste RF") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectionStatusCard(
                    state = BluetoothService.State.CONNECTED,
                    deviceName = "PI-TRACKER-001",
                    cpValue = "PI 1234",
                    onConnectClick = {},
                    onDisconnectClick = {}
                )
                OutlinedTextField(
                    value = "A000001",
                    onValueChange = {},
                    label = { Text("ID da unidade RF") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar Teste RF")
                }
                RfResultCard(result = previewResult)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Testando")
@Composable
private fun RfTestScreenTestingPreview() {
    ScotMobileTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Teste RF") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectionStatusCard(
                    state = BluetoothService.State.CONNECTED,
                    deviceName = "PI-TRACKER-001",
                    cpValue = "PI 1234",
                    onConnectClick = {},
                    onDisconnectClick = {}
                )
                OutlinedTextField(
                    value = "A000001",
                    onValueChange = {},
                    label = { Text("ID da unidade RF") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Cancelar (tentativa 2/6)")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RfTestScreenDarkPreview() {
    ScotMobileTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Teste RF") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectionStatusCard(
                    state = BluetoothService.State.CONNECTED,
                    deviceName = "PI-TRACKER-001",
                    cpValue = "PI 1234",
                    onConnectClick = {},
                    onDisconnectClick = {}
                )
                OutlinedTextField(
                    value = "A000001",
                    onValueChange = {},
                    label = { Text("ID da unidade RF") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar Teste RF")
                }
                RfResultCard(result = previewResult)
            }
        }
    }
}
