package com.example.ratcher.IU.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Animación de "sacudida": cada vez que sube el contador de fallos,
// el valor se mueve a izquierda y derecha. Se usa con Modifier.offset(x = ...)
@Composable
fun rememberSacudida(fallos: Int): Dp {
    val desplazamiento = remember { Animatable(0f) }

    LaunchedEffect(fallos) {
        if (fallos > 0) {
            for (i in 1..3) {
                desplazamiento.animateTo(10f, tween(50))
                desplazamiento.animateTo(-10f, tween(50))
            }
            desplazamiento.animateTo(0f, tween(50))
        }
    }
    return desplazamiento.value.dp
}
