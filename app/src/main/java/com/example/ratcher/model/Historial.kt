package com.example.ratcher.model

// Constituye un anotacion en el historial para asegurar la conexión entre las imágenes y los elementos alterados.
data class Historial(
    val id: Long = 0,
    val levantamientoId: Long,
    val accion: String,
    val detalle: String,
    val fecha: String,
    val usuario: String
)
