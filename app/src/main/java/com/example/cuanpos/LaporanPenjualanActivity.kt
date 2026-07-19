package com.example.cuanpos

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class LaporanPenjualanActivity : AppCompatActivity() {

    private lateinit var tvLabelTotal: TextView
    private lateinit var tvTotalPendapatan: TextView
    private lateinit var rvTransaksi: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_penjualan)

        // 1. Inisialisasi View
        val btnBack = findViewById<ImageView>(R.id.btnBackKeAdmin)
        tvLabelTotal = findViewById(R.id.tvLabelTotal)
        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan)
        rvTransaksi = findViewById(R.id.rvTransaksi)

        val chipHarian = findViewById<Chip>(R.id.chipHarian)
        val chipMingguan = findViewById<Chip>(R.id.chipMingguan)
        val chipBulanan = findViewById<Chip>(R.id.chipBulanan)

        // 2. Konfigurasi RecyclerView
        rvTransaksi.layoutManager = LinearLayoutManager(this)

        // 3. Aksi Kembali
        btnBack.setOnClickListener {
            finish()
        }

        // 4. Logika Filter Berdasarkan Pilihan Chip
        chipHarian.setOnClickListener { updateLaporan("Harian") }
        chipMingguan.setOnClickListener { updateLaporan("Mingguan") }
        chipBulanan.setOnClickListener { updateLaporan("Bulanan") }

        // Load awal data default (Harian)
        updateLaporan("Harian")
    }

    private fun updateLaporan(periode: String) {
        val listDummy = ArrayList<ProdukTerlaris>()

        when (periode) {
            "Harian" -> {
                tvLabelTotal.text = "Total Pendapatan Hari Ini"
                tvTotalPendapatan.text = "Rp 350.000"

                // Data Dummy Harian
                listDummy.add(ProdukTerlaris(1, "Es Pisang Ijo", 12, "Porsi"))
                listDummy.add(ProdukTerlaris(2, "Nasi Goreng Cuan", 8, "Porsi"))
                listDummy.add(ProdukTerlaris(3, "Es Teh Manis", 5, "Gelas"))
            }
            "Mingguan" -> {
                tvLabelTotal.text = "Total Pendapatan Minggu Ini"
                tvTotalPendapatan.text = "Rp 2.450.000"

                // Data Dummy Mingguan
                listDummy.add(ProdukTerlaris(1, "Es Pisang Ijo", 94, "Porsi"))
                listDummy.add(ProdukTerlaris(2, "Nasi Goreng Cuan", 72, "Porsi"))
                listDummy.add(ProdukTerlaris(3, "Es Teh Manis", 45, "Gelas"))
                listDummy.add(ProdukTerlaris(4, "Ayam Geprek", 30, "Porsi"))
            }
            "Bulanan" -> {
                tvLabelTotal.text = "Total Pendapatan Bulan Ini"
                tvTotalPendapatan.text = "Rp 12.800.000"

                // Data Dummy Bulanan
                listDummy.add(ProdukTerlaris(1, "Es Pisang Ijo", 412, "Porsi"))
                listDummy.add(ProdukTerlaris(2, "Nasi Goreng Cuan", 320, "Porsi"))
                listDummy.add(ProdukTerlaris(3, "Es Teh Manis", 195, "Gelas"))
                listDummy.add(ProdukTerlaris(4, "Ayam Geprek", 140, "Porsi"))
                listDummy.add(ProdukTerlaris(5, "Kopi Susu Cuan", 88, "Cup"))
            }
        }

        // Set adapter dengan data dummy yang sudah dipilih
        rvTransaksi.adapter = ProdukTerlarisAdapter(listDummy)
    }
}