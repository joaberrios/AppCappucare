package com.example.appcappucare.model

data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int
) {
    val stockBajo: Boolean
        get() = stock <= stockMinimo
}
