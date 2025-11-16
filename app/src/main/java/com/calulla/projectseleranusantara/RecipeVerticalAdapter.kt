package com.calulla.projectseleranusantara

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
        val img = view.findViewById<ImageView>(R.id.imgExplore)
        val title = view.findViewById<TextView>(R.id.txtExploreTitle)
        val author = view.findViewById<TextView>(R.id.txtExploreAuthor)
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
    }

    override fun getItemCount() = items.size
}
