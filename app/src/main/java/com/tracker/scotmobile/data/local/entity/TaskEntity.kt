package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val fnTaskId: Long,
    val fcTaskNm: String,
    val fcTaskDs: String,
    val fdTaskIniDt: Long? = null,
    val fdTaskEndDt: Long? = null
)

