package com.example.ratcher.IU

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ratcher.IU.componentes.BotonPrincipal
import com.example.ratcher.IU.componentes.CampoTexto
import com.example.ratcher.IU.componentes.EtiquetaSeccion
import com.example.ratcher.IU.componentes.LogoRatcher
import com.example.ratcher.IU.componentes.rememberSacudida
import com.example.ratcher.util.vibrar
import com.example.ratcher.viewmodel.LoginState
import com.example.ratcher.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onIrARegistro: () -> Unit,
    onLoginExitoso: () -> Unit
) {
    val context = LocalContext.current
    val sacudida = rememberSacudida(viewModel.fallos)
    val estado = viewModel.estado

    // Genera una vibración nativa en el dispositivo al capturar errores.
    LaunchedEffect(viewModel.fallos) {
        if (viewModel.fallos > 0) vibrar(context)
    }
    // Responde al estado exitoso del login activando el callback de navegación.
    LaunchedEffect(estado) {
        if (estado is LoginState.Success) onLoginExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Logo y nombre de la app
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoRatcher()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Ratcher",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        EtiquetaSeccion("Levantamiento · CMC360")
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Accede para registrar evidencia y revisar los levantamientos de infraestructura.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))

        CampoTexto(
            valor = viewModel.email,
            onCambio = { viewModel.cambiarEmail(it) },
            etiqueta = "Correo electrónico",
            placeholder = "nombre@correo.cl",
            icono = Icons.Default.Email,
            error = viewModel.emailError,
            teclado = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            valor = viewModel.clave,
            onCambio = { viewModel.cambiarClave(it) },
            etiqueta = "Contraseña",
            placeholder = "Ingresa tu contraseña",
            icono = Icons.Default.Lock,
            error = viewModel.claveError,
            esClave = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Presenta un indicador de carga o el botón principal para ingresar (con animación de temblor).
        if (estado is LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            BotonPrincipal(
                texto = "Iniciar sesión",
                onClick = { viewModel.iniciarSesion() },
                modifier = Modifier.offset(x = sacudida)
            )
        }

        // Contenedor con animación de visibilidad para mostrar el texto dinámico de error.
        AnimatedVisibility(visible = estado is LoginState.Error) {
            Text(
                text = if (estado is LoginState.Error) estado.error else "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = onIrARegistro,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

        //Spacer(modifier = Modifier.height(16.dp))
        //Text(
        //   text = "Usuario de prueba: test@test.com / 123456",
        //   style = MaterialTheme.typography.bodySmall,
        //  color = MaterialTheme.colorScheme.secondary,
        //  modifier = Modifier.align(Alignment.CenterHorizontally)
        //)
    }
}
