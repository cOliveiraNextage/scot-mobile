package com.tracker.scotmobile.data.repository

import com.tracker.scotmobile.data.api.RetrofitClient
import com.tracker.scotmobile.data.mapper.UserMapper
import com.tracker.scotmobile.data.model.LoginRequest
import com.tracker.scotmobile.data.model.LoginResponse
import com.tracker.scotmobile.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val authApi = RetrofitClient.authApi
    
    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = authApi.login(loginRequest)
                
                // Usar o mapper para converter a resposta complexa
                UserMapper.mapLoginResponse(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
