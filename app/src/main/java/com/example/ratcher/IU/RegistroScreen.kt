package com.example.ratcher.IU

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ratcher.IU.componentes.BarraSuperior
import com.example.ratcher.IU.componentes.BotonPrincipal
import com.example.ratcher.IU.componentes.CampoTexto
import com.example.ratcher.IU.componentes.EtiquetaSeccion
import com.example.ratcher.IU.componentes.GrupoOpciones
import com.example.ratcher.IU.componentes.rememberSacudida
import com.example.ratcher.model.Catalogo
import com.example.ratcher.ui.theme.Verde
import com.example.ratcher.util.vibrar
import com.example.ratcher.viewmodel.RegistroViewModel
import kotlinx.coroutines.delay

@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onAtras: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val context = LocalContext.current
    val sacudida = rememberSacudida(viewModel.fallos)

    LaunchedEffect(viewModel.fallos) {
        if (viewModel.fallos > 0) vibrar(context)
    }
    // Cuando se registra, se muestra el mensaje un momento y se vuelve al login
    LaunchedEffect(viewModel.registrado) {
        if (viewModel.registrado) {
            delay(1500)
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = { BarraSuperior("Crear cuenta", onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            EtiquetaSeccion("Registro")
            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = viewModel.nombre,
                onCambio = { viewModel.cambiarNombre(it) },
                etiqueta = "Nombre completo",
                placeholder = "Ej: Camila Muñoz",
                icono = Icons.Default.Person,
                error = viewModel.nombreError
            )
            Spacer(modifier = Modifier.height(12.dp))

            CampoTexto(
                valor = viewModel.correo,
                onCambio = { viewModel.cambiarCorreo(it) },
                etiqueta = "Correo electrónico",
                placeholder = "nombre@correo.cl",
                icono = Icons.Default.Email,
                error = viewModel.correoError,
                teclado = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Cargo", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            GrupoOpciones(
                opciones = Catalogo.cargos,
                seleccionada = viewModel.cargo,
                onSeleccion = { viewModel.cambiarCargo(it) }
            )
            AnimatedVisibility(visible = viewModel.cargoError != null) {
                Text(
                    text = viewModel.cargoError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            CampoTexto(
                valor = viewModel.clave,
                onCambio = { viewModel.cambiarClave(it) },
                etiqueta = "Contraseña",
                icono = Icons.Default.Lock,
                error = viewModel.claveError,
                esClave = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            CampoTexto(
                valor = viewModel.confirmacion,
                onCambio = { viewModel.cambiarConfirmacion(it) },
                etiqueta = "Repetir contraseña",
                icono = Icons.Default.Lock,
                error = viewModel.confirmacionError,
                esClave = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.guardando) {
                CircularProgressIndicator()
            } else {
                BotonPrincipal(
                    texto = "Registrarme",
                    onClick = { viewModel.registrar() },
                    modifier = Modifier.offset(x = sacudida)
                )
            }

            AnimatedVisibility(visible = viewModel.mensajeError != null) {
                Text(
                    text = viewModel.mensajeError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            AnimatedVisibility(visible = viewModel.registrado) {
                Text(
                    text = "Cuenta creada correctamente. Volviendo al inicio de sesión...",
                    color = Verde,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
