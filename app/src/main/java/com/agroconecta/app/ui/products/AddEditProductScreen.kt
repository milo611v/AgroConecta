package com.agroconecta.app.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agroconecta.app.ui.components.CustomTextField
import com.agroconecta.app.ui.components.LoadingState
import com.agroconecta.app.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    viewModel: ProductViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val product by viewModel.product.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var available by remember { mutableStateOf(true) }

    val units = listOf("kg", "unidad", "bulto", "lb", "litro")
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(product) {
        product?.let {
            name = it.name
            description = it.description
            price = it.price.toString()
            quantity = it.quantity.toString()
            unit = it.unit
            available = it.available
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ProductUiState.Success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (product == null) "Agregar Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState is ProductUiState.Loading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre del Producto"
                )

                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descripción",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = "Precio",
                        modifier = Modifier.weight(1f)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            units.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        unit = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                CustomTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "Cantidad en Stock"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Disponible", modifier = Modifier.weight(1f))
                    Switch(
                        checked = available,
                        onCheckedChange = { available = it }
                    )
                }

                if (uiState is ProductUiState.Error) {
                    Text(
                        text = (uiState as ProductUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.weight(1f))

                PrimaryButton(
                    text = if (product == null) "Crear Producto" else "Guardar Cambios",
                    onClick = {
                        viewModel.saveProduct(
                            name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 0,
                            unit = unit,
                            imageUri = null,
                            available = available
                        )
                    }
                )
            }
        }
    }
}
