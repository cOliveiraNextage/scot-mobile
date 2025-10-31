package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.OrderServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderServiceDao {
    @Query("SELECT * FROM order_services")
    fun getAllOrderServices(): Flow<List<OrderServiceEntity>>
    
    @Query("SELECT * FROM order_services WHERE fnServiceOrderId = :orderId")
    suspend fun getOrderServiceById(orderId: Long): OrderServiceEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderService(orderService: OrderServiceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderServices(orderServices: List<OrderServiceEntity>)
    
    @Delete
    suspend fun deleteOrderService(orderService: OrderServiceEntity)
    
    @Query("DELETE FROM order_services")
    suspend fun deleteAllOrderServices()
}

