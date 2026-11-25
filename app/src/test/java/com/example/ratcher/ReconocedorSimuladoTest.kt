package com.example.ratcher

import com.example.ratcher.repository.ReconocedorSimulado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

// Pruebas del reconocedor simulado (usa un archivo temporal como si fuera la foto)
class ReconocedorSimuladoTest {

    @get:Rule
    val carpeta = TemporaryFolder()

    private fun fotoDePrueba(): String {
        val archivo = carpeta.newFile("rack.jpg")
        archivo.writeBytes(ByteArray(1500) { it.toByte() })
        return archivo.absolutePath
    }

    @Test
    fun mismaFotoDaMismoResultado() {
        val ruta = fotoDePrueba()
        assertEquals(ReconocedorSimulado.analizar(1, ruta), ReconocedorSimulado.analizar(1, ruta))
    }

    @Test
    fun detectaEntreCuatroYSeisComponentesSinRackVacio() {
        val resultado = ReconocedorSimulado.analizar(1, fotoDePrueba())
        assertTrue(resultado.size in 4..6)
        assertFalse(resultado.any { it.categoria == "Rack vacío" })
    }

    @Test
    fun laConfianzaEstaEntre60Y98() {
        val resultado = ReconocedorSimulado.analizar(1, fotoDePrueba())
        assertTrue(resultado.all { it.confianza in 60..98 })
    }
}
