package com.example.ratcher.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.repository.LevantamientoRepository
import com.example.ratcher.repository.ReconocedorSimulado
import com.example.ratcher.util.Archivos
import com.example.ratcher.util.Fechas
import com.example.ratcher.util.Sesion
import com.example.ratcher.util.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class NuevoLevantamientoViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = LevantamientoRepository(app)

    var rackId by mutableStateOf("")
        private set
    var rackError by mutableStateOf<String?>(null)
        private set

    var area by mutableStateOf("")
        private set
    var areaError by mutableStateOf<String?>(null)
        private set

    var fecha by mutableStateOf(Fechas.hoy())
        private set
    var fechaError by mutableStateOf<String?>(null)
        private set

    var observaciones by mutableStateOf("")
        private set
    var observacionesError by mutableStateOf<String?>(null)
        private set

    // Ruta de la foto de evidencia ("" si todavía no hay)
    var rutaImagen by mutableStateOf("")
        private set
    var errorImagen by mutableStateOf<String?>(null)
        private set

    var analizando by mutableStateOf(false)
        private set
    var idCreado by mutableStateOf<Long?>(null)
        private set
    var fallos by mutableIntStateOf(0)
        private set

    // Ruta donde la cámara va a guardar la foto
    private var rutaTemporal = ""

    fun cambiarRack(valor: String) {
        rackId = valor
        rackError = Validaciones.rackId(valor)
    }

    fun cambiarArea(valor: String) {
        area = valor
        areaError = Validaciones.area(valor)
    }

    fun cambiarFecha(valor: String) {
        fecha = valor
        fechaError = Validaciones.fecha(valor)
    }

    fun cambiarObservaciones(valor: String) {
        observaciones = valor
        observacionesError = Validaciones.observaciones(valor)
    }

    // ---------- Evidencia: cámara y galería ----------

    // Prepara el archivo donde la cámara guardará la foto y entrega su Uri
    fun prepararFotoCamara(context: Context): Uri {
        val archivo = Archivos.crearArchivoImagen(context)
        rutaTemporal = archivo.absolutePath
        return Archivos.uriDeArchivo(context, archivo)
    }

    fun resultadoCamara(exito: Boolean) {
        if (exito) {
            rutaImagen = rutaTemporal
            errorImagen = null
        } else {
            File(rutaTemporal).delete()
        }
    }

    fun guardarDesdeGaleria(context: Context, uri: Uri) {
        val ruta = Archivos.copiarDesdeGaleria(context, uri)
        if (ruta != null) {
            rutaImagen = ruta
            errorImagen = null
        } else {
            errorImagen = "No se pudo leer la imagen seleccionada"
        }
    }

    fun permisoCamaraDenegado() {
        errorImagen = "Sin permiso de cámara. Puedes elegir una imagen de la galería."
    }

    // ---------- Guardar ----------

    fun guardar() {
        rackError = Validaciones.rackId(rackId)
        areaError = Validaciones.area(area)
        fechaError = Validaciones.fecha(fecha)
        observacionesError = Validaciones.observaciones(observaciones)
        errorImagen = if (rutaImagen.isEmpty()) "Debes tomar una foto o elegir una imagen" else null

        val hayErrores = listOf(rackError, areaError, fechaError, observacionesError, errorImagen)
            .any { it != null }
        if (hayErrores) {
            fallos++
            return
        }

        viewModelScope.launch {
            analizando = true
            val usuario = Sesion.nombre()

            val id = repositorio.crear(
                Levantamiento(
                    rackId = rackId,
                    area = area,
                    fecha = fecha,
                    observaciones = observaciones,
                    rutaImagen = rutaImagen,
                    responsable = usuario
                )
            )
            repositorio.agregarHistorial(
                Historial(
                    levantamientoId = id,
                    accion = "Levantamiento creado",
                    detalle = "Evidencia asociada al rack " + rackId.trim(),
                    fecha = Fechas.ahora(),
                    usuario = usuario
                )
            )

            delay(2000) // pausa para que se vea la animación de análisis
            val componentes = ReconocedorSimulado.analizar(id, rutaImagen)
            repositorio.guardarComponentes(componentes)
            repositorio.agregarHistorial(
                Historial(
                    levantamientoId = id,
                    accion = "Reconocimiento automático",
                    detalle = componentes.size.toString() + " componentes detectados en la imagen",
                    fecha = Fechas.ahora(),
                    usuario = "Sistema"
                )
            )

            analizando = false
            idCreado = id
        }
    }
}
