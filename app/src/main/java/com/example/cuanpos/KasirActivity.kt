package com.example.cuanpos

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.cuanpos.network.ApiClient
import com.example.cuanpos.network.TransactionItemRequest
import com.example.cuanpos.network.TransactionRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KasirActivity : AppCompatActivity() {

    private val listProdukServer = mutableListOf<Produk>()
    private val listKategoriServer = mutableListOf<Kategori>()
    private val keranjangMap = mutableMapOf<Int, Int>()

    private lateinit var tvKeranjangKosong: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var containerKeranjangItems: LinearLayout
    private lateinit var rvKatalogProduk: RecyclerView
    private lateinit var containerKategoriKasir: LinearLayout

    private lateinit var kasirProdukAdapter: KasirProdukAdapter
    private var listProdukDisplay = mutableListOf<Produk>()

    private var kategoriAktifId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kasir)

        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val btnHistory = findViewById<ImageView>(R.id.btnHistory)
        val btnQueue = findViewById<ImageView>(R.id.btnQueue)

        btnLogout.setOnClickListener { tunjukkanDialogLogout() }
        btnHistory.setOnClickListener { tampilkanRiwayatTransaksi() }
        btnQueue.setOnClickListener { tampilkanAntreanBill() }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                tunjukkanDialogLogout()
            }
        })

        tvKeranjangKosong = findViewById(R.id.tvKeranjangKosong)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)

        rvKatalogProduk = findViewById(R.id.rvKatalogProduk)
        containerKeranjangItems = findViewById(R.id.containerKeranjangItems)
        containerKategoriKasir = findViewById(R.id.containerKategoriKasir)

        // Setup RecyclerView Katalog
        kasirProdukAdapter = KasirProdukAdapter(listProdukDisplay) { produk ->
            val currentQty = keranjangMap.getOrDefault(produk.id, 0)
            if (produk.stock <= 0) {
                Toast.makeText(this@KasirActivity, "Stok habis!", Toast.LENGTH_SHORT).show()
            } else if (currentQty >= produk.stock) {
                Toast.makeText(this@KasirActivity, "Stok tidak mencukupi!", Toast.LENGTH_SHORT).show()
            } else {
                keranjangMap[produk.id] = currentQty + 1
                updateKeranjangBelanja()
            }
        }
        rvKatalogProduk.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        rvKatalogProduk.adapter = kasirProdukAdapter
        rvKatalogProduk.isNestedScrollingEnabled = false // Let parent ScrollView handle it

        val btnClearCart = findViewById<MaterialButton>(R.id.btnClearCart)
        val btnSaveOrder = findViewById<MaterialButton>(R.id.btnSaveOrder)
        val btnBayar = findViewById<MaterialButton>(R.id.btnBayar)

        btnClearCart.setOnClickListener { bersihkanSemuaKeranjang() }
        btnSaveOrder.setOnClickListener { simpanBillKeAntrean() }
        btnBayar.setOnClickListener { prosesPembayaran() }

        fetchDataDariServer()
    }

    private fun fetchDataDariServer() {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val responseKategori = ApiClient.instance.getCategories(token)
                val responseProduk = ApiClient.instance.getProducts(token)

                withContext(Dispatchers.Main) {
                    if (responseKategori.isSuccessful && responseKategori.body() != null) {
                        listKategoriServer.clear()
                        listKategoriServer.addAll(responseKategori.body()!!)
                    }

                    if (responseProduk.isSuccessful && responseProduk.body() != null) {
                        listProdukServer.clear()
                        listProdukServer.addAll(responseProduk.body()!!)
                    }

                    if (listKategoriServer.isNotEmpty()) {
                        kategoriAktifId = listKategoriServer[0].id
                    }

                    renderKategoriKeUI()
                    renderKatalogKeUI()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KasirActivity, "Kesalahan koneksi ke server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderKategoriKeUI() {
        containerKategoriKasir.removeAllViews()

        for (kategori in listKategoriServer) {
            val isSelected = (kategori.id == kategoriAktifId)

            val btnBubble = MaterialButton(this).apply {
                text = kategori.name
                textSize = 13f
                isAllCaps = false

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 16, 0)
                }

                cornerRadius = 24

                if (isSelected) {
                    backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#0039ff"))
                    setTextColor(Color.WHITE)
                } else {
                    backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#EFEFEF"))
                    setTextColor(Color.parseColor("#4A4A4A"))
                }

                setOnClickListener {
                    kategoriAktifId = kategori.id
                    renderKategoriKeUI()
                    renderKatalogKeUI()
                }
            }
            containerKategoriKasir.addView(btnBubble)
        }
    }

    private fun renderKatalogKeUI() {
        listProdukDisplay.clear()
        listProdukDisplay.addAll(listProdukServer.filter { it.category_id == kategoriAktifId })
        kasirProdukAdapter.notifyDataSetChanged()
    }

    private fun updateKeranjangBelanja() {
        var totalTagihan = 0

        containerKeranjangItems.removeAllViews()
        containerKeranjangItems.addView(tvKeranjangKosong)

        for ((idProduk, qty) in keranjangMap) {
            if (qty > 0) {
                val produk = listProdukServer.find { it.id == idProduk } ?: continue
                totalTagihan += produk.price * qty

                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 16) }
                }

                val tvDetail = TextView(this).apply {
                    text = "${produk.name} x$qty"
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val tvSubtotal = TextView(this).apply {
                    text = formatRupiah(produk.price * qty)
                    setTextColor(Color.BLACK)
                    setPadding(0, 0, 24, 0)
                }

                val btnMinus = MaterialButton(this).apply {
                    text = "-"
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(110, 110)
                    setPadding(0,0,0,0)
                    cornerRadius = 24
                    setOnClickListener {
                        val currentQty = keranjangMap[produk.id] ?: 0
                        if (currentQty > 0) {
                            keranjangMap[produk.id] = currentQty - 1
                            updateKeranjangBelanja()
                        }
                    }
                }

                row.addView(tvDetail)
                row.addView(tvSubtotal)
                row.addView(btnMinus)
                containerKeranjangItems.addView(row)
            }
        }

        tvKeranjangKosong.visibility = if (totalTagihan > 0) View.GONE else View.VISIBLE
        tvTotalHarga.text = formatRupiah(totalTagihan)
    }

    private fun bersihkanSemuaKeranjang() {
        keranjangMap.clear()
        updateKeranjangBelanja()
    }

    private fun simpanBillKeAntrean() {
        val totalTagihan = hitungTotalTagihan()
        if (totalTagihan == 0) {
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        // Format data: "id1:qty1|id2:qty2..."
        val serializedItems = keranjangMap.filter { it.value > 0 }
            .map { "${it.key}:${it.value}" }
            .joinToString("|")

        val sharedPref = getSharedPreferences("CuanPOS_Antrean", MODE_PRIVATE)
        val idBill = "SAVED_" + System.currentTimeMillis()
        
        sharedPref.edit().putString(idBill, serializedItems).apply()

        Toast.makeText(this, "Bill Berhasil Disimpan di Antrean!", Toast.LENGTH_LONG).show()
        bersihkanSemuaKeranjang()
    }

    private fun tampilkanAntreanBill() {
        val sharedPref = getSharedPreferences("CuanPOS_Antrean", MODE_PRIVATE)
        val allEntries = sharedPref.all.filterKeys { it.startsWith("SAVED_") }.toList().sortedByDescending { it.first }

        val dialogView = layoutInflater.inflate(R.layout.dialog_riwayat, null)
        val containerRiwayat = dialogView.findViewById<LinearLayout>(R.id.containerRiwayat)
        val btnTutupRiwayat = dialogView.findViewById<MaterialButton>(R.id.btnTutupRiwayat)
        val tvJudul = dialogView.findViewById<TextView>(R.id.tvJudulRiwayat)
        tvJudul.text = "Antrean Bill (Saved)"
        
        val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (allEntries.isEmpty()) {
            val tvKosong = TextView(this).apply {
                text = "Belum ada antrean bill."
                setTextColor(Color.GRAY)
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, 40, 0, 40)
            }
            containerRiwayat.addView(tvKosong)
        } else {
            for ((key, value) in allEntries) {
                val serialized = value.toString()
                val items = serialized.split("|")
                
                val cardView = MaterialCardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 16) }
                    setCardBackgroundColor(Color.parseColor("#FFF3E0")) // Warm color for pending
                    radius = 24f
                    strokeWidth = 0
                }

                val layoutKonten = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(36, 28, 36, 28)
                }

                val tvTime = TextView(this).apply {
                    val timestamp = key.substringAfter("SAVED_").toLongOrNull() ?: 0L
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    text = "Saved at: ${sdf.format(Date(timestamp))}"
                    setTextColor(Color.BLACK)
                    textSize = 14f
                    setTypeface(null, Typeface.BOLD)
                }

                val tvItems = TextView(this).apply {
                    val sb = StringBuilder()
                    var total = 0
                    for (itemStr in items) {
                        val parts = itemStr.split(":")
                        if (parts.size == 2) {
                            val id = parts[0].toIntOrNull() ?: 0
                            val qty = parts[1].toIntOrNull() ?: 0
                            val product = listProdukServer.find { it.id == id }
                            sb.append("${product?.name ?: "Unknown"} x$qty\n")
                            total += (product?.price ?: 0) * qty
                        }
                    }
                    sb.append("Total: ${formatRupiah(total)}")
                    text = sb.toString()
                    setTextColor(Color.BLACK)
                    textSize = 13f
                }

                layoutKonten.addView(tvTime)
                layoutKonten.addView(tvItems)
                cardView.addView(layoutKonten)
                
                cardView.setOnClickListener {
                    // Resume this bill
                    if (keranjangMap.filter { it.value > 0 }.isNotEmpty()) {
                        Toast.makeText(this@KasirActivity, "Bersihkan keranjang dulu untuk resume!", Toast.LENGTH_SHORT).show()
                    } else {
                        for (itemStr in items) {
                            val parts = itemStr.split(":")
                            if (parts.size == 2) {
                                val id = parts[0].toIntOrNull() ?: 0
                                val qty = parts[1].toIntOrNull() ?: 0
                                keranjangMap[id] = qty
                            }
                        }
                        updateKeranjangBelanja()
                        sharedPref.edit().remove(key).apply()
                        alertDialog.dismiss()
                        Toast.makeText(this@KasirActivity, "Bill dilanjutkan", Toast.LENGTH_SHORT).show()
                    }
                }

                containerRiwayat.addView(cardView)
            }
        }

        btnTutupRiwayat.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun prosesPembayaran() {
        val totalTagihan = hitungTotalTagihan()
        if (totalTagihan == 0) {
            Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
            return
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
                        val uangDiterima = inputStr.toLong()
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
                    val uangDiterima = inputStr.toLong()
                    if (uangDiterima >= totalTagihan) {
                        simpanTransaksiKeServer(totalTagihan, uangDiterima.toInt(), alertDialog)
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

    private fun simpanTransaksiKeServer(total: Int, bayar: Int, dialog: AlertDialog) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        val items = keranjangMap.filter { it.value > 0 }.map { (id, qty) ->
            TransactionItemRequest(
                product_id = id,
                quantity = qty
            )
        }

        val request = TransactionRequest(channel = "mobile", items = items)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.createTransaction(token, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        dialog.dismiss()
                        
                        val ringkasan = StringBuilder()
                        for (item in items) {
                            val p = listProdukServer.find { it.id == item.product_id }
                            val namaPad = (p?.name ?: "Unknown").padEnd(18, ' ')
                            ringkasan.append("$namaPad x${item.quantity}\n")
                        }

                        tampilkanStrukDigital(
                            ringkasan.toString().trim(),
                            total,
                            bayar,
                            bayar - total
                        )
                        fetchDataDariServer()
                    } else {
                        Toast.makeText(this@KasirActivity, "Gagal simpan transaksi ke server", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KasirActivity, "Kesalahan koneksi server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hitungTotalTagihan(): Int {
        var total = 0
        for ((idProduk, qty) in keranjangMap) {
            val produk = listProdukServer.find { it.id == idProduk }
            if (produk != null) {
                total += (produk.price * qty)
            }
        }
        return total
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale.forLanguageTag("id-ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp. ").replace(",00", "")
    }

    private fun tampilkanRiwayatTransaksi() {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Using getLaporanPenjualan as it returns the transaction list with details
                val response = ApiClient.instance.getKasirHistory(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        showHistoryDialog(response.body()!!.data)
                    } else {
                        Toast.makeText(this@KasirActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KasirActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showHistoryDialog(transactions: List<com.example.cuanpos.network.TransactionResponse>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_riwayat, null)
        val containerRiwayat = dialogView.findViewById<LinearLayout>(R.id.containerRiwayat)
        val btnTutupRiwayat = dialogView.findViewById<MaterialButton>(R.id.btnTutupRiwayat)
        val tvJudul = dialogView.findViewById<TextView>(R.id.tvJudulRiwayat)
        tvJudul.text = "History Transaksi (Server)"

        val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (transactions.isEmpty()) {
            val tvKosong = TextView(this).apply {
                text = "Belum ada riwayat transaksi."
                setTextColor(Color.GRAY)
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, 40, 0, 40)
            }
            containerRiwayat.addView(tvKosong)
        } else {
            for (tx in transactions) {
                val cardView = MaterialCardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 16) }
                    setCardBackgroundColor(if (tx.status == "selesai") Color.parseColor("#E8F5E9") else Color.parseColor("#FFF3E0"))
                    radius = 24f
                    strokeWidth = 0
                }

                val layoutKonten = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(36, 28, 36, 28)
                }

                val tvJudul = TextView(this).apply {
                    text = if (tx.status == "selesai") "[LUNAS]" else "[BATAL]"
                    setTextColor(Color.BLACK)
                    textSize = 14f
                    setTypeface(null, Typeface.BOLD)
                }

                val tvData = TextView(this).apply {
                    val sb = StringBuilder()
                    tx.details?.forEach { sb.append("${it.product?.name} x${it.quantity}\n") }
                    sb.append("--------------------------------\n")
                    sb.append("Total: ${formatRupiah(tx.total_amount)}")
                    
                    text = sb.toString()
                    setTextColor(Color.BLACK)
                    textSize = 13f
                    typeface = Typeface.MONOSPACE
                }

                layoutKonten.addView(tvJudul)
                layoutKonten.addView(tvData)

                if (tx.status == "selesai") {
                    val btnRequestVoid = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                        text = "Request Void"
                        textSize = 12f
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            gravity = Gravity.END
                            topMargin = 8
                        }
                        setOnClickListener {
                            requestVoid(tx, alertDialog)
                        }
                    }
                    layoutKonten.addView(btnRequestVoid)
                }

                cardView.addView(layoutKonten)
                containerRiwayat.addView(cardView)
            }
        }

        btnTutupRiwayat.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun requestVoid(transaction: com.example.cuanpos.network.TransactionResponse, historyDialog: AlertDialog) {
        // Since backend voidTransaction immediately cancels it, we just call it directly or inform the user
        // If the user wants a "request" phase, we'd need a separate endpoint.
        // For now, I'll implement a confirmation dialog.
        
        AlertDialog.Builder(this)
            .setTitle("Request Void")
            .setMessage("Apakah Anda yakin ingin membatalkan transaksi #TRX-${transaction.id}?")
            .setPositiveButton("Ya, Void") { _, _ ->
                sendVoidRequest(transaction.id, historyDialog)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun sendVoidRequest(txId: Int, historyDialog: AlertDialog) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Call the NEW kasir request endpoint
                val response = ApiClient.instance.requestVoidTransaction(token, txId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@KasirActivity, "Permintaan void dikirim ke Admin", Toast.LENGTH_SHORT).show()
                        historyDialog.dismiss()
                    } else {
                        //Toast.makeText(this@KasirActivity, "Gagal mengirim permintaan void", Toast.LENGTH_SHORT).show()
                        // TAMPILKAN ERROR CODE & MESSAGE DARI BACKEND
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@KasirActivity, "Gagal (${response.code()}): $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KasirActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun tampilkanStrukDigital(detailPesanan: String, total: Int, uang: Int, kembalian: Int) {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val witaTgl = sdf.format(Date())

        val formatTotal = formatRupiah(total)
        val formatBayar = formatRupiah(uang)
        val formatKembali = formatRupiah(kembalian)

        val strukText = """
================================
            CuanPOS            
================================
      $witaTgl      
--------------------------------
Items:
$detailPesanan
--------------------------------
Total: $formatTotal
Bayar: $formatBayar
Kembali: $formatKembali
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
            setPadding(55, 40, 20, 40)
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<MaterialButton>(R.id.btnBatalLogout).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnYaLogout).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }
}