package com.example.ratcher.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.model.Componente
import com.example.ratcher.model.Estados
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.repository.LevantamientoRepository
import com.example.ratcher.util.Fechas
import com.example.ratcher.util.Sesion
import com.example.ratcher.util.Validaciones
import kotlinx.coroutines.launch
import java.io.File

class DetalleViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = LevantamientoRepository(app)

    var levantamiento by mutableStateOf<Levantamiento?>(null)
        private set
    var componentes by mutableStateOf<List<Componente>>(emptyList())
        private set
    var historial by mutableStateOf<List<Historial>>(emptyList())
        private set

    // Datos del formulario "Corregir componente"
    var editando by mutableStateOf<Componente?>(null)
        private set
    var categoriaEdicion by mutableStateOf("")
        private set
    var descripcionEdicion by mutableStateOf("")
        private set
    var descripcionError by mutableStateOf<String?>(null)
        private set

    var fallos by mutableIntStateOf(0)
        private set
    var eliminado by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    private var idActual = 0L

    // Se puede validar el levantamiento cuando no queda ningún componente sin revisar
    val puedeValidar: Boolean
        get() = componentes.none { it.estado == Estados.RECONOCIDO }

    val esEditable: Boolean
        get() = levantamiento?.estado != Estados.VALIDADO

    fun cargar(id: Long) {
        idActual = id
        recargar()
    }

    fun recargar() {
        viewModelScope.launch {
            try {
                error = null
                levantamiento = repositorio.obtener(idActual)
                if (levantamiento == null) error = "No se encontró el levantamiento."
                componentes = repositorio.componentes(idActual)
                historial = repositorio.historial(idActual)
            } catch (e: Exception) {
                error = "No se pudo cargar el levantamiento. Verifica tu conexión e inténtalo de nuevo."
                levantamiento = null
                componentes = emptyList()
                historial = emptyList()
            }
        }
    }

    fun reintentar() {
        recargar()
    }

    private suspend fun registrar(accion: String, detalle: String) {
        val h = Historial(
            levantamientoId = idActual,
            accion = accion,
            detalle = detalle,
            fecha = Fechas.ahora(),
            usuario = Sesion.nombre()
        )
        repositorio.agregarHistorial(h)
        historial = historial + h
    }

    // Actualiza la pantalla sin volver a pedir todo a Xano (evita pasarse del límite de requests)
    private fun marcar(id: Long, estado: String, categoria: String? = null, descripcion: String? = null) {
        componentes = componentes.map {
            if (it.id == id) it.copy(
                estado = estado,
                categoria = categoria ?: it.categoria,
                descripcion = descripcion ?: it.descripcion
            ) else it
        }
    }

    // ---------- Acciones sobre un componente ----------

    fun confirmar(c: Componente) {
        viewModelScope.launch {
            try {
                repositorio.actualizarComponente(c.id, c.categoria, c.descripcion, Estados.CONFIRMADO)
                registrar("Componente confirmado", c.categoria)
                marcar(c.id, Estados.CONFIRMADO)
            } catch (e: Exception) {
                fallos++
            }
        }
    }

    fun rechazar(c: Componente) {
        viewModelScope.launch {
            try {
                repositorio.actualizarComponente(c.id, c.categoria, c.descripcion, Estados.RECHAZADO)
                registrar("Componente rechazado", c.categoria)
                marcar(c.id, Estados.RECHAZADO)
            } catch (e: Exception) {
                fallos++
            }
        }
    }

    // ---------- Formulario de corrección ----------

    fun abrirCorreccion(c: Componente) {
        editando = c
        categoriaEdicion = c.categoria
        descripcionEdicion = c.descripcion
        descripcionError = null
    }

    fun cerrarCorreccion() {
        editando = null
    }

    fun cambiarCategoria(valor: String) {
        categoriaEdicion = valor
    }

    fun cambiarDescripcion(valor: String) {
        descripcionEdicion = valor
        descripcionError = Validaciones.descripcion(valor)
    }

    fun guardarCorreccion() {
        val componente = editando ?: return

        descripcionError = Validaciones.descripcion(descripcionEdicion)
        val categoriaError = Validaciones.categoria(categoriaEdicion)
        if (descripcionError != null || categoriaError != null) {
            fallos++
            return
        }

        viewModelScope.launch {
            try {
                repositorio.actualizarComponente(
                    componente.id,
                    categoriaEdicion,
                    descripcionEdicion,
                    Estados.CORREGIDO
                )
                registrar(
                    "Componente corregido",
                    componente.categoria + " -> " + categoriaEdicion
                )
                marcar(componente.id, Estados.CORREGIDO, categoriaEdicion, descripcionEdicion)
                editando = null
            } catch (e: Exception) {
                fallos++
            }
        }
    }

    // ---------- Validar y eliminar ----------

    fun validar() {
        if (!puedeValidar) {
            mensajeError = "No se puede validar: hay componentes pendientes de revisar."
            fallos++
            return
        }
        viewModelScope.launch {
            try {
                cargando = true
                mensajeError = null
                repositorio.actualizarEstado(idActual, Estados.VALIDADO)
                registrar("Levantamiento validado", "Todos los componentes fueron revisados")
                levantamiento = levantamiento?.copy(estado = Estados.VALIDADO)
            } catch (e: Exception) {
                mensajeError = "No se pudo validar el levantamiento. Verifica tu conexión e inténtalo de nuevo."
                fallos++
            } finally {
                cargando = false
            }
        }
    }

    fun eliminar() {
        val ruta = levantamiento?.rutaImagen
        viewModelScope.launch {
            try {
                repositorio.eliminar(idActual)
                if (ruta != null) {
                    File(ruta).delete()
                }
                eliminado = true
            } catch (e: Exception) {
                fallos++
            }
        }
    }
}