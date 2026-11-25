package com.agroconecta.app.domain.repository

import com.agroconecta.app.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductsByProducer(producerId: Long): Flow<List<Product>>
    fun getProductById(id: Long): Flow<Product?>
    suspend fun insertProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
}
