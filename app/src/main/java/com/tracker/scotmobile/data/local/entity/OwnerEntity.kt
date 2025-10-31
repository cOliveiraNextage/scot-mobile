package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "owners",
    primaryKeys = ["fnServiceOrderId", "fcOwnerId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderServiceEntity::class,
            parentColumns = ["fnServiceOrderId"],
            childColumns = ["fnServiceOrderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fnServiceOrderId"), Index("fcOwnerId")]
)
data class OwnerEntity(
    val fnServiceOrderId: Long,
    val fcOwnerId: String,
    val fcDocument: String,
    val fcNumberDocument: String,
    val fcFullName: String
)

