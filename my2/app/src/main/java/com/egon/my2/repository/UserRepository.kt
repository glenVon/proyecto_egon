package com.egon.my2.repository

import com.egon.my2.database.dao.UserDao
import com.egon.my2.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun login(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    suspend fun register(user: UserEntity): Boolean {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                false
            } else {
                userDao.insertUser(user)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(userId: Int): Boolean {
        return try {
            userDao.deleteUserById(userId)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}