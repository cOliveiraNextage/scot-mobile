package com.tracker.scotmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val login: String,
    val document: String,
    val token: String?,
    val roleId: Long?,
    val roleDescription: String?,
    val roleName: String?,
    val lastLogin: Long = System.currentTimeMillis()
)
