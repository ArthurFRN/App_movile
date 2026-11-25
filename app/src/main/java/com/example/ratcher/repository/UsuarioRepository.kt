package com.example.ratcher.repository

<<<<<<< HEAD
import com.example.ratcher.data.ApiClient
import com.example.ratcher.data.AuthApi
import com.example.ratcher.data.LoginDto
import com.example.ratcher.data.RegistroDto
import com.example.ratcher.data.aUsuario
import com.example.ratcher.model.Usuario
import com.example.ratcher.util.Sesion

// Error del servidor con un mensaje claro para mostrar en pantalla
class ErrorServidor(mensaje: String) : Exception(mensaje)

// Repositorio que habla con los endpoints de autenticación de Xano
class UsuarioRepository(private val api: AuthApi = ApiClient.auth) {

    // Inicia sesión: Xano entrega un token y con él se piden los datos del usuario.
    // Devuelve el usuario, o nulo si las credenciales no son correctas.
    suspend fun login(correo: String, clave: String): Usuario? {
        val respuestaLogin = api.login(LoginDto(correo.trim().lowercase(), clave))

        // 400/401/403: Xano rechazó las credenciales
        if (respuestaLogin.code() in listOf(400, 401, 403)) return null
        if (!respuestaLogin.isSuccessful) {
            throw ErrorServidor("Error del servidor al iniciar sesión (código ${respuestaLogin.code()})")
        }

        val token = respuestaLogin.body()?.authToken
        if (token.isNullOrBlank()) {
            throw ErrorServidor("El servidor no devolvió el token de sesión. Revisa el endpoint auth/login en Xano")
        }

        Sesion.token = token
        val respuestaPerfil = api.perfil()
        val perfil = respuestaPerfil.body()
        if (!respuestaPerfil.isSuccessful || perfil == null) {
            Sesion.token = null
            throw ErrorServidor("No se pudo obtener el perfil del usuario. Revisa el endpoint auth/me en Xano")
        }
        return perfil.aUsuario()
    }

    // Crea la cuenta en Xano (la contraseña la guarda Xano con hash).
    // Devuelve false si el correo ya existe o los datos fueron rechazados.
    suspend fun registrar(nombre: String, correo: String, cargo: String, clave: String): Boolean {
        val datos = RegistroDto(
            name = nombre.trim(),
            email = correo.trim().lowercase(),
            password = clave,
            cargo = cargo
        )
        val respuesta = api.registrar(datos)
        if (respuesta.code() in listOf(400, 403, 409)) return false
        if (!respuesta.isSuccessful) {
            throw ErrorServidor("Error del servidor al registrar (código ${respuesta.code()})")
        }
        // Si Xano responde 200 pero sin token, el endpoint no guardó nada
        if (respuesta.body()?.authToken.isNullOrBlank()) {
            throw ErrorServidor("El servidor no creó la cuenta. Revisa el endpoint auth/signup en Xano")
        }
        return true
    }
=======
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
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
}
