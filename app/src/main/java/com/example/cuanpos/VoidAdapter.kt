package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cuanpos.network.TransactionResponse
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class VoidAdapter(
    private val listVoid: ArrayList<TransactionResponse>,
    private val onActionClick: (TransactionResponse, Boolean) -> Unit // Boolean: true = approve, false = reject
) : RecyclerView.Adapter<VoidAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvIdTransaksi)
        val tvTotal: TextView = view.findViewById(R.id.tvTotalVoid)
        val tvAlasan: TextView = view.findViewById(R.id.tvAlasanVoid)
        val btnTolak: MaterialButton = view.findViewById(R.id.btnTolakVoid)
        val btnSetujui: MaterialButton = view.findViewById(R.id.btnSetujuiVoid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_void, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listVoid[position]
        holder.tvId.text = "#TRX-${item.id}"
        holder.tvTotal.text = formatRupiah(item.total_amount)
        holder.tvAlasan.text = "Status: ${item.status}"

        holder.btnTolak.setOnClickListener {
            onActionClick(item, false)
        }

        holder.btnSetujui.setOnClickListener {
            onActionClick(item, true)
        }
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale.forLanguageTag("id-ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp. ").replace(",00", "")
    }

    override fun getItemCount(): Int = listVoid.size
}