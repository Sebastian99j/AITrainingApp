package com.aitrainingapp.data.remote.model

import com.aitrainingapp.domain.model.Profile

data class ProfileEntity(
    val id: Int = 0,
    val name: String,
    val weightChance: Float,
    val repsChance: Int,
    val setsChance: Int
)

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        id = this.id,
        name = this.name,
        weightStep = this.weightChance,
        repsStep = this.repsChance,
        setsStep = this.setsChance
    )
}