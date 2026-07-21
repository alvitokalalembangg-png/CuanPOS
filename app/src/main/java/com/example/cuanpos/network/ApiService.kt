package com.example.cuanpos.network

import com.example.cuanpos.Kategori // Ensure these match your actual package path
import com.example.cuanpos.Produk
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ==========================================
    // AUTH ROUTES (Public)
    // ==========================================

    @POST("login/mobile")
    suspend fun loginMobile(
        @Body request: LoginMobileRequest
    ): Response<LoginResponse>

    @POST("login/admin")
    suspend fun loginAdmin(
        @Body request: LoginAdminRequest
    ): Response<LoginResponse>


    // ==========================================
    // GLOBAL PROTECTED ROUTES (Requires Token)
    // ==========================================

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<UserData>

    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") token: String
    ): Response<List<Produk>>

    @GET("categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<List<Kategori>>


    // ==========================================
    // KASIR ROUTES
    // ==========================================

    @POST("kasir/transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body request: TransactionRequest
    ): Response<Unit>

    @GET("kasir/transactions")
    suspend fun getKasirHistory(
        @Header("Authorization") token: String
    ): Response<LaporanResponse>


    // ==========================================
    // ADMIN ROUTES
    // ==========================================

    @POST("admin/products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body request: Produk
    ): Response<Produk>

    @PUT("admin/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: Produk
    ): Response<Produk>

    @DELETE("admin/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @POST("admin/categories")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Body request: Kategori
    ): Response<Kategori>

    @PUT("admin/categories/{id}")
    suspend fun updateCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: Kategori
    ): Response<Kategori>

    @DELETE("admin/categories/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @GET("admin/laporan")
    suspend fun getLaporanPenjualan(
        @Header("Authorization") token: String
    ): Response<LaporanResponse>

    @GET("admin/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): Response<LaporanResponse> // Assuming same structure as laporan for simplicity or separate if needed

    @PUT("admin/transactions/{id}/approve-void")
    suspend fun voidTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: Int
    ): Response<Unit>

    // KASIR REQUEST VOID (New)
    @PUT("kasir/transactions/{id}/request-void")
    suspend fun requestVoidTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: Int
    ): Response<Unit>

    @PUT("admin/transactions/{id}/reject-void")
    suspend fun rejectVoidTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: Int
    ): Response<Unit>
}