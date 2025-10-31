package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "result_code_warehouse_types",
    foreignKeys = [
        ForeignKey(
            entity = ResultCodeEntity::class,
            parentColumns = ["fnResultCodeId"],
            childColumns = ["fnResultCodeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fnResultCodeId")]
)
data class ResultCodeWarehouseTypeEntity(
    @PrimaryKey
    val fnResultCodeWarehouseTypeId: Long,
    val fnResultCodeId: Long?,
    val fcWarehouseTypeNameList: String
)
