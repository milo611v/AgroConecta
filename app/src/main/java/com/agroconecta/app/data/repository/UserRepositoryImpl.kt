package com.agroconecta.app.data.repository

import com.agroconecta.app.data.local.dao.UserDao
import com.agroconecta.app.data.local.entity.toDomain
import com.agroconecta.app.data.local.entity.toEntity
import com.agroconecta.app.domain.model.User
import com.agroconecta.app.domain.model.UserRole
import com.agroconecta.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override fun getUsers(): Flow<List<User>> {
        return userDao.getUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUsersByRole(role: UserRole): Flow<List<User>> {
        return userDao.getUsersByRole(role.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUserById(id: Long): Flow<User?> {
        return userDao.getUserById(id).map { it?.toDomain() }
    }

    override fun getUserByEmail(email: String): Flow<User?> {
        return userDao.getUserByEmail(email).map { it?.toDomain() }
    }

    override suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }
}
