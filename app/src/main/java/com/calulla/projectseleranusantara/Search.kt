package com.calulla.projectseleranusantara

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Search : AppCompatActivity() {

    private lateinit var adapter: RecipeVerticalAdapter
    private lateinit var edtSearch: EditText
    private lateinit var rvSearchResult: RecyclerView

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupNavbar()

        edtSearch = findViewById(R.id.edtSearch)
        rvSearchResult = findViewById(R.id.rvSearchResult)

        adapter = RecipeVerticalAdapter(emptyList())
        rvSearchResult.layoutManager = LinearLayoutManager(this)
        rvSearchResult.adapter = adapter

        // Load awal history pencarian
        loadSearchHistory()

        // Ambil resep awal (populer - max 5)
        searchRecipesFromApi("")

        // Event enter atau tombol search di keyboard
        edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO) {
                
                val query = edtSearch.text.toString().trim()
                searchJob?.cancel()
                searchRecipesFromApi(query)
                
                if (query.isNotEmpty()) {
                    saveSearchHistory(query)
                }

                // Sembunyikan Keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                true
            } else {
                false
            }
        }

        // Event ketika mulai mengetik (debounce 500ms)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()

                searchJob = lifecycleScope.launch {
                    delay(500)
                    val query = s.toString().trim()
                    searchRecipesFromApi(query)
                    
                    // Otomatis simpan ke riwayat jika user diam mengetik selama 1.5 detik dan query tidak kosong
                    if (query.isNotEmpty()) {
                        delay(1000)
                        if (edtSearch.text.toString().trim() == query) {
                            saveSearchHistory(query)
                        }
                    }
                }
            }
        })
    }

    private fun searchRecipesFromApi(query: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getRecipes(search = query)

                if (response.isSuccessful && response.body()?.status == true) {
                    val recipeList = response.body()!!.data.data
                    // Jika query pencarian kosong, batasi resep populer maks 5 item
                    val finalRecipes = if (query.isEmpty()) {
                        recipeList.take(5)
                    } else {
                        recipeList
                    }
                    adapter.updateData(finalRecipes)
                } else {
                    Toast.makeText(this@Search, "Gagal mencari resep", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Search, "Koneksi Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSearchHistory(query: String) {
        if (query.isEmpty()) return
        val sharedPrefs = getSharedPreferences("search_history_prefs", MODE_PRIVATE)
        val rawHistory = sharedPrefs.getString("history_list", "") ?: ""
        val historyList = if (rawHistory.isEmpty()) mutableListOf() else rawHistory.split("||").toMutableList()

        historyList.remove(query)
        historyList.add(0, query)

        val cappedHistory = historyList.take(5)
        val newRawHistory = cappedHistory.joinToString("||")

        sharedPrefs.edit().putString("history_list", newRawHistory).apply()
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        val sharedPrefs = getSharedPreferences("search_history_prefs", MODE_PRIVATE)
        val rawHistory = sharedPrefs.getString("history_list", "") ?: ""
        val historyList = if (rawHistory.isEmpty()) emptyList() else rawHistory.split("||")

        val containerHistory = findViewById<LinearLayout>(R.id.containerHistory)
        val layoutHistory = findViewById<LinearLayout>(R.id.layoutHistory)

        layoutHistory.removeAllViews()

        if (historyList.isEmpty()) {
            containerHistory.visibility = View.GONE
        } else {
            containerHistory.visibility = View.VISIBLE

            for (query in historyList) {
                val chip = LayoutInflater.from(this).inflate(R.layout.item_history_chip, layoutHistory, false) as TextView
                chip.text = query
                chip.setOnClickListener {
                    edtSearch.setText(query)
                    edtSearch.setSelection(query.length)
                    searchJob?.cancel()
                    searchRecipesFromApi(query)
                }
                layoutHistory.addView(chip)
            }
        }
    }

    private fun setupNavbar() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navSaved = findViewById<LinearLayout>(R.id.navSaved)

        val iconHome = findViewById<ImageView>(R.id.iconHome)
        val iconSearch = findViewById<ImageView>(R.id.iconSearch)
        val iconSaved = findViewById<ImageView>(R.id.iconSaved)

        val textHome = findViewById<TextView>(R.id.textHome)
        val textSearch = findViewById<TextView>(R.id.textSearch)
        val textSaved = findViewById<TextView>(R.id.textSaved)

        iconHome.setColorFilter(Color.parseColor("#C4C4C4"))
        textHome.setTextColor(Color.parseColor("#C4C4C4"))

        iconSearch.setColorFilter(Color.parseColor("#FF9800"))
        textSearch.setTextColor(Color.parseColor("#FF9800"))

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
    }
}
