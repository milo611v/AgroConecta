package com.agroconecta.app.ui.producer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agroconecta.app.ui.components.EmptyState
import com.agroconecta.app.ui.components.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerOrdersScreen(
    viewModel: ProducerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOrderDetail: (Long) -> Unit
) {
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos Recibidos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            EmptyState(
                message = "Aún no has recibido pedidos.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(
                        order = order,
                        onClick = { onNavigateToOrderDetail(order.id) }
                    )
                }
            }
        }
    }
}
