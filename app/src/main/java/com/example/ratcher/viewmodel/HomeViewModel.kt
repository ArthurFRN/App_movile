package com.example.ratcher.viewmodel

<<<<<<< HEAD
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
=======
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
import androidx.lifecycle.viewModelScope
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.repository.LevantamientoRepository
import kotlinx.coroutines.launch

<<<<<<< HEAD
class HomeViewModel : ViewModel() {

    private val repositorio = LevantamientoRepository()
=======
class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = LevantamientoRepository(app)
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

    var lista by mutableStateOf<List<Levantamiento>>(emptyList())
        private set
    var cargando by mutableStateOf(true)
        private set
<<<<<<< HEAD
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
=======

    fun cargar() {
        viewModelScope.launch {
            lista = repositorio.listar()
            cargando = false
        }
    }
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
}
