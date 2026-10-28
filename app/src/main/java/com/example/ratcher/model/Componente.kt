package com.example.ratcher.model

// Un componente que el reconocedor detectó en la foto
data class Componente(
    val id: Long = 0,
    val levantamientoId: Long,
    val categoria: String,
    val descripcion: String,
    val confianza: Int,            // de 0 a 100
    val estado: String = Estados.RECONOCIDO,
    val categoriaOriginal: String  // lo que dijo el reconocedor antes de corregir
)
