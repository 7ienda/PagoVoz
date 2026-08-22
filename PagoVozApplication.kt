package com.pagovoz.app

import android.app.Application
import com.pagovoz.app.service.KeepAliveForegroundService

class PagoVozApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Arranca el servicio en segundo plano apenas la app carga por primera vez.
        // El NotificationListenerService también lo arranca por si el sistema
        // reinicia el listener sin pasar por aquí.
        try {
            KeepAliveForegroundService.start(this)
        } catch (_: Exception) {
            // En algunos OEM no se puede iniciar un foreground service sin interacción
            // directa del usuario; se reintenta desde MainActivity.
        }
    }
}
