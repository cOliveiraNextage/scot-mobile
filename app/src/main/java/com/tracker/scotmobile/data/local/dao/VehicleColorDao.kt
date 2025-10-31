package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.VehicleColorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleColorDao {
    @Query("SELECT * FROM vehicle_colors")
    fun getAllVehicleColors(): Flow<List<VehicleColorEntity>>
    
    @Query("SELECT * FROM vehicle_colors WHERE fnVehicleColorId = :colorId")
    suspend fun getVehicleColorById(colorId: Long): VehicleColorEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicleColor(vehicleColor: VehicleColorEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicleColors(vehicleColors: List<VehicleColorEntity>)
    
    @Delete
    suspend fun deleteVehicleColor(vehicleColor: VehicleColorEntity)
    
    @Query("DELETE FROM vehicle_colors")
    suspend fun deleteAllVehicleColors()
}

