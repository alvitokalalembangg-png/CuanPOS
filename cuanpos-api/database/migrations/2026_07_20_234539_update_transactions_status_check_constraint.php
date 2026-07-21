<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    public function up(): void
    {
        // Drop constraint lama di PostgreSQL
        DB::statement('ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_status_check;');

        // Tambahkan constraint baru yang mencakup 'pending_void'
        DB::statement("ALTER TABLE transactions ADD CONSTRAINT transactions_status_check CHECK (status IN ('selesai', 'batal', 'pending_void'));");
    }

    public function down(): void
    {
        DB::statement('ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_status_check;');
        DB::statement("ALTER TABLE transactions ADD CONSTRAINT transactions_status_check CHECK (status IN ('selesai', 'batal'));");
    }
};