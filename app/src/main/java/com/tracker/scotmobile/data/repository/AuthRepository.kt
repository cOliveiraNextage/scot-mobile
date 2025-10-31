package com.tracker.scotmobile.data.repository

import android.content.Context
import com.tracker.scotmobile.data.api.TokenExpiredEvent
import com.tracker.scotmobile.data.api.RetrofitClient
import com.tracker.scotmobile.data.local.repository.UserLocalRepository
import com.tracker.scotmobile.data.mapper.UserMapper
import com.tracker.scotmobile.data.model.LoginRequest
import com.tracker.scotmobile.data.model.LoginResponse
import com.tracker.scotmobile.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthRepository(context: Context) {
    private val authApi = RetrofitClient.authApi
    private val userLocalRepository = UserLocalRepository(context)
    
    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = authApi.login(loginRequest)
                
                // Usar o mapper para converter a resposta complexa
                val result = UserMapper.mapLoginResponse(response)
                
                // Se o login foi bem-sucedido, salvar no banco local
                result.onSuccess { user ->
                    userLocalRepository.saveUser(user)
                }
                
                result
            } catch (e: HttpException) {
                // Verificar se é erro 401 (Unauthorized)
                if (e.code() == 401) {
                    TokenExpiredEvent.notifyTokenExpired()
                }
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Obtém o usuário logado do banco local
     */
    suspend fun getLoggedInUser(): User? {
        return withContext(Dispatchers.IO) {
            userLocalRepository.getLoggedInUser()
        }
    }
    
    /**
     * Obtém o usuário atual como Flow
     */
    fun getCurrentUser() = userLocalRepository.getCurrentUser()
    
    /**
     * Faz logout removendo dados do banco local
     */
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            userLocalRepository.clearAllUsers()
        }
    }
    
    /**
     * Verifica se há usuário logado
     */
    suspend fun hasLoggedInUser(): Boolean {
        return withContext(Dispatchers.IO) {
            userLocalRepository.hasLoggedInUser()
        }
    }
}
