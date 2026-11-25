package com.agroconecta.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import com.agroconecta.app.data.local.database.AgroDatabase
import com.agroconecta.app.data.repository.OrderRepositoryImpl
import com.agroconecta.app.data.repository.ProductRepositoryImpl
import com.agroconecta.app.data.repository.UserRepositoryImpl
import com.agroconecta.app.navigation.AgroConectaNavigation
import com.agroconecta.app.navigation.AppNavKey
import com.agroconecta.app.navigation.Login
import com.agroconecta.app.ui.theme.AgroConectaTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val database = AgroDatabase.getDatabase(this)
            val userRepository = remember { UserRepositoryImpl(database.userDao()) }
            val productRepository = remember { ProductRepositoryImpl(database.productDao()) }
            val orderRepository = remember { OrderRepositoryImpl(database.orderDao()) }
            
            val backStack = remember { NavBackStack<AppNavKey>(Login) }

            AgroConectaTheme {
                AgroConectaNavigation(
                    userRepository = userRepository,
                    productRepository = productRepository,
                    orderRepository = orderRepository,
                    backStack = backStack,
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}
