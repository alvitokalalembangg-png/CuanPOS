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
import com.example.cuanpos.network.ApiClient
import com.example.cuanpos.network.LoginAdminRequest
import com.example.cuanpos.network.LoginMobileRequest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                // --- LIVE API: LOGIN ADMIN ---
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = ApiClient.instance.loginAdmin(LoginAdminRequest(email, password))
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && response.body() != null) {
                                val result = response.body()!!
                                // Simpan token Sanctum jika diperlukan nanti (misal lewat SharedPreferences)
                                val token = result.token
                                // Gunakan token agar tidak ada warning unused variable
                                android.util.Log.d("MainActivity", "Token: $token")

                                val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
                                sharedPref.edit().putString("AUTH_TOKEN", "Bearer ${result.token}").apply()

                                Toast.makeText(this@MainActivity, "Sukses Masuk Sebagai Admin!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@MainActivity, AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@MainActivity, "Email atau Password Admin Salah!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Gagal terhubung ke server: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            } else {
                // --- LIVE API: LOGIN KASIR (PIN) ---
                val pin = etPin.text.toString().trim()

                if (pin.length != 4) { // Menyesuaikan validasi size:6 di AuthController.php Laravel
                    Toast.makeText(this, "PIN Kasir harus 4 digit!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = ApiClient.instance.loginMobile(LoginMobileRequest(pin))
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && response.body() != null) {
                                val result = response.body()!!
                                val token = result.token
                                android.util.Log.d("MainActivity", "Token: $token")

                                val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
                                sharedPref.edit().putString("AUTH_TOKEN", "Bearer ${result.token}").apply()

                                Toast.makeText(this@MainActivity, "Sukses Masuk Sebagai Kasir!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@MainActivity, KasirActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@MainActivity, "PIN Kasir Salah atau Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Gagal terhubung ke server: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}