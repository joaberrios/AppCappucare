
package com.example.appcappucare.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CamaraProducto(
    onFotoTomada: (Bitmap) -> Unit
) {
    var foto by remember { mutableStateOf<Bitmap?>(null) }

    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen != null) {
            foto = imagen
            onFotoTomada(imagen)
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0F5F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        android.R.drawable.ic_menu_camera
                    ),
                    contentDescription = null,
                    tint = Color(0xFF008F87),
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Fotografia del producto",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF12304A)
            )

            Text(
                text = "Agrega una imagen para identificar facilmente el producto",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF657789)
            )

            Button(
                onClick = { camara.launch(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF008F87)
                )
            ) {
                Icon(
                    painter = painterResource(
                        android.R.drawable.ic_menu_camera
                    ),
                    contentDescription = null
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = if (foto == null) {
                        "Tomar fotografia"
                    } else {
                        "Cambiar fotografia"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }

            foto?.let { imagen ->

                HorizontalDivider(
                    color = Color(0xFFE8EEF2)
                )

                Text(
                    text = "Vista previa",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF657789)
                )

                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = "Fotografia del producto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F7FA)),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Fotografia capturada correctamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF008F87)
                )
            }
        }
    }
}
