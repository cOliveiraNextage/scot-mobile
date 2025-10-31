package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.PhoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneDao {
    @Query("SELECT * FROM phones WHERE fnServiceOrderId = :orderId")
    fun getPhonesByOrderId(orderId: Long): Flow<List<PhoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhone(phone: PhoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhones(phones: List<PhoneEntity>)

    @Query("DELETE FROM phones WHERE fnServiceOrderId = :orderId")
    suspend fun deletePhonesByOrderId(orderId: Long)
}


