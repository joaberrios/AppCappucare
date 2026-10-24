package com.example.appcappucare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcappucare.viewmodel.ProductoViewModel

@Composable
fun InicioScreen(
    vm: ProductoViewModel,
    abrirInventario: () -> Unit
) {
    val productos by vm.productos.collectAsState()

    val totalProductos = productos.size
    val totalUnidades = productos.sumOf { it.stock }
    val stockBajo = productos.count { it.stockBajo }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Cappucare",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Bienvenido al sistema de inventario médico")

        Text(
            "Resumen del inventario",
            style = MaterialTheme.typography.titleLarge
        )

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Productos registrados")
                Text(
                    "$totalProductos",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Unidades disponibles")
                Text(
                    "$totalUnidades",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Productos con stock bajo")
                Text(
                    "$stockBajo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (stockBajo > 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }
        }

        Button(
            onClick = abrirInventario,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver inventario")
        }
    }
}
