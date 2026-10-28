package com.example.ratcher.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.repository.UsuarioRepository
import com.example.ratcher.util.Sesion
import com.example.ratcher.util.Validaciones
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = UsuarioRepository(app)

    var email by mutableStateOf("")
        private set
    var emailError by mutableStateOf<String?>(null)
        private set

    var clave by mutableStateOf("")
        private set
    var claveError by mutableStateOf<String?>(null)
        private set

    var estado by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    // Sube cada vez que hay un error; la pantalla lo usa para sacudir el botón y vibrar
    var fallos by mutableIntStateOf(0)
        private set

    fun cambiarEmail(valor: String) {
        email = valor
        emailError = Validaciones.correo(valor)
    }

    fun cambiarClave(valor: String) {
        clave = valor
        claveError = Validaciones.claveLogin(valor)
    }

    fun iniciarSesion() {
        emailError = Validaciones.correo(email)
        claveError = Validaciones.claveLogin(clave)

        if (emailError != null || claveError != null) {
            estado = LoginState.Error("Revisa los campos marcados en rojo")
            fallos++
            return
        }

        viewModelScope.launch {
            estado = LoginState.Loading
            val usuario = repositorio.login(email.trim(), clave)
            if (usuario != null) {
                Sesion.usuario = usuario
                estado = LoginState.Success("Bienvenido " + usuario.nombre)
            } else {
                estado = LoginState.Error("Correo o contraseña incorrectos")
                fallos++
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}
