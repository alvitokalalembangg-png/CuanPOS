package com.example.cuanpos

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.ImageView
import android.content.Intent

class KasirActivity : AppCompatActivity() {

    // 1. Variabel Data Kuantitas Produk
    private var qtyKopi = 0
    private var qtyMatcha = 0
    private var qtyAmericano = 0
    private var qtyEarlGrey = 0
    private var qtyCroissant = 0
    private var qtyFrenchFries = 0
    private var qtyBrownies = 0
    private var qtySingkong = 0

    // 2. Data Harga Produk
    private val hargaKopi = 15000
    private val hargaMatcha = 22000
    private val hargaAmericano = 18000
    private val hargaEarlGrey = 16000
    private val hargaCroissant = 20000
    private val hargaFrenchFries = 18000
    private val hargaBrownies = 15000
    private val hargaSingkong = 14000

    // Komponen XML global agar bisa diakses seluruh fungsi
    private lateinit var tvKeranjangKosong: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var rowItemKopi: View
    private lateinit var tvDetailKopi: TextView
    private lateinit var tvSubtotalKopi: TextView
    private lateinit var rowItemMatcha: View
    private lateinit var tvDetailMatcha: TextView
    private lateinit var tvSubtotalMatcha: TextView
    private lateinit var rowItemAmericano: View
    private lateinit var tvDetailAmericano: TextView
    private lateinit var tvSubtotalAmericano: TextView
    private lateinit var rowItemEarlGrey: View
    private lateinit var tvDetailEarlGrey: TextView
    private lateinit var tvSubtotalEarlGrey: TextView
    private lateinit var rowItemCroissant: View
    private lateinit var tvDetailCroissant: TextView
    private lateinit var tvSubtotalCroissant: TextView
    private lateinit var rowItemFrenchFries: View
    private lateinit var tvDetailFrenchFries: TextView
    private lateinit var tvSubtotalFrenchFries: TextView
    private lateinit var rowItemBrownies: View
    private lateinit var tvDetailBrownies: TextView
    private lateinit var tvSubtotalBrownies: TextView
    private lateinit var rowItemSingkong: View
    private lateinit var tvDetailSingkong: TextView
    private lateinit var tvSubtotalSingkong: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kasir)
        // 1. Hubungkan ImageView tombol logout dari XML ke Kotlin
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)

