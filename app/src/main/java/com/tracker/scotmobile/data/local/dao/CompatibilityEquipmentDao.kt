package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.CompatibilityEquipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompatibilityEquipmentDao {
    @Query("SELECT * FROM compatibility_equipment WHERE fnServiceOrderId = :orderId")
    fun getCompatibilityByOrderId(orderId: Long): Flow<List<CompatibilityEquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompatibility(item: CompatibilityEquipmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompatibility(items: List<CompatibilityEquipmentEntity>)

    @Query("DELETE FROM compatibility_equipment WHERE fnServiceOrderId = :orderId")
    suspend fun deleteCompatibilityByOrderId(orderId: Long)
}


