package com.example.ratcher.repository

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

    // ---------------- Levantamientos ----------------

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

    // Lista todos los levantamientos, con la cantidad de componentes y cuántos faltan por revisar
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

    // ---------------- Componentes ----------------

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

    // ---------------- Historial (trazabilidad) ----------------

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

    // ---------------- Ayuda ----------------

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
    }
}
