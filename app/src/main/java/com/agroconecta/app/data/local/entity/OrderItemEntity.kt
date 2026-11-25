package com.agroconecta.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.agroconecta.app.domain.model.OrderItem

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

fun OrderItemEntity.toDomain() = OrderItem(
    id = id,
    orderId = orderId,
    productId = productId,
    quantity = quantity,
    unitPrice = unitPrice,
    subtotal = subtotal
)

fun OrderItem.toEntity() = OrderItemEntity(
    id = id,
    orderId = orderId,
    productId = productId,
    quantity = quantity,
    unitPrice = unitPrice,
    subtotal = subtotal
)
