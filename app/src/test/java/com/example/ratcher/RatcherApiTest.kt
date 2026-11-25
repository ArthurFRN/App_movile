package com.example.ratcher

import com.example.ratcher.data.ApiClient
import com.example.ratcher.data.ComponenteDto
import com.example.ratcher.data.RatcherApi
import com.example.ratcher.util.Sesion
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas del consumo de la API con un servidor falso (no necesitan internet ni Xano)
class RatcherApiTest {

    private val servidor = MockWebServer()
    private lateinit var api: RatcherApi

    @Before
    fun preparar() {
        servidor.start()
        api = ApiClient.crearRetrofit(servidor.url("/").toString()).create(RatcherApi::class.java)
    }

    @After
    fun cerrar() {
        servidor.shutdown()
        Sesion.cerrar()
    }

    @Test
    fun listarLevantamientosLeeLosCamposDeXano() {
        servidor.enqueue(
            MockResponse().setBody(
                """[{"id":7,"created_at":1700000000000,"rack_id":"RACK-01","area":"Sala A",
                "fecha":"01/03/2025","observaciones":"","ruta_imagen":"/x.jpg",
                "responsable":"Ana","estado":"En revisión"}]"""
            )
        )
        runBlocking {
            val lista = api.listarLevantamientos().body()!!
            assertEquals(1, lista.size)
            assertEquals("RACK-01", lista[0].rackId)
            assertEquals(7L, lista[0].id)
        }
    }

    @Test
    fun laPeticionLlevaElTokenDeLaSesion() {
        Sesion.token = "token-de-prueba"
        servidor.enqueue(MockResponse().setBody("[]"))
        runBlocking { api.listarLevantamientos() }

        val peticion = servidor.takeRequest()
        assertEquals("Bearer token-de-prueba", peticion.getHeader("Authorization"))
        assertEquals("/levantamiento", peticion.path)
    }

    @Test
    fun crearComponenteEnviaJsonConNombresDeXano() {
        servidor.enqueue(MockResponse().setBody("""{"id":1,"levantamiento_id":7}"""))
        runBlocking {
            api.crearComponente(
                ComponenteDto(
                    levantamientoId = 7, categoria = "Switch", descripcion = "Equipo de red",
                    confianza = 90, estado = "Reconocido", categoriaOriginal = "Switch"
                )
            )
        }

        val peticion = servidor.takeRequest()
        val cuerpo = peticion.body.readUtf8()
        assertEquals("POST", peticion.method)
        assertTrue(cuerpo.contains("\"levantamiento_id\":7"))
        assertTrue(cuerpo.contains("\"categoria_original\":\"Switch\""))
    }
}
