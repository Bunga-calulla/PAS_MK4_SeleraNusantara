package com.calulla.projectseleranusantara

import Recipe
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeVerticalAdapter(
    private val items: List<Recipe>
) : RecyclerView.Adapter<RecipeVerticalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgExplore)
        val title: TextView = view.findViewById(R.id.txtExploreTitle)
        val author: TextView = view.findViewById(R.id.txtExploreAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_vertical, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.img.setImageResource(item.image)
        holder.title.text = item.title
        holder.author.text = item.author

        // --- ONCLICK: buka DetailActivity ---
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

            // kirim ingredients
            intent.putStringArrayListExtra("ingredients", ArrayList(item.ingredients))

            // --- kirim YouTube URL ---
            intent.putExtra("youtubeLink", item.youtubeLink)

            ctx.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size
}
