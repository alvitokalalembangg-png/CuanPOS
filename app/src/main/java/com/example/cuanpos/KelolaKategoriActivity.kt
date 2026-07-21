package com.example.cuanpos

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KelolaKategoriActivity : AppCompatActivity() {

    private lateinit var rvKategori: RecyclerView
    private lateinit var etNamaKategori: EditText
    private lateinit var btnSimpanKategori: MaterialButton
    private lateinit var btnBackKeAdmin: ImageView

    private lateinit var kategoriAdapter: KategoriAdapter
    private var listKategori = ArrayList<Kategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kelola_kategori)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi View dari XML Kategori
        rvKategori = findViewById(R.id.rvKategori)
        etNamaKategori = findViewById(R.id.etNamaKategori)
        btnSimpanKategori = findViewById(R.id.btnSimpanKategori)
        btnBackKeAdmin = findViewById(R.id.btnBackKeAdmin)

        // Setup Adapter
        kategoriAdapter = KategoriAdapter(listKategori) { kategoriDihapus: Kategori ->
            hapusKategori(kategoriDihapus)
        }

        rvKategori.layoutManager = LinearLayoutManager(this)
        rvKategori.adapter = kategoriAdapter

        // Tombol Kembali
        btnBackKeAdmin.setOnClickListener {
            finish()
        }

        // Tombol Tambah Kategori
        btnSimpanKategori.setOnClickListener {
            val namaKategoriBaru = etNamaKategori.text.toString().trim()

            if (namaKategoriBaru.isNotEmpty()) {
                simpanKategori(namaKategoriBaru)
            } else {
                etNamaKategori.error = "Nama kategori tidak boleh kosong!"
            }
        }

        fetchKategori()
    }

    private fun fetchKategori() {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.getCategories(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        listKategori.clear()
                        listKategori.addAll(response.body()!!)
                        kategoriAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@KelolaKategoriActivity, "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KelolaKategoriActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun simpanKategori(nama: String) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val kategoriBaru = Kategori(0, nama) // ID 0 for new
                val response = ApiClient.instance.createCategory(token, kategoriBaru)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        etNamaKategori.text.clear()
                        Toast.makeText(this@KelolaKategoriActivity, "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
                        fetchKategori()
                    } else {
                        Toast.makeText(this@KelolaKategoriActivity, "Gagal menambah kategori", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KelolaKategoriActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hapusKategori(kategori: Kategori) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.deleteCategory(token, kategori.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@KelolaKategoriActivity, "${kategori.name} dihapus", Toast.LENGTH_SHORT).show()
                        fetchKategori()
                    } else {
                        Toast.makeText(this@KelolaKategoriActivity, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KelolaKategoriActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}