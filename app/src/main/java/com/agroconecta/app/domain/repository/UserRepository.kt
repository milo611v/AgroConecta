package com.agroconecta.app.domain.repository

import com.agroconecta.app.domain.model.User
import com.agroconecta.app.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    fun getUsersByRole(role: UserRole): Flow<List<User>>
    fun getUserById(id: Long): Flow<User?>
    fun getUserByEmail(email: String): Flow<User?>
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
}
