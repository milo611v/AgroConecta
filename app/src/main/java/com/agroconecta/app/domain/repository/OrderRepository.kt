package com.agroconecta.app.domain.repository

import com.agroconecta.app.domain.model.Order
import com.agroconecta.app.domain.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrdersByClient(clientId: Long): Flow<List<Order>>
    fun getOrdersByProducer(producerId: Long): Flow<List<Order>>
    fun getOrderById(id: Long): Flow<Order?>
    fun getOrderItems(orderId: Long): Flow<List<OrderItem>>
    suspend fun insertOrder(order: Order, items: List<OrderItem>): Long
    suspend fun updateOrderStatus(orderId: Long, status: String)
}
