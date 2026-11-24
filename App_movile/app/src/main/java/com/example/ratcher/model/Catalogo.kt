package com.example.ratcher.model

// Catálogo centralizado de estados para validar inspecciones y elementos en el campo.
object Estados {
    const val EN_REVISION = "En revisión"
    const val VALIDADO = "Validado"

    const val RECONOCIDO = "Reconocido"
    const val CONFIRMADO = "Confirmado"
    const val CORREGIDO = "Corregido"
    const val RECHAZADO = "Rechazado"
}

// Catálogo inicial de componentes que la app puede reconocer
object Catalogo {
    val categorias = listOf(
        "Switch",
        "Patch panel",
        "Servidor",
        "UPS",
        "PDU",
        "Router",
        "Firewall",
        "Cableado",
        "Rack vacío"
    )

    val cargos = listOf(
        "Inspector técnico",
        "Especialista técnico",
        "Supervisor"
    )

    fun descripcion(categoria: String): String {
        return when (categoria) {
            "Switch" -> "Equipo de red que conecta dispositivos"
            "Patch panel" -> "Panel de conexión de cables de red"
            "Servidor" -> "Servidor montado en rack"
            "UPS" -> "Sistema de respaldo de energía"
            "PDU" -> "Regleta de distribución de energía"
            "Router" -> "Equipo de enrutamiento de red"
            "Firewall" -> "Equipo de seguridad de red"
            "Cableado" -> "Cableado ordenado en el rack"
            else -> "Espacio libre en el rack"
        }
    }
}
