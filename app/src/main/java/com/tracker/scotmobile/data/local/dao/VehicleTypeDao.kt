package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.VehicleTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleTypeDao {
    @Query("SELECT * FROM vehicle_types")
    fun getAllVehicleTypes(): Flow<List<VehicleTypeEntity>>
    
    @Query("SELECT * FROM vehicle_types WHERE fnVehicleTypeId = :typeId")
    suspend fun getVehicleTypeById(typeId: Long): VehicleTypeEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicleType(vehicleType: VehicleTypeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicleTypes(vehicleTypes: List<VehicleTypeEntity>)
    
    @Delete
    suspend fun deleteVehicleType(vehicleType: VehicleTypeEntity)
    
    @Query("DELETE FROM vehicle_types")
    suspend fun deleteAllVehicleTypes()
}

