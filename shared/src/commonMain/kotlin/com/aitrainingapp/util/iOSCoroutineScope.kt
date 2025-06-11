package com.aitrainingapp.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object IOSScope {
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}
