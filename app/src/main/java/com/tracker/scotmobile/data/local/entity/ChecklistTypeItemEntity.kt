package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "checklist_type_items",
    foreignKeys = [
        ForeignKey(
            entity = ChecklistTypeEntity::class,
            parentColumns = ["fnChecklistTypeId"],
            childColumns = ["fnChecklistTypeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChecklistItemEntity::class,
            parentColumns = ["fnChecklistItemId"],
            childColumns = ["fnChecklistItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fnChecklistTypeId"), Index("fnChecklistItemId")]
)
data class ChecklistTypeItemEntity(
    @PrimaryKey
    val fnChecklistTypeItemId: Long,
    val fnChecklistTypeId: Long,
    val fnChecklistItemId: Long
)

