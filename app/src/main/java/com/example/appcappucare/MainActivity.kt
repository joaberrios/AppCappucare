package com.example.appcappucare

import android.os.Bundle
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.appcappucare.model.Producto
import com.example.appcappucare.viewmodel.ProductoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = ViewModelProvider(this)[ProductoViewModel::class.java]

        val permisoNotificaciones = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { permitido ->
            if (permitido) {
                android.util.Log.d("Cappucare", "Notificaciones permitidas")
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            com.example.appcappucare.ui.theme.AppCappucareTheme {
                CappucareApp(vm)
            }
        }
    }
}

@Composable
fun CappucareApp(vm: ProductoViewModel) {
    var pantalla by remember { mutableStateOf("inicio") }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFFF3F7FA),
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.ui.graphics.Color.White
            ) {
                NavigationBarItem(
                    selected = pantalla == "inicio",
                    onClick = { pantalla = "inicio" },
                    icon = {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(
                                android.R.drawable.ic_menu_view
                            ),
                            contentDescription = null
                        )
                    },
                    label = { Text("Inicio") }
                )

                NavigationBarItem(
                    selected = pantalla == "inventario",
                    onClick = { pantalla = "inventario" },
                    icon = {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(
                                android.R.drawable.ic_menu_agenda
                            ),
                            contentDescription = null
                        )
                    },
                    label = { Text("Inventario") }
                )

                NavigationBarItem(
                    selected = pantalla == "agregar",
                    onClick = { pantalla = "agregar" },
                    icon = {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(
                                android.R.drawable.ic_input_add
                            ),
                            contentDescription = null
                        )
                    },
                    label = { Text("Agregar") }
                )
            }
        }
    ) { paddingInterno ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
        ) {
            AnimatedContent(
                targetState = pantalla,
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                },
                label = "NavegacionModerna"
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
    }
}
@Composable
fun PantallaInventario(
    vm: ProductoViewModel,
    agregar: () -> Unit, volverInicio: () -> Unit
) {
    val productos by vm.productos.collectAsState()

    var busqueda by remember { mutableStateOf("") }

    val productosFiltrados = productos.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
        it.categoria.contains(busqueda, ignoreCase = true)
    }

    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }

    var productoParaEditar by remember { mutableStateOf<Producto?>(null) }
    var productoParaEliminar by remember { mutableStateOf<Producto?>(null) }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Mi inventario",
                    style = MaterialTheme.typography.headlineLarge,
                    color = androidx.compose.ui.graphics.Color(0xFF12304A)
                )

                Text(
                    "Gestiona tus insumos medicos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF657789)
                )
            }

            Surface(
                color = androidx.compose.ui.graphics.Color(0xFFDDF5F1),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text(
                    "${productos.size} productos",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF008F87)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar producto") },
            placeholder = { Text("Nombre o categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(
                        android.R.drawable.ic_menu_search
                    ),

                    contentDescription = "Buscar"
                )
            }

        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosFiltrados, key = { it.id }) { producto ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = androidx.compose.ui.graphics.Color.White
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 5.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(105.dp)
                                    .clip(
                                        androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                    )
                                    .background(
                                        androidx.compose.ui.graphics.Color(0xFFE9F5F3)
                                    ),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                val rutaFoto = producto.fotoUri
                                val imagen = remember(rutaFoto) {
                                    rutaFoto?.let {
                                        if (java.io.File(it).exists()) {
                                            android.graphics.BitmapFactory.decodeFile(it)
                                        } else null
                                    }
                                }

                                if (imagen != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = imagen.asImageBitmap(),
                                        contentDescription = "Foto de ${producto.nombre}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        painter = androidx.compose.ui.res.painterResource(
                                            android.R.drawable.ic_menu_gallery
                                        ),
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color(0xFF00A89D),
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    producto.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = androidx.compose.ui.graphics.Color(0xFF12304A)
                                )

                                Text(
                                    producto.categoria,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = androidx.compose.ui.graphics.Color(0xFF657789)
                                )

                                Text(
                                    "$${producto.precio.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = androidx.compose.ui.graphics.Color(0xFF008F87)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = androidx.compose.ui.graphics.Color(0xFFE8EEF2)
                        )
                        Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
) {
    Text(
        "Stock: ${producto.stock}",
        style = MaterialTheme.typography.bodyMedium
    )

    val agotado = producto.stock <= 0
    val bajo = producto.stockBajo

    val colorEstado = when {
        agotado -> androidx.compose.ui.graphics.Color(0xFFC63C43)
        bajo -> androidx.compose.ui.graphics.Color(0xFFE78A36)
        else -> androidx.compose.ui.graphics.Color(0xFF00A89D)
    }

    val textoEstado = when {
        agotado -> "Agotado"
        bajo -> "Stock bajo"
        else -> "Disponible"
    }

    Surface(
        color = colorEstado.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
    ) {
        Text(
            textoEstado,
            color = colorEstado,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}

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

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    productoParaEditar = producto
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }

                            OutlinedButton(
                                onClick = {
                                    productoParaEliminar = producto
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Eliminar")
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

    productoParaEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = {
                productoParaEliminar = null
            },
            title = {
                Text("Eliminar producto")
            },
            text = {
                Text("¿Seguro que deseas eliminar ${producto.nombre}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.eliminarProducto(producto.id) { resultado, exito ->
                            if (exito) {
                                productoParaEliminar = null
                            } else {
                                mensaje = resultado
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productoParaEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    productoParaEditar?.let { producto ->
        var nombreEditar by remember(producto.id) {
            mutableStateOf(producto.nombre)
        }
        var categoriaEditar by remember(producto.id) {
            mutableStateOf(producto.categoria)
        }
        var precioEditar by remember(producto.id) {
            mutableStateOf(producto.precio.toString())
        }
        var minimoEditar by remember(producto.id) {
            mutableStateOf(producto.stockMinimo.toString())
        }
        var errorEditar by remember(producto.id) {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {
                productoParaEditar = null
            },
            title = {
                Text("Editar producto")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nombreEditar,
                        onValueChange = { nombreEditar = it },
                        label = { Text("Nombre") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = categoriaEditar,
                        onValueChange = { categoriaEditar = it },
                        label = { Text("Categoría") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = precioEditar,
                        onValueChange = { nuevo ->
                            if (nuevo.matches(Regex("""\d*\.?\d*"""))) {
                                precioEditar = nuevo
                            }
                        },
                        label = { Text("Precio") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = minimoEditar,
                        onValueChange = { nuevo ->
                            if (nuevo.all { it.isDigit() }) {
                                minimoEditar = nuevo
                            }
                        },
                        label = { Text("Stock mínimo") },
                        singleLine = true
                    )

                    if (errorEditar.isNotEmpty()) {
                        Text(
                            errorEditar,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.editarProducto(
                            producto = producto,
                            nombre = nombreEditar,
                            categoria = categoriaEditar,
                            precio = precioEditar,
                            stockMinimo = minimoEditar
                        ) { resultado, exito ->
                            if (exito) {
                                productoParaEditar = null
                            } else {
                                errorEditar = resultado
                            }
                        }
                    }
                ) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productoParaEditar = null
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
    var fotoProducto by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val contexto = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Nuevo producto",
            style = MaterialTheme.typography.headlineMedium
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = androidx.compose.ui.graphics.Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // TARJETAS_CAPPUCARE
                Text(
                    "INFORMACION DEL PRODUCTO",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF008F87)
                )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del producto") },
            leadingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_edit),
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            leadingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_sort_by_size),
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { nuevo ->
                if (nuevo.matches(Regex("""\d*\.?\d*"""))) {
                    precio = nuevo
                }
            },
            label = { Text("Precio") },
            leadingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_info_details),
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            singleLine = true
        )

            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = androidx.compose.ui.graphics.Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // TARJETAS_CAPPUCARE
                Text(
                    "CONTROL DE INVENTARIO",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF008F87)
                )
        OutlinedTextField(
            value = stock,
            onValueChange = { nuevo -> if (nuevo.all { it.isDigit() }) stock = nuevo },
            label = { Text("Stock inicial") },
            leadingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_add),
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = minimo,
            onValueChange = { nuevo -> if (nuevo.all { it.isDigit() }) minimo = nuevo },
            label = { Text("Stock mínimo") },
            leadingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_dialog_alert),
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            singleLine = true
        )


            }
        }
com.example.appcappucare.ui.components.CamaraProducto(onFotoTomada = { foto -> fotoProducto = foto })
        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val fotoUri = fotoProducto?.let { imagen ->
                    try {
                        com.example.appcappucare.utils.FotoUtils.guardarFoto(
                            contexto,
                            imagen
                        )
                    } catch (e: Exception) {
                        mensaje = "No se pudo guardar la fotografía"
                        return@Button
                    }
                }

                vm.agregarProducto(
                    nombre,
                    categoria,
                    precio,
                    stock,
                    minimo,
                    onResultado = { resultado, exito ->
                    if (exito) {
                        volver()
                    } else {
                        mensaje = resultado
                    }
                },
                    fotoUri = fotoUri
                )            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF008F87))
        ) {
            Text("Guardar producto", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = volver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inventario")
        }
    }
}
