package com.aitrainingapp.android.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aitrainingapp.domain.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val weightChance: Float,
    val repsChance: Int,
    val setsChance: Int
)

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        weightStep = this.weightChance,
        repsStep = this.repsChance,
        setsStep = this.setsChance
    )
}