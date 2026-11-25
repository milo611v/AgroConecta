package com.agroconecta.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.agroconecta.app.domain.model.Product

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["producerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val producerId: Long,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val unit: String,
    val imageUri: String?,
    val available: Boolean,
    val producerName: String = ""
)

fun ProductEntity.toDomain() = Product(
    id = id,
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

fun Product.toEntity() = ProductEntity(
    id = id,
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
