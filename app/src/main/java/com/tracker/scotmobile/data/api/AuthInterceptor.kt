package com.tracker.scotmobile.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Object singleton para notificar quando o token expira
 */
object TokenExpiredEvent {
    private val listeners = mutableSetOf<() -> Unit>()
    
    /**
     * Adiciona um listener para ser notificado quando o token expira
     */
    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }
    
    /**
     * Remove um listener
     */
    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }
    
    /**
     * Notifica todos os listeners que o token expirou
     */
    fun notifyTokenExpired() {
        listeners.forEach { it.invoke() }
    }
}

/**
 * Interceptor que detecta erros de autenticação (401 Unauthorized)
 * e notifica quando o token expira
 */
class AuthInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Verificar se a resposta é 401 (Unauthorized)
        if (response.code == 401) {
            // Notificar que o token expirou
            TokenExpiredEvent.notifyTokenExpired()
        }
        
        return response
    }
}

