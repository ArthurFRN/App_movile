package com.example.ratcher.data

import com.example.ratcher.util.Sesion
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Crea los clientes de Retrofit que usan los repositorios (una única instancia para toda la app)
object ApiClient {

    // Agrega el token de la sesión a cada petición (si ya se inició sesión)
    private val agregarToken = Interceptor { cadena ->
        val token = Sesion.token
        val peticion = if (token.isNullOrBlank()) {
            cadena.request()
        } else {
            cadena.request().newBuilder().header("Authorization", "Bearer $token").build()
        }
        cadena.proceed(peticion)
    }

    // Xano responde 429 cuando se hacen muchas peticiones seguidas: se espera y se reintenta
    private val reintentarSiHayLimite = Interceptor { cadena ->
        var respuesta = cadena.proceed(cadena.request())
        var intentos = 0
        while (respuesta.code == 429 && intentos < 3) {
            respuesta.close()
            Thread.sleep(2000)
            respuesta = cadena.proceed(cadena.request())
            intentos++
        }
        respuesta
    }

    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(agregarToken)
            .addInterceptor(reintentarSiHayLimite)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    fun crearRetrofit(urlBase: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(urlBase.trimEnd('/') + "/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: AuthApi by lazy { crearRetrofit(ApiConfig.URL_AUTH).create(AuthApi::class.java) }
    val datos: RatcherApi by lazy { crearRetrofit(ApiConfig.URL_DATOS).create(RatcherApi::class.java) }
}
