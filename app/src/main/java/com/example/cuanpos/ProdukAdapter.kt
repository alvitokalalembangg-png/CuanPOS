package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ProdukAdapter(
    private val listProduk: ArrayList<Produk>,
    private val onEditClick: (Produk) -> Unit,
    private val onDeleteClick: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_manage, parent, false)
        return ProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.bind(produk, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = listProduk.size

    class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaProduk: TextView = itemView.findViewById(R.id.tvNamaProdukManage)
        private val tvHargaProduk: TextView = itemView.findViewById(R.id.tvHargaProdukManage)
        private val tvStockProduk: TextView = itemView.findViewById(R.id.tvStockProdukManage)
        private val btnEdit: ImageView = itemView.findViewById(R.id.btnEditProdukManage)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteProdukManage)

        fun bind(produk: Produk, onEditClick: (Produk) -> Unit, onDeleteClick: (Produk) -> Unit) {
            tvNamaProduk.text = produk.name
            tvHargaProduk.text = formatRupiah(produk.price)
            tvStockProduk.text = "Stok: ${produk.stock}"

            btnEdit.setOnClickListener {
                onEditClick(produk)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(produk)
            }
        }

        private fun formatRupiah(number: Int): String {
            val localeID = Locale.forLanguageTag("id-ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            return numberFormat.format(number).replace("Rp", "Rp. ").replace(",00", "")
        }
    }
}