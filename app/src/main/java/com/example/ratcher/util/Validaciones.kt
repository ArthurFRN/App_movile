package com.example.ratcher.util

import com.example.ratcher.model.Catalogo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Todas las validaciones están aquí, fuera de la interfaz.
// Cada función devuelve un texto con el error, o null si el valor está bien.
object Validaciones {

    private val patronCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val patronNombre = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$")
    private val patronRack = Regex("^[A-Za-z0-9-]{3,15}$")
    private val patronFecha = Regex("^\\d{2}/\\d{2}/\\d{4}$")

    fun correo(valor: String): String? {
        if (valor.isBlank()) return "El correo es obligatorio"
        if (!patronCorreo.matches(valor.trim())) return "Correo inválido (ej: nombre@empresa.cl)"
        return null
    }

    fun claveLogin(valor: String): String? {
        if (valor.isBlank()) return "La contraseña es obligatoria"
        return null
    }

    fun claveRegistro(valor: String): String? {
        if (valor.isBlank()) return "La contraseña es obligatoria"
        if (valor.length < 6) return "Debe tener al menos 6 caracteres"
        if (!valor.any { it.isDigit() }) return "Debe incluir al menos un número"
        if (!valor.any { it.isLetter() }) return "Debe incluir al menos una letra"
        return null
    }

    fun confirmacionClave(clave: String, confirmacion: String): String? {
        if (confirmacion.isBlank()) return "Repite la contraseña"
        if (clave != confirmacion) return "Las contraseñas no coinciden"
        return null
    }

    fun nombre(valor: String): String? {
        if (valor.isBlank()) return "El nombre es obligatorio"
        if (valor.trim().length < 3) return "Debe tener al menos 3 letras"
        if (!patronNombre.matches(valor.trim())) return "Solo se permiten letras"
        return null
    }

    fun cargo(valor: String): String? {
        if (valor.isBlank()) return "Selecciona un cargo"
        return null
    }

    fun rackId(valor: String): String? {
        if (valor.isBlank()) return "El identificador del rack es obligatorio"
        if (!patronRack.matches(valor.trim())) return "Entre 3 y 15 caracteres: letras, números o guion (ej: RACK-01)"
        return null
    }

    fun area(valor: String): String? {
        if (valor.isBlank()) return "El área revisada es obligatoria"
        if (valor.trim().length < 3) return "Debe tener al menos 3 caracteres"
        return null
    }

    fun fecha(valor: String): String? {
        if (valor.isBlank()) return "La fecha es obligatoria"
        if (!patronFecha.matches(valor.trim())) return "Usa el formato dd/mm/aaaa"

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        formato.isLenient = false
        val fecha: Date? = try {
            formato.parse(valor.trim())
        } catch (e: Exception) {
            null
        }
        if (fecha == null) return "Esa fecha no existe en el calendario"
        if (fecha.after(Date())) return "La fecha no puede ser futura"
        return null
    }

    fun observaciones(valor: String): String? {
        // Es opcional, solo se limita el largo
        if (valor.length > 200) return "Máximo 200 caracteres"
        return null
    }

    fun descripcion(valor: String): String? {
        if (valor.isBlank()) return "La descripción es obligatoria"
        if (valor.trim().length < 5) return "Debe tener al menos 5 caracteres"
        if (valor.length > 100) return "Máximo 100 caracteres"
        return null
    }

    fun categoria(valor: String): String? {
        if (!Catalogo.categorias.contains(valor)) return "Selecciona una categoría del catálogo"
        return null
    }
}
