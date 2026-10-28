package com.example.ratcher.IU.componentes

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ratcher.model.Componente
import com.example.ratcher.model.Estados
import com.example.ratcher.ui.theme.NaranjaSuave

// Tarjeta de un componente reconocido, con su barra de confianza y los botones de revisión.
// Si todavía no se revisa (estado Reconocido) la tarjeta se ve con fondo naranja suave.
@Composable
fun TarjetaComponente(
    componente: Componente,
    editable: Boolean,
    onConfirmar: () -> Unit,
    onCorregir: () -> Unit,
    onRechazar: () -> Unit
) {
    // La barra de confianza se llena con animación al aparecer la tarjeta
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val progreso by animateFloatAsState(
        targetValue = if (visible) componente.confianza / 100f else 0f,
        animationSpec = tween(800),
        label = "confianza"
    )

    val pendiente = componente.estado == Estados.RECONOCIDO

    TarjetaSuave(
        modifier = Modifier.fillMaxWidth(),
        color = if (pendiente) NaranjaSuave else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cuadrado con la primera letra de la categoría
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = componente.categoria.take(1),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = componente.categoria,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = componente.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                EtiquetaEstado(componente.estado)
            }

            if (componente.categoria != componente.categoriaOriginal) {
                Text(
                    text = "Reconocido originalmente como: " + componente.categoriaOriginal,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Confianza del reconocimiento: " + componente.confianza + "%",
                style = MaterialTheme.typography.labelMedium
            )
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )

            // Los botones solo aparecen si el levantamiento aún no está validado
            if (editable) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onConfirmar,
                        enabled = componente.estado != Estados.CONFIRMADO,
                        shape = FormaBoton,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Confirmar") }

                    OutlinedButton(
                        onClick = onCorregir,
                        shape = FormaBoton,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Corregir") }

                    TextButton(
                        onClick = onRechazar,
                        enabled = componente.estado != Estados.RECHAZADO,
                        shape = FormaBoton,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Rechazar", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}
