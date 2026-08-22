package com.pagovoz.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pagovoz.app.ui.alerts.AlertsFragment
import com.pagovoz.app.ui.history.HistoryFragment
import com.pagovoz.app.ui.settings.SettingsFragment
import com.pagovoz.app.util.PrefsManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            openFragment(AlertsFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_alerts -> AlertsFragment()
                R.id.nav_history -> HistoryFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> AlertsFragment()
            }
            openFragment(fragment)
            true
        }

        maybeShowAppLock()
    }

    /**
     * Si el usuario activó "Bloquear app con huella/PIN" en Ajustes, pedimos
     * autenticación biométrica (o PIN/patrón del dispositivo) antes de dejar
     * ver el contenido de la app.
     */
    private fun maybeShowAppLock() {
        val prefs = PrefsManager(this)
        if (!prefs.appLockEnabled) return

        val biometricManager = BiometricManager.from(this)
        val canAuth = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) return

        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    finish()
                }
                override fun onAuthenticationFailed() {
                    // se permite reintentar, no cerramos la app
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("PagoVoz bloqueado")
            .setSubtitle("Verifica tu identidad para continuar")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
