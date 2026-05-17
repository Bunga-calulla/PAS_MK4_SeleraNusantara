package com.calulla.projectseleranusantara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private var recipes: List<RecipeData>,
    private val onItemClick: (RecipeData) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    fun updateData(newRecipes: List<RecipeData>) {
        recipes = newRecipes
        notifyDataSetChanged() // Refresh tampilan
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgRecipe: ImageView = itemView.findViewById(R.id.imgRecipe)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvInfo: TextView = itemView.findViewById(R.id.tvInfo)

        fun bind(recipe: RecipeData) {
            tvTitle.text = recipe.title
            tvDescription.text = recipe.description ?: "Tidak ada deskripsi"
            tvInfo.text = "⏱️ ${recipe.cooking_time} Menit | 🔥 ${recipe.difficulty}"

            // Gunakan custom loadImage untuk gambar dari URL Server Laravel
            imgRecipe.loadImage(recipe.image, R.drawable.bg_register)

            itemView.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        // PERHATIKAN: Pastikan nama layout XML-nya item_recipe
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size
}
