package com.example.cuanpos

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.example.cuanpos.network.ApiClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KelolaProdukActivity : AppCompatActivity() {

    private lateinit var etNamaProduk: EditText
    private lateinit var etHargaProduk: EditText
    private lateinit var etStockProduk: EditText
    private lateinit var cgKategori: ChipGroup
    private lateinit var cgFilterKategori: ChipGroup
    private lateinit var btnTambahProduk: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var tvFormTitle: TextView
    private lateinit var rvProduk: RecyclerView

    private lateinit var produkAdapter: ProdukAdapter
    private var listProdukFull = ArrayList<Produk>()
    private var listProdukDisplay = ArrayList<Produk>()
    private var listKategori = ArrayList<Kategori>()

    private var isEditMode = false
    private var produkDiedit: Produk? = null
    private var filterCategoryId: Int = -1 // -1 means "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kelola_produk)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inisialisasi View
        etNamaProduk = findViewById(R.id.etNamaProduk)
        etHargaProduk = findViewById(R.id.etHargaProduk)
        etStockProduk = findViewById(R.id.etStockProduk)
        cgKategori = findViewById(R.id.cgKategori)
        cgFilterKategori = findViewById(R.id.cgFilterKategori)
        btnTambahProduk = findViewById(R.id.btnTambahProduk)
        btnBack = findViewById(R.id.btnBack)
        tvFormTitle = findViewById(R.id.tvFormTitle)
        rvProduk = findViewById(R.id.rvProduk)

        // 3. Setup RecyclerView & Adapter
        produkAdapter = ProdukAdapter(
            listProdukDisplay,
            onEditClick = { produk -> masukModeEdit(produk) },
            onDeleteClick = { produk -> hapusProduk(produk) }
        )

        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = produkAdapter
        rvProduk.isNestedScrollingEnabled = false // Fix scrolling conflict with NestedScrollView
        rvProduk.isNestedScrollingEnabled = false

        // 4. Aksi Klik Komponen
        btnBack.setOnClickListener { finish() }
        btnTambahProduk.setOnClickListener { prosesSimpanData() }

        fetchDataFromServer()
    }

    private fun fetchDataFromServer() {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val responseKategori = ApiClient.instance.getCategories(token)
                val responseProduk = ApiClient.instance.getProducts(token)

                withContext(Dispatchers.Main) {
                    if (responseKategori.isSuccessful && responseKategori.body() != null) {
                        listKategori.clear()
                        listKategori.addAll(responseKategori.body()!!)
                        renderCategoriesToChips()
                        renderFilterChips()
                    }
                    if (responseProduk.isSuccessful && responseProduk.body() != null) {
                        listProdukFull.clear()
                        listProdukFull.addAll(responseProduk.body()!!)
                        applyFilter()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KelolaProdukActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderFilterChips() {
        cgFilterKategori.removeAllViews()
        
        // Add "Semua" chip
        val chipSemua = Chip(this).apply {
            text = "Semua"
            isCheckable = true
            isChecked = (filterCategoryId == -1)
            setOnClickListener {
                filterCategoryId = -1
                applyFilter()
            }
        }
        cgFilterKategori.addView(chipSemua)

        for (kategori in listKategori) {
            val chip = Chip(this).apply {
                text = kategori.name
                isCheckable = true
                isChecked = (filterCategoryId == kategori.id)
                setOnClickListener {
                    filterCategoryId = kategori.id
                    applyFilter()
                }
            }
            cgFilterKategori.addView(chip)
        }
    }

    private fun applyFilter() {
        listProdukDisplay.clear()
        if (filterCategoryId == -1) {
            listProdukDisplay.addAll(listProdukFull)
        } else {
            listProdukDisplay.addAll(listProdukFull.filter { it.category_id == filterCategoryId })
        }
        produkAdapter.notifyDataSetChanged()
    }

    private fun renderCategoriesToChips() {
        cgKategori.removeAllViews()
        for (kategori in listKategori) {
            val chip = Chip(this).apply {
                id = View.generateViewId()
                text = kategori.name
                isCheckable = true
                tag = kategori.id
                // Menggunakan style yang sudah ada di XML jika memungkinkan, atau set manual
                // Di sini saya asumsikan style standar sudah cukup
            }
            cgKategori.addView(chip)
        }
    }

    private fun prosesSimpanData() {
        val nama = etNamaProduk.text.toString().trim()
        val hargaStr = etHargaProduk.text.toString().trim()
        val stockStr = etStockProduk.text.toString().trim()

        val selectedChipId = cgKategori.checkedChipId
        if (selectedChipId == View.NO_ID) {
            Toast.makeText(this, "Pilih kategori terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedChip = findViewById<Chip>(selectedChipId)
        val categoryId = selectedChip.tag as Int

        if (nama.isEmpty() || hargaStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val harga = hargaStr.toInt()
        val stock = stockStr.toInt()
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = if (isEditMode && produkDiedit != null) {
                    val p = produkDiedit!!
                    p.name = nama
                    p.price = harga
                    p.stock = stock
                    p.category_id = categoryId
                    ApiClient.instance.updateProduct(token, p.id, p)
                } else {
                    val produkBaru = Produk(0, categoryId, nama, harga, stock)
                    ApiClient.instance.createProduct(token, produkBaru)
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@KelolaProdukActivity, "Berhasil simpan produk", Toast.LENGTH_SHORT).show()
                        resetForm()
                        fetchDataFromServer()
                    } else {
                        Toast.makeText(this@KelolaProdukActivity, "Gagal simpan produk", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KelolaProdukActivity, "Kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hapusProduk(produk: Produk) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.deleteProduct(token, produk.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@KelolaProdukActivity, "Produk dihapus", Toast.LENGTH_SHORT).show()
                        fetchDataFromServer()
                        if (isEditMode && produkDiedit?.id == produk.id) resetForm()
                    } else {
                        Toast.makeText(this@KelolaProdukActivity, "Gagal hapus produk", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KelolaProdukActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun masukModeEdit(produk: Produk) {
        isEditMode = true
        produkDiedit = produk

        etNamaProduk.setText(produk.name)
        etHargaProduk.setText(produk.price.toString())
        etStockProduk.setText(produk.stock.toString())

        // Pilih chip yang sesuai
        for (i in 0 until cgKategori.childCount) {
            val chip = cgKategori.getChildAt(i) as Chip
            if (chip.tag == produk.category_id) {
                chip.isChecked = true
                break
            }
        }

        tvFormTitle.text = "Edit Produk: ${produk.name}"
        btnTambahProduk.text = "SIMPAN PERUBAHAN"
    }

    private fun resetForm() {
        isEditMode = false
        produkDiedit = null
        etNamaProduk.text.clear()
        etHargaProduk.text.clear()
        etStockProduk.text.clear()
        cgKategori.clearCheck()

        tvFormTitle.text = "Tambah Produk Baru"
        btnTambahProduk.text = "TAMBAH PRODUK"
    }
}