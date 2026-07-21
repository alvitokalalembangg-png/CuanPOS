package com.example.cuanpos.network

// ==========================================
// AUTHENTICATION MODELS
// ==========================================

// Request body for Kasir (Mobile)
data class LoginMobileRequest(
    val pin: String
)

// Request body for Admin (Dashboard)
data class LoginAdminRequest(
    val email: String,
    val password: String
)

// Response body for both logins
data class LoginResponse(
    val token: String,
    val user: UserData
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String?,
    val role: String,
    val pin: String?
)

// ==========================================
// TRANSACTION MODELS
// ==========================================

data class TransactionRequest(
    val channel: String, // "web" or "mobile"
    val items: List<TransactionItemRequest>
)

data class TransactionItemRequest(
    val product_id: Int,
    val quantity: Int
)

data class TransactionResponse(
    val id: Int,
    val kasir_id: Int,
    val total_amount: Int,
    val status: String, // selesai, batal
    val void_status: String?,
    val channel: String,
    val created_at: String,
    val kasir: UserData?,
    val details: List<TransactionDetailResponse>?
)

data class TransactionDetailResponse(
    val id: Int,
    val transaction_id: Int,
    val product_id: Int,
    val quantity: Int,
    val price: Int, // Snapshot price
    val subtotal: Int,
    val product: com.example.cuanpos.Produk?
)

// ==========================================
// WRAPPER & LAPORAN MODELS
// ==========================================

data class ApiWrapper<T>(
    val data: T,
    val message: String? = null
)

data class LaporanResponse(
    val data: List<TransactionResponse>
)