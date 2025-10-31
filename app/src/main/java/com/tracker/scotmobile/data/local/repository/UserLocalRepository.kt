package com.tracker.scotmobile.data.local.repository

import android.content.Context
import com.tracker.scotmobile.data.local.AppDatabase
import com.tracker.scotmobile.data.local.mapper.UserLocalMapper
import com.tracker.scotmobile.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserLocalRepository(context: Context) {
    private val userDao = AppDatabase.getDatabase(context).userDao()
    
    /**
     * Salva o usuário no banco local
     */
    suspend fun saveUser(user: User) {
        val userEntity = UserLocalMapper.userToEntity(user)
        userDao.insertUser(userEntity)
    }
    
    /**
     * Obtém o usuário atual do banco local
     */
    fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { entity ->
            entity?.let { UserLocalMapper.entityToUser(it) }
        }
    }
    
    /**
     * Obtém o usuário logado (com token)
     */
    suspend fun getLoggedInUser(): User? {
        val entity = userDao.getLoggedInUser()
        return entity?.let { UserLocalMapper.entityToUser(it) }
    }
    
    /**
     * Atualiza o usuário no banco
     */
    suspend fun updateUser(user: User) {
        val userEntity = UserLocalMapper.userToEntity(user)
        userDao.updateUser(userEntity)
    }
    
    /**
     * Remove o usuário do banco
     */
    suspend fun deleteUser(userId: Long) {
        userDao.deleteUser(userId)
    }
    
    /**
     * Remove todos os usuários (logout)
     */
    suspend fun clearAllUsers() {
        userDao.deleteAllUsers()
    }
    
    /**
     * Verifica se há usuário logado
     */
    suspend fun hasLoggedInUser(): Boolean {
        return userDao.getUserCount() > 0
    }
}
