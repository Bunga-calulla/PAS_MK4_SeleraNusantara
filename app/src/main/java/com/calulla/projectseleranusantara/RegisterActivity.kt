package com.calulla.projectseleranusantara

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.calulla.projectseleranusantara.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Tombol Daftar ditekan
        binding.btnSignUp.setOnClickListener {
            val name = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Validasi Input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Kata sandi tidak sama!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Kata sandi minimal 8 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ganti teks tombol
            binding.btnSignUp.text = "Mohon Tunggu..."
            binding.btnSignUp.isEnabled = false

            // Jalankan fungsi daftar
            doRegister(name, email, password, confirmPassword)
        }

        // Teks "Sudah punya akun?" ditekan -> Balik ke halaman Login
        binding.tvSignIn.setOnClickListener {
            finish() // Langsung hancurkan halaman Register, otomatis balik ke Login
        }
    }

    private fun doRegister(name: String, email: String, pass: String, passConf: String) {
        lifecycleScope.launch {
            try {
                // Tembak API Register
                val request = RegisterRequest(name, email, pass, passConf)
                val response = RetrofitClient.instance.register(request)

                if (response.isSuccessful && response.body()?.status == true) {
                    val bodyData = response.body()!!

                    // Simpan Token, Role, dan Name ke SessionManager
                    sessionManager.saveToken(bodyData.token)

                    // 🔥 INI DIA YANG DIBENERIN (ditambah ?: "user") 🔥
                    sessionManager.saveRole(bodyData.data.role ?: "user")

                    sessionManager.saveUserName(bodyData.data.name)

                    Toast.makeText(this@RegisterActivity, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()

                    // Pindah ke MainActivity
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finishAffinity() // Hapus semua riwayat halaman sebelumnya biar gak bisa di-back ke register

                } else {
                    binding.btnSignUp.text = "Daftar"
                    binding.btnSignUp.isEnabled = true
                    // Biasanya gagal karena email sudah dipakai
                    Toast.makeText(this@RegisterActivity, "Gagal: Email mungkin sudah terdaftar", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                binding.btnSignUp.text = "Daftar"
                binding.btnSignUp.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
