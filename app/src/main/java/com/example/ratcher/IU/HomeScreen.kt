package com.example.ratcher.IU

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ratcher.IU.componentes.BarraSuperior
import com.example.ratcher.IU.componentes.BotonPrincipal
import com.example.ratcher.IU.componentes.EtiquetaEstado
import com.example.ratcher.IU.componentes.ImagenEvidencia
import com.example.ratcher.IU.componentes.TarjetaSuave
import com.example.ratcher.model.Estados
import com.example.ratcher.ui.theme.AzulClaro
import com.example.ratcher.ui.theme.AzulNoche
import com.example.ratcher.util.Sesion
import com.example.ratcher.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNuevo: () -> Unit,
    onAbrir: (Long) -> Unit,
    onSalir: () -> Unit
) {
    // Cada vez que se entra a la pantalla se recarga la lista desde la base de datos
    LaunchedEffect(Unit) { viewModel.cargar() }

    val usuario = Sesion.usuario

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Ratcher",
                conLogo = true,
                acciones = {
                    // Círculo con la inicial del usuario
                    if (usuario != null && usuario.nombre.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = usuario.nombre.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TextButton(onClick = onSalir) { Text("Salir") }
                }
            )
        },
        // El botón para crear un levantamiento queda fijo abajo
        bottomBar = {
            Surface(shadowElevation = 6.dp) {
                BotonPrincipal(
                    texto = "Nuevo levantamiento",
                    onClick = onNuevo,
                    conFlecha = true,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (usuario != null) {
                Text(
                    text = "Hola, " + usuario.nombre + " · " + usuario.cargo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            Text(
                text = "Mis levantamientos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Registra evidencia y revisa cada componente.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Tarjeta oscura con el resumen (se calcula con la lista que ya existe)
            TarjetaResumen(
                total = viewModel.lista.size,
                porCompletar = viewModel.lista.count { it.estado != Estados.VALIDADO }
            )

            Text(
                text = "Actividad reciente",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )

            if (viewModel.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.lista.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aún no hay levantamientos.\nPresiona \"Nuevo levantamiento\" para crear el primero.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.lista, key = { it.id }) { l ->
                        TarjetaSuave(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAbrir(l.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ImagenEvidencia(
                                    ruta = l.rutaImagen,
                                    maxLado = 256,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = l.rackId,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = l.area,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = l.totalComponentes.toString() + " componentes · " +
                                            l.pendientes + " por revisar",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    EtiquetaEstado(l.estado)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = l.fecha,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Tarjeta azul oscura con dos números: levantamientos totales y por completar
@Composable
fun TarjetaResumen(total: Int, porCompletar: Int) {
    Surface(
        color = AzulNoche,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Data Center · CMC360",
                style = MaterialTheme.typography.bodySmall,
                color = AzulClaro
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                NumeroResumen(numero = total, texto = "Levantamientos", modifier = Modifier.weight(1f))
                NumeroResumen(numero = porCompletar, texto = "Por completar", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun NumeroResumen(numero: Int, texto: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = numero.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = AzulClaro
        )
    }
}
