package com.example.ratcher.IU

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ratcher.IU.componentes.BarraSuperior
import com.example.ratcher.IU.componentes.BotonPrincipal
import com.example.ratcher.IU.componentes.CampoTexto
import com.example.ratcher.IU.componentes.EtiquetaEstado
import com.example.ratcher.IU.componentes.EtiquetaSeccion
import com.example.ratcher.IU.componentes.GrupoOpciones
import com.example.ratcher.IU.componentes.ImagenEvidencia
import com.example.ratcher.IU.componentes.TarjetaComponente
import com.example.ratcher.IU.componentes.TarjetaSuave
import com.example.ratcher.IU.componentes.rememberSacudida
import com.example.ratcher.model.Catalogo
import com.example.ratcher.model.Estados
import com.example.ratcher.util.vibrar
import com.example.ratcher.viewmodel.DetalleViewModel

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    idLevantamiento: Long,
    onAtras: () -> Unit
) {
    val context = LocalContext.current
    val sacudida = rememberSacudida(viewModel.fallos)
    var confirmarBorrado by remember { mutableStateOf(false) }

    LaunchedEffect(idLevantamiento) { viewModel.cargar(idLevantamiento) }
    LaunchedEffect(viewModel.eliminado) {
        if (viewModel.eliminado) onAtras()
    }
    LaunchedEffect(viewModel.fallos) {
        if (viewModel.fallos > 0) vibrar(context)
    }

    val lev = viewModel.levantamiento

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Detalle del levantamiento",
                onAtras = onAtras,
                acciones = {
                    IconButton(onClick = { confirmarBorrado = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar levantamiento")
                    }
                }
            )
        },
        bottomBar = {
            if (lev != null && lev.estado != Estados.VALIDADO) {
                Surface(shadowElevation = 6.dp) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        if (!viewModel.puedeValidar) {
                            Text(
                                text = "Confirma, corrige o rechaza todos los componentes para poder validar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        BotonPrincipal(
                            texto = "Validar levantamiento",
                            onClick = { viewModel.validar() },
                            enabled = viewModel.puedeValidar,
                            modifier = Modifier.offset(x = sacudida)
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (lev == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Datos del levantamiento
                item {
                    TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            EtiquetaSeccion("Levantamiento")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lev.rackId,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                EtiquetaEstado(lev.estado)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Área: " + lev.area, style = MaterialTheme.typography.bodyMedium)
                            Text("Fecha: " + lev.fecha, style = MaterialTheme.typography.bodyMedium)
                            Text("Responsable: " + lev.responsable, style = MaterialTheme.typography.bodyMedium)
                            if (lev.observaciones.isNotBlank()) {
                                Text(
                                    text = "Observaciones: " + lev.observaciones,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Tres números: total, revisados y pendientes
                item {
                    val total = viewModel.componentes.size
                    val pendientes = viewModel.componentes.count { it.estado == Estados.RECONOCIDO }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Contador(total, "Componentes", Modifier.weight(1f))
                        Contador(total - pendientes, "Revisados", Modifier.weight(1f))
                        Contador(pendientes, "Pendientes", Modifier.weight(1f))
                    }
                }

                // Foto de evidencia
                item {
                    Text(
                        text = "Evidencia vinculada",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    ImagenEvidencia(
                        ruta = lev.rutaImagen,
                        escala = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                // Componentes reconocidos
                item {
                    Text(
                        text = "Componentes reconocidos (" + viewModel.componentes.size + ")",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(viewModel.componentes, key = { it.id }) { c ->
                    TarjetaComponente(
                        componente = c,
                        editable = viewModel.esEditable,
                        onConfirmar = { viewModel.confirmar(c) },
                        onCorregir = { viewModel.abrirCorreccion(c) },
                        onRechazar = { viewModel.rechazar(c) }
                    )
                }

                // Trazabilidad
                item {
                    Text(
                        text = "Trazabilidad",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            for (h in viewModel.historial) {
                                Text(
                                    text = h.fecha + " · " + h.usuario,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = h.accion + ": " + h.detalle,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Formulario de corrección (ventana emergente)
    if (viewModel.editando != null) {
        DialogCorreccion(viewModel)
    }

    // Confirmación antes de eliminar
    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Eliminar levantamiento") },
            text = { Text("Se borrarán la foto, los componentes y el historial. ¿Continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrado = false
                    viewModel.eliminar()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun DialogCorreccion(viewModel: DetalleViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.cerrarCorreccion() },
        title = { Text("Corregir componente") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Categoría correcta", style = MaterialTheme.typography.labelLarge)
                GrupoOpciones(
                    opciones = Catalogo.categorias,
                    seleccionada = viewModel.categoriaEdicion,
                    onSeleccion = { viewModel.cambiarCategoria(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CampoTexto(
                    valor = viewModel.descripcionEdicion,
                    onCambio = { viewModel.cambiarDescripcion(it) },
                    etiqueta = "Descripción",
                    icono = Icons.Default.Create,
                    error = viewModel.descripcionError
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.guardarCorreccion() }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.cerrarCorreccion() }) { Text("Cancelar") }
        }
    )
}

// Cuadro con un número grande y un texto debajo (ej: 04 Revisados)
@Composable
fun Contador(numero: Int, texto: String, modifier: Modifier = Modifier) {
    TarjetaSuave(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = numero.toString().padStart(2, '0'),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
