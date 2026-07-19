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
import com.google.android.material.button.MaterialButton

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

        // Dummy Data awal
        listKategori.add(Kategori("1", "Makanan"))
        listKategori.add(Kategori("2", "Minuman"))

        // Setup Adapter
        kategoriAdapter = KategoriAdapter(listKategori) { kategoriDihapus: Kategori ->
            listKategori.remove(kategoriDihapus)
            kategoriAdapter.notifyDataSetChanged()
            Toast.makeText(this, "${kategoriDihapus.nama} berhasil dihapus", Toast.LENGTH_SHORT).show()
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
                val idUnik = System.currentTimeMillis().toString()
                listKategori.add(Kategori(id = idUnik, nama = namaKategoriBaru))
                kategoriAdapter.notifyDataSetChanged()
                rvKategori.scrollToPosition(listKategori.size - 1)
                etNamaKategori.text.clear()
                Toast.makeText(this, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            } else {
                etNamaKategori.error = "Nama kategori tidak boleh kosong!"
            }
        }
    }
}