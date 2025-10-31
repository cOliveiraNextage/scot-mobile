package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_services")
data class OrderServiceEntity(
    @PrimaryKey
    val fnServiceOrderId: Long,
    val fnServiceTypeId: Long,
    val fcWarehouseNm: String,
    val fcWarehouseType: String,
    val fdSchedule: String,
    val fcVehicleContractProductId: String,
    val fcCostumer: String,
    val isInspection: Boolean,
    val fcOwnerId: String? = null
)

