package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.AccessoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessoryDao {
    @Query("SELECT * FROM accessories WHERE fnServiceOrderId = :orderId")
    fun getAccessoriesByOrderId(orderId: Long): Flow<List<AccessoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessory(item: AccessoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessories(items: List<AccessoryEntity>)

    @Query("DELETE FROM accessories WHERE fnServiceOrderId = :orderId")
    suspend fun deleteAccessoriesByOrderId(orderId: Long)
}


