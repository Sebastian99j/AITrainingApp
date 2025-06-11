@file:JvmName("FlowWatcherKt")

package com.aitrainingapp.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

interface Closeable {
    fun close()
}

object FlowWatcher {
    @JvmStatic
    fun <T> watch(flow: StateFlow<T>, block: (T) -> Unit): Closeable {
        val job = MainScope().launch(Dispatchers.Main) {
            flow.collect { block(it) }
        }
        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}
