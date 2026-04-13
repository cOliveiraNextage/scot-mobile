package com.tracker.scotmobile.data.bluetooth

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Implementação FAKE do BluetoothService para uso no emulador ou testes de UI.
 *
 * Simula todo o fluxo do protocolo RF:
 *   1. Conexão imediata (sem hardware)
 *   2. Resposta ACK "To\r" após receber AutoReport
 *   3. Resposta CP "Y1234\r" simulando PI number
 *   4. Resposta AutoReport "r,-047,04,{ID}..." com dados realistas
 *
 * Para ativar: passe useFake = true no BluetoothViewModel
 * ou defina BluetoothServiceFactory.USE_FAKE = true.
 */
class FakeBluetoothService {

    companion object {
        private const val TAG = "FakeBluetoothService"

        /** Dispositivos fictícios que aparecem na lista de pareados */
        val FAKE_DEVICES = listOf(
            BluetoothScanRepository.ScannedDevice(
                name = "PI-TRACKER-001",
                address = "00:11:22:33:44:55",
                paired = true
            ),
            BluetoothScanRepository.ScannedDevice(
                name = "PI-TRACKER-002",
                address = "AA:BB:CC:DD:EE:FF",
                paired = true
            )
        )
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(BluetoothService.State.NONE)
    val state: StateFlow<BluetoothService.State> = _state.asStateFlow()

    val frameChannel = Channel<String>(Channel.BUFFERED)

    private var connectedDevice: String = ""

    /** Simula conexão com ~800ms de delay (como uma conexão BT real). */
    suspend fun connect(deviceName: String): Result<Unit> {
        _state.value = BluetoothService.State.CONNECTING
        delay(800)
        connectedDevice = deviceName
        _state.value = BluetoothService.State.CONNECTED
        Log.d(TAG, "Conectado ao dispositivo fake: $deviceName")
        return Result.success(Unit)
    }

    /**
     * Processa o comando enviado e emite as respostas simuladas do protocolo RF.
     *
     * Fluxo:
     *   - Recebe ID Tower  → ignora (PI real não responde)
     *   - Recebe AutoReport (T,3C,04,ID) → responde ACK + CP + dados
     *   - Recebe GoToNormal (T,3C,0C,ID) → ignora
     */
    fun write(bytes: ByteArray) {
        val text = String(bytes)
        Log.d(TAG, "Comando recebido pelo fake: $text")

        when {
            // ID Tower — sem resposta (igual à PI real)
            bytes.contentEquals(RfProtocol.ID_TOWER_BYTES) -> {
                Log.d(TAG, "ID Tower recebido — sem resposta (aguardando AutoReport)")
            }

            // AutoReport: T,3C,04,XXXXXX\r
            text.startsWith("T,3C,04,") -> {
                val rawId = text.removePrefix("T,3C,04,").trimEnd('\r', '\n')
                Log.d(TAG, "AutoReport recebido para ID: $rawId")
                simulateRfResponse(rawId)
            }

            // GoToNormal — sem resposta necessária
            text.startsWith("T,3C,0C,") -> {
                Log.d(TAG, "GoToNormal recebido — fluxo encerrado")
            }
        }
    }

    fun disconnect() {
        _state.value = BluetoothService.State.NONE
        connectedDevice = ""
        Log.d(TAG, "Desconectado")
    }

    // ---- simulação de respostas ----

    private fun simulateRfResponse(rawId: String) {
        scope.launch {
            // 1. ACK "To\r" — chega logo após o AutoReport
            delay(300)
            frameChannel.send("To\r")
            Log.d(TAG, "Enviando ACK: To")

            // 2. CP value "Y1234\r" — PI number
            delay(200)
            frameChannel.send("Y1234\r")
            Log.d(TAG, "Enviando CP: Y1234")

            // 3. Dados do AutoReport "r,..." — frame de 64 chars
            delay(400)
            val dataFrame = buildFakeDataFrame(rawId)
            frameChannel.send(dataFrame)
            Log.d(TAG, "Enviando AutoReport: $dataFrame")
        }
    }

    /**
     * Monta um frame 'r' de 64 caracteres com valores realistas.
     *
     * Formato: r,-047,04,IIIIII,05,14,00XX XX XX XX XX...
     * Posições importantes (mesmas do RfProtocol.decodeFrame):
     *   7-8:   msgType  = "04"
     *   10-15: ID       = rawId
     *   23-24: ExtBat   = "0E" → 14 → 14.0 V
     *   25-26: BkpBat   = "7D" → 125 → 12.5 V
     *   29-30: Percent  = "64" → 100%
     *   31-32: Temp     = "19" → 25 °C
     *   39-40: Mode     = "03"
     *   51-52: BBTX     = "01"
     *   55-58: HourRxBB = "0064" → 100 h
     *   59-62: FWv      = "0105" → 261
     */
    private fun buildFakeDataFrame(rawId: String): String {
        // Garante que rawId tem exatamente 6 chars
        val id = rawId.padEnd(6, '0').take(6)

        // Constrói campo a campo para bater com as posições do parser
        // r,-047,04,IIIIII,05,14,00EEBB00PP TT 000000 00 00 00 03 00 00 00 00 00 BB 00 00 BT 00 HHHH FFFF
        //  0123456789012345678901234567890123456789012345678901234567890123
        //  0         1         2         3         4         5         6
        val frame = buildString {
            append("r,-047,")    // 0-6   (7 chars)
            append("04,")        // 7-9   msgType=04
            append(id)           // 10-15 ID (6 chars)
            append(",05,14,")    // 16-22 (7 chars)
            append("0E")         // 23-24 ExtBat = 14 V
            append("7D")         // 25-26 BkpBat = 125 → 12.5 V
            append("00")         // 27-28 pad
            append("64")         // 29-30 Percentage = 100%
            append("19")         // 31-32 Temperature = 25 °C
            append("000000")     // 33-38 pad
            append("03")         // 39-40 Mode = 3
            append("0000000000") // 41-50 pad
            append("01")         // 51-52 BBTX = 1
            append("00")         // 53-54 pad
            append("0064")       // 55-58 HourRxBB = 100 h
            append("0105")       // 59-62 FirmwareVersion = 261
            append("\r")         // 63    delimitador
        }
        return frame
    }
}
