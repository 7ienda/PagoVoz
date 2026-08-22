package com.pagovoz.app.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.pagovoz.app.R

/**
 * Muestra una tarjeta flotante ("Pago recibido S/ 50.00") encima de
 * cualquier app, e incluso sobre la pantalla de bloqueo, usando
 * TYPE_APPLICATION_OVERLAY. Requiere el permiso "Mostrar sobre otras
 * apps" concedido manualmente por el usuario.
 */
class OverlayAlertService : Service() {

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_SENDER = "extra_sender"
    }

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: "Pago"
        val amount = intent?.getStringExtra(EXTRA_AMOUNT) ?: ""
        val sender = intent?.getStringExtra(EXTRA_SENDER)

        showOverlay(appName, amount, sender)
        return START_NOT_STICKY
    }

    private fun showOverlay(appName: String, amount: String, sender: String?) {
        removeOverlay()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_payment_alert, null)

        overlayView?.findViewById<TextView>(R.id.tvOverlayApp)?.text = appName
        overlayView?.findViewById<TextView>(R.id.tvOverlayAmount)?.text = amount
        overlayView?.findViewById<TextView>(R.id.tvOverlaySender)?.text = sender ?: ""

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP
        params.y = 60

        try {
            windowManager?.addView(overlayView, params)
        } catch (_: Exception) {
            stopSelf()
            return
        }

        // Auto-ocultar después de 5 segundos
        object : CountDownTimer(5000, 5000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                removeOverlay()
                stopSelf()
            }
        }.start()
    }

    private fun removeOverlay() {
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) { }
        }
        overlayView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
