package com.aitrainingapp.domain.model

import com.aitrainingapp.database.Profiles

data class Profile(
    val id: Int,
    val name: String,
    val weightStep: Float,
    val repsStep: Int,
    val setsStep: Int
)

fun Profiles.toDomain(): Profile {
    return Profile(
        id = id.toInt(),
        name = name,
        weightStep = weightChance.toFloat(),
        repsStep = repsChance?.toInt() ?: 0,
        setsStep = setsChance?.toInt() ?: 0
    )
}