package com.aitrainingapp.android.room.dao

import androidx.room.*
import com.aitrainingapp.android.room.ProfileEntity

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): ProfileEntity?

    @Query("DELETE FROM profiles")
    suspend fun deleteAll()

    @Query("SELECT * FROM profiles")
    suspend fun getAll(): List<ProfileEntity>
}
