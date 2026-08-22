package com.pagovoz.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.materialswitch.MaterialSwitch
import com.pagovoz.app.data.PaymentRepository
import com.pagovoz.app.databinding.FragmentSettingsBinding
import com.pagovoz.app.util.PrefsManager
import com.pagovoz.app.util.SupportedApps
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PrefsManager
    private lateinit var repository: PaymentRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        repository = PaymentRepository(requireContext())

        buildAppToggles()
        loadCurrentValues()
        setupPermissionButtons()
        setupDataButtons()

        binding.btnSave.setOnClickListener { saveValues() }
    }

    private fun buildAppToggles() {
        binding.containerAppsToggles.removeAllViews()
        SupportedApps.ALL.filter { it.packageName != "custom" }.forEach { app ->
            val sw = MaterialSwitch(requireContext())
            sw.text = app.displayName
            sw.isChecked = prefs.isAppEnabled(app.packageName)
            sw.setPadding(0, 8, 0, 8)
            sw.setOnCheckedChangeListener { _, checked ->
                prefs.setAppEnabled(app.packageName, checked)
            }
            binding.containerAppsToggles.addView(sw)
        }
    }

    private fun loadCurrentValues() {
        binding.switchVoice.isChecked = prefs.voiceEnabled
        binding.switchVibration.isChecked = prefs.vibrationEnabled
        binding.switchOverlay.isChecked = prefs.overlayEnabled
        binding.switchQuietHours.isChecked = prefs.quietHoursEnabled
        binding.switchAppLock.isChecked = prefs.appLockEnabled
        binding.switchDarkTheme.isChecked = prefs.darkThemeEnabled
        binding.etTemplate.setText(prefs.announceTemplate)
        binding.etMinAmount.setText(if (prefs.minAmountToAnnounce > 0) prefs.minAmountToAnnounce.toString() else "")
        binding.etBusinessName.setText(prefs.businessName)
    }

    private fun saveValues() {
        prefs.voiceEnabled = binding.switchVoice.isChecked
        prefs.vibrationEnabled = binding.switchVibration.isChecked
        prefs.overlayEnabled = binding.switchOverlay.isChecked
        prefs.quietHoursEnabled = binding.switchQuietHours.isChecked
        prefs.appLockEnabled = binding.switchAppLock.isChecked
        prefs.darkThemeEnabled = binding.switchDarkTheme.isChecked
        prefs.announceTemplate = binding.etTemplate.text?.toString()?.ifBlank {
            "Pago recibido de {app}. {monto}"
        } ?: "Pago recibido de {app}. {monto}"
        prefs.minAmountToAnnounce = binding.etMinAmount.text?.toString()?.toFloatOrNull() ?: 0f
        prefs.businessName = binding.etBusinessName.text?.toString() ?: ""

        Toast.makeText(requireContext(), "Ajustes guardados", Toast.LENGTH_SHORT).show()
    }

    private fun setupPermissionButtons() {
        binding.btnNotifAccess.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        binding.btnOverlayAccess.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
            }
        }

        binding.btnBatteryOptimization.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val pm = requireContext().getSystemService(android.content.Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(requireContext().packageName)) {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${requireContext().packageName}")
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Ya está desactivada la optimización de batería", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupDataButtons() {
        binding.btnClearHistory.setOnClickListener {
            lifecycleScope.launch {
                repository.clearAll()
                Toast.makeText(requireContext(), "Historial borrado", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExportCsv.setOnClickListener {
            lifecycleScope.launch {
                exportToCsv()
            }
        }
    }

    private suspend fun exportToCsv() {
        val payments = repository.getAllForExport()
        if (payments.isEmpty()) {
            Toast.makeText(requireContext(), "No hay pagos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        val dir = File(requireContext().cacheDir, "exports").apply { mkdirs() }
        val fileName = "pagovoz_historial_${System.currentTimeMillis()}.csv"
        val file = File(dir, fileName)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PE"))

        FileWriter(file).use { writer ->
            writer.append("App,Monto,Moneda,Remitente,Fecha\n")
            payments.forEach { p ->
                writer.append("${p.appDisplayName},${p.amount},${p.currencySymbol},${p.senderName ?: ""},${dateFormat.format(Date(p.timestamp))}\n")
            }
        }

        val uri = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.fileprovider", file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Exportar historial"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
