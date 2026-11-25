package com.example.ratcher.IU.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// Lista de opciones con botones de radio (se elige solo una)
@Composable
fun GrupoOpciones(
    opciones: List<String>,
    seleccionada: String,
    onSeleccion: (String) -> Unit
) {
    Column {
        for (opcion in opciones) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSeleccion(opcion) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = opcion == seleccionada,
                    onClick = { onSeleccion(opcion) }
                )
                Text(opcion)
            }
        }
    }
}
