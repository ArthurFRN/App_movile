package com.example.ratcher.repository

import android.content.ContentValues
import android.content.Context
import com.example.ratcher.data.BaseDatos
import com.example.ratcher.model.Usuario
import com.example.ratcher.util.Seguridad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repositorio que gestiona las operaciones de persistencia y verificación de usuarios.
class UsuarioRepository(context: Context) {

    private val baseDatos = BaseDatos.obtener(context)

    // Verifica las credenciales del usuario y devuelve sus datos o nulo si no son correctos
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

    //  Registra un usuario aplicando un hash a la contraseña y verifica si la inserción fue exitosa.
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
