package com.agroconecta.app.data.repository

import com.agroconecta.app.data.local.dao.ProductDao
import com.agroconecta.app.data.local.entity.toDomain
import com.agroconecta.app.data.local.entity.toEntity
import com.agroconecta.app.domain.model.Product
import com.agroconecta.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> {
        return productDao.getProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProductsByProducer(producerId: Long): Flow<List<Product>> {
        return productDao.getProductsByProducer(producerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProductById(id: Long): Flow<Product?> {
        return productDao.getProductById(id).map { it?.toDomain() }
    }

    override suspend fun insertProduct(product: Product): Long {
        return productDao.insertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}
