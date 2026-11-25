package com.example.ratcher.data

import com.example.ratcher.model.Componente
import com.example.ratcher.model.Estados
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import com.example.ratcher.model.Usuario

// Conversión entre los objetos de la red (DTO) y los modelos que usa la app.
// Xano puede devolver campos nulos, por eso se reemplazan por valores vacíos.

fun UsuarioDto.aUsuario(): Usuario {
    return Usuario(id = id ?: 0, correo = email.orEmpty(), nombre = name.orEmpty(), cargo = cargo.orEmpty())
}

fun LevantamientoDto.aLevantamiento(totalComponentes: Int = 0, pendientes: Int = 0): Levantamiento {
    return Levantamiento(
        id = id ?: 0,
        rackId = rackId.orEmpty(),
        area = area.orEmpty(),
        fecha = fecha.orEmpty(),
        observaciones = observaciones.orEmpty(),
        rutaImagen = rutaImagen.orEmpty(),
        responsable = responsable.orEmpty(),
        estado = estado ?: Estados.EN_REVISION,
        totalComponentes = totalComponentes,
        pendientes = pendientes
    )
}

fun Levantamiento.aDto(): LevantamientoDto {
    return LevantamientoDto(
        rackId = rackId.trim(),
        area = area.trim(),
        fecha = fecha.trim(),
        observaciones = observaciones.trim(),
        rutaImagen = rutaImagen,
        responsable = responsable,
        estado = estado
    )
}

fun ComponenteDto.aComponente(): Componente {
    val categoriaActual = categoria.orEmpty()
    return Componente(
        id = id ?: 0,
        levantamientoId = levantamientoId ?: 0,
        categoria = categoriaActual,
        descripcion = descripcion.orEmpty(),
        confianza = confianza ?: 0,
        estado = estado ?: Estados.RECONOCIDO,
        categoriaOriginal = categoriaOriginal ?: categoriaActual
    )
}

fun Componente.aDto(): ComponenteDto {
    return ComponenteDto(
        levantamientoId = levantamientoId,
        categoria = categoria,
        descripcion = descripcion,
        confianza = confianza,
        estado = estado,
        categoriaOriginal = categoriaOriginal
    )
}

fun HistorialDto.aHistorial(): Historial {
    return Historial(
        id = id ?: 0,
        levantamientoId = levantamientoId ?: 0,
        accion = accion.orEmpty(),
        detalle = detalle.orEmpty(),
        fecha = fecha.orEmpty(),
        usuario = usuario.orEmpty()
    )
}

fun Historial.aDto(): HistorialDto {
    return HistorialDto(
        levantamientoId = levantamientoId,
        accion = accion,
        detalle = detalle,
        fecha = fecha,
        usuario = usuario
    )
}
