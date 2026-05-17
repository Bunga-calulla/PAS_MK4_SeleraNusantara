package com.calulla.projectseleranusantara

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private var categoryId: Int = -1
    private var categoryName: String = ""

    private lateinit var popularAdapter: CategoryPopularAdapter
    private lateinit var gridAdapter: CategoryGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Resep"

        // Set Header Title
        val tvCategoryTitle = findViewById<TextView>(R.id.tvCategoryTitle)
        tvCategoryTitle.text = "Kategori $categoryName"

        // Set Section Titles
        val tvPopularHeader = findViewById<TextView>(R.id.tvPopularHeader)
        val tvRecommendationHeader = findViewById<TextView>(R.id.tvRecommendationHeader)

        tvPopularHeader.text = "🔥 $categoryName Populer"
        tvRecommendationHeader.text = "🍴 Rekomendasi $categoryName"

        // Setup Back Button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Setup Recycler Views
        val rvPopularCategory = findViewById<RecyclerView>(R.id.rvPopularCategory)
        rvPopularCategory.layoutManager = LinearLayoutManager(this)
        popularAdapter = CategoryPopularAdapter(emptyList())
        rvPopularCategory.adapter = popularAdapter

        val rvGridCategory = findViewById<RecyclerView>(R.id.rvGridCategory)
        rvGridCategory.layoutManager = GridLayoutManager(this, 2)
        gridAdapter = CategoryGridAdapter(emptyList())
        rvGridCategory.adapter = gridAdapter

        // Fetch Recipes belonging to this Category
        fetchCategoryRecipes()
    }

    private fun fetchCategoryRecipes() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getRecipes(categoryId = categoryId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val allRecipes = response.body()!!.data.data

                    if (allRecipes.isEmpty()) {
                        Toast.makeText(this@CategoryActivity, "Tidak ada resep di kategori ini", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Split into Popular (first 3) and Recommendation (the rest)
                    val popularList = allRecipes.take(3)
                    val gridList = allRecipes.drop(3)

                    popularAdapter.updateData(popularList)
                    gridAdapter.updateData(gridList)
                } else {
                    Toast.makeText(this@CategoryActivity, "Gagal mengambil resep kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
