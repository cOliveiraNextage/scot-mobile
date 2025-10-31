package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "phones",
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
data class PhoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fcOwnerId: String,
    val fnServiceOrderId: Long,
    val fcDDD: String,
    val fcPhone: String,
    val fcPhoneType: String,
    val fcExtensionLine: String? = null
)

