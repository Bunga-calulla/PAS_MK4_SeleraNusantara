package com.calulla.projectseleranusantara

import Recipe
import android.content.Intent // DITAMBAHKAN: Untuk berpindah Activity
import android.os.Bundle
import android.widget.LinearLayout // DITAMBAHKAN: Untuk mengakses elemen navigasi
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val popularList = listOf(
            Recipe(
                image = R.drawable.burger,
                title = "Chicken Burger",
                author = "Albert",
                creatorAvatar = R.drawable.avatar,
                description = "Burger ayam crispy dengan saus BBQ spesial.",
                ingredients = listOf(
                    "Roti burger",
                    "Daging ayam crispy",
                    "Selada",
                    "Tomat",
                    "Saus BBQ"
                ),
                username = "@albert99",
                rating = 4.7,
                youtubeLink = "https://www.youtube.com/watch?v=tSDtNCp51s4"
            ),
            Recipe(
                image = R.drawable.pancake,
                title = "Cute Pancake",
                author = "Sheila",
                creatorAvatar = R.drawable.avatar,
                description = "Pancake lembut dengan topping buah segar.",
                ingredients = listOf("Tepung", "Susu", "Telur", "Gula", "Madu"),
                username = "@sheilafoodie",
                rating = 4.7,
                youtubeLink = "https://youtu.be/SaKgKfAqzAs?si=FHiKSeVZPZgHKTCG"
            ),
            Recipe(
                image = R.drawable.kentaki,
                title = "Crispy Fried Chicken",
                author = "Erin Gemini",
                creatorAvatar = R.drawable.avatar,
                description = "Ayam goreng crispy ala Kentucky dengan bumbu rahasia.",
                ingredients = listOf("Ayam", "Tepung", "Lada", "Garam", "Telur"),
                username = "@jameswk",
                rating = 4.7,
                youtubeLink = "https://www.youtube.com/watch?v=tSDtNCp51s4"
            )
        )


        val newList = listOf(
            Recipe(
                R.drawable.sotoayam,
                "Soto Ayam",    
                "Adrianne Curl",
                R.drawable.avatar,
                "Soto ayam hangat dengan kuah kuning khas.",
                listOf("Ayam", "Soun", "Telur", "Daun bawang"),
                username = "@adriannec",
                rating = 4.7,
                youtubeLink = "https://youtu.be/pJ-PMDJ0x38?si=Q1NfLjV5qA9mzCaL"
            ),
            Recipe(
                R.drawable.nasi_kuning,
                "Nasi Kuning",
                "Budi",
                R.drawable.avatar,
                "Nasi kuning gurih dengan lauk komplet.",
                listOf("Nasi", "Kunyit", "Telur", "Ayam suwir"),
                username = "@budicook",
                rating = 4.7,
                youtubeLink = "https://youtu.be/qPrZT0btu7s?si=0Y7OFiv6RfoFvFe7"
            ),
            Recipe(
                R.drawable.lontong,
                "Lontong Sayur",
                "Resep Wina",
                R.drawable.avatar,
                "Lontong dengan kuah santan gurih dan rempah.",
                listOf("Lontong", "Ayam", "Santan", "Sayur labu"),
                username = "@dimaschef",
                rating = 4.7,
                youtubeLink = "https://youtu.be/91T206VNMpk?si=dEobVdZ2U4T0QYbW"
            )
        )


        val exploreList = listOf(
            Recipe(
                R.drawable.ribeye,
                "Traditional spare ribs baked",
                "Clara Luis",
                R.drawable.avatar,
                "Spare ribs panggang dengan bumbu tradisional.",
                listOf("Daging ribs", "Lada", "Garam", "Madu"),
                username = "@claraluis",
                rating = 4.7,
                youtubeLink = "https://youtu.be/q0p8Nyag5dQ?si=DIPdq_HotMiGi0Y7"
            ),
            Recipe(
                R.drawable.nasi_kuning,
                "Spicy fried rice chicken bali",
                "Mega Haru",
                R.drawable.avatar,
                "Nasi goreng pedas khas Bali.",
                listOf("Nasi", "Ayam", "Cabe", "Kecap"),
                username = "@megaharu",
                rating = 4.7,
                youtubeLink = "https://youtu.be/qPrZT0btu7s?si=0Y7OFiv6RfoFvFe7"
            )
        )


        // Popular
        findViewById<RecyclerView>(R.id.rvPopular).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = RecipeHorizontalAdapter(popularList)
        }

        // New
        findViewById<RecyclerView>(R.id.rvNew).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = RecipeHorizontalAdapter(newList)
        }

        // Explore
        findViewById<RecyclerView>(R.id.rvExplore).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecipeVerticalAdapter(exploreList)
        }

        // ----------------------------------------
        // 🚀 LOGIKA NAVIGASI BOTTOM BAR (DITAMBAHKAN)
        // ----------------------------------------

        // 1. Identifikasi tombol Search (LinearLayout) dari layout (asumsi ID: navSearch)
        val navSearch = findViewById<LinearLayout>(R.id.navSearch)

        // 2. Tambahkan Click Listener
        navSearch.setOnClickListener {
            // Membuat Intent untuk berpindah dari MainActivity ke Search Activity
            startActivity(Intent(this, Search::class.java))
            // Opsional: Menonaktifkan animasi transisi (Seperti yang Anda lakukan di Search.kt)
            overridePendingTransition(0, 0)
            // Opsional: Jika Anda tidak ingin kembali ke MainActivity setelah pindah
            // finish()
        }

        // Anda mungkin ingin menambahkan navigasi untuk navSaved dan navHome juga di sini
        // Misalnya:
        /*
        val navSaved = findViewById<LinearLayout>(R.id.navSaved)
        navSaved.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
            overridePendingTransition(0, 0)
        }
        */

    }
}