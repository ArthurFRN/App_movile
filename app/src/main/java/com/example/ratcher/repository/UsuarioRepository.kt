package com.example.ratcher.repository

import android.content.ContentValues
import android.content.Context
import com.example.ratcher.data.BaseDatos
import com.example.ratcher.model.Usuario
import com.example.ratcher.util.Seguridad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Intermediario entre los ViewModels y la tabla "usuarios"
class UsuarioRepository(context: Context) {

    private val baseDatos = BaseDatos.obtener(context)

    // Devuelve el usuario si el correo y la contraseña son correctos, o null si no
    suspend fun login(correo: String, clave: String): Usuario? = withContext(Dispatchers.IO) {
        val db = baseDatos.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, correo, cargo FROM usuarios WHERE correo = ? AND clave = ?",
            arrayOf(correo.lowercase(), Seguridad.hash(clave))
        )
        cursor.use {
            if (it.moveToFirst()) {
                Usuario(
                    id = it.getLong(0),
                    nombre = it.getString(1),
                    correo = it.getString(2),
                    cargo = it.getString(3)
                )
            } else {
                null
            }
        }
    }

    // Devuelve false si el correo ya estaba registrado
    suspend fun registrar(nombre: String, correo: String, cargo: String, clave: String): Boolean =
        withContext(Dispatchers.IO) {
            val db = baseDatos.writableDatabase
            val valores = ContentValues()
            valores.put("nombre", nombre.trim())
            valores.put("correo", correo.trim().lowercase())
            valores.put("cargo", cargo)
            valores.put("clave", Seguridad.hash(clave))
            db.insert("usuarios", null, valores) != -1L
        }
}
