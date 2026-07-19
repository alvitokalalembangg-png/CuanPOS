package com.example.cuanpos

data class Produk(
    val id: String,
    var nama: String,
    var harga: Int,
    var kategori: String // <-- Tambahan variabel kategori
)