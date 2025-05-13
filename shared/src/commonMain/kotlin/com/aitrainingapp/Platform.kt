package com.aitrainingapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform