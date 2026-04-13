package com.tracker.scotmobile.ui.viewmodel

import android.Manifest
import android.app.Application
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.scotmobile.data.bluetooth.BluetoothScanRepository
import com.tracker.scotmobile.data.bluetooth.BluetoothService
import com.tracker.scotmobile.data.bluetooth.FakeBluetoothService
import com.tracker.scotmobile.data.bluetooth.RfProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BluetoothUiState(
    // Conexão
    val connectionState: BluetoothService.State = BluetoothService.State.NONE,
    val connectedDeviceName: String = "",

    // Scan de dispositivos
    val isScanning: Boolean = false,
    val pairedDevices: List<BluetoothScanRepository.ScannedDevice> = emptyList(),
    val discoveredDevices: List<BluetoothScanRepository.ScannedDevice> = emptyList(),

    // Teste RF
    val idUnit: String = "",
    val cpValue: String = "PI 0000",
    val isTesting: Boolean = false,
    val retryCount: Int = 0,
    val testResult: RfTestResult? = null,

    // Log de comunicação
    val log: String = "",

    // Mensagem de erro/toast
    val errorMessage: String? = null
)

data class RfTestResult(
    val idReceive: String,
    val externBattery: Float,
    val backupBattery: Float,
    val percentageBb: Int,
    val temperature: Int,
    val mode: Int,
    val bbtx: Int,
    val hourRxBb: Int,
    val firmwareVersion: Int
)

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Mude para TRUE para usar o modo fake (emulador / sem hardware Bluetooth).
     * Em produção mantenha FALSE.
     */
    var useFakeMode: Boolean = false

    private val bluetoothService = BluetoothService(application)
    private val fakeService = FakeBluetoothService()
    private val scanRepository = BluetoothScanRepository(application)

    private val _uiState = MutableStateFlow(BluetoothUiState())
    val uiState: StateFlow<BluetoothUiState> = _uiState.asStateFlow()

    private var frameReaderJob: Job? = null
    private var retryJob: Job? = null

    private var pendingRawId: String = ""
    private var sentIdTowerThisAttempt = false
    private var communicatedRF = false

    init {
        // Observa estado da conexão real
        viewModelScope.launch {
            bluetoothService.state.collect { state ->
                if (!useFakeMode) {
                    _uiState.value = _uiState.value.copy(connectionState = state)
                    if (state == BluetoothService.State.NONE) appendLog("Bluetooth desconectado.")
                }
            }
        }
        // Observa estado da conexão fake
        viewModelScope.launch {
            fakeService.state.collect { state ->
                if (useFakeMode) {
                    _uiState.value = _uiState.value.copy(connectionState = state)
                    if (state == BluetoothService.State.NONE) appendLog("Bluetooth desconectado.")
                }
            }
        }
    }

    // ---- Scan / conexão ----

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun loadPairedDevices() {
        val devices = if (useFakeMode) {
            FakeBluetoothService.FAKE_DEVICES
        } else {
            scanRepository.getBondedDevices()
        }
        _uiState.value = _uiState.value.copy(pairedDevices = devices)
    }

    fun startScan() {
        if (_uiState.value.isScanning) return

        if (useFakeMode) {
            // No modo fake, simula scan rápido sem BroadcastReceiver
            _uiState.value = _uiState.value.copy(isScanning = true, discoveredDevices = emptyList())
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500)
                _uiState.value = _uiState.value.copy(isScanning = false)
            }
            return
        }

        _uiState.value = _uiState.value.copy(isScanning = true, discoveredDevices = emptyList())
        viewModelScope.launch {
            scanRepository.scanDevices().collect { event ->
                when (event) {
                    is BluetoothScanRepository.ScanEvent.DeviceFound -> {
                        val updated = _uiState.value.discoveredDevices + event.device
                        _uiState.value = _uiState.value.copy(discoveredDevices = updated)
                    }
                    is BluetoothScanRepository.ScanEvent.ScanFinished ->
                        _uiState.value = _uiState.value.copy(isScanning = false)
                    is BluetoothScanRepository.ScanEvent.Error ->
                        _uiState.value = _uiState.value.copy(
                            isScanning = false,
                            errorMessage = event.message
                        )
                    else -> Unit
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        if (!useFakeMode) scanRepository.stopScan()
        _uiState.value = _uiState.value.copy(isScanning = false)
    }

    fun connectToDevice(address: String) {
        val allDevices = _uiState.value.pairedDevices + _uiState.value.discoveredDevices
        val scanned = allDevices.find { it.address == address } ?: return

        viewModelScope.launch(Dispatchers.IO) {
            appendLog("Conectando a ${scanned.name}…")

            if (useFakeMode) {
                val result = fakeService.connect(scanned.name)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            connectedDeviceName = scanned.name,
                            errorMessage = null
                        )
                        appendLog("Conectado a ${scanned.name}. [MODO FAKE]")
                        startFrameReader()
                    },
                    onFailure = { e ->
                        appendLog("FALHA: ${e.message}")
                        _uiState.value = _uiState.value.copy(errorMessage = "Falha ao conectar.")
                    }
                )
                return@launch
            }

            val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter() ?: return@launch
            val device = adapter.getRemoteDevice(address)
            val result = bluetoothService.connect(device)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        connectedDeviceName = scanned.name,
                        errorMessage = null
                    )
                    appendLog("Conectado a ${scanned.name}.")
                    startFrameReader()
                },
                onFailure = { e ->
                    appendLog("FALHA ao conectar: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Não foi possível conectar ao dispositivo."
                    )
                }
            )
        }
    }

    fun disconnect() {
        cancelTesting()
        if (useFakeMode) fakeService.disconnect() else bluetoothService.disconnect()
        frameReaderJob?.cancel()
        frameReaderJob = null
        _uiState.value = _uiState.value.copy(connectedDeviceName = "", cpValue = "PI 0000")
    }

    // ---- Teste RF ----

    fun updateIdUnit(value: String) {
        _uiState.value = _uiState.value.copy(idUnit = value, errorMessage = null)
    }

    /**
     * Inicia o fluxo de teste RF:
     *  1. Envia ID Tower
     *  2. Aguarda 500ms e envia AutoReport
     *  3. Reagenda a cada RETRY_INTERVAL_MS até MAX_RETRIES ou sucesso
     */
    fun startRfTest() {
        val state = _uiState.value

        if (state.idUnit.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Informe o ID da unidade RF.")
            return
        }

        if (state.connectionState != BluetoothService.State.CONNECTED) {
            _uiState.value = state.copy(errorMessage = "Nenhum dispositivo Bluetooth conectado.")
            return
        }

        communicatedRF = false
        cancelTesting()

        _uiState.value = _uiState.value.copy(
            isTesting = true,
            retryCount = 0,
            testResult = null,
            log = "******* Iniciando teste de RF *******\n"
        )

        retryJob = viewModelScope.launch {
            var attempt = 0
            while (attempt < RfProtocol.MAX_RETRIES && !communicatedRF) {
                attempt++
                _uiState.value = _uiState.value.copy(retryCount = attempt)
                sentIdTowerThisAttempt = false
                writeReportRequest(attempt)
                delay(RfProtocol.RETRY_INTERVAL_MS)
            }

            if (!communicatedRF) {
                appendLog("Número máximo de tentativas atingido sem resposta.")
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    errorMessage = "Sem resposta do equipamento RF após ${RfProtocol.MAX_RETRIES} tentativas."
                )
            }
        }
    }

    fun cancelTesting() {
        retryJob?.cancel()
        retryJob = null
        _uiState.value = _uiState.value.copy(isTesting = false, retryCount = 0)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // ---- internals ----

    private fun startFrameReader() {
        frameReaderJob?.cancel()
        val channel = if (useFakeMode) fakeService.frameChannel else bluetoothService.frameChannel
        frameReaderJob = viewModelScope.launch {
            for (frame in channel) {
                Log.d("BluetoothVM", "Frame recebido: $frame")
                appendLog("Dados recebidos: \"${frame.trim()}\"")
                handleFrame(frame)
            }
        }
    }

    private fun handleFrame(frame: String) {
        val result = RfProtocol.decodeFrame(frame) ?: return
        when (result) {
            is RfProtocol.FrameResult.Ack -> {
                appendLog("ACK recebido da PI.")
                sentIdTowerThisAttempt = true
            }
            is RfProtocol.FrameResult.CpValue -> {
                appendLog("PI conectada — CP: ${result.cpStr}")
                _uiState.value = _uiState.value.copy(cpValue = "PI ${result.cpStr}")
            }
            is RfProtocol.FrameResult.AutoReportData -> {
                val state = _uiState.value
                val rawId = RfProtocol.extractRawId(state.idUnit)

                if (rawId.equals(result.idReceive, ignoreCase = true)) {
                    communicatedRF = true
                    cancelTesting()

                    val testResult = RfTestResult(
                        idReceive = result.idReceive,
                        externBattery = result.externBattery,
                        backupBattery = result.backupBattery,
                        percentageBb = result.percentageBb,
                        temperature = result.temperature,
                        mode = result.mode,
                        bbtx = result.bbtx,
                        hourRxBb = result.hourRxBb,
                        firmwareVersion = result.firmwareVersion
                    )

                    appendLog("AutoReport recebido com sucesso. Enviando GoToNormal…")
                    sendGoToNormal(rawId)

                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        testResult = testResult,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun writeBytes(bytes: ByteArray) {
        if (useFakeMode) fakeService.write(bytes) else bluetoothService.write(bytes)
    }

    private suspend fun writeReportRequest(attempt: Int) {
        val rawId = RfProtocol.extractRawId(_uiState.value.idUnit)
        pendingRawId = rawId

        if (!sentIdTowerThisAttempt) {
            appendLog("$attempt.ª Tentativa — Enviando ID Tower…")
            writeBytes(RfProtocol.ID_TOWER_BYTES)
            delay(RfProtocol.ID_TOWER_DELAY_MS)
        }

        appendLog("Enviando AutoReport: T,3C,04,$rawId")
        writeBytes(RfProtocol.buildAutoReportCommand(rawId))
    }

    private fun sendGoToNormal(rawId: String) {
        appendLog("Enviando GoToNormal: T,3C,0C,$rawId,04")
        writeBytes(RfProtocol.buildGoToNormalCommand(rawId))
    }

    private fun appendLog(message: String) {
        val current = _uiState.value.log
        _uiState.value = _uiState.value.copy(log = "$current$message\n")
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothService.disconnect()
    }
}
