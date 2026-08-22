package com.pagovoz.app.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pagovoz.app.data.PaymentRepository
import com.pagovoz.app.databinding.FragmentAlertsBinding
import com.pagovoz.app.ui.PaymentAdapter
import com.pagovoz.app.util.PaymentParser
import java.util.Calendar

class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: PaymentRepository
    private val adapter = PaymentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = PaymentRepository(requireContext())

        binding.rvAlerts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlerts.adapter = adapter

        repository.getRecentLive(50).observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvAlerts.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis

        repository.getTotalSince(startOfDay).observe(viewLifecycleOwner) { total ->
            binding.tvTodayTotal.text = PaymentParser.formatAmount(total ?: 0.0, "S/")
        }

        updateListenerStatus()
    }

    private fun updateListenerStatus() {
        val enabledListeners = android.provider.Settings.Secure.getString(
            requireContext().contentResolver, "enabled_notification_listeners"
        ) ?: ""
        val isEnabled = enabledListeners.contains(requireContext().packageName)
        binding.tvListenerStatus.text = if (isEnabled) {
            "● Escuchando notificaciones"
        } else {
            "● Permiso de notificaciones desactivado — actívalo en Ajustes"
        }
    }

    override fun onResume() {
        super.onResume()
        updateListenerStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
