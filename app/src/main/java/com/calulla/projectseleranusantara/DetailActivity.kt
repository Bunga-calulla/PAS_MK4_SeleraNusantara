package com.calulla.projectseleranusantara

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // VIEW ID
        val ivFood = findViewById<ImageView>(R.id.ivFood)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvRating = findViewById<TextView>(R.id.tvRating)
        val ivCreator = findViewById<ImageView>(R.id.ivCreator)
        val tvCreatorName = findViewById<TextView>(R.id.tvCreatorName)
        val tvCreatorUsername = findViewById<TextView>(R.id.tvCreatorUsername)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val ingredientsContainer = findViewById<LinearLayout>(R.id.ingredientsContainer)
        val btnWatch = findViewById<Button>(R.id.btnWatch)

        // AMBIL DATA
        val image = intent.getIntExtra("image", 0)
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val description = intent.getStringExtra("description")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val creatorAvatar = intent.getIntExtra("creatorAvatar", 0)
        val username = intent.getStringExtra("username")
        val youtubeLink = intent.getStringExtra("youtubeLink")
        val ingredients = intent.getStringArrayListExtra("ingredients")

        // SET DATA
        ivFood.setImageResource(image)
        tvTitle.text = title
        tvRating.text = "(${rating})"
        ivCreator.setImageResource(creatorAvatar)
        tvCreatorName.text = author
        tvCreatorUsername.text = username
        tvDescription.text = description

        // INGREDIENTS LIST
        ingredients?.forEach { item ->
            val textView = TextView(this)
            textView.text = "• $item"
            textView.textSize = 14f
            textView.setPadding(0, 6, 0, 6)
            ingredientsContainer.addView(textView)
        }

        // WATCH BUTTON OPEN YOUTUBE
        btnWatch.setOnClickListener {
            if (!youtubeLink.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Video tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
