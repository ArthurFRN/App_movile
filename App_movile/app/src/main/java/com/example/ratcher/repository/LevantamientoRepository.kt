package com.example.ratcher.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.example.ratcher.data.BaseDatos
import com.example.ratcher.model.Componente
import com.example.ratcher.model.Estados
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LevantamientoRepository(context: Context) {

    private val baseDatos = BaseDatos.obtener(context)
    private val api = RetrofitInstance.api

    private val columnasLevantamiento =
        "id, rack_id, area, fecha, observaciones, ruta_imagen, responsable, estado"

    // ---------- Listar ----------
    suspend fun listar(): List<Levantamiento> = withContext(Dispatchers.IO) {
        return@withContext try {
            api.getRacks()
        } catch (e: Exception) {
            Log.e("Ratcher", "GET levantamiento (lista) fallo", e)
            listarLocal()
        }
    }

    // ---------- Obtener uno ----------
    suspend fun obtener(id: Long): Levantamiento? = withContext(Dispatchers.IO) {
        return@withContext try {
            val respuesta = api.getRack(id)
            if (!respuesta.isSuccessful || respuesta.body() == null) {
                Log.e("Ratcher", "GET levantamiento/" + id + " -> " + respuesta.code() + " " + respuesta.errorBody()?.string())
            }
            respuesta.body() ?: obtenerLocal(id)
        } catch (e: Exception) {
            Log.e("Ratcher", "GET levantamiento/" + id + " lanzo excepcion", e)
            obtenerLocal(id)
        }
    }

    // ---------- Crear ----------
    suspend fun crear(l: Levantamiento): Long = withContext(Dispatchers.IO) {
        return@withContext try {
            val creado = api.createRack(l)
            if (creado.id > 0) creado.id else crearLocal(l)
        } catch (e: Exception) {
            crearLocal(l)
        }
    }

    // ---------- Actualizar estado ----------
    suspend fun actualizarEstado(id: Long, estado: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            api.updateRackEstado(id, mapOf("estado" to estado))
        } catch (e: Exception) {
            Log.e("Ratcher", "PATCH levantamiento/" + id + " fallo", e)
            actualizarEstadoLocal(id, estado)
        }
    }

    // ---------- Eliminar ----------
    suspend fun eliminar(id: Long) = withContext(Dispatchers.IO) {
        return@withContext try {
            api.deleteRack(id)
        } catch (e: Exception) {
            eliminarLocal(id)
        }
    }

    // ---------- Componentes ----------
    // Guarda cada componente en Xano (uno por request). Si uno falla, ese queda en SQLite.
    suspend fun guardarComponentes(lista: List<Componente>) = withContext(Dispatchers.IO) {
        for (c in lista) {
            try {
                api.createComponente(c)
            } catch (e: Exception) {
                Log.e("Ratcher", "POST componente fallo", e)
                guardarComponentesLocal(listOf(c))
            }
        }
    }

    suspend fun componentes(levantamientoId: Long): List<Componente> = withContext(Dispatchers.IO) {
        return@withContext try {
            val remotos = api.getComponentes(levantamientoId)
                .filter { it.levantamientoId == levantamientoId }
            // Si Xano no tiene nada, mostrar los que quedaron guardados en el teléfono
            if (remotos.isEmpty()) return@withContext componentesLocal(levantamientoId)
            remotos
                .map { c ->
                    Componente(
                        id = c.id,
                        levantamientoId = c.levantamientoId ?: levantamientoId,
                        categoria = c.categoria ?: "Sin categoría",
                        descripcion = c.descripcion ?: "Sin descripción",
                        confianza = c.confianza ?: 0,
                        estado = c.estado ?: Estados.RECONOCIDO,
                        categoriaOriginal = c.categoriaOriginal ?: c.categoria ?: "Sin categoría"
                    )
                }
        } catch (e: Exception) {
            componentesLocal(levantamientoId)
        }
    }

    suspend fun actualizarComponente(id: Long, categoria: String, descripcion: String, estado: String) =
        withContext(Dispatchers.IO) {
            return@withContext try {
                api.patchComponente(id, mapOf(
                    "categoria" to categoria,
                    "descripcion" to descripcion,
                    "estado" to estado
                ))
            } catch (e: Exception) {
                actualizarComponenteLocal(id, categoria, descripcion, estado)
            }
        }

    // ---------- Historial ----------
    suspend fun agregarHistorial(h: Historial) = withContext(Dispatchers.IO) {
        return@withContext try {
            api.createHistorial(h)
        } catch (e: Exception) {
            agregarHistorialLocal(h)
        }
    }

    suspend fun historial(levantamientoId: Long): List<Historial> = withContext(Dispatchers.IO) {
        return@withContext try {
            api.getHistorial(levantamientoId)
        } catch (e: Exception) {
            historialLocal(levantamientoId)
        }
    }

    // ---------- Subir imagen a Xano ----------
    suspend fun subirImagen(rutaArchivo: String): String = withContext(Dispatchers.IO) {
        val archivo = java.io.File(rutaArchivo)
        val requestFile = archivo.asRequestBody("image/*".toMediaTypeOrNull())
        val cuerpo = MultipartBody.Part.createFormData("file", archivo.name, requestFile)
        val respuesta = api.uploadImagen(cuerpo)
        val ruta = respuesta.url ?: respuesta.path ?: throw Exception("Respuesta sin ruta de imagen")
        if (ruta.startsWith("http")) ruta else "https://x8ki-letl-twmt.n7.xano.io" + ruta
    }

    // ==================== FALLBACK SQLite local ====================

    private suspend fun listarLocal(): List<Levantamiento> = withContext(Dispatchers.IO) {
        val resultado = mutableListOf<Levantamiento>()
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT $columnasLevantamiento FROM levantamientos ORDER BY id DESC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                resultado.add(leerLevantamientoLocal(it))
            }
        }
        resultado
    }

    private suspend fun obtenerLocal(id: Long): Levantamiento? = withContext(Dispatchers.IO) {
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT $columnasLevantamiento FROM levantamientos WHERE id = ?",
            arrayOf(id.toString())
        )
        cursor.use {
            if (it.moveToFirst()) leerLevantamientoLocal(it) else null
        }
    }

    private suspend fun crearLocal(l: Levantamiento): Long = withContext(Dispatchers.IO) {
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

    private suspend fun actualizarEstadoLocal(id: Long, estado: String) = withContext(Dispatchers.IO) {
        val valores = ContentValues()
        valores.put("estado", estado)
        baseDatos.writableDatabase.update("levantamientos", valores, "id = ?", arrayOf(id.toString()))
    }

    private suspend fun eliminarLocal(id: Long) = withContext(Dispatchers.IO) {
        baseDatos.writableDatabase.delete("levantamientos", "id = ?", arrayOf(id.toString()))
    }

    private suspend fun guardarComponentesLocal(lista: List<Componente>) = withContext(Dispatchers.IO) {
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

    private suspend fun componentesLocal(levantamientoId: Long): List<Componente> = withContext(Dispatchers.IO) {
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

    private suspend fun actualizarComponenteLocal(id: Long, categoria: String, descripcion: String, estado: String) =
        withContext(Dispatchers.IO) {
            val valores = ContentValues()
            valores.put("categoria", categoria)
            valores.put("descripcion", descripcion.trim())
            valores.put("estado", estado)
            baseDatos.writableDatabase.update("componentes", valores, "id = ?", arrayOf(id.toString()))
        }

    private suspend fun agregarHistorialLocal(h: Historial) = withContext(Dispatchers.IO) {
        val valores = ContentValues()
        valores.put("levantamiento_id", h.levantamientoId)
        valores.put("accion", h.accion)
        valores.put("detalle", h.detalle)
        valores.put("fecha", h.fecha)
        valores.put("usuario", h.usuario)
        baseDatos.writableDatabase.insert("historial", null, valores)
    }

    private suspend fun historialLocal(levantamientoId: Long): List<Historial> = withContext(Dispatchers.IO) {
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

    private suspend fun leerLevantamientoLocal(c: Cursor): Levantamiento {
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
    }
}