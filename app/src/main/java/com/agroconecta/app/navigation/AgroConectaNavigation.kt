package com.agroconecta.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.agroconecta.app.domain.model.User
import com.agroconecta.app.domain.model.UserRole
import com.agroconecta.app.domain.repository.OrderRepository
import com.agroconecta.app.domain.repository.ProductRepository
import com.agroconecta.app.domain.repository.UserRepository
import com.agroconecta.app.ui.auth.AuthUiState
import com.agroconecta.app.ui.auth.AuthViewModel
import com.agroconecta.app.ui.auth.LoginScreen
import com.agroconecta.app.ui.auth.RegisterScreen
import com.agroconecta.app.ui.client.*
import com.agroconecta.app.ui.home.ProducerHomeScreen
import com.agroconecta.app.ui.orders.ClientOrdersScreen
import com.agroconecta.app.ui.producer.*
import com.agroconecta.app.ui.products.AddEditProductScreen
import com.agroconecta.app.ui.products.ProductViewModel
import com.agroconecta.app.ui.profile.ProfileScreen

@Composable
fun AgroConectaNavigation(
    userRepository: UserRepository,
    productRepository: ProductRepository,
    orderRepository: OrderRepository,
    backStack: NavBackStack<AppNavKey>,
    windowSizeClass: WindowSizeClass
) {
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userRepository) as T
            }
        }
    )

    val authState by authViewModel.uiState.collectAsState()
    val currentUser = (authState as? AuthUiState.Authenticated)?.user
    val currentKey = backStack.lastOrNull()

    // Handle global loading state during transitions
    if (authState is AuthUiState.Loading && currentKey == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Redirect to Login if not authenticated and on a protected route
    val isAuthRoute = currentKey is Login || currentKey is Register
    if (currentUser == null && !isAuthRoute && authState !is AuthUiState.Loading) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            backStack.clear()
            backStack.add(Login)
        }
    }

    val isMainScreen = currentKey is ProducerHome || currentKey is ProducerProducts || 
                       currentKey is ProducerOrders || currentKey is ClientHome || 
                       currentKey is Cart || currentKey is ClientOrders || currentKey is Profile

    if (currentUser != null && isMainScreen) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                if (currentUser.role == UserRole.PRODUCER) {
                    item(
                        selected = currentKey is ProducerHome,
                        onClick = { 
                            backStack.clear()
                            backStack.add(ProducerHome) 
                        },
                        icon = { Icon(Icons.Rounded.Dashboard, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    item(
                        selected = currentKey is ProducerProducts,
                        onClick = { 
                            backStack.clear()
                            backStack.add(ProducerHome)
                            backStack.add(ProducerProducts) 
                        },
                        icon = { Icon(Icons.Rounded.Inventory2, contentDescription = null) },
                        label = { Text("Productos") }
                    )
                    item(
                        selected = currentKey is ProducerOrders,
                        onClick = { 
                            backStack.clear()
                            backStack.add(ProducerHome)
                            backStack.add(ProducerOrders) 
                        },
                        icon = { Icon(Icons.Rounded.ReceiptLong, contentDescription = null) },
                        label = { Text("Pedidos") }
                    )
                } else {
                    item(
                        selected = currentKey is ClientHome,
                        onClick = { 
                            backStack.clear()
                            backStack.add(ClientHome) 
                        },
                        icon = { Icon(Icons.Rounded.Store, contentDescription = null) },
                        label = { Text("Catálogo") }
                    )
                    item(
                        selected = currentKey is Cart,
                        onClick = { 
                            backStack.clear()
                            backStack.add(ClientHome)
                            backStack.add(Cart) 
                        },
                        icon = { Icon(Icons.Rounded.ShoppingCart, contentDescription = null) },
                        label = { Text("Carrito") }
                    )
                    item(
                        selected = currentKey is ClientOrders,
                        onClick = { 
                            backStack.clear()
                            backStack.add(ClientHome)
                            backStack.add(ClientOrders) 
                        },
                        icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                        label = { Text("Pedidos") }
                    )
                }
                item(
                    selected = currentKey is Profile,
                    onClick = { 
                        backStack.clear()
                        if (currentUser.role == UserRole.PRODUCER) backStack.add(ProducerHome) else backStack.add(ClientHome)
                        backStack.add(Profile) 
                    },
                    icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    label = { Text("Perfil") }
                )
            }
        ) {
            AppNavDisplay(backStack, userRepository, productRepository, orderRepository, authViewModel, currentUser, windowSizeClass)
        }
    } else {
        AppNavDisplay(backStack, userRepository, productRepository, orderRepository, authViewModel, currentUser, windowSizeClass)
    }
}

