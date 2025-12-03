package com.calulla.projectseleranusantara

import Recipe
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calulla.projectseleranusantara.SavedActivity

class Search : AppCompatActivity() {

    // 🚨 PERBAIKAN 1: Deklarasi Variabel Kelas (agar tidak Unresolved Reference)
    private lateinit var adapter: RecipeVerticalAdapter
    private lateinit var edtSearch: EditText
    private lateinit var rvSearchResult: RecyclerView

    // Gunakan 'lateinit' untuk List
    private lateinit var originalList: MutableList<Recipe>
    private lateinit var filteredList: MutableList<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // NAVBAR ----
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navSearch = findViewById<LinearLayout>(R.id.navSearch)
        val navSaved = findViewById<LinearLayout>(R.id.navSaved)

        val iconHome = findViewById<ImageView>(R.id.iconHome)
        val iconSearch = findViewById<ImageView>(R.id.iconSearch)
        val iconSaved = findViewById<ImageView>(R.id.iconSaved)

        val textHome = findViewById<TextView>(R.id.textHome)
        val textSearch = findViewById<TextView>(R.id.textSearch)
        val textSaved = findViewById<TextView>(R.id.textSaved)

        iconHome.setColorFilter(Color.parseColor("#C4C4C4"))
        textHome.setTextColor(Color.parseColor("#C4C4C4"))

        iconSearch.setColorFilter(Color.parseColor("#FFA200"))
        textSearch.setTextColor(Color.parseColor("#FFA200"))

        iconSaved.setColorFilter(Color.parseColor("#C4C4C4"))
        textSaved.setTextColor(Color.parseColor("#C4C4C4"))

        navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navSaved.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // SEARCH UI ----
        // 🚨 PERBAIKAN 2: Gunakan variabel anggota kelas yang sudah dideklarasikan
        edtSearch = findViewById(R.id.edtSearch)
        rvSearchResult = findViewById(R.id.rvSearchResult)

        // ----------------------------------------
        // 🔍 --- DATA INITIALIZATION ---
        // ----------------------------------------

        // 🚨 PERBAIKAN 3: Memenuhi semua parameter (9 parameter) yang dibutuhkan oleh Recipe
        originalList = mutableListOf(
            Recipe(
                image = R.drawable.burger,
                title = "Chicken Burger",
                author = "Albert",
                creatorAvatar = R.drawable.avatar, // Diberi nilai default
                description = "Burger ayam crispy dengan saus BBQ spesial.", // Diberi nilai default
                ingredients = listOf("Roti burger", "Daging ayam crispy"), // Diberi nilai default
                username = "@albert99", // Diberi nilai default
                rating = 4.7, // Diberi nilai default
                youtubeLink = "https://www.youtube.com/watch?v=tSDtNCp51s4" // Diberi nilai default
            ),
            Recipe(
                image = R.drawable.kentaki,
                title = "Crispy Fried Chicken",
                author = "Erin Gemini",
                creatorAvatar = R.drawable.avatar,
                description = "Ayam goreng crispy ala Kentucky.",
                ingredients = listOf("Ayam", "Tepung", "Bumbu"),
                username = "@jameswk",
                rating = 4.7,
                youtubeLink = "https://youtu.be/h96RK21ovDU?si=5iV5-eCuZmBNcqCN"
            ),
            Recipe(
                image = R.drawable.lontong,
                title = "Lontong Sayur",
                author = "Resep Wina",
                creatorAvatar = R.drawable.avatar,
                description = "Lontong kuah santan gurih.",
                ingredients = listOf("Lontong", "Sayur", "Santan"),
                username = "@chefC",
                rating = 4.7,
                youtubeLink = "https://youtu.be/91T206VNMpk?si=dEobVdZ2U4T0QYbW"
            ),
            Recipe(
                image = R.drawable.pancake,
                title = "Cute Pancake",
                author = "Dimas",
                creatorAvatar = R.drawable.avatar,
                description = "Pancake lembut dengan topping buah.",
                ingredients = listOf("Tepung", "Susu", "Telur"),
                username = "@dimaschef",
                rating = 4.7,
                youtubeLink = "https://youtu.be/SaKgKfAqzAs?si=FHiKSeVZPZgHKTCG"
            ),
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
                R.drawable.ribeye,
                "Traditional spare ribs baked",
                "Clara Luis",
                R.drawable.avatar,
                "Spare ribs panggang dengan bumbu tradisional.",
                listOf("Daging ribs", "Lada", "Garam", "Madu"),
                username = "@claraluis",
                rating = 4.7,
                youtubeLink = "https://youtu.be/q0p8Nyag5dQ?si=DIPdq_HotMiGi0Y7"
            )
        )

        filteredList = originalList.toMutableList()

        // INISIALISASI ADAPTER DAN RV (Hanya lakukan sekali)
        adapter = RecipeVerticalAdapter(filteredList)
        rvSearchResult.layoutManager = LinearLayoutManager(this)
        rvSearchResult.adapter = adapter

        // HAPUS addTextChangedListener LAMBDA YANG SALAH

        // Event ketika mengetik (Sudah benar)
        edtSearch.addTextChangedListener(object : TextWatcher { // Ganti edt menjadi edtSearch
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
        })
    }

    private fun filterList(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(originalList)
        } else {
            filteredList.addAll(
                originalList.filter {
                    // FIX LOGIKA: Menggunakan it.title untuk pencarian
                    it.title.contains(query, ignoreCase = true)
                }
            )
        }

        adapter.notifyDataSetChanged()
    }
}