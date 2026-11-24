package com.example.ratcher.remote

import com.example.ratcher.model.Componente
import com.example.ratcher.model.Historial
import com.example.ratcher.model.Levantamiento
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Part
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
interface ApiService {
    // ---------- Levantamientos (Xano: tabla 'levantamiento') ----------

    @GET("levantamiento")
    suspend fun getRacks(): List<Levantamiento>

    @GET("levantamiento/{id}")
    suspend fun getRack(@Path("id") id: Long): Response<Levantamiento>

    @POST("levantamiento")
    suspend fun createRack(@Body levantamiento: Levantamiento): Levantamiento

    @PUT("levantamiento/{id}")
    suspend fun updateRack(@Path("id") id: Long, @Body levantamiento: Levantamiento): Levantamiento

    @PATCH("levantamiento/{id}")
    suspend fun updateRackEstado(@Path("id") id: Long, @Body estado: Map<String, String>): Unit

    @DELETE("levantamiento/{id}")
    suspend fun deleteRack(@Path("id") id: Long): Unit

    // ---------- Componentes ----------

    @GET("componente")
    suspend fun getComponentes(@Query("levantamiento_id") levantamientoId: Long): List<Componente>

    // El endpoint de Xano guarda un componente por request
    @POST("componente")
    suspend fun createComponente(@Body componente: Componente): Componente

    @PATCH("componente/{id}")
    suspend fun patchComponente(@Path("id") id: Long, @Body cambios: Map<String, Any>): Unit

    // ---------- Historial ----------

    @GET("historial")
    suspend fun getHistorial(@Query("levantamiento_id") levantamientoId: Long): List<Historial>

    @POST("historial")
    suspend fun createHistorial(@Body historial: Historial): Historial

    data class UsuarioDto(
        @SerializedName("id") val id: Long = 0,
        @SerializedName("nombre") val nombre: String,
        @SerializedName("correo") val correo: String,
        @SerializedName("cargo") val cargo: String,
        @SerializedName("clave") val clave: String
    )

    @GET("usuarios")
    suspend fun getUsuarios(
        @Query("correo") correo: String,
        @Query("clave") clave: String
    ): List<UsuarioDto>

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: UsuarioDto): UsuarioDto

    // ---------- Subida de imagen ----------
    data class UploadResponse(
        @SerializedName("path") val path: String? = null,
        @SerializedName("url") val url: String? = null
    )

    @Multipart
    @POST("upload")
    suspend fun uploadImagen(@Part file: MultipartBody.Part): UploadResponse
}