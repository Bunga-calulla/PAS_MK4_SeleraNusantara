package com.calulla.projectseleranusantara

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeminumanusantaraAdapter(
    private var items: List<RecipeData>
) : RecyclerView.Adapter<RecipeminumanusantaraAdapter.ViewHolder>() {

    fun updateData(newItems: List<RecipeData>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // PERHATIAN: Pastikan ID-nya sama dengan yang ada di XML kamu
        val img: ImageView = view.findViewById(R.id.imgExplore)
        val title: TextView = view.findViewById(R.id.txtExploreTitle)
        val author: TextView = view.findViewById(R.id.txtExploreAuthor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // PERHATIAN: Kalau nama layout XML-nya bukan item_explore, tolong diganti ya!
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_explore, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Load gambar dari Server Laravel pakai custom loadImage
        holder.img.loadImage(item.image)

        holder.title.text = item.title
        holder.author.text = item.user?.name ?: "Anonim"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)

            // 🔥 CUKUP KIRIM ID SAJA 🔥
            intent.putExtra("RECIPE_ID", item.id)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
