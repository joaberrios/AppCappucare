package com.example.appcappucare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcappucare.viewmodel.ProductoViewModel

private val Azul = Color(0xFF12304A)
private val Turquesa = Color(0xFF00A89D)
private val Fondo = Color(0xFFF3F7FA)
private val TextoSecundario = Color(0xFF657789)

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
            .background(Fondo)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Azul,
                    RoundedCornerShape(
                        bottomStart = 30.dp,
                        bottomEnd = 30.dp
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                "CAPPUCARE",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "Panel de control",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Gestiona tus insumos médicos de forma fácil y segura.",
                color = Color(0xFFD5E5ED),
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "● Sistema de inventario activo",
                    color = Color.White,
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(2.dp))

            Text(
                "Resumen general",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Azul
            )

            Text(
                "Información actualizada de tu inventario",
                fontSize = 13.sp,
                color = TextoSecundario
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaResumen(
                    titulo = "Productos",
                    valor = "$totalProductos",
                    simbolo = "▦",
                    color = Turquesa,
                    modifier = Modifier.weight(1f)
                )

                TarjetaResumen(
                    titulo = "Unidades",
                    valor = "$totalUnidades",
                    simbolo = "◈",
                    color = Color(0xFF3478C8),
                    modifier = Modifier.weight(1f)
                )
            }

            TarjetaResumen(
                titulo = "Productos con stock bajo",
                valor = "$stockBajo",
                simbolo = "!",
                color = if (stockBajo > 0)
                    Color(0xFFE78A36)
                else
                    Turquesa,
                modifier = Modifier.fillMaxWidth()
            )

            if (stockBajo > 0) {
                Surface(
                    color = Color(0xFFFFF0E3),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Atención: stock bajo",
                            color = Color(0xFF995018),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Tienes $stockBajo producto(s) que necesitan revisión o reposición.",
                            color = Color(0xFF995018),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "Acceso rápido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Azul
            )

            Button(
                onClick = abrirInventario,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Turquesa
                )
            ) {
                Text(
                    "Ver inventario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "Desde el inventario puedes agregar productos, registrar ventas y controlar las existencias.",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun TarjetaResumen(
    titulo: String,
    valor: String,
    simbolo: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    simbolo,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(
                        horizontal = 13.dp,
                        vertical = 8.dp
                    )
                )
            }

            Text(
                valor,
                color = Azul,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                titulo,
                color = TextoSecundario,
                fontSize = 13.sp
            )
        }
    }
}
