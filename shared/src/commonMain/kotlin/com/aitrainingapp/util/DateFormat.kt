package com.aitrainingapp.util

import kotlinx.datetime.LocalDateTime

fun formatDate(dateString: String): String {
    return try {
        val parsedDate = LocalDateTime.parse(dateString)
        "${parsedDate.dayOfMonth.toString().padStart(2, '0')}." +
                "${parsedDate.monthNumber.toString().padStart(2, '0')}." +
                parsedDate.year
    } catch (e: Exception) {
        dateString
    }
}
