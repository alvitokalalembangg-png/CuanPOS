package com.example.cuanpos

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cuanpos.network.ApiClient
import com.example.cuanpos.network.TransactionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApprovalVoidActivity : AppCompatActivity() {

    private lateinit var rvVoid: RecyclerView
    private val listTransaksi = ArrayList<TransactionResponse>()
    private lateinit var voidAdapter: VoidAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_void)

        val btnBack = findViewById<ImageView>(R.id.btnBackKeAdmin)
        rvVoid = findViewById(R.id.rvVoid)

        btnBack.setOnClickListener { finish() }

        // Setup RecyclerView
        voidAdapter = VoidAdapter(listTransaksi) { transaction, isApprove ->
            if (isApprove) {
                setujuiVoid(transaction)
            } else {
                tolakVoid(transaction)
            }
        }
        rvVoid.layoutManager = LinearLayoutManager(this)
        rvVoid.adapter = voidAdapter

        fetchTransactions()
    }

    private fun fetchTransactions() {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Use getLaporanPenjualan as it is the most reliable endpoint for transaction data
                val response = ApiClient.instance.getLaporanPenjualan(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        listTransaksi.clear()
                        // Filter transactions that have requested void
                        // Robust check: check both void_status and the main status
                        listTransaksi.addAll(response.body()!!.data.filter { 
                            it.void_status == "pending" || it.status == "pending_void" 
                        })
                        voidAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@ApprovalVoidActivity, "Gagal memuat transaksi", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalVoidActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setujuiVoid(transaction: TransactionResponse) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.voidTransaction(token, transaction.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ApprovalVoidActivity, "Void disetujui", Toast.LENGTH_SHORT).show()
                        fetchTransactions()
                    } else {
                        Toast.makeText(this@ApprovalVoidActivity, "Gagal menyetujui void", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalVoidActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun tolakVoid(transaction: TransactionResponse) {
        val sharedPref = getSharedPreferences("CuanPOS_Prefs", MODE_PRIVATE)
        val token = sharedPref.getString("AUTH_TOKEN", "") ?: ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.rejectVoidTransaction(token, transaction.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ApprovalVoidActivity, "Permintaan void ditolak", Toast.LENGTH_SHORT).show()
                        fetchTransactions()
                    } else {
                        Toast.makeText(this@ApprovalVoidActivity, "Gagal menolak permintaan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalVoidActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}