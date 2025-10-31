package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result_codes")
data class ResultCodeEntity(
    @PrimaryKey
    val fnResultCodeId: Long,
    val fcResultCodeNm: String,
    val fcResultCodeDs: String,
    val fnResultCodeSt: Boolean,
    val fcWarehouseTypeNameList: String?
)
