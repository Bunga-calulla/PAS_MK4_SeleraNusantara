package com.calulla.projectseleranusantara

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ManageCategoryActivity : AppCompatActivity() {

    private lateinit var rvCategory: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)

        sessionManager = SessionManager(this)

        rvCategory = findViewById(R.id.rvCategory)
        rvCategory.layoutManager = LinearLayoutManager(this)

        adapter = CategoryAdapter(
            emptyList(),
            onEditClick = { category ->
                showCategoryDialog(category)
            },
            onDeleteClick = { category ->
                deleteCategory(category.id)
            }
        )
        rvCategory.adapter = adapter

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnAddCategory).setOnClickListener {
            showCategoryDialog(null)
        }

        fetchCategories()
    }

    private fun fetchCategories() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                if (response.isSuccessful && response.body()?.status == true) {
                    val categories = response.body()!!.data
                    adapter.updateData(categories)
                } else {
                    Toast.makeText(this@ManageCategoryActivity, "Gagal mengambil kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCategoryDialog(category: CategoryData?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (category == null) "Tambah Kategori" else "Edit Kategori")

        val input = EditText(this)
        input.hint = "Nama Kategori"
        if (category != null) {
            input.setText(category.name)
        }
        builder.setView(input)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                if (category == null) {
                    saveCategory(name)
                } else {
                    updateCategory(category.id, name)
                }
            } else {
                Toast.makeText(this, "Nama Kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveCategory(name: String) {
        lifecycleScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val request = CategoryRequest(name = name)
                val response = RetrofitClient.instance.createCategory(token, request)
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@ManageCategoryActivity, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    fetchCategories()
                } else {
                    Toast.makeText(this@ManageCategoryActivity, "Gagal menambahkan kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCategory(id: Int, name: String) {
        lifecycleScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val request = CategoryRequest(name = name)
                val response = RetrofitClient.instance.updateCategory(token, id, request)
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@ManageCategoryActivity, "Kategori berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    fetchCategories()
                } else {
                    Toast.makeText(this@ManageCategoryActivity, "Gagal memperbarui kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteCategory(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori ini?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val token = "Bearer ${sessionManager.getToken()}"
                        val response = RetrofitClient.instance.deleteCategory(token, id)
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@ManageCategoryActivity, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show()
                            fetchCategories()
                        } else {
                            Toast.makeText(this@ManageCategoryActivity, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
