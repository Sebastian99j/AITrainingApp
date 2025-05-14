package com.aitrainingapp.android.room.dao

import androidx.room.*
import com.aitrainingapp.android.room.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirstUser(): UserEntity?

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
