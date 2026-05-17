package com.calulla.projectseleranusantara

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.calulla.projectseleranusantara.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mengaktifkan ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Cek kalau sudah login, langsung lempar ke Home / Admin (gak usah login lagi)
        if (sessionManager.isLoggedIn()) {
            goToHomeBasedOnRole()
            return
        }

        // Tombol Sign In ditekan
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ubah text tombol biar kelihatan lagi loading
            binding.btnSignIn.text = "Mohon Tunggu..."
            binding.btnSignIn.isEnabled = false

            // Jalankan fungsi login
            doLogin(email, password)
        }

        // Teks "Belum punya akun?" ditekan -> Pindah ke halaman Register
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Tembak API Login lewat Retrofit
                val response = RetrofitClient.instance.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body()?.status == true) {
                    val bodyData = response.body()!!
                    val userToken = bodyData.token
                    val userRole = bodyData.data.role
                    val userName = bodyData.data.name

                    // Simpan data ke SessionManager (YANG TADI ERROR UDAH DIBENERIN DI SINI)
                    sessionManager.saveToken(userToken)
                    sessionManager.saveRole(userRole ?: "user")
                    sessionManager.saveUserName(userName)

                    Toast.makeText(this@LoginActivity, "Selamat datang, $userName!", Toast.LENGTH_SHORT).show()

                    // Pindah halaman
                    goToHomeBasedOnRole()

                } else {
                    // Kalau email/password salah
                    binding.btnSignIn.text = "Masuk"
                    binding.btnSignIn.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Login gagal: Email atau sandi salah", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                // Kalau error koneksi, server mati, atau wifi putus
                binding.btnSignIn.text = "Masuk"
                binding.btnSignIn.isEnabled = true
                Toast.makeText(this@LoginActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToHomeBasedOnRole() {
        if (sessionManager.isAdmin()) {
            // Kalau dia Admin, arahkan ke Dashboard Admin
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            // Kalau User biasa, arahkan ke Home / MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish() // Hancurkan activity login biar nggak bisa di-back
    }
}
