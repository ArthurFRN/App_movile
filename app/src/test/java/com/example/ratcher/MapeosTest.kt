package com.example.ratcher

import com.example.ratcher.data.ComponenteDto
import com.example.ratcher.data.LevantamientoDto
import com.example.ratcher.data.aComponente
import com.example.ratcher.data.aDto
import com.example.ratcher.data.aLevantamiento
import com.example.ratcher.model.Estados
import com.example.ratcher.model.Levantamiento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de la conversión entre lo que responde Xano y los modelos de la app
class MapeosTest {

    @Test
    fun camposNulosDeXanoSeConviertenEnValoresPorDefecto() {
        val dto = LevantamientoDto(
            id = 5, rackId = "RACK-01", area = null, fecha = null,
            observaciones = null, rutaImagen = null, responsable = null, estado = null
        )
        val modelo = dto.aLevantamiento(totalComponentes = 4, pendientes = 2)

        assertEquals("", modelo.area)
        assertEquals(Estados.EN_REVISION, modelo.estado)
        assertEquals(4, modelo.totalComponentes)
        assertEquals(2, modelo.pendientes)
    }

    @Test
    fun levantamientoNuevoSeEnviaSinIdYConTextosLimpios() {
        val dto = Levantamiento(
            rackId = "  RACK-02 ", area = " Sala 1 ", fecha = "01/03/2025",
            observaciones = "  ", rutaImagen = "/ruta/foto.jpg", responsable = "Ana"
        ).aDto()

        assertNull(dto.id)
        assertEquals("RACK-02", dto.rackId)
        assertEquals("Sala 1", dto.area)
        assertEquals("", dto.observaciones)
    }

    @Test
    fun componenteSinCategoriaOriginalUsaLaCategoriaActual() {
        val componente = ComponenteDto(
            id = 9, levantamientoId = 5, categoria = "UPS", descripcion = "Respaldo",
            confianza = 80, estado = null, categoriaOriginal = null
        ).aComponente()

        assertEquals("UPS", componente.categoriaOriginal)
        assertEquals(Estados.RECONOCIDO, componente.estado)
    }
}
