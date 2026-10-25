package com.example.appcappucare.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificacionUtils {

    private const val CANAL_ID = "stock_bajo"

    fun mostrarStockBajo(
        context: Context,
        productoId: Int,
        nombre: String,
        stock: Int
    ) {
        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val canal = NotificationChannel(
            CANAL_ID,
            "Alertas de inventario",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(canal)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Stock bajo en Cappucare")
            .setContentText("$nombre tiene solo $stock unidades disponibles")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(productoId, notificacion)
    }
}
