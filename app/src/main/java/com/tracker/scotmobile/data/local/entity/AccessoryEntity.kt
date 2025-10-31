package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "accessories",
    foreignKeys = [
        ForeignKey(
            entity = OwnerEntity::class,
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
data class AccessoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fcOwnerId: String,
    val fnServiceOrderId: Long,
    val fcAccessoryName: String,
    val fcAccessoryProtheusId: String
)

