package com.example.ratcher.model

// Entidad central del ascenso que aglutina los metadatos de la revisión del
// rack, diferenciando las columnas persistentes de los campos calculados en la consulta.


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
