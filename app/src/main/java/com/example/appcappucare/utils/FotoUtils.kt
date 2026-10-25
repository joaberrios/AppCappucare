package com.example.appcappucare.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.util.UUID

object FotoUtils {

    fun guardarFoto(context: Context, bitmap: Bitmap): String {
        val carpeta = File(context.filesDir, "fotos_productos")
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw IllegalStateException("No se pudo crear la carpeta de fotos")
        }

        val archivo = File(carpeta, "${UUID.randomUUID()}.jpg")

        archivo.outputStream().use { salida ->
            val guardado = bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                salida
            )
            if (!guardado) {
                throw IllegalStateException("No se pudo guardar la fotografia")
            }
        }

        return archivo.absolutePath
    }
}
