package com.example.ratcher.viewmodel

<<<<<<< HEAD
=======
import android.app.Application
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
<<<<<<< HEAD
import androidx.lifecycle.ViewModel
=======
import androidx.lifecycle.AndroidViewModel
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
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

<<<<<<< HEAD
class DetalleViewModel : ViewModel() {

    private val repositorio = LevantamientoRepository()
=======
class DetalleViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = LevantamientoRepository(app)
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

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
<<<<<<< HEAD
    var cargando by mutableStateOf(true)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    // Pasa a true cuando el levantamiento acaba de validarse (la pantalla lanza la notificación)
    var recienValidado by mutableStateOf(false)
        private set
=======
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

    private var idActual = 0L

    // Se puede validar el levantamiento cuando no queda ningún componente sin revisar
    val puedeValidar: Boolean
        get() = componentes.none { it.estado == Estados.RECONOCIDO }

    val esEditable: Boolean
        get() = levantamiento?.estado != Estados.VALIDADO

    fun cargar(id: Long) {
        idActual = id
<<<<<<< HEAD
        ejecutar { refrescar() }
    }

    // Ejecuta una acción contra Xano; si falla la conexión se avisa en vez de cerrar la app
    private fun ejecutar(accion: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                accion()
            } catch (e: Exception) {
                mensajeError = "No se pudo completar la operación. Revisa tu conexión."
            } finally {
                cargando = false
            }
        }
    }

    private suspend fun refrescar() {
        levantamiento = repositorio.obtener(idActual)
        componentes = repositorio.componentes(idActual)
        historial = repositorio.historial(idActual)
    }

=======
        recargar()
    }

    private fun recargar() {
        viewModelScope.launch {
            levantamiento = repositorio.obtener(idActual)
            componentes = repositorio.componentes(idActual)
            historial = repositorio.historial(idActual)
        }
    }

>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
    private suspend fun registrar(accion: String, detalle: String) {
        repositorio.agregarHistorial(
            Historial(
                levantamientoId = idActual,
                accion = accion,
                detalle = detalle,
                fecha = Fechas.ahora(),
                usuario = Sesion.nombre()
            )
        )
    }

    // ---------- Acciones sobre un componente ----------

    fun confirmar(c: Componente) {
<<<<<<< HEAD
        ejecutar {
            repositorio.actualizarComponente(c.id, c.categoria, c.descripcion, Estados.CONFIRMADO)
            registrar("Componente confirmado", c.categoria)
            refrescar()
=======
        viewModelScope.launch {
            repositorio.actualizarComponente(c.id, c.categoria, c.descripcion, Estados.CONFIRMADO)
            registrar("Componente confirmado", c.categoria)
            recargar()
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
        }
    }

    fun rechazar(c: Componente) {
<<<<<<< HEAD
        ejecutar {
            repositorio.actualizarComponente(c.id, c.categoria, c.descripcion, Estados.RECHAZADO)
            registrar("Componente rechazado", c.categoria)
            refrescar()
=======
        viewModelScope.launch {
            repositorio.actualizarComponente(c.id, c.categoria, c.descripcion, Estados.RECHAZADO)
            registrar("Componente rechazado", c.categoria)
            recargar()
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
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

<<<<<<< HEAD
        ejecutar {
=======
        viewModelScope.launch {
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
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
            editando = null
<<<<<<< HEAD
            refrescar()
=======
            recargar()
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
        }
    }

    // ---------- Validar y eliminar ----------

    fun validar() {
        if (!puedeValidar) {
            fallos++
            return
        }
<<<<<<< HEAD
        ejecutar {
            repositorio.actualizarEstado(idActual, Estados.VALIDADO)
            registrar("Levantamiento validado", "Todos los componentes fueron revisados")
            refrescar()
            recienValidado = true
        }
    }

    fun notificacionEntregada() {
        recienValidado = false
    }

    fun eliminar() {
        val ruta = levantamiento?.rutaImagen
        ejecutar {
            repositorio.eliminar(idActual)
            if (!ruta.isNullOrEmpty()) {
=======
        viewModelScope.launch {
            repositorio.actualizarEstado(idActual, Estados.VALIDADO)
            registrar("Levantamiento validado", "Todos los componentes fueron revisados")
            recargar()
        }
    }

    fun eliminar() {
        val ruta = levantamiento?.rutaImagen
        viewModelScope.launch {
            repositorio.eliminar(idActual)
            if (ruta != null) {
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
                File(ruta).delete()
            }
            eliminado = true
        }
    }
<<<<<<< HEAD

    fun limpiarError() {
        mensajeError = null
    }
=======
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
}
