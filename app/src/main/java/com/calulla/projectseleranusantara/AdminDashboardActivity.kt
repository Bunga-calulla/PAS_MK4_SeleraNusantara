package com.calulla.projectseleranusantara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var rvAdmin: RecyclerView
    private lateinit var adapter: AdminAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)
        rvAdmin = findViewById(R.id.rvAdmin)

        // Inisialisasi adapter kosong dulu dengan aksi Edit dan Delete
        adapter = AdminAdapter(
            list = emptyList(),
            onEditClick = { clickedRecipe ->
                val intent = Intent(this, FormRecipeActivity::class.java).apply {
                    putExtra("IS_EDIT", true)
                    putExtra("RECIPE_ID", clickedRecipe.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { clickedRecipe ->
                // Panggil fungsi hapus ke API
                deleteRecipeFromAPI(clickedRecipe.id)
            }
        )

        rvAdmin.layoutManager = LinearLayoutManager(this)
        rvAdmin.adapter = adapter

        // 🟢 TOMBOL TAMBAH RESEP
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            // Pindah ke halaman form tambah resep
            startActivity(Intent(this, FormRecipeActivity::class.java))
        }

        // 🟢 TOMBOL KELOLA KATEGORI
        val btnManageCategory = findViewById<Button>(R.id.btnManageCategory)
        btnManageCategory.setOnClickListener {
            startActivity(Intent(this, ManageCategoryActivity::class.java))
        }

        // 🛑 TOMBOL LOGOUT ADMIN 🛑
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()

            // Lempar ke login dan bersihkan semua tumpukan Activity sebelumnya
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        // Tampilkan data awal
        fetchAdminRecipes()
    }

    // 🟢 FUNGSI REFRESH OTOMATIS
    // Dipanggil setiap kali halaman ini muncul kembali ke layar (misal setelah selesai nambah resep)
    override fun onResume() {
        super.onResume()
        fetchAdminRecipes()
    }

    private fun fetchAdminRecipes() {
        lifecycleScope.launch {
            try {
                // Tembak API untuk ambil data
                val response = RetrofitClient.instance.getRecipes()

                if (response.isSuccessful && response.body()?.status == true) {
                    val recipeList = response.body()!!.data.data

                    // Update tampilan adapter dengan data dari server
                    adapter.updateData(recipeList)
                } else {
                    Toast.makeText(this@AdminDashboardActivity, "Gagal memuat resep", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🟢 FUNGSI HAPUS RESEP
    private fun deleteRecipeFromAPI(recipeId: Int) {
        lifecycleScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken()}"

                // Tembak endpoint DELETE di Laravel
                val response = RetrofitClient.instance.deleteRecipe(token, recipeId)

                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminDashboardActivity, "Berhasil menghapus resep!", Toast.LENGTH_SHORT).show()
                    // Refresh data setelah dihapus
                    fetchAdminRecipes()
                } else {
                    Toast.makeText(this@AdminDashboardActivity, "Gagal menghapus resep", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
