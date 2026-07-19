package com.example.cuanpos

import android.os.Bundle
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class KelolaProdukActivity : AppCompatActivity() {

    private lateinit var etNamaProduk: EditText
    private lateinit var etHargaProduk: EditText
    private lateinit var cgKategori: ChipGroup
    private lateinit var chipMakanan: Chip
    private lateinit var chipMinuman: Chip
    private lateinit var btnTambahProduk: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var tvFormTitle: TextView
    private lateinit var rvProduk: RecyclerView

    private lateinit var produkAdapter: ProdukAdapter
    private var listProduk = ArrayList<Produk>()

    private var isEditMode = false
    private var indexProdukYangDiedit = -1

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
        cgKategori = findViewById(R.id.cgKategori)
        chipMakanan = findViewById(R.id.chipMakanan)
        chipMinuman = findViewById(R.id.chipMinuman)
        btnTambahProduk = findViewById(R.id.btnTambahProduk)
        btnBack = findViewById(R.id.btnBack)
        tvFormTitle = findViewById(R.id.tvFormTitle)
        rvProduk = findViewById(R.id.rvProduk)

        // 2. Data Awal (Dummy)
        listProduk.add(Produk("1", "Es Pisang Ijo", 15000, "Makanan"))
        listProduk.add(Produk("2", "Es Teh", 5000, "Minuman"))

        // 3. Setup RecyclerView & Adapter
        produkAdapter = ProdukAdapter(
            listProduk,
            onEditClick = { produk -> masukModeEdit(produk) },
            onDeleteClick = { produk ->
                listProduk.remove(produk)
                produkAdapter.notifyDataSetChanged()
                Toast.makeText(this, "${produk.nama} dihapus", Toast.LENGTH_SHORT).show()
                if (isEditMode) resetForm()
            }
        )

        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = produkAdapter

        // 4. Aksi Klik Komponen
        btnBack.setOnClickListener { finish() }
        btnTambahProduk.setOnClickListener { prosesSimpanData() }
    }

    private fun prosesSimpanData() {
        val nama = etNamaProduk.text.toString().trim()
        val hargaStr = etHargaProduk.text.toString().trim()

        // Mendeteksi Kategori Berdasarkan Chip yang Aktif
        val kategoriDipilih = when (cgKategori.checkedChipId) {
            R.id.chipMakanan -> "Makanan"
            R.id.chipMinuman -> "Minuman"
            else -> "Makanan"
        }

        if (nama.isEmpty()) {
            etNamaProduk.error = "Nama produk wajib diisi!"
            return
        }
        if (hargaStr.isEmpty()) {
            etHargaProduk.error = "Harga wajib diisi!"
            return
        }

        val harga = hargaStr.toInt()

        if (isEditMode) {
            // Proses Perbarui Data (Mode Edit)
            val produkLama = listProduk[indexProdukYangDiedit]
            produkLama.nama = nama
            produkLama.harga = harga
            produkLama.kategori = kategoriDipilih

            Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
            resetForm()
        } else {
            // Proses Tambah Data Baru
            val idUnik = System.currentTimeMillis().toString()
            val produkBaru = Produk(idUnik, nama, harga, kategoriDipilih)
            listProduk.add(produkBaru)

            Toast.makeText(this, "Berhasil menambahkan ke [$kategoriDipilih]", Toast.LENGTH_SHORT).show()
            etNamaProduk.text.clear()
            etHargaProduk.text.clear()
            chipMakanan.isChecked = true
        }

        produkAdapter.notifyDataSetChanged()
    }

    private fun masukModeEdit(produk: Produk) {
        isEditMode = true
        indexProdukYangDiedit = listProduk.indexOf(produk)

        etNamaProduk.setText(produk.nama)
        etHargaProduk.setText(produk.harga.toString())

        // Sesuaikan chip dengan kategori produk yang di-edit
        if (produk.kategori == "Makanan") {
            chipMakanan.isChecked = true
        } else if (produk.kategori == "Minuman") {
            chipMinuman.isChecked = true
        }

        tvFormTitle.text = "Edit Produk: ${produk.nama}"
        btnTambahProduk.text = "SIMPAN PERUBAHAN"
    }

    private fun resetForm() {
        isEditMode = false
        indexProdukYangDiedit = -1
        etNamaProduk.text.clear()
        etHargaProduk.text.clear()
        chipMakanan.isChecked = true

        tvFormTitle.text = "Tambah Produk Baru"
        btnTambahProduk.text = "TAMBAH PRODUK"
    }
}