package com.pagovoz.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pagovoz.app.service.KeepAliveForegroundService

/**
 * Reinicia el servicio en segundo plano automáticamente cuando el
 * teléfono se enciende, sin que el usuario tenga que abrir la app.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            KeepAliveForegroundService.start(context)
        }
    }
}
