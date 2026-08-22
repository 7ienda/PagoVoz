package com.pagovoz.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.pagovoz.app.data.PaymentRepository
import com.pagovoz.app.databinding.FragmentHistoryBinding
import com.pagovoz.app.util.SupportedApps
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

/**
 * Historial de pagos con una pestaña "Todos" + una pestaña dedicada por
 * cada app de pago de la que se hayan recibido notificaciones (Yape,
 * Plin, Tunki, etc.), tal como se pidió.
 */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = PaymentRepository(requireContext())

        lifecycleScope.launch {
            val distinctApps = repository.getDistinctApps()
            val tabs = mutableListOf<Pair<String, String?>>()
            tabs.add("Todos" to null)
            distinctApps.forEach { app ->
                tabs.add(SupportedApps.displayNameFor(app.packageName) to app.packageName)
            }

            val pagerAdapter = HistoryPagerAdapter(childFragmentManager, lifecycle, tabs)
            binding.viewPager.adapter = pagerAdapter

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = tabs[position].first
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class HistoryPagerAdapter(
        fm: FragmentManager,
        lifecycle: Lifecycle,
        private val tabs: List<Pair<String, String?>>
    ) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = tabs.size
        override fun createFragment(position: Int): Fragment =
            PaymentListFragment.newInstance(tabs[position].second)
    }
}
