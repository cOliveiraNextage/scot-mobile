package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users ORDER BY lastLogin DESC LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT * FROM users WHERE token IS NOT NULL AND token != ''")
    suspend fun getLoggedInUser(): UserEntity?
}
