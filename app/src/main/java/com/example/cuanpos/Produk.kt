package com.example.cuanpos

data class Produk(
    val id: Int,
    var category_id: Int,
    var name: String,
    var price: Int,
    var stock: Int,
    var gambar_url: String? = null,
    var category: Kategori? = null
)