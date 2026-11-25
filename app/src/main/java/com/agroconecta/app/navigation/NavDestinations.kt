package com.agroconecta.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey

@Serializable
data object Login : AppNavKey

@Serializable
data object Register : AppNavKey

@Serializable
data object ClientHome : AppNavKey

@Serializable
data object ProducerHome : AppNavKey

// Producer Destinations
@Serializable
data object ProducerProducts : AppNavKey

@Serializable
data class AddEditProduct(val productId: Long? = null) : AppNavKey

@Serializable
data object ProducerOrders : AppNavKey

@Serializable
data class ProducerOrderDetail(val orderId: Long) : AppNavKey

// Client Destinations
@Serializable
data object ClientCatalog : AppNavKey

@Serializable
data class ProductDetail(val productId: Long) : AppNavKey

@Serializable
data object Cart : AppNavKey

@Serializable
data object ClientOrders : AppNavKey

@Serializable
data object Profile : AppNavKey
