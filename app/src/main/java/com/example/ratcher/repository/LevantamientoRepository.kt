package com.example.ratcher.repository

<<<<<<< HEAD
import com.example.ratcher.data.ApiClient
import com.example.ratcher.data.ComponenteEdicionDto
import com.example.ratcher.data.EstadoDto
import com.example.ratcher.data.RatcherApi
import com.example.ratcher.data.aComponente
import com.example.ratcher.data.aDto
import com.example.ratcher.data.aHistorial
import com.example.ratcher.data.aLevantamiento
import com.example.ratcher.model.Componente
import com.example.ratcher.model.Estados
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import retrofit2.Response
import java.io.IOException

// Intermediario entre los ViewModels y la API de Xano (levantamientos, componentes e historial)
class LevantamientoRepository(private val api: RatcherApi = ApiClient.datos) {

    // Devuelve el cuerpo de la respuesta o lanza error si el servidor respondió con fallo
    private fun <T> Response<T>.cuerpo(): T {
        val contenido = body()
        if (!isSuccessful || contenido == null) {
            throw IOException("Error del servidor (código " + code() + ")")
        }
        return contenido
    }

    // Crea el levantamiento y devuelve el id que le asignó Xano
    suspend fun crear(l: Levantamiento): Long {
        val creado = api.crearLevantamiento(l.aDto()).cuerpo()
        return creado.id ?: throw IOException("Xano no devolvió el id del levantamiento")
    }

    // Lista los levantamientos (más nuevos primero) con el total de componentes y los que faltan por revisar
    suspend fun listar(): List<Levantamiento> {
        val levantamientos = api.listarLevantamientos().cuerpo()
        val componentesPorLevantamiento = api.listarComponentes(null).cuerpo().groupBy { it.levantamientoId }

        return levantamientos.map { dto ->
            val suyos = componentesPorLevantamiento[dto.id].orEmpty()
            dto.aLevantamiento(
                totalComponentes = suyos.size,
                pendientes = suyos.count { it.estado == Estados.RECONOCIDO }
            )
        }.sortedByDescending { it.id }
    }

    suspend fun obtener(id: Long): Levantamiento? {
        val respuesta = api.obtenerLevantamiento(id)
        if (respuesta.code() == 404) return null
        return respuesta.cuerpo().aLevantamiento()
    }

    suspend fun actualizarEstado(id: Long, estado: String) {
        api.actualizarEstado(id, EstadoDto(estado))
    }

    // En Xano, este endpoint también borra los componentes y el historial del levantamiento
    suspend fun eliminar(id: Long) {
        api.eliminarLevantamiento(id)
    }

    // Xano recibe un componente por petición
    suspend fun guardarComponentes(lista: List<Componente>) {
        for (c in lista) {
            api.crearComponente(c.aDto()).cuerpo()
        }
    }

    suspend fun componentes(levantamientoId: Long): List<Componente> {
        return api.listarComponentes(levantamientoId).cuerpo()
            .filter { it.levantamientoId == levantamientoId }
            .sortedBy { it.id }
            .map { it.aComponente() }
    }

    suspend fun actualizarComponente(id: Long, categoria: String, descripcion: String, estado: String) {
        api.actualizarComponente(id, ComponenteEdicionDto(categoria, descripcion.trim(), estado))
    }

    // Registra una nueva acción en el historial de trazabilidad
    suspend fun agregarHistorial(h: Historial) {
        api.crearHistorial(h.aDto()).cuerpo()
    }

    suspend fun historial(levantamientoId: Long): List<Historial> {
        return api.listarHistorial(levantamientoId).cuerpo()
            .filter { it.levantamientoId == levantamientoId }
            .sortedBy { it.id }
            .map { it.aHistorial() }
=======
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.ratcher.data.BaseDatos
import com.example.ratcher.model.Componente
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Intermediario entre los ViewModels y las tablas de levantamientos, componentes e historial
class LevantamientoRepository(context: Context) {

    private val baseDatos = BaseDatos.obtener(context)

    private val columnasLevantamiento =
        "id, rack_id, area, fecha, observaciones, ruta_imagen, responsable, estado"

    // Realiza la inserción del levantamiento en el hilo de E/S ([Dispatchers.IO]),
    // mapeando las propiedades a ContentValues con limpieza básica de cadenas.

    suspend fun crear(l: Levantamiento): Long = withContext(Dispatchers.IO) {
        val valores = ContentValues()
        valores.put("rack_id", l.rackId.trim())
        valores.put("area", l.area.trim())
        valores.put("fecha", l.fecha.trim())
        valores.put("observaciones", l.observaciones.trim())
        valores.put("ruta_imagen", l.rutaImagen)
        valores.put("responsable", l.responsable)
        valores.put("estado", l.estado)
        baseDatos.writableDatabase.insert("levantamientos", null, valores)
    }

    // Realiza una consulta en el hilo de E/S ([Dispatchers.IO]) para obtener los registros organizados por ID,
    // incluyendo contadores acumulados por cada elevación
    suspend fun listar(): List<Levantamiento> = withContext(Dispatchers.IO) {
        val resultado = mutableListOf<Levantamiento>()
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT $columnasLevantamiento, " +
                "(SELECT COUNT(*) FROM componentes WHERE levantamiento_id = levantamientos.id), " +
                "(SELECT COUNT(*) FROM componentes WHERE levantamiento_id = levantamientos.id " +
                "AND estado = 'Reconocido') " +
                "FROM levantamientos ORDER BY id DESC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                resultado.add(leerLevantamiento(it))
            }
        }
        resultado
    }

