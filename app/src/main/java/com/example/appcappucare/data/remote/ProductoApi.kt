
package com.example.appcappucare.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class ProductoDto(
    val id: Long? = null,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int
)

interface ProductoApi {

    @GET("api/productos")
    suspend fun listarProductos(): List<ProductoDto>

    @GET("api/productos/{id}")
    suspend fun buscarProducto(
        @Path("id") id: Long
    ): ProductoDto

    @POST("api/productos")
    suspend fun agregarProducto(
        @Body producto: ProductoDto
    ): ProductoDto

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoDto
    ): ProductoDto

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Unit>
}
