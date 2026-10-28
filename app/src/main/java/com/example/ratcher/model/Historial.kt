package com.example.ratcher.model

// Cada acción queda registrada: así hay trazabilidad entre imagen y componentes
data class Historial(
    val id: Long = 0,
    val levantamientoId: Long,
    val accion: String,
    val detalle: String,
    val fecha: String,
    val usuario: String
)
