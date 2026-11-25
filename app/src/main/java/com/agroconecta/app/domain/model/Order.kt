package com.agroconecta.app.domain.model

import java.util.Date

data class Order(
    val id: Long = 0,
    val clientId: Long,
    val producerId: Long,
    val date: Date = Date(),
    val total: Double,
    val status: OrderStatus = OrderStatus.PENDING
)
