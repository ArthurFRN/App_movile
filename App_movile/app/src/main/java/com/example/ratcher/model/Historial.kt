package com.example.ratcher.model

import com.google.gson.annotations.SerializedName

// Constituye un anotacion en el historial para asegurar la conexión entre las imágenes y los elementos alterados.
data class Historial(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("levantamiento_id") val levantamientoId: Long,
    @SerializedName("accion") val accion: String,
    @SerializedName("detalle") val detalle: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("usuario") val usuario: String
)