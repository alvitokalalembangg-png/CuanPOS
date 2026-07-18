    package com.example.cuanpos

    import android.content.Intent // PENTING: Untuk perpindahan halaman
    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import com.google.android.material.button.MaterialButton
    import com.google.android.material.tabs.TabLayout
    import com.google.android.material.textfield.TextInputLayout

    class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            // Mengatur padding Edge-to-Edge agar UI presisi
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.formContainer)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
                insets
            }

            // 1. Inisialisasi Komponen View
            val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
            val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
            val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
            val tilPin = findViewById<TextInputLayout>(R.id.tilPin)
            val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

            val etEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
            val etPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword)
            val etPin = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPin)

            // Variabel penanda posisi Tab (Default true = Admin)
            var isAdminTab = true

            // 2. Logika ketika Tab berpindah (Admin <-> Kasir)
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == 0) {
                        isAdminTab = true // Mode Admin aktif
                        tilEmail.visibility = View.VISIBLE
                        tilPassword.visibility = View.VISIBLE
                        tilPin.visibility = View.GONE
                    } else {
                        isAdminTab = false // Mode Kasir aktif (PENTING!)
                        tilEmail.visibility = View.GONE
                        tilPassword.visibility = View.GONE
                        tilPin.visibility = View.VISIBLE
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            // 3. Logika Klik Tombol MASUK / LOGIN
            btnLogin.setOnClickListener {
                if (isAdminTab) {
                    // --- PROSES LOGIN ADMIN ---
                    val email = etEmail.text.toString().trim()
                    val password = etPassword.text.toString().trim()

                    if (email == "admin@cuanpos.com" && password == "admin123") {
                        Toast.makeText(this, "Sukses Masuk Sebagai Admin!", Toast.LENGTH_SHORT).show()
                        // TODO: Halaman Admin belum dibuat, nanti ditaruh di sini
                    } else {
                        Toast.makeText(this, "Email atau Password Admin Salah!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // --- PROSES LOGIN KASIR ---
                    val pin = etPin.text.toString().trim()

                    if (pin == "1234") {
                        Toast.makeText(this, "Sukses Masuk Sebagai Kasir!", Toast.LENGTH_SHORT).show()

                        // KODE PERPINDAHAN TEMPAT KE PAGE KASIR
                        val intent = Intent(this, KasirActivity::class.java)
                        startActivity(intent)
                        finish() // Menutup MainActivity agar tidak bisa di-back ke login lagi

                    } else {
                        Toast.makeText(this, "PIN Kasir Salah! (Gunakan 1234)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }