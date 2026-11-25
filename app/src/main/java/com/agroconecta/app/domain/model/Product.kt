package com.agroconecta.app.domain.model

data class Product(
    val id: Long = 0,
    val producerId: Long,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val unit: String,
    val imageUri: String?,
    val available: Boolean = true,
    val producerName: String = ""
)
