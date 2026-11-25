package com.agroconecta.app.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agroconecta.app.domain.model.Product
import com.agroconecta.app.domain.repository.ProductRepository
import com.agroconecta.app.ui.components.LoadingState
import com.agroconecta.app.ui.components.PrimaryButton
import kotlinx.coroutines.flow.firstOrNull
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    productRepository: ProductRepository,
    viewModel: ClientViewModel,
    onNavigateBack: () -> Unit
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    LaunchedEffect(productId) {
        product = productRepository.getProductById(productId).firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            LoadingState(modifier = Modifier.padding(padding))
        } else {
            val p = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = p.name.take(1).uppercase(),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = p.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = currencyFormatter.format(p.price) + " / " + p.unit,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = p.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Stock disponible: ${p.quantity} ${p.unit}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Rounded.Remove, contentDescription = "Disminuir")
                            }
                            
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(horizontal = 24.dp),
                                fontWeight = FontWeight.Bold
                            )
                            
                            IconButton(
                                onClick = { if (quantity < p.quantity) quantity++ },
                                enabled = quantity < p.quantity
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Aumentar")
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(32.dp))

                PrimaryButton(
                    text = "Agregar al pedido",
                    icon = Icons.Rounded.ShoppingCart,
                    onClick = {
                        viewModel.addToCart(p, quantity)
                        onNavigateBack()
                    },
                    enabled = p.quantity > 0
                )
            }
        }
    }
}
