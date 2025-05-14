package com.aitrainingapp.android.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aitrainingapp.android.room.ProfileEntity
import com.aitrainingapp.android.room.UserEntity
import com.aitrainingapp.android.room.dao.ProfileDao
import com.aitrainingapp.android.room.dao.UserDao

@Database(
    entities = [UserEntity::class, ProfileEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
}
