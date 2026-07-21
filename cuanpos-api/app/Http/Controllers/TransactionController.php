<?php

namespace App\Http\Controllers;

use App\Models\Product;
use App\Models\Transaction;
use App\Models\TransactionDetail;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TransactionController extends Controller
{
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

                $productIds = collect($request->items)->pluck('product_id');
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
                        'price' => $product->price,
                        'subtotal' => $subtotal,
                    ];

                    $product->decrement('stock', $item['quantity']);
                }

                $transaction = Transaction::create([
                    'kasir_id' => $request->user()->id,
                    'total_amount' => $totalAmount,
                    'status' => 'selesai',
                    'channel' => $request->channel,
                ]);

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

    // --- RIWAYAT TRANSAKSI KASIR ---
public function history(Request $request) {
    // Mengambil transaksi khusus milik kasir yang sedang login
    $transactions = Transaction::with(['details.product'])
        ->where('kasir_id', $request->user()->id)
        ->orderBy('created_at', 'desc')
        ->get();

    return response()->json(['data' => $transactions]);
}

    // --- REQUEST VOID OLEH KASIR ---
    public function requestVoid($id) {
    $transaction = Transaction::find($id);
    if (!$transaction) return response()->json(['message' => 'Not found'], 404);

    $transaction->update([
        'status' => 'pending_void',
        'void_status' => 'pending'
    ]);
    return response()->json(['message' => 'Void requested']);
}
}