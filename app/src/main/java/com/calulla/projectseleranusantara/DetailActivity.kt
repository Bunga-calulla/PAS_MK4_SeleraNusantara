package com.calulla.projectseleranusantara

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.calulla.projectseleranusantara.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var sessionManager: SessionManager
    private var isFavorite: Boolean = false
    private var recipeData: RecipeData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. Ambil ID resep yang dikirim dari MainActivity
        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        if (recipeId != -1) {
            loadRecipeDetail(recipeId)
            checkFavoriteState(recipeId)
        } else {
            Toast.makeText(this, "Resep tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Tombol Kembali
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Tombol Share (Bagikan)
        binding.btnShare.setOnClickListener {
            shareRecipe()
        }

        // Tombol Cari Toko Bahan Terdekat di Google Maps
        binding.btnFindIngredients.setOnClickListener {
            val query = "toko bahan makanan terdekat"
            val intentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
            startActivity(mapIntent)
        }

        // Tombol Favorit
        binding.btnFavorite.setOnClickListener {
            if (sessionManager.isLoggedIn()) {
                toggleFavoriteState(recipeId)
            } else {
                Toast.makeText(this, "Silakan login terlebih dahulu untuk menyimpan favorit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkFavoriteState(recipeId: Int) {
        if (!sessionManager.isLoggedIn()) {
            isFavorite = false
            updateFavoriteUI()
            return
        }

        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val response = RetrofitClient.instance.checkFavorite(token, recipeId)
                if (response.isSuccessful && response.body()?.status == true) {
                    isFavorite = response.body()!!.is_favorite
                    updateFavoriteUI()
                }
            } catch (e: Exception) {
                // Abaikan error saat pengecekan awal
            }
        }
    }

    private fun toggleFavoriteState(recipeId: Int) {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val response = RetrofitClient.instance.toggleFavorite(token, recipeId)
                if (response.isSuccessful && response.body()?.status == true) {
                    isFavorite = response.body()!!.is_favorite
                    updateFavoriteUI()
                    val message = response.body()!!.message ?: if (isFavorite) "Ditambahkan ke favorit" else "Dihapus dari favorit"
                    Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DetailActivity, "Gagal mengubah favorit", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteUI() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_saved)
            binding.btnFavorite.imageTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#FF9800")
            )
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_saved)
            binding.btnFavorite.imageTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#111111")
            )
        }
    }

    private fun loadRecipeDetail(id: Int) {
        lifecycleScope.launch {
            try {
                // Tembak API Laravel
                val response = RetrofitClient.instance.getRecipeDetail(id)

                if (response.isSuccessful && response.body()?.status == true) {
                    val recipe = response.body()!!.data
                    recipeData = recipe

                    // 2. Pasang Text (Judul, Durasi, Deskripsi, dll)
                    binding.tvTitle.text = recipe.title
                    binding.tvDuration.text = "  •  ${recipe.cooking_time} Menit"
                    binding.tvDescription.text = recipe.description ?: "Tidak ada deskripsi."

                    // Nama Creator (Author)
                    binding.tvCreatorName.text = recipe.user?.name ?: "Anonim"
                    binding.tvCreatorUsername.text = "@${recipe.user?.name?.lowercase()?.replace(" ", "") ?: "user"}"

                    // 3. Load Gambar Utama pakai custom loadImage
                    binding.ivFood.loadImage(recipe.image)

                    // 4. Logika YouTube Video
                    if (!recipe.video_url.isNullOrEmpty()) {
                        binding.youtubeSection.visibility = View.VISIBLE
                        binding.tvVideoTitle.text = recipe.title
                        
                        // Ekstrak ID video youtube dan tampilkan thumbnail
                        val videoId = getYoutubeVideoId(recipe.video_url)
                        if (!videoId.isNullOrEmpty()) {
                            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
                            binding.imgYoutube.loadImage(thumbnailUrl)
                        }

                        binding.btnYoutube.visibility = View.VISIBLE
                        binding.btnWatch.visibility = View.VISIBLE
                        binding.imgYoutube.visibility = View.VISIBLE

                        // Tombol YouTube Ditekan -> Buka App YouTube
                        val openYoutube = View.OnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.video_url))
                            startActivity(intent)
                        }
                        binding.btnYoutube.setOnClickListener(openYoutube)
                        binding.btnWatch.setOnClickListener(openYoutube)
                    } else {
                        // Sembunyikan bagian video kalau tidak tersedia
                        binding.youtubeSection.visibility = View.GONE
                    }

                    // 5. Render Daftar Bahan (Ingredients) secara dinamis
                    if (!recipe.ingredients.isNullOrEmpty()) {
                        binding.tvIngredientCount.text = "${recipe.ingredients.size} Bahan"
                        binding.ingredientsContainer.removeAllViews()

                        for (bahan in recipe.ingredients) {
                            val ingredientView = createIngredientView(bahan)
                            binding.ingredientsContainer.addView(ingredientView)
                        }
                    } else {
                        binding.tvIngredientCount.text = "0 Bahan"
                        binding.ingredientsContainer.removeAllViews()
                    }

                    // 6. Render Daftar Langkah (Steps) secara dinamis
                    if (!recipe.steps.isNullOrEmpty()) {
                        binding.stepsContainer.removeAllViews()
                        
                        // Urutkan langkah berdasarkan step_number
                        val sortedSteps = recipe.steps.sortedBy { it.step_number }
                        for (step in sortedSteps) {
                            val stepView = createStepView(step)
                            binding.stepsContainer.addView(stepView)
                        }
                    } else {
                        binding.stepsContainer.removeAllViews()
                    }

                } else {
                    Toast.makeText(this@DetailActivity, "Gagal memuat resep", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getYoutubeVideoId(url: String): String? {
        val pattern = "^(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})".toRegex()
        val matchResult = pattern.find(url)
        return matchResult?.groupValues?.get(1)
    }

    // Fungsi bantuan untuk membuat UI 1 baris Bahan secara otomatis
    private fun createIngredientView(bahan: IngredientData): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 24) }
            setPadding(32, 24, 32, 24)
            gravity = android.view.Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.input_bg)
        }

        // Icon Emoji
        val icon = TextView(this).apply {
            text = "🥘"
            textSize = 22f
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(100, 100)
        }

        // Teks Container (Nama & Kuantitas)
        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(24, 0, 0, 0) }
        }

        val tvName = TextView(this).apply {
            text = bahan.name
            setTextColor(android.graphics.Color.parseColor("#111111"))
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvQty = TextView(this).apply {
            text = "${bahan.quantity} ${bahan.unit ?: ""}"
            setTextColor(android.graphics.Color.parseColor("#FF9800"))
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.NORMAL)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 4, 0, 0) }
        }

        textLayout.addView(tvName)
        textLayout.addView(tvQty)

        layout.addView(icon)
        layout.addView(textLayout)

        return layout
    }

    // Fungsi bantuan untuk membuat UI 1 baris Langkah secara otomatis
    private fun createStepView(step: StepData): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 24) }
            setPadding(32, 24, 32, 24)
            gravity = android.view.Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.input_bg)
        }

        // Bulatan Nomor Langkah
        val circleDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(android.graphics.Color.parseColor("#FF9800"))
        }

        val stepBadge = TextView(this).apply {
            text = step.step_number.toString()
            setTextColor(android.graphics.Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            background = circleDrawable
            layoutParams = LinearLayout.LayoutParams(80, 80)
        }

        // Teks Instruksi
        val tvInstruction = TextView(this).apply {
            text = step.instruction
            setTextColor(android.graphics.Color.parseColor("#333333"))
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(24, 0, 0, 0) }
        }

        layout.addView(stepBadge)
        layout.addView(tvInstruction)

        return layout
    }

    private fun shareRecipe() {
        val recipe = recipeData
        if (recipe == null) {
            Toast.makeText(this, "Data resep belum siap dibagikan", Toast.LENGTH_SHORT).show()
            return
        }

        val shareBuilder = StringBuilder()
        shareBuilder.append("🍲 *RESEP: ${recipe.title.uppercase()}* 🍲\n\n")
        
        if (!recipe.description.isNullOrEmpty()) {
            shareBuilder.append("${recipe.description}\n\n")
        }
        
        shareBuilder.append("⏱️ *Waktu Memasak:* ${recipe.cooking_time} Menit\n")
        shareBuilder.append("👥 *Porsi:* ${recipe.servings} Porsi\n")
        shareBuilder.append("🔥 *Tingkat Kesulitan:* ${recipe.difficulty}\n\n")

        shareBuilder.append("🛒 *BAHAN-BAHAN:*\n")
        if (!recipe.ingredients.isNullOrEmpty()) {
            for (bahan in recipe.ingredients) {
                shareBuilder.append("- ${bahan.name} (${bahan.quantity} ${bahan.unit ?: ""})\n")
            }
        } else {
            shareBuilder.append("- Tidak ada bahan terdaftar.\n")
        }
        shareBuilder.append("\n")

        shareBuilder.append("👨‍🍳 *LANGKAH PEMBUATAN:*\n")
        if (!recipe.steps.isNullOrEmpty()) {
            val sortedSteps = recipe.steps.sortedBy { it.step_number }
            for (step in sortedSteps) {
                shareBuilder.append("${step.step_number}. ${step.instruction}\n")
            }
        } else {
            shareBuilder.append("- Tidak ada langkah terdaftar.\n")
        }
        
        shareBuilder.append("\nBagikan resep lezat ini via aplikasi *Selera Nusantara*! 🇮🇩✨")

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareBuilder.toString())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan Resep via"))
    }
}
