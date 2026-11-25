package com.example.ratcher.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.ratcher.R

// Segundo recurso nativo: notificaciones locales del sistema
object Notificaciones {

    private const val ID_CANAL = "levantamientos"
    private const val ID_NOTIFICACION_VALIDADO = 1001

    // Desde Android 13 hay que pedir el permiso en tiempo de ejecución
    fun necesitaPermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    }

    // Avisa que un levantamiento quedó validado (si el usuario no dio permiso, no hace nada)
    @SuppressLint("MissingPermission")
    fun avisarLevantamientoValidado(context: Context, rackId: String) {
        if (necesitaPermiso(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                ID_CANAL,
                "Levantamientos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(context, ID_CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Levantamiento validado")
            .setContentText("El rack " + rackId + " quedó validado y registrado.")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_NOTIFICACION_VALIDADO, notificacion)
    }
}
