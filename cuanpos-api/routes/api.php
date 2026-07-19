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
    
    // Rute Global (Kasir & Admin bisa akses)
    Route::get('/user', function (Request $request) { return $request->user(); });
    Route::get('/products', function () { return response()->json(Product::with('category')->get()); });
    Route::get('/categories', function () { return response()->json(Category::all()); });

    // --- AREA KHUSUS KASIR ---
    Route::middleware('role:kasir')->prefix('kasir')->group(function () {
        Route::post('/transactions', [TransactionController::class, 'store']);
    });

    // --- AREA KHUSUS ADMIN ---
    Route::middleware('role:admin')->prefix('admin')->group(function () {
        // Endpoint untuk KelolaProdukActivity
        Route::post('/products', [AdminController::class, 'storeProduct']);
        
        // Endpoint untuk KelolaKategoriActivity
        Route::post('/categories', [AdminController::class, 'storeCategory']);
        
        // Endpoint untuk LaporanPenjualanActivity
        Route::get('/laporan', [AdminController::class, 'laporanPenjualan']);
        
        // Endpoint untuk ApprovalVoidActivity
        Route::put('/transactions/{id}/void', [AdminController::class, 'voidTransaction']);
    });
});