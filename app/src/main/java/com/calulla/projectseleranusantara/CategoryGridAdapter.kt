package com.calulla.projectseleranusantara

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryGridAdapter(
    private var list: List<RecipeData>
) : RecyclerView.Adapter<CategoryGridAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgRecipe: ImageView = itemView.findViewById(R.id.imgRecipe)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = list[position]
        holder.imgRecipe.loadImage(recipe.image)
        holder.tvTitle.text = recipe.title

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("RECIPE_ID", recipe.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<RecipeData>) {
        list = newList
        notifyDataSetChanged()
    }
}
