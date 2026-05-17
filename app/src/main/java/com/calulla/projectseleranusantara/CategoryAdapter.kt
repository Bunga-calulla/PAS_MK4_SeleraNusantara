package com.calulla.projectseleranusantara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private var list: List<CategoryData>,
    private val onEditClick: (CategoryData) -> Unit,
    private val onDeleteClick: (CategoryData) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val btnEditCategory: ImageView = itemView.findViewById(R.id.btnEditCategory)
        val btnDeleteCategory: ImageView = itemView.findViewById(R.id.btnDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_admin, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = list[position]
        holder.tvCategoryName.text = item.name

        holder.btnEditCategory.setOnClickListener { onEditClick(item) }
        holder.btnDeleteCategory.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<CategoryData>) {
        list = newList
        notifyDataSetChanged()
    }
}
