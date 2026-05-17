package com.calulla.projectseleranusantara

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeCategoryAdapter(
    private var list: List<CategoryData>,
    private val onCategoryClick: (CategoryData) -> Unit
) : RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvCategoryName.text = item.name

        if (position == selectedPosition) {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_category_active)
            holder.tvCategoryName.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_category_inactive)
            holder.tvCategoryName.setTextColor(Color.parseColor("#FF9800"))
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onCategoryClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<CategoryData>) {
        val fullList = mutableListOf(CategoryData(id = -1, name = "🍛 Semua Resep"))
        fullList.addAll(newList)
        list = fullList
        notifyDataSetChanged()
    }
}
