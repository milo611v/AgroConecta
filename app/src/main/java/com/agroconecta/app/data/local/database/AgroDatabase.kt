package com.agroconecta.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.agroconecta.app.data.local.dao.OrderDao
import com.agroconecta.app.data.local.dao.ProductDao
import com.agroconecta.app.data.local.dao.UserDao
import com.agroconecta.app.data.local.entity.OrderEntity
import com.agroconecta.app.data.local.entity.OrderItemEntity
import com.agroconecta.app.data.local.entity.ProductEntity
import com.agroconecta.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AgroDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AgroDatabase? = null

        fun getDatabase(context: Context): AgroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgroDatabase::class.java,
                    "agro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