@Composable
fun AppNavDisplay(
    backStack: NavBackStack<AppNavKey>,
    userRepository: UserRepository,
    productRepository: ProductRepository,
    orderRepository: OrderRepository,
    authViewModel: AuthViewModel,
    currentUser: User?,
    windowSizeClass: WindowSizeClass
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Login> {
                LoginScreen(
                    onLoginSuccess = { role ->
                        backStack.removeLastOrNull()
                        if (role == UserRole.PRODUCER) {
                            backStack.add(ProducerHome)
                        } else {
                            backStack.add(ClientHome)
                        }
                    },
                    onNavigateToRegister = {
                        backStack.add(Register)
                    },
                    viewModel = authViewModel
                )
            }
            entry<Register> {
                RegisterScreen(
                    onRegisterSuccess = { role ->
                        backStack.removeLastOrNull()
                        if (role == UserRole.PRODUCER) {
                            backStack.add(ProducerHome)
                        } else {
                            backStack.add(ClientHome)
                        }
                    },
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    },
                    viewModel = authViewModel
                )
            }
            
            // Producer Screens
            entry<ProducerHome> {
                currentUser?.let { user ->
                    val viewModel: ProducerViewModel = viewModel(
                        key = "ProducerVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProducerViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    ProducerHomeScreen(
                        viewModel = viewModel,
                        onNavigateToProducts = { backStack.add(ProducerProducts) },
                        onNavigateToOrders = { backStack.add(ProducerOrders) },
                        onNavigateToProfile = { backStack.add(Profile) },
                        windowSizeClass = windowSizeClass
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<ProducerProducts> {
                currentUser?.let { user ->
                    val viewModel: ProducerViewModel = viewModel(
                        key = "ProducerVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProducerViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    ProducerProductsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onNavigateToAddProduct = { backStack.add(AddEditProduct()) },
                        onNavigateToEditProduct = { id -> backStack.add(AddEditProduct(id)) }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<AddEditProduct> { key ->
                currentUser?.let { user ->
                    val viewModel: ProductViewModel = viewModel(
                        key = "ProductVM_${key.productId}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProductViewModel(productRepository, userRepository, user.id, key.productId) as T
                            }
                        }
                    )
                    AddEditProductScreen(
                        viewModel = viewModel,
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onSuccess = { backStack.removeLastOrNull() }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<ProducerOrders> {
                currentUser?.let { user ->
                    val viewModel: ProducerViewModel = viewModel(
                        key = "ProducerVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProducerViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    ProducerOrdersScreen(
                        viewModel = viewModel,
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onNavigateToOrderDetail = { id -> backStack.add(ProducerOrderDetail(id)) }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<ProducerOrderDetail> { key ->
                val viewModel: ProducerOrderDetailViewModel = viewModel(
                    key = "OrderDetailVM_${key.orderId}",
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProducerOrderDetailViewModel(orderRepository, userRepository, key.orderId) as T
                        }
                    }
                )
                ProducerOrderDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            // Client Screens
            entry<ClientHome> {
                currentUser?.let { user ->
                    val viewModel: ClientViewModel = viewModel(
                        key = "ClientVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ClientViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    ClientCatalogScreen(
                        viewModel = viewModel,
                        onNavigateToProductDetail = { id -> backStack.add(ProductDetail(id)) },
                        onNavigateToCart = { backStack.add(Cart) },
                        onNavigateToOrders = { backStack.add(ClientOrders) },
                        onNavigateToProfile = { backStack.add(Profile) },
                        windowSizeClass = windowSizeClass
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<ProductDetail> { key ->
                currentUser?.let { user ->
                    val viewModel: ClientViewModel = viewModel(
                        key = "ClientVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ClientViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    ProductDetailScreen(
                        productId = key.productId,
                        productRepository = productRepository,
                        viewModel = viewModel,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<Cart> {
                currentUser?.let { user ->
                    val viewModel: ClientViewModel = viewModel(
                        key = "ClientVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ClientViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    CartScreen(
                        viewModel = viewModel,
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onOrderPlaced = {
                            backStack.removeLastOrNull()
                            backStack.add(ClientOrders)
                        }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<ClientOrders> {
                currentUser?.let { user ->
                    val viewModel: ClientViewModel = viewModel(
                        key = "ClientVM_${user.id}",
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ClientViewModel(productRepository, orderRepository, user.id) as T
                            }
                        }
                    )
                    ClientOrdersScreen(
                        viewModel = viewModel,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<Profile> {
                currentUser?.let { user ->
                    ProfileScreen(
                        user = user,
                        onLogout = {
                            authViewModel.resetState()
                            backStack.clear()
                            backStack.add(Login)
                        },
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}
