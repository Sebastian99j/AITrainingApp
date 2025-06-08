package com.aitrainingapp.database

import com.squareup.sqldelight.db.SqlDriver

object DatabaseModule {
    fun provideDatabase(driver: SqlDriver): AppDatabase {
        return AppDatabase(driver)
    }
}
