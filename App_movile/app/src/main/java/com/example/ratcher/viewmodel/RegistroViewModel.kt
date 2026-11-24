package com.example.ratcher.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.repository.UsuarioRepository
import com.example.ratcher.util.Validaciones
import kotlinx.coroutines.launch

class RegistroViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = UsuarioRepository(app)

    var nombre by mutableStateOf("")
        private set
    var nombreError by mutableStateOf<String?>(null)
        private set

    var correo by mutableStateOf("")
        private set
    var correoError by mutableStateOf<String?>(null)
        private set

    var cargo by mutableStateOf("")
        private set
    var cargoError by mutableStateOf<String?>(null)
        private set

    var clave by mutableStateOf("")
        private set
    var claveError by mutableStateOf<String?>(null)
        private set

    var confirmacion by mutableStateOf("")
        private set
    var confirmacionError by mutableStateOf<String?>(null)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set
    var registrado by mutableStateOf(false)
        private set
    var guardando by mutableStateOf(false)
        private set
    var fallos by mutableIntStateOf(0)
        private set

    fun cambiarNombre(valor: String) {
        nombre = valor
        nombreError = Validaciones.nombre(valor)
    }

    fun cambiarCorreo(valor: String) {
        correo = valor
        correoError = Validaciones.correo(valor)
    }

    fun cambiarCargo(valor: String) {
        cargo = valor
        cargoError = Validaciones.cargo(valor)
    }

    fun cambiarClave(valor: String) {
        clave = valor
        claveError = Validaciones.claveRegistro(valor)
        // Si ya escribió la confirmación, se vuelve a comprobar
        if (confirmacion.isNotEmpty()) {
            confirmacionError = Validaciones.confirmacionClave(valor, confirmacion)
        }
    }

    fun cambiarConfirmacion(valor: String) {
        confirmacion = valor
        confirmacionError = Validaciones.confirmacionClave(clave, valor)
    }

    fun registrar() {
        nombreError = Validaciones.nombre(nombre)
        correoError = Validaciones.correo(correo)
        cargoError = Validaciones.cargo(cargo)
        claveError = Validaciones.claveRegistro(clave)
        confirmacionError = Validaciones.confirmacionClave(clave, confirmacion)
        mensajeError = null

        val hayErrores = listOf(nombreError, correoError, cargoError, claveError, confirmacionError)
            .any { it != null }
        if (hayErrores) {
            fallos++
            return
        }

        viewModelScope.launch {
            guardando = true
            val exito = repositorio.registrar(nombre, correo, cargo, clave)
            guardando = false
            if (exito) {
                registrado = true
            } else {
                mensajeError = "Ese correo ya está registrado"
                fallos++
            }
        }
    }
}
