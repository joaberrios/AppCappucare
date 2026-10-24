package com.example.appcappucare.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun CamaraProducto() {
    var foto by remember { mutableStateOf<Bitmap?>(null) }

    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen != null) {
            foto = imagen
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = { camara.launch(null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tomar foto del producto")
        }

        foto?.let { imagen ->
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = "Fotografía del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
