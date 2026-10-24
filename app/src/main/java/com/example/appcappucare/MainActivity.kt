package com.example.appcappucare

import android.os.Bundle
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.appcappucare.model.Producto
import com.example.appcappucare.viewmodel.ProductoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = ViewModelProvider(this)[ProductoViewModel::class.java]

        setContent {
            MaterialTheme {
                CappucareApp(vm)
            }
        }
    }
}

@Composable
fun CappucareApp(vm: ProductoViewModel) {
    var pantalla by remember { mutableStateOf("inicio") }

    AnimatedContent(
        targetState = pantalla,
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
        label = "AnimacionPantallas"
    ) { pantallaActual ->

        when (pantallaActual) {
            "inicio" -> com.example.appcappucare.ui.screens.InicioScreen(
                vm = vm,
                abrirInventario = { pantalla = "inventario" }
            )

            "agregar" -> FormularioProducto(
                vm = vm,
                volver = { pantalla = "inventario" }
            )

            else -> PantallaInventario(
                vm = vm,
                agregar = { pantalla = "agregar" },
                volverInicio = { pantalla = "inicio" }
            )
        }
    }
}
@Composable
fun PantallaInventario(
    vm: ProductoViewModel,
    agregar: () -> Unit, volverInicio: () -> Unit
) {
    val productos by vm.productos.collectAsState()

    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }

    var esEntrada by remember {
        mutableStateOf(false)
    }

    var cantidad by remember {
        mutableStateOf("")
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "Cappucare",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Sistema de inventario médico")

        OutlinedButton(
            onClick = volverInicio,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = agregar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Agregar producto")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Productos disponibles",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos, key = { it.id }) { producto ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            producto.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Categoría: ${producto.categoria}")
                        Text("Precio: $${producto.precio.toInt()}")
                        Text("Stock: ${producto.stock}")

                        if (producto.stockBajo) {
                            Text(
                                "⚠ Stock bajo",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    productoSeleccionado = producto
                                    esEntrada = false
                                    cantidad = ""
                                    mensaje = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Vender")
                            }

                            OutlinedButton(
                                onClick = {
                                    productoSeleccionado = producto
                                    esEntrada = true
                                    cantidad = ""
                                    mensaje = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+ Stock")
                            }
                        }
                    }
                }
            }
        }
    }

    if (productoSeleccionado != null) {
        val producto = productoSeleccionado!!

        AlertDialog(
            onDismissRequest = {
                productoSeleccionado = null
            },
            title = {
                Text(
                    if (esEntrada) "Agregar stock" else "Registrar venta"
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Producto: ${producto.nombre}")
                    Text("Stock disponible: ${producto.stock}")

                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad") },
                        singleLine = true
                    )

                    if (mensaje.isNotEmpty()) {
                        Text(
                            mensaje,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val numero = cantidad.toIntOrNull()

                        if (numero == null || numero <= 0) {
                            mensaje = "Ingresa una cantidad válida"
                        } else {
                            vm.registrarMovimiento(
                                producto.id,
                                numero,
                                esEntrada
                            ) { resultado, exito ->
                                if (exito) {
                                    productoSeleccionado = null
                                } else {
                                    mensaje = resultado
                                }
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productoSeleccionado = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FormularioProducto(
    vm: ProductoViewModel,
    volver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minimo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Agregar producto",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock inicial") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = minimo,
            onValueChange = { minimo = it },
            label = { Text("Stock mínimo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                vm.agregarProducto(
                    nombre,
                    categoria,
                    precio,
                    stock,
                    minimo
                ) { resultado, exito ->
                    if (exito) {
                        volver()
                    } else {
                        mensaje = resultado
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar producto")
        }

        OutlinedButton(
            onClick = volver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inventario")
        }
    }
}




