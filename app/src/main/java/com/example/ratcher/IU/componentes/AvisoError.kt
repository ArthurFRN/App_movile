package com.example.ratcher.IU.componentes

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

// Muestra un mensaje de error en una barra inferior (Snackbar) cada vez que el ViewModel tiene uno.
// Se usa con Scaffold(snackbarHost = { SnackbarHost(estado) })
@Composable
fun rememberAvisoError(mensaje: String?, onMostrado: () -> Unit): SnackbarHostState {
    val estadoSnackbar = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            estadoSnackbar.showSnackbar(mensaje)
            onMostrado()
        }
    }
    return estadoSnackbar
}
