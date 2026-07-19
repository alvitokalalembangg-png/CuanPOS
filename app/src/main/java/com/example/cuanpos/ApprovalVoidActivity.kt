package com.example.cuanpos

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApprovalVoidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_void)

        val btnBack = findViewById<ImageView>(R.id.btnBackKeAdmin)
        val rvVoid = findViewById<RecyclerView>(R.id.rvVoid)

        btnBack.setOnClickListener { finish() }

        // Setup RecyclerView
        rvVoid.layoutManager = LinearLayoutManager(this)

        // Data Dummy Pengajuan Void
        val listDummy = ArrayList<VoidTransaksi>()
        listDummy.add(VoidTransaksi("#TRX-8821", "Rp 55.000", "Pelanggan membatalkan pesanan kopi"))
        listDummy.add(VoidTransaksi("#TRX-8825", "Rp 120.000", "Salah input kuantitas makanan"))
        listDummy.add(VoidTransaksi("#TRX-8829", "Rp 32.000", "Menu yang dipesan ternyata habis"))

        rvVoid.adapter = VoidAdapter(listDummy)
    }
}