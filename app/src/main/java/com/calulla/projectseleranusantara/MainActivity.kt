package com.calulla.projectseleranusantara

import android.os.Bundle
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
            Recipe(R.drawable.nasi_kuning, "Indonesian chicken burger", "By Adrianne Curl"),
            Recipe(R.drawable.nasi_kuning, "Home made cute pancake", "By James Woklen"),
        )

        val newList = listOf(
            Recipe(R.drawable.nasi_kuning, "Indonesian chicken burger", "By Adrianne Curl"),
            Recipe(R.drawable.nasi_kuning, "Home made cute pancake", "By James Woklen"),
        )

        val exploreList = listOf(
            Recipe(R.drawable.nasi_kuning, "Traditional spare ribs baked", "By Clara Luis"),
            Recipe(R.drawable.nasi_kuning, "Spicy fried rice chicken bali", "By Mega Haru"),
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
    }
}