// 2. Beri aksi ketika tombol diklik
        btnLogout.setOnClickListener {
            tunjukkanDialogLogout()
        }
        // Menangani tombol back fisik HP dengan cara modern (AndroidX)
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                tunjukkanDialogLogout()
            }
        })

        // ================= INISIALISASI KOMPONEN XML =================
        val tvKatMinuman = findViewById<TextView>(R.id.tvKatMinuman)
        val tvKatMakanan = findViewById<TextView>(R.id.tvKatMakanan)
        tvKeranjangKosong = findViewById(R.id.tvKeranjangKosong)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)

        val btnClearCart = findViewById<MaterialButton>(R.id.btnClearCart)
        val btnSaveOrder = findViewById<MaterialButton>(R.id.btnSaveOrder)
        val btnBayar = findViewById<MaterialButton>(R.id.btnBayar)

        val btnProdukKopi = findViewById<MaterialCardView>(R.id.btnProdukKopi)
        val btnProdukMatcha = findViewById<MaterialCardView>(R.id.btnProdukMatcha)
        val btnProdukAmericano = findViewById<MaterialCardView>(R.id.btnProdukAmericano)
        val btnProdukEarlGrey = findViewById<MaterialCardView>(R.id.btnProdukEarlGrey)

        val btnProdukCroissant = findViewById<MaterialCardView>(R.id.btnProdukCroissant)
        val btnProdukFrenchFries = findViewById<MaterialCardView>(R.id.btnProdukFrenchFries)
        val btnProdukBrownies = findViewById<MaterialCardView>(R.id.btnProdukBrownies)
        val btnProdukSingkong = findViewById<MaterialCardView>(R.id.btnProdukSingkong)

        rowItemKopi = findViewById(R.id.rowItemKopi)
        tvDetailKopi = findViewById(R.id.tvDetailKopi)
        tvSubtotalKopi = findViewById(R.id.tvSubtotalKopi)
        val btnKurangKopi = findViewById<MaterialButton>(R.id.btnKurangKopi)

        rowItemMatcha = findViewById(R.id.rowItemMatcha)
        tvDetailMatcha = findViewById(R.id.tvDetailMatcha)
        tvSubtotalMatcha = findViewById(R.id.tvSubtotalMatcha)
        val btnKurangMatcha = findViewById<MaterialButton>(R.id.btnKurangMatcha)

        rowItemAmericano = findViewById(R.id.rowItemAmericano)
        tvDetailAmericano = findViewById(R.id.tvDetailAmericano)
        tvSubtotalAmericano = findViewById(R.id.tvSubtotalAmericano)
        val btnKurangAmericano = findViewById<MaterialButton>(R.id.btnKurangAmericano)

        rowItemEarlGrey = findViewById(R.id.rowItemEarlGrey)
        tvDetailEarlGrey = findViewById(R.id.tvDetailEarlGrey)
        tvSubtotalEarlGrey = findViewById(R.id.tvSubtotalEarlGrey)
        val btnKurangEarlGrey = findViewById<MaterialButton>(R.id.btnKurangEarlGrey)

        rowItemCroissant = findViewById(R.id.rowItemCroissant)
        tvDetailCroissant = findViewById(R.id.tvDetailCroissant)
        tvSubtotalCroissant = findViewById(R.id.tvSubtotalCroissant)
        val btnKurangCroissant = findViewById<MaterialButton>(R.id.btnKurangCroissant)

        rowItemFrenchFries = findViewById(R.id.rowItemFrenchFries)
        tvDetailFrenchFries = findViewById(R.id.tvDetailFrenchFries)
        tvSubtotalFrenchFries = findViewById(R.id.tvSubtotalFrenchFries)
        val btnKurangFrenchFries = findViewById<MaterialButton>(R.id.btnKurangFrenchFries)

        rowItemBrownies = findViewById(R.id.rowItemBrownies)
        tvDetailBrownies = findViewById(R.id.tvDetailBrownies)
        tvSubtotalBrownies = findViewById(R.id.tvSubtotalBrownies)
        val btnKurangBrownies = findViewById<MaterialButton>(R.id.btnKurangBrownies)

        rowItemSingkong = findViewById(R.id.rowItemSingkong)
        tvDetailSingkong = findViewById(R.id.tvDetailSingkong)
        tvSubtotalSingkong = findViewById(R.id.tvSubtotalSingkong)
        val btnKurangSingkong = findViewById<MaterialButton>(R.id.btnKurangSingkong)

        val warnaBiruCustom = Color.parseColor("#0039ff")
        val warnaAbuMuted = Color.parseColor("#7A7A7A")

        // ================= INTERAKSI KLIK TAMBAH PRODUK =================
        btnProdukKopi.setOnClickListener { qtyKopi++; updateKeranjangBelanja() }
        btnProdukMatcha.setOnClickListener { qtyMatcha++; updateKeranjangBelanja() }
        btnProdukAmericano.setOnClickListener { qtyAmericano++; updateKeranjangBelanja() }
        btnProdukEarlGrey.setOnClickListener { qtyEarlGrey++; updateKeranjangBelanja() }
        btnProdukCroissant.setOnClickListener { qtyCroissant++; updateKeranjangBelanja() }
        btnProdukFrenchFries.setOnClickListener { qtyFrenchFries++; updateKeranjangBelanja() }
        btnProdukBrownies.setOnClickListener { qtyBrownies++; updateKeranjangBelanja() }
        btnProdukSingkong.setOnClickListener { qtySingkong++; updateKeranjangBelanja() }

        // ================= INTERAKSI TOMBOL MINUS (-) DI KERANJANG =================
        btnKurangKopi.setOnClickListener { if (qtyKopi > 0) qtyKopi--; updateKeranjangBelanja() }
        btnKurangMatcha.setOnClickListener { if (qtyMatcha > 0) qtyMatcha--; updateKeranjangBelanja() }
        btnKurangAmericano.setOnClickListener { if (qtyAmericano > 0) qtyAmericano--; updateKeranjangBelanja() }
        btnKurangEarlGrey.setOnClickListener { if (qtyEarlGrey > 0) qtyEarlGrey--; updateKeranjangBelanja() }
        btnKurangCroissant.setOnClickListener { if (qtyCroissant > 0) qtyCroissant--; updateKeranjangBelanja() }
        btnKurangFrenchFries.setOnClickListener { if (qtyFrenchFries > 0) qtyFrenchFries--; updateKeranjangBelanja() }
        btnKurangBrownies.setOnClickListener { if (qtyBrownies > 0) qtyBrownies--; updateKeranjangBelanja() }
        btnKurangSingkong.setOnClickListener { if (qtySingkong > 0) qtySingkong--; updateKeranjangBelanja() }

        btnClearCart.setOnClickListener { bersihkanSemuaKeranjang() }

        // ================= FILTER MENU BERDASARKAN KATEGORI =================
        fun tampilkanMenuBerdasarkanKategori(isMinuman: Boolean) {
            if (isMinuman) {
                tvKatMinuman.setTextColor(warnaBiruCustom)
                tvKatMinuman.setTypeface(null, Typeface.BOLD)
                tvKatMakanan.setTextColor(warnaAbuMuted)
                tvKatMakanan.setTypeface(null, Typeface.NORMAL)

                btnProdukKopi.visibility = View.VISIBLE
                btnProdukMatcha.visibility = View.VISIBLE
                btnProdukAmericano.visibility = View.VISIBLE
                btnProdukEarlGrey.visibility = View.VISIBLE

                btnProdukCroissant.visibility = View.GONE
                btnProdukFrenchFries.visibility = View.GONE
                btnProdukBrownies.visibility = View.GONE
                btnProdukSingkong.visibility = View.GONE
            } else {
                tvKatMakanan.setTextColor(warnaBiruCustom)
                tvKatMakanan.setTypeface(null, Typeface.BOLD)
                tvKatMinuman.setTextColor(warnaAbuMuted)
                tvKatMinuman.setTypeface(null, Typeface.NORMAL)

                btnProdukKopi.visibility = View.GONE
                btnProdukMatcha.visibility = View.GONE
                btnProdukAmericano.visibility = View.GONE
                btnProdukEarlGrey.visibility = View.GONE

                btnProdukCroissant.visibility = View.VISIBLE
                btnProdukFrenchFries.visibility = View.VISIBLE
                btnProdukBrownies.visibility = View.VISIBLE
                btnProdukSingkong.visibility = View.VISIBLE
            }
        }

        tampilkanMenuBerdasarkanKategori(isMinuman = true)
        tvKatMinuman.setOnClickListener { tampilkanMenuBerdasarkanKategori(isMinuman = true) }
        tvKatMakanan.setOnClickListener { tampilkanMenuBerdasarkanKategori(isMinuman = false) }

        // ================= LOGIKA TOMBOL SAVE BILL =================
        btnSaveOrder.setOnClickListener {
            val totalTagihan = (qtyKopi * hargaKopi) + (qtyMatcha * hargaMatcha) +
                    (qtyAmericano * hargaAmericano) + (qtyEarlGrey * hargaEarlGrey) +
                    (qtyCroissant * hargaCroissant) + (qtyFrenchFries * hargaFrenchFries) +
                    (qtyBrownies * hargaBrownies) + (qtySingkong * hargaSingkong)

            if (totalTagihan == 0) {
                tampilkanRiwayatTransaksi()
                return@setOnClickListener
            }

            val ringkasanPesanan = StringBuilder()
            if (qtyKopi > 0) ringkasanPesanan.append("Es Kopi Susu x$qtyKopi\n")
            if (qtyMatcha > 0) ringkasanPesanan.append("Matcha Latte x$qtyMatcha\n")
            if (qtyAmericano > 0) ringkasanPesanan.append("Iced Americano x$qtyAmericano\n")
            if (qtyEarlGrey > 0) ringkasanPesanan.append("Earl Grey Tea x$qtyEarlGrey\n")
            if (qtyCroissant > 0) ringkasanPesanan.append("Croissant x$qtyCroissant\n")
            if (qtyFrenchFries > 0) ringkasanPesanan.append("French Fries x$qtyFrenchFries\n")
            if (qtyBrownies > 0) ringkasanPesanan.append("Choco Brownies x$qtyBrownies\n")
            if (qtySingkong > 0) ringkasanPesanan.append("Singkong Goreng x$qtySingkong\n")

            val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
            val idBill = "BILL_" + System.currentTimeMillis()
            val dataSimpan = "${ringkasanPesanan.toString().trim()} | Total: ${formatRupiah(totalTagihan)}"

            sharedPref.edit().putString(idBill, dataSimpan).apply()

            Toast.makeText(this, "Bill Berhasil Disimpan di Antrean!", Toast.LENGTH_LONG).show()
            bersihkanSemuaKeranjang()
        }

        // ================= LOGIKA TOMBOL BAYAR =================
        btnBayar.setOnClickListener {
            val totalTagihan = (qtyKopi * hargaKopi) + (qtyMatcha * hargaMatcha) +
                    (qtyAmericano * hargaAmericano) + (qtyEarlGrey * hargaEarlGrey) +
                    (qtyCroissant * hargaCroissant) + (qtyFrenchFries * hargaFrenchFries) +
                    (qtyBrownies * hargaBrownies) + (qtySingkong * hargaSingkong)

            if (totalTagihan == 0) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dialogView = layoutInflater.inflate(R.layout.dialog_pembayaran, null)
            val tvDialogTotal = dialogView.findViewById<TextView>(R.id.tvDialogTotal)
            val etUangDiterima = dialogView.findViewById<EditText>(R.id.etUangDiterima)
            val tvDialogKembalian = dialogView.findViewById<TextView>(R.id.tvDialogKembalian)

            tvDialogTotal.text = formatRupiah(totalTagihan)

            val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(false)
            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            etUangDiterima.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val inputStr = s.toString().trim()
                    if (inputStr.isNotEmpty()) {
                        try {
                            val uangDiterima = inputStr.toLong() // Perbaikan 1: Pindah ke Long agar anti-crash nominal besar
                            val kembalian = uangDiterima - totalTagihan
                            if (kembalian >= 0) {
                                tvDialogKembalian.text = formatRupiah(kembalian.toInt())
                                tvDialogKembalian.setTextColor(Color.parseColor("#4CAF50"))
                            } else {
                                tvDialogKembalian.text = "Uang Kurang!"
                                tvDialogKembalian.setTextColor(Color.RED)
                            }
                        } catch (e: NumberFormatException) {
                            tvDialogKembalian.text = "Nominal Terlalu Besar"
                        }
                    } else {
                        tvDialogKembalian.text = "Rp. 0"
                        tvDialogKembalian.setTextColor(Color.BLACK)
                    }
                }
            })

            dialogView.findViewById<MaterialButton>(R.id.btnDialogBatal).setOnClickListener { alertDialog.dismiss() }

            dialogView.findViewById<MaterialButton>(R.id.btnDialogSelesai).setOnClickListener {
                val inputStr = etUangDiterima.text.toString().trim()
                if (inputStr.isNotEmpty()) {
                    try {
                        val uangDiterima = inputStr.toLong() // Perbaikan 2: Diubah ke Long agar aman saat diklik selesai
                        if (uangDiterima >= totalTagihan) {

                            val ringkasanPesanan = StringBuilder()
                            val ringkasanPenyimpanan = StringBuilder()

                            // Format Tampilan Struk Digital agar Rata Kanan Kiri Monospace Sempurna
                            fun formatBarisStruk(nama: String, qty: String): String {
                                val spaceCount = maxOf(1, 32 - nama.length - qty.length)
                                return nama + " ".repeat(spaceCount) + qty + "\n"
                            }

                            if (qtyKopi > 0) ringkasanPesanan.append(formatBarisStruk("Es Kopi Susu", "x$qtyKopi"))
                            if (qtyMatcha > 0) ringkasanPesanan.append(formatBarisStruk("Matcha Latte", "x$qtyMatcha"))
                            if (qtyAmericano > 0) ringkasanPesanan.append(formatBarisStruk("Iced Americano", "x$qtyAmericano"))
                            if (qtyEarlGrey > 0) ringkasanPesanan.append(formatBarisStruk("Earl Grey Tea", "x$qtyEarlGrey"))
                            if (qtyCroissant > 0) ringkasanPesanan.append(formatBarisStruk("Croissant", "x$qtyCroissant"))
                            if (qtyFrenchFries > 0) ringkasanPesanan.append(formatBarisStruk("French Fries", "x$qtyFrenchFries"))
                            if (qtyBrownies > 0) ringkasanPesanan.append(formatBarisStruk("Choco Brownies", "x$qtyBrownies"))
                            if (qtySingkong > 0) ringkasanPesanan.append(formatBarisStruk("Singkong Goreng", "x$qtySingkong"))

                            // Format Simpan Database (Tetap Konsisten untuk Riwayat)
                            if (qtyKopi > 0) ringkasanPenyimpanan.append("Es Kopi Susu x$qtyKopi\n")
                            if (qtyMatcha > 0) ringkasanPenyimpanan.append("Matcha Latte x$qtyMatcha\n")
                            if (qtyAmericano > 0) ringkasanPenyimpanan.append("Iced Americano x$qtyAmericano\n")
                            if (qtyEarlGrey > 0) ringkasanPenyimpanan.append("Earl Grey Tea x$qtyEarlGrey\n")
                            if (qtyCroissant > 0) ringkasanPenyimpanan.append("Croissant x$qtyCroissant\n")
                            if (qtyFrenchFries > 0) ringkasanPenyimpanan.append("French Fries x$qtyFrenchFries\n")
                            if (qtyBrownies > 0) ringkasanPenyimpanan.append("Choco Brownies x$qtyBrownies\n")
                            if (qtySingkong > 0) ringkasanPenyimpanan.append("Singkong Goreng x$qtySingkong\n")

                            val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
                            val idTrx = "TRX_" + System.currentTimeMillis()
                            val dataSimpan = "${ringkasanPenyimpanan.toString().trim()} | Total: ${formatRupiah(totalTagihan)}"

                            sharedPref.edit().putString(idTrx, dataSimpan).apply()
                            alertDialog.dismiss()

                            tampilkanStrukDigital(
                                ringkasanPesanan.toString().trim(),
                                totalTagihan,
                                uangDiterima.toInt(),
                                (uangDiterima - totalTagihan).toInt()
                            )
                        } else {
                            Toast.makeText(this@KasirActivity, "Pembayaran belum cukup!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this@KasirActivity, "Nominal input terlalu besar!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@KasirActivity, "Masukkan jumlah uang terlebih dahulu!", Toast.LENGTH_SHORT).show()
                }
            }
            alertDialog.show()
        }
    }

    // ================= FUNGSI FORMAT UTILITY INDEPENDEN =================

    private fun formatRupiah(number: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp. ").replace(",00", "")
    }

    private fun updateKeranjangBelanja() {
        val totalKopi = qtyKopi * hargaKopi
        val totalMatcha = qtyMatcha * hargaMatcha
        val totalAmericano = qtyAmericano * hargaAmericano
        val totalEarlGrey = qtyEarlGrey * hargaEarlGrey
        val totalCroissant = qtyCroissant * hargaCroissant
        val totalFrenchFries = qtyFrenchFries * hargaFrenchFries
        val totalBrownies = qtyBrownies * hargaBrownies
        val totalSingkong = qtySingkong * hargaSingkong

        val totalTagihan = totalKopi + totalMatcha + totalAmericano + totalEarlGrey +
                totalCroissant + totalFrenchFries + totalBrownies + totalSingkong

        if (qtyKopi > 0) {
            rowItemKopi.visibility = View.VISIBLE
            tvDetailKopi.text = "Es Kopi Susu x$qtyKopi"
            tvSubtotalKopi.text = formatRupiah(totalKopi)
        } else { rowItemKopi.visibility = View.GONE }

        if (qtyMatcha > 0) {
            rowItemMatcha.visibility = View.VISIBLE
            tvDetailMatcha.text = "Matcha Latte x$qtyMatcha"
            tvSubtotalMatcha.text = formatRupiah(totalMatcha)
        } else { rowItemMatcha.visibility = View.GONE }

        if (qtyAmericano > 0) {
            rowItemAmericano.visibility = View.VISIBLE
            tvDetailAmericano.text = "Iced Americano x$qtyAmericano"
            tvSubtotalAmericano.text = formatRupiah(totalAmericano)
        } else { rowItemAmericano.visibility = View.GONE }

        if (qtyEarlGrey > 0) {
            rowItemEarlGrey.visibility = View.VISIBLE
            tvDetailEarlGrey.text = "Earl Grey Tea x$qtyEarlGrey"
            tvSubtotalEarlGrey.text = formatRupiah(totalEarlGrey)
        } else { rowItemEarlGrey.visibility = View.GONE }

        if (qtyCroissant > 0) {
            rowItemCroissant.visibility = View.VISIBLE
            tvDetailCroissant.text = "Croissant x$qtyCroissant"
            tvSubtotalCroissant.text = formatRupiah(totalCroissant)
        } else { rowItemCroissant.visibility = View.GONE }

        if (qtyFrenchFries > 0) {
            rowItemFrenchFries.visibility = View.VISIBLE
            tvDetailFrenchFries.text = "French Fries x$qtyFrenchFries"
            tvSubtotalFrenchFries.text = formatRupiah(totalFrenchFries)
        } else { rowItemFrenchFries.visibility = View.GONE }

        if (qtyBrownies > 0) {
            rowItemBrownies.visibility = View.VISIBLE
            tvDetailBrownies.text = "Choco Brownies x$qtyBrownies"
            tvSubtotalBrownies.text = formatRupiah(totalBrownies)
        } else { rowItemBrownies.visibility = View.GONE }

        if (qtySingkong > 0) {
            rowItemSingkong.visibility = View.VISIBLE
            tvDetailSingkong.text = "Singkong Goreng x$qtySingkong"
            tvSubtotalSingkong.text = formatRupiah(totalSingkong)
        } else { rowItemSingkong.visibility = View.GONE }

        tvKeranjangKosong.visibility = if (totalTagihan > 0) View.GONE else View.VISIBLE
        tvTotalHarga.text = formatRupiah(totalTagihan)
    }

    private fun bersihkanSemuaKeranjang() {
        qtyKopi = 0; qtyMatcha = 0; qtyAmericano = 0; qtyEarlGrey = 0
        qtyCroissant = 0; qtyFrenchFries = 0; qtyBrownies = 0; qtySingkong = 0
        updateKeranjangBelanja()
    }

    // ================= FUNGSI DIALOG RIWAYAT TRANSAKSI =================
    private fun tampilkanRiwayatTransaksi() {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val allEntries = sharedPref.all
            .filter { it.key.startsWith("BILL_") || it.key.startsWith("TRX_") }
            .toList()
            .sortedByDescending { it.first }

        val dialogView = layoutInflater.inflate(R.layout.dialog_riwayat, null)
        val containerRiwayat = dialogView.findViewById<LinearLayout>(R.id.containerRiwayat)
        val btnTutupRiwayat = dialogView.findViewById<MaterialButton>(R.id.btnTutupRiwayat)

        val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val customFont = try {
            androidx.core.content.res.ResourcesCompat.getFont(this, R.font.open_sauce_regular)
        } catch (e: Exception) { Typeface.create(Typeface.DEFAULT, Typeface.NORMAL) }

        val mediumFont = try {
            androidx.core.content.res.ResourcesCompat.getFont(this, R.font.open_sauce_medium)
        } catch (e: Exception) { Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }

        if (allEntries.isEmpty()) {
            val tvKosong = TextView(this).apply {
                text = "Belum ada riwayat transaksi hari ini."
                setTextColor(Color.GRAY)
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 40, 0, 40)
                typeface = customFont
            }
            containerRiwayat.addView(tvKosong)
        } else {
            for ((key, value) in allEntries) {
                val cardView = MaterialCardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 16) }
                    setCardBackgroundColor(if (key.startsWith("TRX_")) Color.parseColor("#E8F5E9") else Color.parseColor("#FFF3E0"))
                    radius = 24f
                    strokeWidth = 0
                }

                val layoutKonten = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(36, 28, 36, 28)
                }

                val tvJudul = TextView(this).apply {
                    text = if (key.startsWith("TRX_")) "[LUNAS]" else "[BELUM BAYAR]"
                    setTextColor(Color.BLACK)
                    textSize = 14f
                    typeface = mediumFont
                }

                val tvData = TextView(this).apply {
                    val rawData = value.toString()
                    val bersihItem = rawData.substringBefore(" | Total:").trim()

                    val nominalTotal = rawData.substringAfter("Total: Rp. ", "").substringAfter("Total: Rp ", "")
                        .substringBefore("\n").trim()
                        .ifEmpty { rawData.substringAfter("| Total:", "0").replace("Rp", "").replace(".", "").trim() }

                    val barisItemFormatted = bersihItem.split("\n").joinToString("\n") { baris ->
                        val item = baris.trim()
                        if (item.contains(" x")) {
                            val nama = item.substringBefore(" x").trim()
                            val qty = item.substringAfter(" x").trim()
                            val spaces = " ".repeat(maxOf(1, 30 - nama.length))
                            "$nama$spaces$qty"
                        } else if (item.contains("x")) {
                            val nama = item.substringBefore("x").trim()
                            val qty = "x" + item.substringAfter("x").trim()
                            val spaces = " ".repeat(maxOf(1, 30 - nama.length))
                            "$nama$spaces$qty"
                        } else {
                            item
                        }
                    }

                    val totalTeks = "Rp. " + nominalTotal.replace("Rp.", "").replace("Rp", "").trim()
                    val spacesTotal = " ".repeat(maxOf(1, 26 - "total".length))
                    val barisTotalFormatted = "total$spacesTotal$totalTeks"

                    text = "--------------------------------\n$barisItemFormatted\n--------------------------------\n$barisTotalFormatted"
                    setTextColor(Color.BLACK)
                    textSize = 13f
                    typeface = Typeface.MONOSPACE
                }

                layoutKonten.addView(tvJudul)
                layoutKonten.addView(tvData)
                cardView.addView(layoutKonten)

                // Fitur klik pas diclick pada antrean BILL / BELUM BAYAR
                if (key.startsWith("BILL_")) {
                    cardView.setOnClickListener {
                        val dataStr = value.toString()
                        bersihkanSemuaKeranjang()

                        // OPTIMASI: Ambil daftar item yang bersih sebelum tanda pembatas "| Total:"
                        val bersihItem = dataStr.substringBefore(" | Total:").trim()
                        // Pisahkan per baris berdasarkan enter (\n)
                        val barisTeks = bersihItem.split("\n")

                        // Fungsi pembantu parsing yang diperbaiki total
                        fun ambilKuantitasDariTeks(namaMenu: String): Int {
                            val barisKetemu = barisTeks.find { it.contains(namaMenu) } ?: return 0
                            // Ambil angka setelah huruf 'x' secara aman
                            val setelahX = barisKetemu.substringAfter("x", "").trim()
                            return setelahX.toIntOrNull() ?: 0
                        }

                        // Mengisi kembali variabel kuantitas dengan data yang akurat
                        qtyKopi = ambilKuantitasDariTeks("Es Kopi Susu")
                        qtyMatcha = ambilKuantitasDariTeks("Matcha Latte")
                        qtyAmericano = ambilKuantitasDariTeks("Iced Americano")
                        qtyEarlGrey = ambilKuantitasDariTeks("Earl Grey Tea")
                        qtyCroissant = ambilKuantitasDariTeks("Croissant")
                        qtyFrenchFries = ambilKuantitasDariTeks("French Fries")
                        qtyBrownies = ambilKuantitasDariTeks("Choco Brownies")
                        qtySingkong = ambilKuantitasDariTeks("Singkong Goreng")

                        // Render ulang tampilan keranjang belanja
                        updateKeranjangBelanja()

                        // Hapus bill tua dari antrean SharedPreferences agar tidak menumpuk
                        sharedPref.edit().remove(key).apply()
                        Toast.makeText(this@KasirActivity, "Bill berhasil dimuat kembali!", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }
                }
                containerRiwayat.addView(cardView)
            }
        }

        btnTutupRiwayat.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    // ================= FUNGSI DIALOG STRUK DIGITAL =================
    private fun tampilkanStrukDigital(detailPesanan: String, total: Int, uang: Int, kembalian: Int) {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val witaTgl = sdf.format(Date())

        val formatTotal = formatRupiah(total)
        val formatBayar = formatRupiah(uang)
        val formatKembali = formatRupiah(kembalian)

        val barisTotal = "Total".padEnd(32 - formatTotal.length) + formatTotal
        val barisBayar = "Bayar".padEnd(32 - formatBayar.length) + formatBayar
        val barisKembali = "Kembali".padEnd(32 - formatKembali.length) + formatKembali

        val strukText = """
================================
            CuanPOS            
================================
      $witaTgl      
--------------------------------
Items:
$detailPesanan
--------------------------------
$barisTotal
$barisBayar
$barisKembali
================================
       Terima Kasih Atas        
         Kunjungan Anda         
================================
        """.trimIndent()

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val textViewStruk = TextView(this).apply {
            text = strukText
            textSize = 15f
            setTextColor(Color.BLACK)
            typeface = Typeface.MONOSPACE
            textAlignment = View.TEXT_ALIGNMENT_TEXT_START

            setPadding(55, 40, 20, 40)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER_HORIZONTAL
            }
        }
        scrollView.addView(textViewStruk)

        AlertDialog.Builder(this)
            .setTitle("")
            .setView(scrollView)
            .setPositiveButton("Selesai") { _, _ -> bersihkanSemuaKeranjang() }
            .setCancelable(false)
            .show()
    }
    private fun tunjukkanDialogLogout() {
        // 1. Inflate layout custom dialog_logout yang baru dibuat
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()

        // Agar background luar MaterialCardView transparan dan rounded corner-nya terlihat rapi
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 2. Hubungkan komponen MaterialButton dari layout custom
        val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btnBatalLogout)
        val btnYa = dialogView.findViewById<MaterialButton>(R.id.btnYaLogout)

        // 3. Logika Aksi Tombol BATAL (Menutup dialog saja)
        btnBatal.setOnClickListener {
            dialog.dismiss()
        }

        // 4. Logika Aksi Tombol KELUAR (Pindah ke halaman utama/Login)
        btnYa.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }
}