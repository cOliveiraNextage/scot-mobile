package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_types")
data class VehicleTypeEntity(
    @PrimaryKey
    val fnVehicleTypeId: Long,
    val fcVehicleTypeNm: String,
    val fcVehicleTypeDs: String,
    val fcTypeUnion: String? = null
)

