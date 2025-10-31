package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey
    val fnChecklistItemId: Long,
    val fcName: String,
    val fcDescription: String
)

