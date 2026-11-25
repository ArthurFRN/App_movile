package com.example.ratcher.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints de levantamientos, componentes e historial (todos requieren el token de sesión)
interface RatcherApi {

    @GET("levantamiento")
    suspend fun listarLevantamientos(): Response<List<LevantamientoDto>>

    @GET("levantamiento/{id}")
    suspend fun obtenerLevantamiento(@Path("id") id: Long): Response<LevantamientoDto>

    @POST("levantamiento")
    suspend fun crearLevantamiento(@Body datos: LevantamientoDto): Response<LevantamientoDto>

    @PATCH("levantamiento/{id}")
    suspend fun actualizarEstado(@Path("id") id: Long, @Body datos: EstadoDto): Response<Unit>

    @DELETE("levantamiento/{id}")
    suspend fun eliminarLevantamiento(@Path("id") id: Long): Response<Unit>

    // Si se envía levantamiento_id solo devuelve los componentes de ese levantamiento
    @GET("componente")
    suspend fun listarComponentes(@Query("levantamiento_id") levantamientoId: Long?): Response<List<ComponenteDto>>

    @POST("componente")
    suspend fun crearComponente(@Body datos: ComponenteDto): Response<ComponenteDto>

    @PATCH("componente/{id}")
    suspend fun actualizarComponente(@Path("id") id: Long, @Body datos: ComponenteEdicionDto): Response<Unit>

    @GET("historial")
    suspend fun listarHistorial(@Query("levantamiento_id") levantamientoId: Long?): Response<List<HistorialDto>>

    @POST("historial")
    suspend fun crearHistorial(@Body datos: HistorialDto): Response<HistorialDto>
}
