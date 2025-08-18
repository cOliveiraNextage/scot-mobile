package com.tracker.scotmobile.data.mapper

import com.tracker.scotmobile.data.model.*

object UserMapper {
    
    /**
     * Mapeia a resposta complexa da API para o modelo simplificado do app
     */
    fun mapLoginResponse(loginResponse: LoginResponse): Result<User> {
        return try {
            val loginObject = loginResponse.`object`
            if (loginObject == null) {
                return Result.failure(Exception("Resposta da API inválida"))
            }
            
            val loginData = loginObject.`object`
            if (loginData == null) {
                return Result.failure(Exception("Dados de login não encontrados"))
            }
            
            val jwt = loginData.jwt
            val scUserDTO = loginData.scUserDTO
            
            if (jwt == null || scUserDTO == null) {
                return Result.failure(Exception("Token ou dados do usuário não encontrados"))
            }
            
            val user = User(
                id = scUserDTO.fnUserId,
                name = scUserDTO.fcUserNm,
                login = scUserDTO.fcUserLogin,
                document = scUserDTO.fcUserDocNu,
                role = scUserDTO.scRole?.let { mapRole(it) },
                token = jwt.accessToken
            )
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Mapeia a role da API para o modelo simplificado
     */
    private fun mapRole(scRole: ScRole): Role {
        return Role(
            id = scRole.fnRoleId,
            description = scRole.fcRoleDs,
            name = scRole.fcRoleNm
        )
    }
    
    /**
     * Verifica se o login foi bem-sucedido
     */
    fun isLoginSuccessful(loginResponse: LoginResponse): Boolean {
        return loginResponse.success && 
               loginResponse.`object`?.success == true
    }
    
    /**
     * Extrai a mensagem de resposta
     */
    fun getMessage(loginResponse: LoginResponse): String {
        return loginResponse.`object`?.message ?: "Resposta inválida"
    }
}
