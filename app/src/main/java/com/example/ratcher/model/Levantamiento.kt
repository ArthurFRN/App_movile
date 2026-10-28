package com.example.ratcher.model

// Un levantamiento = la revisión de un rack con su foto de evidencia
data class Levantamiento(
    val id: Long = 0,
    val rackId: String,
    val area: String,
    val fecha: String,
    val observaciones: String,
    val rutaImagen: String,
    val responsable: String,
    val estado: String = Estados.EN_REVISION,
    // Estos dos se calculan al listar (no son columnas de la tabla)
    val totalComponentes: Int = 0,
    val pendientes: Int = 0
)
