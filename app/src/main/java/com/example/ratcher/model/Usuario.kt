package com.example.ratcher.model

data class Usuario(
    val id: Long = 0,
    val correo: String,
    val nombre: String = "",
    val cargo: String = ""
)
