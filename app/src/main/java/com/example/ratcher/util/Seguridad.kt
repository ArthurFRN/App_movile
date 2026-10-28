package com.example.ratcher.util

import java.security.MessageDigest

object Seguridad {
    // La contraseña no se guarda tal cual: se guarda su hash SHA-256
    fun hash(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
