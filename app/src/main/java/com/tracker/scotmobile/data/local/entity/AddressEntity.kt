package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "addresses",
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
data class AddressEntity(
    @PrimaryKey
    val fcOwnerId: String,
    val fnServiceOrderId: Long,
    val fcZipCode: String,
    val fcAddress: String,
    val fcNumber: String,
    val fcDistrict: String,
    val fcComplement: String? = null,
    val fcCity: String,
    val fcState: String,
    val fcLatitude: String? = null,
    val fcLongitude: String? = null
)

