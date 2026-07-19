<?php

namespace App\Http\Controllers;

use App\Models\Product;
use App\Models\Category;
use App\Models\Transaction;
use Illuminate\Http\Request;

class AdminController extends Controller
{
    // --- KELOLA PRODUK ---
    public function storeProduct(Request $request) {
        $validated = $request->validate([
            'category_id' => 'required|exists:categories,id',
            'name' => 'required|string|max:255',
            'price' => 'required|numeric|min:0',
            'stock' => 'required|integer|min:0',
            'gambar_url' => 'nullable|url'
        ]);

        $product = Product::create($validated);
        return response()->json(['message' => 'Produk berhasil ditambahkan', 'data' => $product], 201);
    }

    // --- KELOLA KATEGORI ---
    public function storeCategory(Request $request) {
        $validated = $request->validate([
            'name' => 'required|string|max:255'
        ]);

        $category = Category::create($validated);
        return response()->json(['message' => 'Kategori berhasil ditambahkan', 'data' => $category], 201);
    }

    // --- LAPORAN PENJUALAN ---
    public function laporanPenjualan() {
        // Mengambil semua transaksi beserta detail dan data kasirnya
        $transactions = Transaction::with(['details.product', 'kasir'])->orderBy('created_at', 'desc')->get();
        return response()->json(['data' => $transactions]);
    }

    // --- APPROVAL VOID (Pembatalan Transaksi) ---
    public function voidTransaction($id) {
        $transaction = Transaction::find($id);
        
        if (!$transaction) {
            return response()->json(['message' => 'Transaksi tidak ditemukan'], 404);
        }
        
        if ($transaction->status === 'batal') {
            return response()->json(['message' => 'Transaksi sudah dibatalkan sebelumnya'], 400);
        }

        $transaction->update(['status' => 'batal']);

        // Logika mengembalikan stok produk
        foreach ($transaction->details as $detail) {
            $detail->product->increment('stock', $detail->quantity);
        }

        return response()->json(['message' => 'Transaksi berhasil di-void dan stok dikembalikan']);
    }
}
