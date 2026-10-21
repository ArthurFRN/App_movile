package com.example.ratcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.model.Usuario
import com.example.ratcher.repository.Conexion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = Conexion()

    var email = MutableStateFlow("")
    var password = MutableStateFlow("")

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onLoginClick() {

        viewModelScope.launch {
            _uiState.value = LoginState.Loading

            val user = repository.login(email.value, password.value)

            if (user != null) {
                _uiState.value = LoginState.Success("Bienvenido")
            } else {
                _uiState.value = LoginState.Error("Credenciales incorrectas")
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