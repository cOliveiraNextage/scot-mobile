package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE fnServiceOrderId = :orderId")
    fun getProductsByOrderId(orderId: Long): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE fnServiceOrderId = :orderId LIMIT 1")
    suspend fun getFirstProductByOrderId(orderId: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE fnServiceOrderId = :orderId")
    suspend fun deleteProductsByOrderId(orderId: Long)
}


