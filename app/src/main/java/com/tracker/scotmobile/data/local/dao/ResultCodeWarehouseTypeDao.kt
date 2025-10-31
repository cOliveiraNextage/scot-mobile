package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.ResultCodeWarehouseTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultCodeWarehouseTypeDao {
    
    @Query("SELECT * FROM result_code_warehouse_types")
    fun getAllResultCodeWarehouseTypes(): Flow<List<ResultCodeWarehouseTypeEntity>>
    
    @Query("SELECT * FROM result_code_warehouse_types WHERE fnResultCodeId = :resultCodeId")
    fun getResultCodeWarehouseTypesByResultCodeId(resultCodeId: Long): Flow<List<ResultCodeWarehouseTypeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResultCodeWarehouseType(resultCodeWarehouseType: ResultCodeWarehouseTypeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResultCodeWarehouseTypes(resultCodeWarehouseTypes: List<ResultCodeWarehouseTypeEntity>)
    
    @Delete
    suspend fun deleteResultCodeWarehouseType(resultCodeWarehouseType: ResultCodeWarehouseTypeEntity)
    
    @Query("DELETE FROM result_code_warehouse_types")
    suspend fun deleteAllResultCodeWarehouseTypes()
    
    @Query("SELECT COUNT(*) FROM result_code_warehouse_types")
    suspend fun getResultCodeWarehouseTypesCount(): Int
}
