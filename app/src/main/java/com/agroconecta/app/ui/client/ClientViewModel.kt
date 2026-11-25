package com.agroconecta.app.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroconecta.app.domain.model.*
import com.agroconecta.app.domain.repository.OrderRepository
import com.agroconecta.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ClientViewModel(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val clientId: Long
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _products = productRepository.getProducts()
    val catalogProducts = combine(_products, _searchQuery) { products, query ->
        if (query.isBlank()) {
            products.filter { it.available && it.quantity > 0 }
        } else {
            products.filter { 
                it.available && it.quantity > 0 && 
                (it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _cart = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cart: StateFlow<Map<Long, CartItem>> = _cart.asStateFlow()

    val cartTotal = cart.map { cartMap ->
        cartMap.values.sumOf { it.product.price * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    private val _orders = orderRepository.getOrdersByClient(clientId)
    val orders: StateFlow<List<Order>> = _orders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow<ClientUiState>(ClientUiState.Idle)
    val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addToCart(product: Product, quantity: Int) {
        val currentCart = _cart.value.toMutableMap()
        val existingItem = currentCart[product.id]
        val newQuantity = (existingItem?.quantity ?: 0) + quantity
        
        if (newQuantity <= product.quantity) {
            currentCart[product.id] = CartItem(product, newQuantity)
            _cart.value = currentCart
        }
    }

    fun removeFromCart(productId: Long) {
        val currentCart = _cart.value.toMutableMap()
        currentCart.remove(productId)
        _cart.value = currentCart
    }

    fun checkout() {
        if (_cart.value.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = ClientUiState.Loading
            
            try {
                // Group items by producer
                val itemsByProducer = _cart.value.values.groupBy { it.product.producerId }
                
                for ((producerId, items) in itemsByProducer) {
                    val total = items.sumOf { it.product.price * it.quantity }
                    val order = Order(
                        id = 0,
                        clientId = clientId,
                        producerId = producerId,
                        date = Date(),
                        total = total,
                        status = OrderStatus.PENDING
                    )
                    
                    val orderItems = items.map { 
                        OrderItem(
                            id = 0,
                            orderId = 0,
                            productId = it.product.id,
                            quantity = it.quantity,
                            unitPrice = it.product.price,
                            subtotal = it.product.price * it.quantity
                        )
                    }
                    
                    orderRepository.insertOrder(order, orderItems)
                    
                    // Update inventory
                    for (item in items) {
                        val updatedProduct = item.product.copy(
                            quantity = item.product.quantity - item.quantity,
                            available = (item.product.quantity - item.quantity) > 0
                        )
                        productRepository.updateProduct(updatedProduct)
                    }
                }
                
                _cart.value = emptyMap()
                _uiState.value = ClientUiState.Success
            } catch (e: Exception) {
                _uiState.value = ClientUiState.Error("Error al procesar el pedido: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ClientUiState.Idle
    }
}

data class CartItem(
    val product: Product,
    val quantity: Int
)

sealed interface ClientUiState {
    data object Idle : ClientUiState
    data object Loading : ClientUiState
    data object Success : ClientUiState
    data class Error(val message: String) : ClientUiState
}
