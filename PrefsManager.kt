package com.pagovoz.app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Todas las preferencias configurables de la app (funciones "premium").
 */
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pagovoz_prefs", Context.MODE_PRIVATE)

    // --- Apps habilitadas para monitorear ---
    fun isAppEnabled(pkg: String): Boolean = prefs.getBoolean("app_enabled_$pkg", true)
    fun setAppEnabled(pkg: String, enabled: Boolean) =
        prefs.edit().putBoolean("app_enabled_$pkg", enabled).apply()

    // --- Voz ---
    var voiceEnabled: Boolean
        get() = prefs.getBoolean("voice_enabled", true)
        set(v) = prefs.edit().putBoolean("voice_enabled", v).apply()

    var voiceSpeed: Float
        get() = prefs.getFloat("voice_speed", 1.0f)
        set(v) = prefs.edit().putFloat("voice_speed", v).apply()

    var voicePitch: Float
        get() = prefs.getFloat("voice_pitch", 1.0f)
        set(v) = prefs.edit().putFloat("voice_pitch", v).apply()

    // Plantilla del anuncio. Variables: {app} {monto} {nombre}
    var announceTemplate: String
        get() = prefs.getString("announce_template", "Pago recibido de {app}. {monto}") ?: ""
        set(v) = prefs.edit().putString("announce_template", v).apply()

    // --- Monto mínimo para anunciar (filtro anti-spam) ---
    var minAmountToAnnounce: Float
        get() = prefs.getFloat("min_amount", 0f)
        set(v) = prefs.edit().putFloat("min_amount", v).apply()

    // --- Vibración + sonido extra al recibir pago ---
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(v) = prefs.edit().putBoolean("vibration_enabled", v).apply()

    // --- Overlay flotante (burbuja con el monto) ---
    var overlayEnabled: Boolean
        get() = prefs.getBoolean("overlay_enabled", true)
        set(v) = prefs.edit().putBoolean("overlay_enabled", v).apply()

    // --- Horario de silencio (No Molestar) ---
    var quietHoursEnabled: Boolean
        get() = prefs.getBoolean("quiet_hours_enabled", false)
        set(v) = prefs.edit().putBoolean("quiet_hours_enabled", v).apply()

    var quietHoursStart: Int // hora 0-23
        get() = prefs.getInt("quiet_start", 22)
        set(v) = prefs.edit().putInt("quiet_start", v).apply()

    var quietHoursEnd: Int
        get() = prefs.getInt("quiet_end", 7)
        set(v) = prefs.edit().putInt("quiet_end", v).apply()

    // --- Bloqueo con PIN / biometría al abrir la app ---
    var appLockEnabled: Boolean
        get() = prefs.getBoolean("app_lock_enabled", false)
        set(v) = prefs.edit().putBoolean("app_lock_enabled", v).apply()

    // --- Nombre del negocio (para personalizar anuncios / exportes) ---
    var businessName: String
        get() = prefs.getString("business_name", "") ?: ""
        set(v) = prefs.edit().putString("business_name", v).apply()

    // --- Tema oscuro ---
    var darkThemeEnabled: Boolean
        get() = prefs.getBoolean("dark_theme", false)
        set(v) = prefs.edit().putBoolean("dark_theme", v).apply()

    fun isWithinQuietHours(): Boolean {
        if (!quietHoursEnabled) return false
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return if (quietHoursStart <= quietHoursEnd) {
            hour in quietHoursStart until quietHoursEnd
        } else {
            hour >= quietHoursStart || hour < quietHoursEnd
        }
    }
}
