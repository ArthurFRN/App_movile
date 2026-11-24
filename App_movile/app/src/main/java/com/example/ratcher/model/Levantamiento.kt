package com.example.ratcher.model

import com.google.gson.annotations.SerializedName

// Entidad central del ascenso que aglutina los metadatos de la revisión del
// rack, diferenciando las columnas persistentes de los campos calculados en la consulta.

data class Levantamiento(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("rack_id") val rackId: String,
    @SerializedName("area") val area: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("observaciones") val observaciones: String,
    @SerializedName("ruta_imagen") val rutaImagen: String,
    @SerializedName("responsable") val responsable: String,
    @SerializedName("estado") val estado: String = Estados.EN_REVISION,
    // Estos dos se calculan al listar (no son columnas de la tabla)
    @SerializedName("total_componentes") val totalComponentes: Int = 0,
    @SerializedName("pendientes") val pendientes: Int = 0
)