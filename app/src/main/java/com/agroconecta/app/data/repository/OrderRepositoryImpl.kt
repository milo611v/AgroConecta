package com.agroconecta.app.data.repository

import com.agroconecta.app.data.local.dao.OrderDao
import com.agroconecta.app.data.local.entity.toDomain
import com.agroconecta.app.data.local.entity.toEntity
import com.agroconecta.app.domain.model.Order
import com.agroconecta.app.domain.model.OrderItem
import com.agroconecta.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {
    override fun getOrders(): Flow<List<Order>> {
        return orderDao.getOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getOrdersByClient(clientId: Long): Flow<List<Order>> {
        return orderDao.getOrdersByClient(clientId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getOrdersByProducer(producerId: Long): Flow<List<Order>> {
        return orderDao.getOrdersByProducer(producerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getOrderById(id: Long): Flow<Order?> {
        return orderDao.getOrderById(id).map { it?.toDomain() }
    }

    override fun getOrderItems(orderId: Long): Flow<List<OrderItem>> {
        return orderDao.getOrderItems(orderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertOrder(order: Order, items: List<OrderItem>): Long {
        val orderId = orderDao.insertOrder(order.toEntity())
        val itemEntities = items.map { it.copy(orderId = orderId).toEntity() }
        orderDao.insertOrderItems(itemEntities)
        return orderId
    }

    override suspend fun updateOrderStatus(orderId: Long, status: String) {
        orderDao.updateOrderStatus(orderId, status)
    }
}
