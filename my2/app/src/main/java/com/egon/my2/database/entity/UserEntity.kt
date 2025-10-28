package com.egon.my2.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)