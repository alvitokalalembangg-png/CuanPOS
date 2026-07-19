package com.example.cuanpos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdminActivity : AppCompatActivity() { // <-- PASTIKAN DEKLARASI CLASS INI ADA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inflate layout secara terprogram agar mendapatkan root view-nya secara langsung
        val rootView = layoutInflater.inflate(R.layout.activity_admin, null)
        setContentView(rootView)

        // 2. Gunakan rootView langsung untuk set padding window
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Logika Klik Tombol Logout Admin
        val btnAdminLogout = findViewById<ImageView>(R.id.btnAdminLogout)
        btnAdminLogout.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_logout_admin, null)
            val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btnBatalLogoutAdmin)
            val btnYa = dialogView.findViewById<MaterialButton>(R.id.btnYaLogoutAdmin)

            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.show()

            btnBatal.setOnClickListener {
                alertDialog.dismiss()
            }

            btnYa.setOnClickListener {
                alertDialog.dismiss()
                Toast.makeText(this, "Berhasil Keluar dari Admin", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 4. Inisialisasi Menu Grid Admin
        val menuProduk = findViewById<MaterialCardView>(R.id.menuProduk)
        val menuKategori = findViewById<MaterialCardView>(R.id.menuKategori)
        val menuLaporan = findViewById<MaterialCardView>(R.id.menuLaporan)
        val menuVoid = findViewById<MaterialCardView>(R.id.menuVoid)

        menuProduk.setOnClickListener {
            Toast.makeText(this, "Membuka Kelola Produk", Toast.LENGTH_SHORT).show()
            // Tambahkan 2 baris intent di bawah ini untuk connect ke KelolaProdukActivity
            val intent = Intent(this, KelolaProdukActivity::class.java)
            startActivity(intent)
        }
        // PERBAIKAN: Menggabungkan perpindahan halaman & Toast untuk Kelola Kategori agar tidak bertabrakan
        menuKategori.setOnClickListener {
            Toast.makeText(this, "Membuka Kelola Kategori", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, KelolaKategoriActivity::class.java)
            startActivity(intent)
        }
        menuLaporan.setOnClickListener {
            Toast.makeText(this, "Membuka Laporan Penjualan", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LaporanPenjualanActivity::class.java)
            startActivity(intent)
        }

        menuVoid.setOnClickListener {
            Toast.makeText(this, "Membuka Approval Void", Toast.LENGTH_SHORT).show()
            // ⬇️ PENGHUBUNG AKHIR SELESAI ⬇️
            val intent = Intent(this, ApprovalVoidActivity::class.java)
            startActivity(intent)
        }
    }
}