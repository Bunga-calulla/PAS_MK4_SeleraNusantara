package com.calulla.projectseleranusantara

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.calulla.projectseleranusantara.databinding.ActivitySavedBinding
import kotlinx.coroutines.launch

class SavedActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: RecipeVerticalAdapter
    private var allFavorites: List<RecipeData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // Setup RecyclerView
        binding.rvSaved.layoutManager = LinearLayoutManager(this)
        adapter = RecipeVerticalAdapter(emptyList())
        binding.rvSaved.adapter = adapter

        // Fetch favorite recipes from API
        fetchFavorites()

        // Setup search filter
        binding.etSearchSaved.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRecipes(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Setup back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Button Explore (Go to Search to discover more recipes)
        binding.btnExplore.setOnClickListener {
            startActivity(Intent(this, Search::class.java))
            overridePendingTransition(0, 0)
        }

        // Bottom Navigation click listeners
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        
        binding.navSearch.setOnClickListener {
            startActivity(Intent(this, Search::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh favorites list when user returns from DetailActivity (in case they unfavorited there)
        fetchFavorites()
    }

    private fun fetchFavorites() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val bearerToken = "Bearer $token"
                val response = RetrofitClient.instance.getFavoriteRecipes(bearerToken)

                if (response.isSuccessful && response.body()?.status == true) {
                    val favorites = response.body()?.data ?: emptyList()
                    allFavorites = favorites
                    
                    // Update UI
                    adapter.updateData(favorites)
                    binding.tvSavedCount.text = "${favorites.size} Resep Tersimpan"
                } else {
                    Toast.makeText(this@SavedActivity, "Gagal memuat resep favorit", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SavedActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterRecipes(query: String) {
        val filtered = if (query.isEmpty()) {
            allFavorites
        } else {
            allFavorites.filter {
                it.title.lowercase().contains(query.lowercase()) ||
                (it.user?.name ?: "").lowercase().contains(query.lowercase())
            }
        }
        adapter.updateData(filtered)
        binding.tvSavedCount.text = "${filtered.size} Resep Tersimpan"
    }
}
