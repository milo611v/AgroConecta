package com.agroconecta.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agroconecta.app.ui.producer.ProducerStats
import com.agroconecta.app.ui.producer.ProducerViewModel
import com.agroconecta.app.ui.theme.AgroConectaTheme

@Composable
fun ProducerHomeScreen(
    viewModel: ProducerViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val stats by viewModel.stats.collectAsState()
    ProducerHomeScreenContent(
        stats = stats,
        onNavigateToProducts = onNavigateToProducts,
        onNavigateToOrders = onNavigateToOrders,
        onNavigateToProfile = onNavigateToProfile,
        windowSizeClass = windowSizeClass
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerHomeScreenContent(
    stats: ProducerStats,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val columns = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 2
        WindowWidthSizeClass.Medium -> 3
        else -> 4
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Productor") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Rounded.Person, contentDescription = "Mi Perfil")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val statsItems = listOf(
                        StatItem("Total Productos", stats.totalProducts.toString(), Icons.Rounded.Inventory2, MaterialTheme.colorScheme.primary),
                        StatItem("Pedidos Pendientes", stats.pendingOrders.toString(), Icons.Rounded.Pending, MaterialTheme.colorScheme.secondary),
                        StatItem("En Proceso", stats.inProcessOrders.toString(), Icons.Rounded.ShoppingCart, MaterialTheme.colorScheme.tertiary),
                        StatItem("Completados", stats.completedOrders.toString(), Icons.Rounded.CheckCircle, Color(0xFF4CAF50))
                    )

                    statsItems.chunked(columns).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { item ->
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard(
                                        title = item.title,
                                        value = item.value,
                                        icon = item.icon,
                                        color = item.color
                                    )
                                }
                            }
                            if (rowItems.size < columns) {
                                repeat(columns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Accesos Rápidos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    MenuButton(
                        text = "Gestionar Mis Productos",
                        icon = Icons.Rounded.List,
                        onClick = onNavigateToProducts
                    )
                    
                    MenuButton(
                        text = "Ver Mis Pedidos",
                        icon = Icons.Rounded.ReceiptLong,
                        onClick = onNavigateToOrders
                    )
                }
            }
        }
    }
}

data class StatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Text(text = text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,navigation=buttons")
@Composable
fun ProducerHomeScreenPreview() {
    AgroConectaTheme {
        val windowSizeClass = WindowSizeClass.calculateFromSize(androidx.compose.ui.unit.DpSize(411.dp, 891.dp))
        ProducerHomeScreenContent(
            stats = ProducerStats(10, 5, 3, 20),
            onNavigateToProducts = {},
            onNavigateToOrders = {},
            onNavigateToProfile = {},
            windowSizeClass = windowSizeClass
        )
    }
}
