package com.agroconecta.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agroconecta.app.domain.model.Order
import com.agroconecta.app.domain.model.OrderStatus
import java.util.Date

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val producerId: Long,
    val date: Long, // Stored as timestamp
    val total: Double,
    val status: String
)

fun OrderEntity.toDomain() = Order(
    id = id,
    clientId = clientId,
    producerId = producerId,
    date = Date(date),
    total = total,
    status = OrderStatus.valueOf(status)
)

fun Order.toEntity() = OrderEntity(
    id = id,
    clientId = clientId,
    producerId = producerId,
    date = date.time,
    total = total,
    status = status.name
)
