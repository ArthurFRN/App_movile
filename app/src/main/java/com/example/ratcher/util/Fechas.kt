package com.example.ratcher.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Fechas {
    fun hoy(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())
    }

    fun ahora(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date())
    }
}
