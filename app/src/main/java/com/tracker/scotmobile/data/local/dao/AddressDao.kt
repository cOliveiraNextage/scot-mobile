package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE fnServiceOrderId = :orderId")
    fun getAddressesByOrderId(orderId: Long): Flow<List<AddressEntity>>
    
    @Query("SELECT * FROM addresses WHERE fnServiceOrderId = :orderId LIMIT 1")
    suspend fun getFirstAddressByOrderId(orderId: Long): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)

    @Query("DELETE FROM addresses WHERE fnServiceOrderId = :orderId")
    suspend fun deleteAddressesByOrderId(orderId: Long)
}


