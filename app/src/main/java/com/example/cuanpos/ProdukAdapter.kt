package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProdukAdapter(
    private val listProduk: ArrayList<Produk>,
    private val onEditClick: (Produk) -> Unit,
    private val onDeleteClick: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        // Menghubungkan ke layout file item_produk.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return ProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.bind(produk, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = listProduk.size

    class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 1. Daftarkan semua View dari XML di sini
        private val tvNamaProdukSample: TextView = itemView.findViewById(R.id.tvNamaProdukSample)
        private val tvHargaProdukSample: TextView = itemView.findViewById(R.id.tvHargaProdukSample)
        private val tvKategoriProdukSample: TextView = itemView.findViewById(R.id.tvKategoriProdukSample)
        private val btnEditProdukSample: ImageView = itemView.findViewById(R.id.btnEditProdukSample)
        private val btnDeleteProdukSample: ImageView = itemView.findViewById(R.id.btnDeleteProdukSample)

        fun bind(produk: Produk, onEditClick: (Produk) -> Unit, onDeleteClick: (Produk) -> Unit) {
            // 2. Set data ke masing-masing View (Tanpa embel-embel itemView lagi)
            tvNamaProdukSample.text = produk.nama
            tvHargaProdukSample.text = "Rp ${produk.harga}"
            tvKategoriProdukSample.text = produk.kategori

            // 3. Set aksi klik tombol
            btnEditProdukSample.setOnClickListener {
                onEditClick(produk)
            }

            btnDeleteProdukSample.setOnClickListener {
                onDeleteClick(produk)
            }
        }
    }
}