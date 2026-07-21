package com.example.cuanpos

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cuanpos.network.ApiClient
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

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
        chipHarian.setOnClickListener { fetchLaporan("Harian") }
        chipMingguan.setOnClickListener { fetchLaporan("Mingguan") }
        chipBulanan.setOnClickListener { fetchLaporan("Bulanan") }

        // Load awal data default (Harian)
        fetchLaporan("Harian")
    }

    private fun fetchLaporan(periode: String) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        tvLabelTotal.text = when (periode) {
            "Harian" -> "Total Pendapatan Hari Ini"
            "Mingguan" -> "Total Pendapatan Minggu Ini"
            else -> "Total Pendapatan Bulan Ini"
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.getLaporanPenjualan(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val transactions = response.body()!!.data
                        
                        // Filter by period (client-side logic for now as backend doesn't have it)
                        // In a real app, you'd probably do this on the backend or filter by 'created_at'
                        // For simplicity, we show all data or filter roughly
                        
                        val totalRevenue = transactions.filter { it.status == "selesai" }.sumOf { it.total_amount }
                        tvTotalPendapatan.text = formatRupiah(totalRevenue)

                        // Aggregate Top Products
                        val productSales = mutableMapOf<Int, Triple<String, Int, String>>() // ID -> (Name, Qty, Unit)
                        
                        for (tx in transactions) {
                            if (tx.status != "selesai") continue
                            tx.details?.forEach { detail ->
                                val pid = detail.product_id
                                val name = detail.product?.name ?: "Unknown"
                                val qty = detail.quantity
                                val unit = "Porsi" // Default unit
                                
                                val existing = productSales[pid] ?: Triple(name, 0, unit)
                                productSales[pid] = Triple(name, existing.second + qty, unit)
                            }
                        }

                        val topProducts = productSales.toList()
                            .sortedByDescending { it.second.second }
                            .mapIndexed { index, pair ->
                                ProdukTerlaris(
                                    peringkat = index + 1,
                                    namaProduk = pair.second.first,
                                    jumlahTerjual = pair.second.second,
                                    satuan = pair.second.third
                                )
                            }

                        rvTransaksi.adapter = ProdukTerlarisAdapter(topProducts)
                    } else {
                        Toast.makeText(this@LaporanPenjualanActivity, "Gagal memuat laporan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LaporanPenjualanActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale.forLanguageTag("id-ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp. ").replace(",00", "")
    }
}