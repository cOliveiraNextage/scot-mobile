package com.tracker.scotmobile.data.bluetooth

/**
 * Protocolo RF de comunicação com a PI (Radio Interface).
 *
 * Migrado de ITS_Android.java — encapsula toda a lógica de
 * codificação de comandos e decodificação de frames recebidos.
 *
 * Sequência de comunicação:
 *  1. Envia ID Tower: P,03,TTTT1 (bytes fixos)
 *  2. Aguarda 500ms e envia AutoReport: T,3C,04,{ID}\r
 *  3. Recebe ACK "To" (frame 'T', length 3, char[1] == 'o')
 *  4. Recebe frame 'Y' com valor CP (PI number)
 *  5. Recebe frame 'r' (60 ou 64 chars) com dados completos
 *  6. Envia GoToNormal: T,3C,0C,{ID},04\r
 */
object RfProtocol {

    /** Comando de ID Tower — sempre os mesmos bytes. */
    val ID_TOWER_BYTES: ByteArray = byteArrayOf(
        0x50, 0x2C, 0x30, 0x33, 0x2C,
        0x54, 0x54, 0x54, 0x54, 0x31, 0x0D
    )

    /** Comprimento esperado do ID da unidade RF (sem o primeiro caractere). */
    const val ID_UNIT_RAW_LENGTH = 6

    /** Número máximo de tentativas de AutoReport antes de desistir. */
    const val MAX_RETRIES = 6

    /** Intervalo entre tentativas em ms. */
    const val RETRY_INTERVAL_MS = 10_000L

    /** Atraso entre ID Tower e AutoReport em ms. */
    const val ID_TOWER_DELAY_MS = 500L

    /**
     * Monta o comando AutoReport para o ID informado.
     * @param rawId 6 caracteres (serial sem o primeiro char, uppercase)
     */
    fun buildAutoReportCommand(rawId: String): ByteArray =
        "T,3C,04,$rawId\r".toByteArray()

    /**
     * Monta o comando GoToNormal para o ID informado.
     * @param rawId 6 caracteres (serial sem o primeiro char, uppercase)
     */
    fun buildGoToNormalCommand(rawId: String): ByteArray =
        "T,3C,0C,$rawId,04\r".toByteArray()

    /**
     * Extrai o rawId do serial digitado pelo usuário:
     * remove o primeiro caractere e converte para uppercase.
     */
    fun extractRawId(fullSerial: String): String =
        fullSerial.uppercase().substring(1)

    /**
     * Decodifica um frame recebido via Bluetooth.
     * Retorna o resultado adequado ou null se o frame for irrelevante.
     */
    fun decodeFrame(frame: String): FrameResult? {
        if (frame.isEmpty()) return null
        return try {
            when (frame[0]) {
                'T' -> decodeAck(frame)
                'Y' -> decodeCp(frame)
                'r' -> decodeData(frame)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    // ---- decodificadores privados ----

    private fun decodeAck(frame: String): FrameResult? {
        // ACK: frame de 3 chars onde char[1] == 'o'  →  "To\r"
        return if (frame.length == 3 && frame[1] == 'o') FrameResult.Ack else null
    }

    private fun decodeCp(frame: String): FrameResult? {
        // Y + 4 dígitos hex do valor CP
        if (frame.length < 5) return null
        val cpStr = frame.substring(1, 5)
        val cpInt = cpStr.toIntOrNull() ?: return null
        val hexStr = "%04X".format(cpInt)
        val cpH = hexStr.substring(2, 4).toInt(16)
        val cpL = hexStr.substring(0, 2).toInt(16)
        return FrameResult.CpValue(cpStr, cpInt, cpH, cpL)
    }

    private fun decodeData(frame: String): FrameResult? {
        if (frame.length != 60 && frame.length != 64) return null

        // r,-047,04,IIIIII,... — tipo em pos 7-8, ID em pos 10-15
        val msgType = frame.substring(7, 9)
        val idReceive = frame.substring(10, 16)

        // Só processa AutoReport (04) — GoToNormal (0C) é ignorado na decodificação
        if (msgType != "04") return null

        val aux = StringBuilder()

        fun hex2(start: Int) = frame.substring(start, start + 2).toInt(16)
        fun hex4(start: Int) = frame.substring(start, start + 4).toInt(16)

        val externBattery = hex2(23).toFloat()
        val backupBattery = hex2(25).toFloat() / 10f
        val percentageBb = hex2(29)
        val temperature = hex2(31)
        val mode = hex2(39)
        val bbtx = hex2(51)

        val hourRxBb: Int
        val firmwareVersion: Int
        val lengthH: Byte
        val lengthL: Byte
        val lengthData: Byte

        if (frame.length == 64) {
            lengthH = 0x4C
            lengthL = 0x00
            lengthData = 0x43
            hourRxBb = hex4(55)
            firmwareVersion = hex4(59)
        } else {
            lengthH = 0x48
            lengthL = 0x00
            lengthData = 0x3F
            hourRxBb = 0
            firmwareVersion = 0xDB
        }

        return FrameResult.AutoReportData(
            idReceive = idReceive,
            externBattery = externBattery,
            backupBattery = backupBattery,
            percentageBb = percentageBb,
            temperature = temperature,
            mode = mode,
            bbtx = bbtx,
            hourRxBb = hourRxBb,
            firmwareVersion = firmwareVersion,
            lengthH = lengthH,
            lengthL = lengthL,
            lengthData = lengthData
        )
    }

    // ---- tipos de resultado ----

    sealed class FrameResult {
        /** ACK de confirmação "To" */
        object Ack : FrameResult()

        /** Valor CP (PI number) recebido via frame 'Y' */
        data class CpValue(
            val cpStr: String,
            val cpInt: Int,
            val cpH: Int,
            val cpL: Int
        ) : FrameResult()

        /** Dados completos do AutoReport via frame 'r' */
        data class AutoReportData(
            val idReceive: String,
            val externBattery: Float,
            val backupBattery: Float,
            val percentageBb: Int,
            val temperature: Int,
            val mode: Int,
            val bbtx: Int,
            val hourRxBb: Int,
            val firmwareVersion: Int,
            val lengthH: Byte,
            val lengthL: Byte,
            val lengthData: Byte
        ) : FrameResult()
    }
}
