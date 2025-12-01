package com.calulla.projectseleranusantara

import Recipe
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeHorizontalAdapter(
    private val items: List<Recipe>
) : RecyclerView.Adapter<RecipeHorizontalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgRecipe)
        val title: TextView = view.findViewById(R.id.txtTitle)
        val author: TextView = view.findViewById(R.id.txtAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_horizontal, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Set data list
        holder.img.setImageResource(item.image)
        holder.title.text = item.title
        holder.author.text = item.author

        // Klik item → pindah ke DetailActivity
        holder.itemView.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, DetailActivity::class.java)

            intent.putExtra("image", item.image)
            intent.putExtra("title", item.title)
            intent.putExtra("author", item.author)
            intent.putExtra("description", item.description)
            intent.putExtra("rating", item.rating)
            intent.putExtra("creatorAvatar", item.creatorAvatar)
            intent.putExtra("username", item.username)

            // Ingredients
            intent.putStringArrayListExtra("ingredients", ArrayList(item.ingredients))

            // YOUTUBE LINK (NEW)
            intent.putExtra("youtubeLink", item.youtubeLink)

            ctx.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size
}
