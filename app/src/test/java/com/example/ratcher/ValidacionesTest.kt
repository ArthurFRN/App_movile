package com.example.ratcher

import com.example.ratcher.util.Validaciones
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de la lógica de validación (no necesitan emulador)
class ValidacionesTest {

    @Test
    fun correoValidoNoTieneError() {
        assertNull(Validaciones.correo("test@test.com"))
    }

    @Test
    fun correoSinArrobaTieneError() {
        assertNotNull(Validaciones.correo("correo.com"))
    }

    @Test
    fun correoVacioTieneError() {
        assertNotNull(Validaciones.correo(""))
    }

    @Test
    fun claveCortaTieneError() {
        assertNotNull(Validaciones.claveRegistro("ab1"))
    }

    @Test
    fun claveSinNumeroTieneError() {
        assertNotNull(Validaciones.claveRegistro("abcdefg"))
    }

    @Test
    fun claveCorrectaNoTieneError() {
        assertNull(Validaciones.claveRegistro("abc123"))
    }

    @Test
    fun confirmacionDistintaTieneError() {
        assertNotNull(Validaciones.confirmacionClave("abc123", "abc124"))
    }

    @Test
    fun rackValidoNoTieneError() {
        assertNull(Validaciones.rackId("RACK-01"))
    }

    @Test
    fun rackConEspaciosTieneError() {
        assertNotNull(Validaciones.rackId("rack 01"))
    }

    @Test
    fun fechaValidaNoTieneError() {
        assertNull(Validaciones.fecha("01/03/2025"))
    }

    @Test
    fun fechaInexistenteTieneError() {
        assertNotNull(Validaciones.fecha("31/02/2025"))
    }

    @Test
    fun fechaFuturaTieneError() {
        assertNotNull(Validaciones.fecha("01/01/2999"))
    }

    @Test
    fun categoriaFueraDelCatalogoTieneError() {
        assertNotNull(Validaciones.categoria("Impresora"))
    }
}
