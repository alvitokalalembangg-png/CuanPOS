package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

class KasirProdukAdapter(
    private val listProduk: List<Produk>,
    private val onProductClick: (Produk) -> Unit
) : RecyclerView.Adapter<KasirProdukAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view as MaterialCardView
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaProduk)
        val tvStock: TextView = view.findViewById(R.id.tvStockProduk)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.tvNama.text = produk.name
        holder.tvHarga.text = formatRupiah(produk.price)
        holder.tvStock.text = "Stok: ${produk.stock}"
        holder.cardView.setOnClickListener { onProductClick(produk) }
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale.forLanguageTag("id-ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp. ").replace(",00", "")
    }

    override fun getItemCount(): Int = listProduk.size
}