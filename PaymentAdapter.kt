package com.pagovoz.app.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pagovoz.app.data.PaymentEntity
import com.pagovoz.app.databinding.ItemPaymentBinding
import com.pagovoz.app.util.PaymentParser
import com.pagovoz.app.util.SupportedApps
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentAdapter : ListAdapter<PaymentEntity, PaymentAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PaymentEntity>() {
            override fun areItemsTheSame(oldItem: PaymentEntity, newItem: PaymentEntity) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: PaymentEntity, newItem: PaymentEntity) =
                oldItem == newItem
        }
        private val timeFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "PE"))
    }

    inner class VH(val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvItemApp.text = item.appDisplayName
            tvItemSender.text = item.senderName ?: "Remitente no identificado"
            tvItemTime.text = timeFormat.format(Date(item.timestamp))
            tvItemAmount.text = PaymentParser.formatAmount(item.amount, item.currencySymbol)
            val app = SupportedApps.findByPackage(item.packageName)
            try {
                viewAppColorDot.setBackgroundColor(Color.parseColor(app?.colorHex ?: "#7B2FE0"))
            } catch (_: Exception) { }
        }
    }
}
