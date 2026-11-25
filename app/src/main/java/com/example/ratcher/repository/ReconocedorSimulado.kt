package com.example.ratcher.repository

import com.example.ratcher.model.Catalogo
import com.example.ratcher.model.Componente
import java.io.File
import java.util.Random

// Mock del reconocedor de componentes que simula la inferencia mediante un generador
// pseudoaleatorio determinista basado en el peso del archivo.
object ReconocedorSimulado {

    fun analizar(levantamientoId: Long, rutaImagen: String): List<Componente> {
        val azar = Random(File(rutaImagen).length())
        val cantidad = 4 + azar.nextInt(3) // entre 4 y 6 componentes

        val categorias = Catalogo.categorias
            .filter { it != "Rack vacío" }
            .shuffled(azar)
            .take(cantidad)

        val resultado = mutableListOf<Componente>()
        for (categoria in categorias) {
            val unidad = 1 + azar.nextInt(42)
            resultado.add(
                Componente(
                    levantamientoId = levantamientoId,
                    categoria = categoria,
                    descripcion = Catalogo.descripcion(categoria) + " (unidad U" + unidad + ")",
                    confianza = 60 + azar.nextInt(39), // entre 60% y 98%
                    categoriaOriginal = categoria
                )
            )
        }
        return resultado
    }
}
