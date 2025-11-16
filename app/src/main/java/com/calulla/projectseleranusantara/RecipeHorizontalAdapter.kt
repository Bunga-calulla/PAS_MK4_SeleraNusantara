package com.calulla.projectseleranusantara

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
        val img = view.findViewById<ImageView>(R.id.imgRecipe)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val author = view.findViewById<TextView>(R.id.txtAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_horizontal, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.img.setImageResource(item.image)
        holder.title.text = item.title
        holder.author.text = item.author
    }

    override fun getItemCount() = items.size
}

