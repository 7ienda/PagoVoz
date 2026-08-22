package com.pagovoz.app.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pagovoz.app.MainActivity
import com.pagovoz.app.databinding.ActivityOnboardingBinding
import com.pagovoz.app.service.KeepAliveForegroundService

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si el usuario ya concedió el permiso antes, saltamos directo a la app
        if (isNotificationAccessGranted()) {
            goToMain()
            return
        }

        binding.btnGrantPermissions.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        binding.btnContinue.setOnClickListener {
            goToMain()
        }
    }

    private fun isNotificationAccessGranted(): Boolean {
        val enabledListeners = android.provider.Settings.Secure.getString(
            contentResolver, "enabled_notification_listeners"
        ) ?: ""
        return enabledListeners.contains(packageName)
    }

    private fun goToMain() {
        KeepAliveForegroundService.start(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
