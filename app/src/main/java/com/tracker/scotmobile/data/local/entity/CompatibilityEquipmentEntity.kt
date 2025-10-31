package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "compatibility_equipment",
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
data class CompatibilityEquipmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fcOwnerId: String,
    val fnServiceOrderId: Long,
    val fcAccessory: String,
    val fcCompatibility: String
)

