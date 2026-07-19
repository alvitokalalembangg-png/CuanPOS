<?php

namespace App\Http\Controllers;

use App\Models\Product;
use App\Models\Transaction;
use App\Models\TransactionDetail;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TransactionController extends Controller
{
    //
    public function store(Request $request) {
        $request->validate([
            'channel' => 'required|in:web,mobile',
            'items' => 'required|array',
            'items.*.product_id' => 'required|exists:products,id',
            'items.*.quantity' => 'required|integer|min:1',
        ]);

        try {
            $result = DB::transaction(function () use ($request) {
                $totalAmount = 0;
                $details = [];

                // Ambil semua ID produk dari request
                $productIds = collect($request->items)->pluck('product_id');
                
                // Lock tabel produk terkait agar kasir lain harus antre jika mengakses produk yang sama
                $products = Product::whereIn('id', $productIds)->lockForUpdate()->get()->keyBy('id');

                foreach ($request->items as $item) {
                    $product = $products[$item['product_id']];

                    if ($product->stock < $item['quantity']) {
                        throw new \Exception("Stok {$product->name} tidak mencukupi.");
                    }

                    $subtotal = $product->price * $item['quantity'];
                    $totalAmount += $subtotal;

                    $details[] = [
                        'product_id' => $product->id,
                        'quantity' => $item['quantity'],
                        'price' => $product->price, // Snapshot harga
                        'subtotal' => $subtotal,
                    ];

                    // Kurangi stok
                    $product->decrement('stock', $item['quantity']);
                }

                // Buat header transaksi
                $transaction = Transaction::create([
                    'kasir_id' => $request->user()->id,
                    'total_amount' => $totalAmount,
                    'status' => 'selesai',
                    'channel' => $request->channel,
                ]);

                // Insert detail transaksi sekaligus
                $transaction->details()->createMany($details);

                return $transaction->load('details.product');
            });

            return response()->json([
                'message' => 'Transaksi berhasil',
                'data' => $result
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'message' => 'Transaksi gagal',
                'error' => $e->getMessage()
            ], 400);
        }
    }
}
