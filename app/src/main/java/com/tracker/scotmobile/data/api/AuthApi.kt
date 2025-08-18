package com.tracker.scotmobile.data.api

import com.tracker.scotmobile.data.model.LoginRequest
import com.tracker.scotmobile.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login/login-authentication")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}

