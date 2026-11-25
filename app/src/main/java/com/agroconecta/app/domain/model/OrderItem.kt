package com.agroconecta.app.domain.model

data class OrderItem(
    val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)
