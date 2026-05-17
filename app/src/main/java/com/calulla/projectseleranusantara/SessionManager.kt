package com.calulla.projectseleranusantara

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    fun getToken(): String?      = prefs.getString(Constants.KEY_TOKEN, null)
    fun getBearerToken(): String = "Bearer ${getToken()}"
    fun saveUserName(name: String) = prefs.edit().putString(Constants.KEY_USER_NAME, name).apply()
    fun getUserName(): String?   = prefs.getString(Constants.KEY_USER_NAME, null)
    fun isLoggedIn(): Boolean    = getToken() != null
    fun logout()                 = prefs.edit().clear().apply()
    // Tambahkan 3 fungsi ini di dalam SessionManager.kt

    // Simpan Role
    fun saveRole(role: String) {
        prefs.edit().putString("user_role", role).apply()
    }

    // Ambil Role
    fun getRole(): String? = prefs.getString("user_role", "user") // defaultnya 'user'

    // Cek Admin
    fun isAdmin(): Boolean = getRole() == "admin"

    // Fungsi untuk menghapus semua data saat Logout
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }


}
