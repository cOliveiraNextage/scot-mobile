package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_types")
data class ChecklistTypeEntity(
    @PrimaryKey
    val fnChecklistTypeId: Long,
    val fcName: String
)

