package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "equipment",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["fnServiceOrderId", "fcOwnerId"],
            childColumns = ["fnServiceOrderId", "fcOwnerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrderServiceEntity::class,
            parentColumns = ["fnServiceOrderId"],
            childColumns = ["fnServiceOrderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fcOwnerId"), Index("fnServiceOrderId")]
)
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fcOwnerId: String,
    val fnServiceOrderId: Long,
    val nameEquipment: String,
    val typeEquipment: String,
    val fcAccessory: String? = null,
    val serial: String? = null,
    val locationInstalled: String? = null,
    val fcCompatibility: String? = null
)

