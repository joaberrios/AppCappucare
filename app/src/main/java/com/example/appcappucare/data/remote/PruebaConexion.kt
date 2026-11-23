package com.example.appcappucare.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PruebaConexion {

    suspend fun probar() {
        withContext(Dispatchers.IO) {
            try {
                val productos = RetrofitClient.productoApi.listarProductos()

                Log.i(
                    "CAPPUCARE_API",
                    "Conexion exitosa. Productos recibidos: ${productos.size}"
                )

                productos.forEach { producto ->
                    Log.i(
                        "CAPPUCARE_API",
                        "Producto: ${producto.nombre}"
                    )
                }

            } catch (error: Exception) {
                Log.e(
                    "CAPPUCARE_API",
                    "Error al conectar con Spring Boot",
                    error
                )
            }
        }
    }
}