    suspend fun obtener(id: Long): Levantamiento? = withContext(Dispatchers.IO) {
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT $columnasLevantamiento FROM levantamientos WHERE id = ?",
            arrayOf(id.toString())
        )
        cursor.use {
            if (it.moveToFirst()) leerLevantamiento(it) else null
        }
    }

    suspend fun actualizarEstado(id: Long, estado: String) = withContext(Dispatchers.IO) {
        val valores = ContentValues()
        valores.put("estado", estado)
        baseDatos.writableDatabase.update("levantamientos", valores, "id = ?", arrayOf(id.toString()))
    }

    // Al borrar el levantamiento se borran solos sus componentes e historial (ON DELETE CASCADE)
    suspend fun eliminar(id: Long) = withContext(Dispatchers.IO) {
        baseDatos.writableDatabase.delete("levantamientos", "id = ?", arrayOf(id.toString()))
    }

    // Agrega varios elementos en una sola transacción de base de datos para mejorar el rendimiento y garantizar la integridad de los datos.

    suspend fun guardarComponentes(lista: List<Componente>) = withContext(Dispatchers.IO) {
        val db = baseDatos.writableDatabase
        db.beginTransaction()
        try {
            for (c in lista) {
                val valores = ContentValues()
                valores.put("levantamiento_id", c.levantamientoId)
                valores.put("categoria", c.categoria)
                valores.put("descripcion", c.descripcion)
                valores.put("confianza", c.confianza)
                valores.put("estado", c.estado)
                valores.put("categoria_original", c.categoriaOriginal)
                db.insert("componentes", null, valores)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    suspend fun componentes(levantamientoId: Long): List<Componente> = withContext(Dispatchers.IO) {
        val resultado = mutableListOf<Componente>()
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT id, levantamiento_id, categoria, descripcion, confianza, estado, categoria_original " +
                "FROM componentes WHERE levantamiento_id = ? ORDER BY id",
            arrayOf(levantamientoId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                resultado.add(
                    Componente(
                        id = it.getLong(0),
                        levantamientoId = it.getLong(1),
                        categoria = it.getString(2),
                        descripcion = it.getString(3),
                        confianza = it.getInt(4),
                        estado = it.getString(5),
                        categoriaOriginal = it.getString(6)
                    )
                )
            }
        }
        resultado
    }

    suspend fun actualizarComponente(id: Long, categoria: String, descripcion: String, estado: String) =
        withContext(Dispatchers.IO) {
            val valores = ContentValues()
            valores.put("categoria", categoria)
            valores.put("descripcion", descripcion.trim())
            valores.put("estado", estado)
            baseDatos.writableDatabase.update("componentes", valores, "id = ?", arrayOf(id.toString()))
        }

    // Registra una nueva acción en el registro de trazabilidad de manera asíncrona.

    suspend fun agregarHistorial(h: Historial) = withContext(Dispatchers.IO) {
        val valores = ContentValues()
        valores.put("levantamiento_id", h.levantamientoId)
        valores.put("accion", h.accion)
        valores.put("detalle", h.detalle)
        valores.put("fecha", h.fecha)
        valores.put("usuario", h.usuario)
        baseDatos.writableDatabase.insert("historial", null, valores)
    }

    suspend fun historial(levantamientoId: Long): List<Historial> = withContext(Dispatchers.IO) {
        val resultado = mutableListOf<Historial>()
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT id, levantamiento_id, accion, detalle, fecha, usuario " +
                "FROM historial WHERE levantamiento_id = ? ORDER BY id",
            arrayOf(levantamientoId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                resultado.add(
                    Historial(
                        id = it.getLong(0),
                        levantamientoId = it.getLong(1),
                        accion = it.getString(2),
                        detalle = it.getString(3),
                        fecha = it.getString(4),
                        usuario = it.getString(5)
                    )
                )
            }
        }
        resultado
    }

    //Función de apoyo que descompone los datos de un Cursor SQL en el modelo de dominio Levantamiento.

    private fun leerLevantamiento(c: Cursor): Levantamiento {
        return Levantamiento(
            id = c.getLong(0),
            rackId = c.getString(1),
            area = c.getString(2),
            fecha = c.getString(3),
            observaciones = c.getString(4),
            rutaImagen = c.getString(5),
            responsable = c.getString(6),
            estado = c.getString(7),
            totalComponentes = if (c.columnCount > 8) c.getInt(8) else 0,
            pendientes = if (c.columnCount > 9) c.getInt(9) else 0
        )
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
    }
}
