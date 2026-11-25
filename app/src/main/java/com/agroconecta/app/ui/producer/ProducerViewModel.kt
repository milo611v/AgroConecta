package com.agroconecta.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroconecta.app.domain.model.Order
import com.agroconecta.app.domain.model.OrderStatus
import com.agroconecta.app.domain.model.Product
import com.agroconecta.app.domain.repository.OrderRepository
import com.agroconecta.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProducerViewModel(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val producerId: Long
) : ViewModel() {

    private val _products = productRepository.getProductsByProducer(producerId)
    val products: StateFlow<List<Product>> = _products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _orders = orderRepository.getOrdersByProducer(producerId)
    val orders: StateFlow<List<Order>> = _orders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats = combine(products, orders) { products, orders ->
        ProducerStats(
            totalProducts = products.size,
            pendingOrders = orders.count { it.status == OrderStatus.PENDING },
            inProcessOrders = orders.count { it.status == OrderStatus.IN_PROCESS },
            completedOrders = orders.count { it.status == OrderStatus.COMPLETED }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProducerStats()
    )

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, newStatus.name)
        }
    }
}

data class ProducerStats(
    val totalProducts: Int = 0,
    val pendingOrders: Int = 0,
    val inProcessOrders: Int = 0,
    val completedOrders: Int = 0
)
