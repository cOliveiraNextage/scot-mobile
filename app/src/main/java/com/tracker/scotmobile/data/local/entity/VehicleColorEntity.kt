package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_colors")
data class VehicleColorEntity(
    @PrimaryKey
    val fnVehicleColorId: Long,
    val fcVehicleColorNm: String,
    val fnVehicleColorSt: Int
)

