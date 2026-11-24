package com.example.ratcher.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.ratcher.util.Archivos

// Muestra la foto de evidencia que está guardada en el teléfono
@Composable
fun ImagenEvidencia(
    ruta: String,
    modifier: Modifier = Modifier,
    maxLado: Int = 1024,
    escala: ContentScale = ContentScale.Crop
) {
    if (ruta.startsWith("http")) {
        AsyncImage(
            model = ruta,
            contentDescription = "Evidencia fotográfica del rack",
            modifier = modifier,
            contentScale = escala
        )
        return
    }

    val imagen = remember(ruta) { Archivos.cargarImagen(ruta, maxLado) }

    if (imagen != null) {
        Image(
            bitmap = imagen,
            contentDescription = "Evidencia fotográfica del rack",
            modifier = modifier,
            contentScale = escala
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("Sin imagen", style = MaterialTheme.typography.bodySmall)
        }
    }
}
