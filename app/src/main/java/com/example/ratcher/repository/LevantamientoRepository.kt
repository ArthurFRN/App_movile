package com.example.ratcher.repository

import com.example.ratcher.data.ApiClient
import com.example.ratcher.data.ComponenteEdicionDto
import com.example.ratcher.data.EstadoDto
import com.example.ratcher.data.RatcherApi
import com.example.ratcher.data.aComponente
import com.example.ratcher.data.aDto
import com.example.ratcher.data.aHistorial
import com.example.ratcher.data.aLevantamiento
import com.example.ratcher.model.Componente
import com.example.ratcher.model.Estados
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import retrofit2.Response
import java.io.IOException

// Intermediario entre los ViewModels y la API de Xano (levantamientos, componentes e historial)
class LevantamientoRepository(private val api: RatcherApi = ApiClient.datos) {

    // Devuelve el cuerpo de la respuesta o lanza error si el servidor respondió con fallo
    private fun <T> Response<T>.cuerpo(): T {
        val contenido = body()
        if (!isSuccessful || contenido == null) {
            throw IOException("Error del servidor (código " + code() + ")")
        }
        return contenido
    }

    // Crea el levantamiento y devuelve el id que le asignó Xano
    suspend fun crear(l: Levantamiento): Long {
        val creado = api.crearLevantamiento(l.aDto()).cuerpo()
        return creado.id ?: throw IOException("Xano no devolvió el id del levantamiento")
    }

    // Lista los levantamientos (más nuevos primero) con el total de componentes y los que faltan por revisar
    suspend fun listar(): List<Levantamiento> {
        val levantamientos = api.listarLevantamientos().cuerpo()
        val componentesPorLevantamiento = api.listarComponentes(null).cuerpo().groupBy { it.levantamientoId }

        return levantamientos.map { dto ->
            val suyos = componentesPorLevantamiento[dto.id].orEmpty()
            dto.aLevantamiento(
                totalComponentes = suyos.size,
                pendientes = suyos.count { it.estado == Estados.RECONOCIDO }
            )
        }.sortedByDescending { it.id }
    }

    suspend fun obtener(id: Long): Levantamiento? {
        val respuesta = api.obtenerLevantamiento(id)
        if (respuesta.code() == 404) return null
        return respuesta.cuerpo().aLevantamiento()
    }

    suspend fun actualizarEstado(id: Long, estado: String) {
        api.actualizarEstado(id, EstadoDto(estado))
    }

    // En Xano, este endpoint también borra los componentes y el historial del levantamiento
    suspend fun eliminar(id: Long) {
        api.eliminarLevantamiento(id)
    }

    // Xano recibe un componente por petición
    suspend fun guardarComponentes(lista: List<Componente>) {
        for (c in lista) {
            api.crearComponente(c.aDto()).cuerpo()
        }
    }

    suspend fun componentes(levantamientoId: Long): List<Componente> {
        return api.listarComponentes(levantamientoId).cuerpo()
            .filter { it.levantamientoId == levantamientoId }
            .sortedBy { it.id }
            .map { it.aComponente() }
    }

    suspend fun actualizarComponente(id: Long, categoria: String, descripcion: String, estado: String) {
        api.actualizarComponente(id, ComponenteEdicionDto(categoria, descripcion.trim(), estado))
    }

    // Registra una nueva acción en el historial de trazabilidad
    suspend fun agregarHistorial(h: Historial) {
        api.crearHistorial(h.aDto()).cuerpo()
    }

    suspend fun historial(levantamientoId: Long): List<Historial> {
        return api.listarHistorial(levantamientoId).cuerpo()
            .filter { it.levantamientoId == levantamientoId }
            .sortedBy { it.id }
            .map { it.aHistorial() }
    }
}
