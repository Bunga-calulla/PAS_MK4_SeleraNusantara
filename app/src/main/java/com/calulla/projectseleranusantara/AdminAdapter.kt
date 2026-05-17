package com.calulla.projectseleranusantara

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdminAdapter(
    private var list: List<RecipeData>,
    private val onEditClick: (RecipeData) -> Unit,    // Untuk Phase 5
    private val onDeleteClick: (RecipeData) -> Unit   // Untuk Phase 5
) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

    // Fungsi untuk memperbarui data resep di Admin
    fun updateData(newList: List<RecipeData>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgRecipe)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val author = view.findViewById<TextView>(R.id.txtAuthor)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // Load gambar dari Server Laravel pakai Glide
        var imageUrl = item.image
        if (imageUrl != null) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = "${Constants.BASE_URL.replace("api/", "")}storage/$imageUrl"
            }
        }

        android.util.Log.d("SatoerasaGlide", "Loading Image URL: $imageUrl")

        holder.img.loadImage(imageUrl)

        holder.title.text = item.title
        holder.author.text = item.user?.name ?: "Admin"

        // TOMBOL DELETE
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Hapus Resep")
                .setMessage("Yakin ingin menghapus resep '${item.title}'?")
                .setPositiveButton("Ya, Hapus") { _, _ ->
                    // Aksi hapus dilempar ke AdminDashboardActivity
                    onDeleteClick(item)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // TOMBOL EDIT
        holder.btnEdit.setOnClickListener {
            // Aksi edit dilempar ke AdminDashboardActivity
            onEditClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
