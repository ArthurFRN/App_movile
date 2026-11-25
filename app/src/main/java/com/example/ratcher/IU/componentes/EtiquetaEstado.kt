package com.example.ratcher.IU.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ratcher.model.Estados
import com.example.ratcher.ui.theme.AzulEstado
import com.example.ratcher.ui.theme.Naranja
import com.example.ratcher.ui.theme.Rojo
import com.example.ratcher.ui.theme.Verde

// Etiqueta de color según el estado (fondo suave y texto de color).
// El color cambia con animación cuando cambia el estado.
@Composable
fun EtiquetaEstado(estado: String) {
    val colorFinal = when (estado) {
        Estados.VALIDADO, Estados.CONFIRMADO -> Verde
        Estados.CORREGIDO -> AzulEstado
        Estados.RECHAZADO -> Rojo
        else -> Naranja
    }
    val color by animateColorAsState(
        targetValue = colorFinal,
        animationSpec = tween(500),
        label = "colorEstado"
    )

    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
        Text(
            text = estado,
            color = color,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
