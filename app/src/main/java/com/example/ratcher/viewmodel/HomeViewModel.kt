package com.example.ratcher.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.repository.LevantamientoRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repositorio = LevantamientoRepository()

    var lista by mutableStateOf<List<Levantamiento>>(emptyList())
        private set
    var cargando by mutableStateOf(true)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun cargar() {
        viewModelScope.launch {
            try {
                lista = repositorio.listar()
            } catch (e: Exception) {
                mensajeError = "No se pudo cargar la lista. Revisa tu conexión."
            } finally {
                cargando = false
            }
        }
    }

    fun limpiarError() {
        mensajeError = null
    }
}
