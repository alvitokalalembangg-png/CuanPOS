<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    //
    public function loginMobile(Request $request) {
        $request->validate([
            'pin' => 'required|string|size:6'
        ]);

        $user = User::where('pin', $request->pin)->first();

        if (!$user) {
            return response()->json(['message' => 'PIN salah atau tidak ditemukan'], 401);
        }

        $token = $user->createToken('mobile-pos-token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user' => $user
        ]);
    }

    public function loginAdmin(Request $request) {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required'
        ]);

        // Cari user yang emailnya cocok DAN rolenya adalah admin
        $user = User::where('email', $request->email)->where('role', 'admin')->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return response()->json(['message' => 'Email atau password salah, atau Anda bukan admin.'], 401);
        }

        $token = $user->createToken('admin-token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user' => $user
        ]);
    }
}
