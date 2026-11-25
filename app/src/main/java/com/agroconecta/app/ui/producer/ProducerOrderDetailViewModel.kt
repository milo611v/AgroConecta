package com.agroconecta.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroconecta.app.domain.model.Order
import com.agroconecta.app.domain.model.OrderItem
import com.agroconecta.app.domain.model.OrderStatus
import com.agroconecta.app.domain.model.User
import com.agroconecta.app.domain.repository.OrderRepository
import com.agroconecta.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProducerOrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val orderId: Long
) : ViewModel() {

    private val _order = orderRepository.getOrderById(orderId)
    val order: StateFlow<Order?> = _order.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _items = orderRepository.getOrderItems(orderId)
    val items: StateFlow<List<OrderItem>> = _items.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _client = MutableStateFlow<User?>(null)
    val client: StateFlow<User?> = _client.asStateFlow()

    init {
        viewModelScope.launch {
            _order.filterNotNull().collect { order ->
                _client.value = userRepository.getUserById(order.clientId).firstOrNull()
            }
        }
    }

    fun updateStatus(newStatus: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, newStatus.name)
        }
    }
}
