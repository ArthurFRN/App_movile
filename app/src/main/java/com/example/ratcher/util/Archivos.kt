package com.example.ratcher.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.File

// Funciones para manejar las fotos de evidencia
object Archivos {

    // Crea un archivo vacío dentro de la app donde se guardará la foto
    fun crearArchivoImagen(context: Context): File {
        val carpeta = File(context.filesDir, "evidencias")
        if (!carpeta.exists()) {
            carpeta.mkdirs()
        }
        return File(carpeta, "rack_" + System.currentTimeMillis() + ".jpg")
    }

    // La cámara necesita una Uri (no un File) para poder escribir la foto
    fun uriDeArchivo(context: Context, archivo: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)
    }

    // Copia la imagen elegida en la galería a la carpeta de la app.
    // Devuelve la ruta del archivo copiado, o null si algo falló.
    fun copiarDesdeGaleria(context: Context, uri: Uri): String? {
        try {
            val destino = crearArchivoImagen(context)
            val entrada = context.contentResolver.openInputStream(uri) ?: return null
            entrada.use { lectura ->
                destino.outputStream().use { escritura ->
                    lectura.copyTo(escritura)
                }
            }
            return destino.absolutePath
        } catch (e: Exception) {
            return null
        }
    }

    // Carga la foto reducida (para no gastar mucha memoria) y la gira si hace falta
    fun cargarImagen(ruta: String, maxLado: Int = 1024): ImageBitmap? {
        val archivo = File(ruta)
        if (!archivo.exists()) return null

        // 1) Leer solo el tamaño
        val limites = BitmapFactory.Options()
        limites.inJustDecodeBounds = true
        BitmapFactory.decodeFile(ruta, limites)

        // 2) Calcular cuánto reducir
        var reduccion = 1
        while (limites.outWidth / reduccion > maxLado || limites.outHeight / reduccion > maxLado) {
            reduccion *= 2
        }

        // 3) Cargar la imagen reducida
        val opciones = BitmapFactory.Options()
        opciones.inSampleSize = reduccion
        var bitmap = BitmapFactory.decodeFile(ruta, opciones) ?: return null

        // 4) Las fotos de cámara pueden venir giradas: se corrige con los datos EXIF
        val grados: Float = try {
            val exif = ExifInterface(ruta)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: Exception) {
            0f
        }
        if (grados != 0f) {
            val matriz = Matrix()
            matriz.postRotate(grados)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        }
        return bitmap.asImageBitmap()
    }
}
