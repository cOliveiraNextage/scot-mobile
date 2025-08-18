package com.tracker.scotmobile.data.api

object ApiConfig {
    // URLs da API - Altere conforme necessário
    const val BASE_URL = "http://192.168.200.144:8080/"
    
    // Para desenvolvimento local (se necessário)
    // const val BASE_URL = "http://10.0.2.2:8080/" // Para emulador Android
    // const val BASE_URL = "http://localhost:8080/" // Para dispositivo físico
    
    // Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}

