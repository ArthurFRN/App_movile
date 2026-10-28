package com.example.ratcher.IU.componentes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Forma de los botones (esquinas redondeadas, no totalmente ovalados)
val FormaBoton = RoundedCornerShape(10.dp)

// Botón azul grande que usan todas las pantallas
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    conFlecha: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBoton,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold)
        if (conFlecha) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Botón con borde, para acciones secundarias
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = FormaBoton,
        modifier = modifier.height(48.dp)
    ) {
        Text(texto, fontWeight = FontWeight.Medium)
    }
}
