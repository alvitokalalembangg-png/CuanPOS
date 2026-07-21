<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\TransactionController;
use App\Http\Controllers\AdminController;
use App\Models\Product;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// --- PUBLIC ROUTES (Auth) ---
Route::post('/login/mobile', [AuthController::class, 'loginMobile']);
Route::post('/login/admin', [AuthController::class, 'loginAdmin']);

// --- PROTECTED ROUTES (Butuh Token) ---
Route::middleware('auth:sanctum')->group(function () {
    
    // Rute Global (Kasir & Admin)
    Route::get('/user', function (Request $request) { return $request->user(); });
    Route::get('/products', function () { return response()->json(Product::with('category')->get()); });
    Route::get('/categories', function () { return response()->json(Category::all()); });

    // --- AREA KHUSUS KASIR ---
    Route::middleware('role:kasir')->prefix('kasir')->group(function () {
        Route::post('/transactions', [TransactionController::class, 'store']);
        // Permintaan Void dari Kasir
        Route::put('/transactions/{id}/request-void', [TransactionController::class, 'requestVoid']);
        Route::get('/transactions', [TransactionController::class, 'history']);
    });

    // --- AREA KHUSUS ADMIN ---
    Route::middleware('role:admin')->prefix('admin')->group(function () {
        // Kelola Produk (Full CRUD)
        Route::post('/products', [AdminController::class, 'storeProduct']);
        Route::put('/products/{id}', [AdminController::class, 'updateProduct']);
        Route::delete('/products/{id}', [AdminController::class, 'deleteProduct']);
        
        // Kelola Kategori (Full CRUD)
        Route::post('/categories', [AdminController::class, 'storeCategory']);
        Route::put('/categories/{id}', [AdminController::class, 'updateCategory']);
        Route::delete('/categories/{id}', [AdminController::class, 'deleteCategory']);
        
        // Laporan & Transaksi
        Route::get('/laporan', [AdminController::class, 'laporanPenjualan']);
        Route::get('/transactions', [AdminController::class, 'laporanPenjualan']);
        
        // Approval / Reject Void Transaksi
        Route::put('/transactions/{id}/approve-void', [AdminController::class, 'voidTransaction']);
        Route::put('/transactions/{id}/reject-void', [AdminController::class, 'rejectVoid']);
        Route::get('/transactions', [AdminController::class, 'getPendingVoid']);
    });
});