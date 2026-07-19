package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KategoriAdapter(
    private val listKategori: ArrayList<Kategori>,
    private val onDeleteClick: (Kategori) -> Unit
) : RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kategori, parent, false)
        return KategoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val kategori = listKategori[position]
        holder.bind(kategori, onDeleteClick)
    }

    override fun getItemCount(): Int = listKategori.size

    class KategoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaKategoriItem: TextView = itemView.findViewById(R.id.tvNamaKategoriItem)
        private val btnHapusKategori: ImageView = itemView.findViewById(R.id.btnHapusKategori)

        fun bind(kategori: Kategori, onDeleteClick: (Kategori) -> Unit) {
            tvNamaKategoriItem.text = kategori.nama

            btnHapusKategori.setOnClickListener {
                onDeleteClick(kategori)
            }
        }
    }
}