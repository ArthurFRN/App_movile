package com.example.ratcher.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.repository.LevantamientoRepository
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = LevantamientoRepository(app)

    var lista by mutableStateOf<List<Levantamiento>>(emptyList())
        private set
    var cargando by mutableStateOf(true)
        private set

    fun cargar() {
        viewModelScope.launch {
            lista = repositorio.listar()
            cargando = false
        }
    }
}
