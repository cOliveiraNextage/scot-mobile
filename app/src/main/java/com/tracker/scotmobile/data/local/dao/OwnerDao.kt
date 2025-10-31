package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.OwnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owners")
    fun getAllOwners(): Flow<List<OwnerEntity>>
    
    @Query("SELECT * FROM owners WHERE fcOwnerId = :ownerId")
    suspend fun getOwnerById(ownerId: String): OwnerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(owner: OwnerEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwners(owners: List<OwnerEntity>)
    
    @Delete
    suspend fun deleteOwner(owner: OwnerEntity)
    
    @Query("DELETE FROM owners")
    suspend fun deleteAllOwners()
}

