package com.agroconecta.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agroconecta.app.domain.model.User
import com.agroconecta.app.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    phone = phone,
    password = password,
    role = UserRole.valueOf(role)
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    phone = phone,
    password = password,
    role = role.name
)
