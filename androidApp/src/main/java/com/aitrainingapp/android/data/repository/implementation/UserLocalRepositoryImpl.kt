package com.aitrainingapp.android.data.repository.implementation

import com.aitrainingapp.android.data.repository.UserLocalRepository
import com.aitrainingapp.android.room.dao.UserDao
import com.aitrainingapp.android.room.toEntity
import com.aitrainingapp.domain.model.User

class UserLocalRepositoryImpl(
    private val userDao: UserDao
) : UserLocalRepository {

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }
}
