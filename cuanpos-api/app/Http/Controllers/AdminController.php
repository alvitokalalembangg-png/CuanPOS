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

    public function updateProduct(Request $request, $id) {
        $product = Product::find($id);
        if (!$product) {
            return response()->json(['message' => 'Produk tidak ditemukan'], 404);
        }

        $validated = $request->validate([
            'category_id' => 'sometimes|exists:categories,id',
            'name' => 'sometimes|string|max:255',
            'price' => 'sometimes|numeric|min:0',
            'stock' => 'sometimes|integer|min:0',
            'gambar_url' => 'nullable|url'
        ]);

        $product->update($validated);
        return response()->json(['message' => 'Produk berhasil diperbarui', 'data' => $product]);
    }

    public function deleteProduct($id) {
        $product = Product::find($id);
        if (!$product) {
            return response()->json(['message' => 'Produk tidak ditemukan'], 404);
        }

        $product->delete();
        return response()->json(['message' => 'Produk berhasil dihapus']);
    }

    // --- KELOLA KATEGORI ---
    public function storeCategory(Request $request) {
        $validated = $request->validate([
            'name' => 'required|string|max:255'
        ]);

        $category = Category::create($validated);
        return response()->json(['message' => 'Kategori berhasil ditambahkan', 'data' => $category], 201);
    }

    public function updateCategory(Request $request, $id) {
        $category = Category::find($id);
        if (!$category) {
            return response()->json(['message' => 'Kategori tidak ditemukan'], 404);
        }

        $validated = $request->validate([
            'name' => 'required|string|max:255'
        ]);

        $category->update($validated);
        return response()->json(['message' => 'Kategori berhasil diperbarui', 'data' => $category]);
    }

    public function deleteCategory($id) {
        $category = Category::find($id);
        if (!$category) {
            return response()->json(['message' => 'Kategori tidak ditemukan'], 404);
        }

        // Cek jika kategori masih dipakai di produk
        if ($category->products()->count() > 0) {
            return response()->json(['message' => 'Kategori tidak dapat dihapus karena masih digunakan oleh produk'], 400);
        }

        $category->delete();
        return response()->json(['message' => 'Kategori berhasil dihapus']);
    }

    // --- LAPORAN PENJUALAN ---
    public function laporanPenjualan() {
        $transactions = Transaction::with(['details.product', 'kasir'])
            ->orderBy('created_at', 'desc')
            ->get();
        return response()->json(['data' => $transactions]);
    }

    // 1. Ambil transaksi untuk Approval Void (Admin hanya melihat yang void_status == 'pending')
    public function getPendingVoid() {
        $transactions = Transaction::with(['details.product', 'kasir'])
            ->where('void_status', 'pending')
            ->orderBy('created_at', 'desc')
            ->get();
            
        return response()->json(['data' => $transactions]);
    }

    public function voidTransaction($id) { // This is APPROVE
    $transaction = Transaction::with('details.product')->find($id);
    if (!$transaction) return response()->json(['message' => 'Transaksi tidak ditemukan'], 404);

    // Tandai sebagai BATAL dan APPROVED
    $transaction->update([
        'status' => 'batal',
        'void_status' => 'approved'
    ]);

    // BARU DI SINI stok dikembalikan
    foreach ($transaction->details as $detail) {
        $detail->product->increment('stock', $detail->quantity);
    }

    return response()->json(['message' => 'Transaksi berhasil dibatalkan dan stok dikembalikan']);
}

public function rejectVoid($id) {
    $transaction = Transaction::find($id);
    if (!$transaction) return response()->json(['message' => 'Transaksi tidak ditemukan'], 404);

    // Kembalikan ke SELESAI dan REJECTED
    $transaction->update([
        'status' => 'selesai',
        'void_status' => 'rejected'
    ]);
    
    return response()->json(['message' => 'Permintaan void ditolak, transaksi tetap berlaku']);
}
}