package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProdukTerlarisAdapter(private val listProduk: List<ProdukTerlaris>) :
    RecyclerView.Adapter<ProdukTerlarisAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvNamaProduk: TextView = view.findViewById(R.id.tvNamaProdukTerlaris)
        val tvJumlahTerjual: TextView = view.findViewById(R.id.tvJumlahTerjual)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk_terlaris, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.tvRank.text = produk. peringkat.toString()
        holder.tvNamaProduk.text = produk.namaProduk
        holder.tvJumlahTerjual.text = "${produk.jumlahTerjual} ${produk.satuan}"
    }

    override fun getItemCount(): Int = listProduk.size
}