package com.agroconecta.app.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroconecta.app.domain.model.Product
import com.agroconecta.app.domain.repository.ProductRepository
import com.agroconecta.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val producerId: Long,
    private val productId: Long? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private var producerName: String = ""

    init {
        viewModelScope.launch {
            val user = userRepository.getUserById(producerId).firstOrNull()
            producerName = user?.name ?: ""
            
            productId?.let { id ->
                _product.value = productRepository.getProductById(id).firstOrNull()
            }
        }
    }

    fun saveProduct(
        name: String,
        description: String,
        price: Double,
        quantity: Int,
        unit: String,
        imageUri: String?,
        available: Boolean
    ) {
        if (name.isBlank() || description.isBlank() || price <= 0 || quantity < 0 || unit.isBlank()) {
            _uiState.value = ProductUiState.Error("Por favor completa todos los campos correctamente")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            val productToSave = Product(
                id = productId ?: 0,
                producerId = producerId,
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                unit = unit,
                imageUri = imageUri,
                available = available,
                producerName = producerName
            )

            if (productId == null) {
                productRepository.insertProduct(productToSave)
            } else {
                productRepository.updateProduct(productToSave)
            }
            _uiState.value = ProductUiState.Success
        }
    }
}

sealed interface ProductUiState {
    data object Idle : ProductUiState
    data object Loading : ProductUiState
    data object Success : ProductUiState
    data class Error(val message: String) : ProductUiState
}
