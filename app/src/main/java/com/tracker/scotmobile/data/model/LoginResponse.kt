package com.tracker.scotmobile.data.model

// Resposta completa da API
data class LoginResponse(
    val success: Boolean,
    val `object`: LoginObject? = null
)

data class LoginObject(
    val success: Boolean,
    val message: String,
    val `object`: LoginData? = null
)

data class LoginData(
    val jwt: JwtToken? = null,
    val scUserDTO: ScUserDTO? = null,
    val accountPermission: String? = null // Não vamos usar
)

data class JwtToken(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val userId: Long
)

data class ScUserDTO(
    val fnUserId: Long,
    val fcUserNm: String,
    val fcUserLogin: String,
    val fcUserDocNu: String,
    val scRole: ScRole? = null
)

data class ScRole(
    val fnRoleId: Long,
    val fcRoleDs: String,
    val fcRoleNm: String
)

// Modelo simplificado para uso no app
data class User(
    val id: Long,
    val name: String,
    val login: String,
    val document: String,
    val role: Role? = null,
    val token: String? = null
)

data class Role(
    val id: Long,
    val description: String,
    val name: String
)
