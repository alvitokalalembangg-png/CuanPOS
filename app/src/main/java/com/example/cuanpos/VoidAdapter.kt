package com.example.cuanpos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class VoidAdapter(private val listVoid: ArrayList<VoidTransaksi>) :
    RecyclerView.Adapter<VoidAdapter.ViewHolder>() {

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
        holder.tvId.text = item.idTransaksi
        holder.tvTotal.text = item.total
        holder.tvAlasan.text = "Alasan: ${item.alasan}"

        holder.btnTolak.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${item.idTransaksi} Ditolak", Toast.LENGTH_SHORT).show()
            listVoid.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listVoid.size)
        }

        holder.btnSetujui.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${item.idTransaksi} Disetujui (Void)", Toast.LENGTH_SHORT).show()
            listVoid.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listVoid.size)
        }
    }

    override fun getItemCount(): Int = listVoid.size
}