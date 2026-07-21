<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Attributes\Fillable; 

#[Fillable(['kasir_id', 'total_amount', 'status', 'channel', 'void_status'])]
class Transaction extends Model
{
    //
    public function kasir() {
        return $this->belongsTo(User::class, 'kasir_id');
    }

    public function details() {
        return $this->hasMany(TransactionDetail::class);
    }
}
