package com.example.appcappucare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcappucare.data.remote.ClienteMedicamentos
import com.example.appcappucare.data.remote.MedicamentoExterno
import kotlinx.coroutines.launch

@Composable
fun MedicamentosScreen() {

    var busqueda by remember { mutableStateOf("") }
    var resultados by remember {
        mutableStateOf<List<MedicamentoExterno>>(emptyList())
    }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val alcance = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Consulta de medicamentos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Informacion externa obtenida desde openFDA.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Nombre del medicamento") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val nombre = busqueda.trim()

                if (nombre.isBlank()) {
                    mensaje = "Ingresa un medicamento."
                    resultados = emptyList()
                } else {
                    alcance.launch {
                        cargando = true
                        mensaje = ""
                        resultados = emptyList()

                        try {
                            val respuesta =
                                ClienteMedicamentos.api.buscarMedicamentos(
                                    busqueda = "openfda.brand_name:" +
                                        nombre.replace(" ", "+")
                                )

                            resultados = respuesta.results

                            mensaje = if (resultados.isEmpty()) {
                                "No se encontraron resultados."
                            } else {
                                "${resultados.size} resultados encontrados."
                            }

                        } catch (error: Exception) {
                            mensaje =
                                "No se pudo consultar la API. Revisa la conexion."
                        } finally {
                            cargando = false
                        }
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar medicamento")
        }

        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mensaje.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(mensaje)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(resultados) { medicamento ->

                val nombre = medicamento.openfda.brand_name
                    .firstOrNull() ?: "Sin nombre comercial"

                val generico = medicamento.openfda.generic_name
                    .firstOrNull() ?: "No informado"

                val fabricante = medicamento.openfda.manufacturer_name
                    .firstOrNull() ?: "No informado"

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Nombre generico: $generico")
                        Text("Fabricante: $fabricante")

                        Text(
                            "Fuente: openFDA",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
