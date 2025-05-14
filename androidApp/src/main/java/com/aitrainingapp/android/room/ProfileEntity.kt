package com.aitrainingapp.android.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val weightChance: Float,
    val repsChance: Int,
    val setsChance: Int
)
