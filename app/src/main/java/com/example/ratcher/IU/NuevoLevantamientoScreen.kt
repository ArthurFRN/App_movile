package com.example.ratcher.IU

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ratcher.IU.componentes.BarraSuperior
import com.example.ratcher.IU.componentes.BotonPrincipal
import com.example.ratcher.IU.componentes.BotonSecundario
import com.example.ratcher.IU.componentes.CampoTexto
import com.example.ratcher.IU.componentes.EtiquetaSeccion
import com.example.ratcher.IU.componentes.ImagenEvidencia
import com.example.ratcher.IU.componentes.rememberSacudida
import com.example.ratcher.util.Sesion
import com.example.ratcher.util.vibrar
import com.example.ratcher.viewmodel.NuevoLevantamientoViewModel

@Composable
fun NuevoLevantamientoScreen(
    viewModel: NuevoLevantamientoViewModel,
    onAtras: () -> Unit,
    onCreado: (Long) -> Unit
) {
    val context = LocalContext.current
    val sacudida = rememberSacudida(viewModel.fallos)

    // Acuerdo de resultado de tarea para obtener imágenes utilizando la cámara del aparato.
    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito -> viewModel.resultadoCamara(exito) }

    val abrirCamara: () -> Unit = {
        val uri = viewModel.prepararFotoCamara(context)
        lanzadorCamara.launch(uri)
    }

    // Permiso de cámara en tiempo de ejecución
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara() else viewModel.permisoCamaraDenegado()
    }

    // Acuerdo de resultado para elegir un archivo multimedia a través del selector del sistema.
    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.guardarDesdeGaleria(context, uri)
    }

    // Efecto colateral que activa el servicio de vibración del aparato ante errores en los campos.
    LaunchedEffect(viewModel.fallos) {
        if (viewModel.fallos > 0) vibrar(context)
    }
    // Accede a la pantalla de detalle ingresando el ID generado al concluir el proceso.
    LaunchedEffect(viewModel.idCreado) {
        viewModel.idCreado?.let { onCreado(it) }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Nuevo levantamiento",
                onAtras = if (viewModel.analizando) null else onAtras
            )
        },
        // Barra inferior estática con el botón de guardado; se oculta de forma temporal durante el análisis.
        bottomBar = {
            if (!viewModel.analizando) {
                Surface(shadowElevation = 6.dp) {
                    BotonPrincipal(
                        texto = "Guardar y analizar imagen",
                        onClick = { viewModel.guardar() },
                        conFlecha = true,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .offset(x = sacudida)
                    )
                }
            }
        }
    ) { padding ->
        if (viewModel.analizando) {
            PantallaAnalisis(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                EtiquetaSeccion("Paso 1 de 2 · Antecedentes")
                Text(
                    text = "Identifica el rack",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                CampoTexto(
                    valor = viewModel.rackId,
                    onCambio = { viewModel.cambiarRack(it) },
                    etiqueta = "Identificador del rack",
                    placeholder = "Ej: RACK-01",
                    icono = Icons.Default.Info,
                    error = viewModel.rackError
                )
                Spacer(modifier = Modifier.height(14.dp))

                CampoTexto(
                    valor = viewModel.area,
                    onCambio = { viewModel.cambiarArea(it) },
                    etiqueta = "Área revisada",
                    placeholder = "Ej: Sala de servidores",
                    icono = Icons.Default.Place,
                    error = viewModel.areaError
                )
                Spacer(modifier = Modifier.height(14.dp))

                CampoTexto(
                    valor = viewModel.fecha,
                    onCambio = { viewModel.cambiarFecha(it) },
                    etiqueta = "Fecha",
                    placeholder = "dd/mm/aaaa",
                    icono = Icons.Default.DateRange,
                    error = viewModel.fechaError
                )
                Spacer(modifier = Modifier.height(14.dp))

                CampoTexto(
                    valor = viewModel.observaciones,
                    onCambio = { viewModel.cambiarObservaciones(it) },
                    etiqueta = "Observación inicial (opcional)",
                    icono = Icons.Default.Create,
                    error = viewModel.observacionesError,
                    lineas = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Responsable: " + Sesion.nombre(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(28.dp))
                EtiquetaSeccion("Paso 2 de 2 · Evidencia")
                Text(
                    text = "Añade una imagen",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Usa solo imágenes autorizadas, sin rostros ni datos sensibles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Muestra el componente de imagen de forma condicional cuando hay una ruta de archivo válida en el ViewModel.
                if (viewModel.rutaImagen.isNotEmpty()) {
                    ImagenEvidencia(
                        ruta = viewModel.rutaImagen,
                        escala = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (viewModel.errorImagen != null) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.outline
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no hay imagen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                AnimatedVisibility(visible = viewModel.errorImagen != null) {
                    Text(
                        text = viewModel.errorImagen ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotonSecundario(
                        texto = "Tomar foto",
                        onClick = {
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (tienePermiso) {
                                abrirCamara()
                            } else {
                                lanzadorPermiso.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    BotonSecundario(
                        texto = "Galería",
                        onClick = {
                            lanzadorGaleria.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Establece una transición continua para animar la opacidad del componente, simulando un estado de análisis en curso.
@Composable
fun PantallaAnalisis(modifier: Modifier = Modifier) {
    val transicion = rememberInfiniteTransition(label = "pulso")
    val transparencia = transicion.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "transparencia"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Analizando imagen del rack...",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.alpha(transparencia.value)
        )
        Text(
            text = "Identificando componentes del catálogo",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
