package com.pagovoz.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pagovoz.app.data.PaymentRepository
import com.pagovoz.app.databinding.FragmentPaymentListBinding
import com.pagovoz.app.ui.PaymentAdapter
import com.pagovoz.app.util.PaymentParser

/**
 * Una pestaña del historial. Si packageName es null muestra TODOS los pagos
 * (pestaña "Todos"); si no, filtra solo esa app (Yape, Plin, etc.).
 */
class PaymentListFragment : Fragment() {

    companion object {
        private const val ARG_PACKAGE = "arg_package"

        fun newInstance(packageName: String?): PaymentListFragment {
            val f = PaymentListFragment()
            f.arguments = Bundle().apply { putString(ARG_PACKAGE, packageName) }
            return f
        }
    }

    private var _binding: FragmentPaymentListBinding? = null
    private val binding get() = _binding!!
    private val adapter = PaymentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pkg = arguments?.getString(ARG_PACKAGE)
        val repository = PaymentRepository(requireContext())

        binding.rvPaymentsTab.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPaymentsTab.adapter = adapter

        val liveList = if (pkg == null) repository.getAllLive() else repository.getByAppLive(pkg)
        liveList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvTabEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvPaymentsTab.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE

            val total = list.sumOf { it.amount }
            binding.tvTabTotal.text = PaymentParser.formatAmount(total, "S/")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
