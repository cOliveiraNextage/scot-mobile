package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.EquipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment WHERE fnServiceOrderId = :orderId")
    fun getEquipmentByOrderId(orderId: Long): Flow<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(item: EquipmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(items: List<EquipmentEntity>)

    @Query("DELETE FROM equipment WHERE fnServiceOrderId = :orderId")
    suspend fun deleteEquipmentByOrderId(orderId: Long)
}


