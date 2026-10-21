package com.example.ratcher.repository

import com.example.ratcher.model.Usuario

class Conexion{
    fun login(gmail: String, contrasena: String): Usuario? {
        return if (gmail == "test@test.com" && contrasena == "123456") {
            Usuario(
                gmail = gmail
            )
        } else {
            null
        }
    }
}
