package com.example.ratcher.model

import com.google.gson.annotations.SerializedName

// Entidad del esquema que guarda los pormenores del objeto identificado, el porcentaje de certeza y el historial de ajustes.
data class Componente(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("levantamiento_id") val levantamientoId: Long = 0,
    @SerializedName("categoria") val categoria: String = "Sin categoría",
    @SerializedName("descripcion") val descripcion: String = "Sin descripción",
    @SerializedName("confianza") val confianza: Int = 0,            // de 0 a 100
    @SerializedName("estado") val estado: String = Estados.RECONOCIDO,
    @SerializedName("categoria_original") val categoriaOriginal: String = "Sin categoría"  // lo que dijo el reconocedor antes de corregir
)
