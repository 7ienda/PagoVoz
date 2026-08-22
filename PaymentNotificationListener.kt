package com.pagovoz.app.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.content.ContextCompat
import com.pagovoz.app.data.PaymentEntity
import com.pagovoz.app.data.PaymentRepository
import com.pagovoz.app.util.PaymentParser
import com.pagovoz.app.util.PrefsManager
import com.pagovoz.app.util.SupportedApps
import com.pagovoz.app.util.VoiceAnnouncer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servicio que Android llama automáticamente cada vez que llega
 * CUALQUIER notificación al teléfono (funciona con pantalla apagada
 * o bloqueada, porque el sistema operativo procesa notificaciones
 * de forma independiente al estado de la pantalla).
 *
 * Requiere que el usuario active manualmente el permiso "Acceso a
 * notificaciones" para esta app en Ajustes del sistema.
 */
class PaymentNotificationListener : NotificationListenerService() {

    private lateinit var repository: PaymentRepository
    private lateinit var voiceAnnouncer: VoiceAnnouncer
    private lateinit var prefs: PrefsManager
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        repository = PaymentRepository(applicationContext)
        voiceAnnouncer = VoiceAnnouncer(applicationContext)
        prefs = PrefsManager(applicationContext)

        // Aseguramos que el servicio de primer plano también esté corriendo
        val serviceIntent = Intent(this, KeepAliveForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName ?: return

        val supported = SupportedApps.findByPackage(pkg) ?: return
        if (!prefs.isAppEnabled(pkg)) return

        val notification: Notification = sbn.notification ?: return
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

        val fullText = listOf(title, text, bigText).filter { it.isNotBlank() }.joinToString(" | ")
        if (fullText.isBlank()) return

        val parsed = PaymentParser.parse(fullText) ?: return

        if (parsed.amount < prefs.minAmountToAnnounce) return

        val hash = repository.makeHash(pkg, fullText)
        val entity = PaymentEntity(
            packageName = pkg,
            appDisplayName = supported.displayName,
            amount = parsed.amount,
            currencySymbol = parsed.currencySymbol,
            senderName = parsed.senderName,
            rawText = fullText,
            timestamp = System.currentTimeMillis(),
            dedupeHash = hash
        )

        scope.launch {
            val inserted = repository.insertIfNotDuplicate(entity)
            if (inserted) {
                if (!prefs.isWithinQuietHours()) {
                    val amountFormatted = PaymentParser.formatAmount(parsed.amount, parsed.currencySymbol)
                    voiceAnnouncer.announcePayment(
                        appName = supported.displayName,
                        amountFormatted = amountFormatted,
                        senderName = parsed.senderName
                    )

                    if (prefs.overlayEnabled) {
                        showOverlayAlert(supported.displayName, amountFormatted, parsed.senderName)
                    }
                }
            }
        }
    }

    private fun showOverlayAlert(appName: String, amountFormatted: String, sender: String?) {
        val overlayIntent = Intent(this, OverlayAlertService::class.java).apply {
            putExtra(OverlayAlertService.EXTRA_APP_NAME, appName)
            putExtra(OverlayAlertService.EXTRA_AMOUNT, amountFormatted)
            putExtra(OverlayAlertService.EXTRA_SENDER, sender)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, overlayIntent)
            } else {
                startService(overlayIntent)
            }
        } catch (_: Exception) {
            // Si no se concedió el permiso "Mostrar sobre otras apps", ignoramos silenciosamente
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No se requiere acción
    }
}
