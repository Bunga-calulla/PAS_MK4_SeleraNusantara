package com.calulla.projectseleranusantara

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var categoryAdapter: HomeCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        // Tampilkan Nama User dari Session
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        tvUserName.text = sessionManager.getUserName() ?: "Guest"

        // 🛑 FUNGSI LOGOUT (TOMBOL POJOK KANAN ATAS) 🛑
        val btnLogout = findViewById<FrameLayout>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Hapus sesi (Token, Role, dll) dari SessionManager
            sessionManager.clearSession()
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()

            // Lempar kembali ke halaman Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Hancurkan halaman Home biar nggak bisa di-back ke sini lagi
        }

        // Klik Kolom Cari Dummy di Home langsung mengarah ke halaman Search (Cari)
        val searchBarContainer = findViewById<LinearLayout>(R.id.searchBarContainer)
        searchBarContainer.setOnClickListener {
            startActivity(Intent(this, Search::class.java))
            overridePendingTransition(0, 0)
        }

        // Setup Category Horizontal RecyclerView
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = HomeCategoryAdapter(emptyList()) { clickedCategory ->
            if (clickedCategory.id == -1) {
                // Semua Resep
                fetchRecipesFromAPI(null)
            } else {
                // Pindah ke Halaman Detail Kategori Dinamis!
                val intent = Intent(this, CategoryActivity::class.java).apply {
                    putExtra("CATEGORY_ID", clickedCategory.id)
                    putExtra("CATEGORY_NAME", clickedCategory.name)
                }
                startActivity(intent)
            }
        }
        rvCategories.adapter = categoryAdapter

        // Setup RecyclerView Resep Populer
        val rvPopular = findViewById<RecyclerView>(R.id.rvPopular)
        rvPopular.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi Adapter kosong dulu (nanti diisi pas data dari API datang)
        recipeAdapter = RecipeAdapter(emptyList()) { clickedRecipe ->
            // Aksi kalau resep diklik -> Pindah ke DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("RECIPE_ID", clickedRecipe.id)
            startActivity(intent)
        }
        rvPopular.adapter = recipeAdapter

        // ==========================================
        // 🔥 TARIK DATA KATEGORI & RESEP DARI LARAVEL (API) 🔥
        // ==========================================
        fetchCategoriesFromAPI()
        fetchRecipesFromAPI(null)

        // ----------------------------------------
        // LOGIKA NAVIGASI BOTTOM BAR
        // ----------------------------------------
        val navSearch = findViewById<LinearLayout>(R.id.navSearch)
        navSearch.setOnClickListener {
            startActivity(Intent(this, Search::class.java))
            overridePendingTransition(0, 0)
        }

        val navSaved = findViewById<LinearLayout>(R.id.navSaved)
        navSaved.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun fetchCategoriesFromAPI() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                if (response.isSuccessful && response.body()?.status == true) {
                    val categoryList = response.body()!!.data
                    categoryAdapter.updateData(categoryList)
                } else {
                    Toast.makeText(this@MainActivity, "Gagal mengambil data kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Koneksi Error Kategori: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchRecipesFromAPI(categoryId: Int? = null) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getRecipes(categoryId = categoryId)

                if (response.isSuccessful && response.body()?.status == true) {
                    val recipeList = response.body()!!.data.data // Ambil list resep

                    // Update data di adapter
                    recipeAdapter.updateData(recipeList)
                } else {
                    Toast.makeText(this@MainActivity, "Gagal mengambil data resep", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
