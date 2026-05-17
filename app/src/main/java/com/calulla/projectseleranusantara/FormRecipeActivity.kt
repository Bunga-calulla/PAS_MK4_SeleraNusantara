package com.calulla.projectseleranusantara

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class FormRecipeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null
    private var categoryList: List<CategoryData> = emptyList()
    
    // Status Mode Edit / Tambah
    private var isEditMode = false
    private var recipeId = -1

    // Setup pemilih gambar galeri (Image Picker)
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            findViewById<ImageView>(R.id.imgRecipePreview).setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_recipe)

        sessionManager = SessionManager(this)

        // Deteksi mode Edit / Tambah dari Intent
        isEditMode = intent.getBooleanExtra("IS_EDIT", false)
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)
        val btnSaveRecipe = findViewById<Button>(R.id.btnSaveRecipe)
        val tvFormTitle = findViewById<TextView>(R.id.tvFormTitle)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etVideoUrl = findViewById<EditText>(R.id.etVideoUrl)
        val etCookingTime = findViewById<EditText>(R.id.etCookingTime)
        val etServings = findViewById<EditText>(R.id.etServings)

        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerDifficulty = findViewById<Spinner>(R.id.spinnerDifficulty)

        // Komponen Dinamis
        val btnAddIngredient = findViewById<Button>(R.id.btnAddIngredient)
        val llIngredientsContainer = findViewById<LinearLayout>(R.id.llIngredientsContainer)
        val btnAddStep = findViewById<Button>(R.id.btnAddStep)
        val llStepsContainer = findViewById<LinearLayout>(R.id.llStepsContainer)

        // Sesuaikan teks berdasarkan mode
        if (isEditMode) {
            tvFormTitle.text = "Edit Resep"
            btnSaveRecipe.text = "Simpan Perubahan"
        } else {
            tvFormTitle.text = "Tambah Resep Baru"
            btnSaveRecipe.text = "Simpan Resep"
        }

        // Load Categories dari API
        fetchCategories(spinnerCategory, etTitle, etDescription, etVideoUrl, etCookingTime, etServings, spinnerDifficulty, llIngredientsContainer, llStepsContainer)

        val difficulties = arrayOf("Mudah", "Sedang", "Sulit")
        spinnerDifficulty.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, difficulties)

        btnBack.setOnClickListener { finish() }

        btnSelectImage.setOnClickListener {
            getContent.launch("image/*")
        }

        // Jika mode TAMBAH, isi dengan 1 baris kosong di awal secara otomatis
        if (!isEditMode) {
            addIngredientRow(llIngredientsContainer)
            addStepRow(llStepsContainer)
        }

        // Tombol Tambah Dinamis
        btnAddIngredient.setOnClickListener {
            addIngredientRow(llIngredientsContainer)
        }

        btnAddStep.setOnClickListener {
            addStepRow(llStepsContainer)
        }

        // Tombol Simpan (Bisa handle tambah resep baru maupun simpan perubahan resep lama)
        btnSaveRecipe.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val videoUrl = etVideoUrl.text.toString().trim()
            val time = etCookingTime.text.toString().trim()
            val portions = etServings.text.toString().trim()
            val selectedCategory = categoryList.getOrNull(spinnerCategory.selectedItemPosition)
            val categoryId = selectedCategory?.id ?: 1
            val difficulty = spinnerDifficulty.selectedItem.toString().lowercase()

            // Jika tambah resep, gambar wajib diisi. Jika edit, gambar boleh kosong (pakai gambar lama)
            if (title.isEmpty() || time.isEmpty() || portions.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data utama resep!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isEditMode && selectedImageUri == null) {
                Toast.makeText(this, "Harap pilih gambar resep terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Siapkan Map untuk dikirim ke API
            val partMap = mutableMapOf<String, RequestBody>()

            partMap["title"] = title.toRequestBody("text/plain".toMediaTypeOrNull())
            partMap["category_id"] = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            partMap["cooking_time"] = time.toRequestBody("text/plain".toMediaTypeOrNull())
            partMap["servings"] = portions.toRequestBody("text/plain".toMediaTypeOrNull())
            partMap["difficulty"] = difficulty.toRequestBody("text/plain".toMediaTypeOrNull())
            partMap["description"] = desc.toRequestBody("text/plain".toMediaTypeOrNull())
            if (videoUrl.isNotEmpty()) {
                partMap["video_url"] = videoUrl.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            // Ambil data Bahan-Bahan dari Layout Dinamis
            var validIngredientCount = 0
            for (i in 0 until llIngredientsContainer.childCount) {
                val row = llIngredientsContainer.getChildAt(i)
                val etName = row.findViewById<EditText>(R.id.etIngredientName)
                val etQty = row.findViewById<EditText>(R.id.etIngredientQuantity)

                val name = etName.text.toString().trim()
                val qty = etQty.text.toString().trim()

                if (name.isNotEmpty() && qty.isNotEmpty()) {
                    partMap["ingredients[$validIngredientCount][name]"] = name.toRequestBody("text/plain".toMediaTypeOrNull())
                    partMap["ingredients[$validIngredientCount][quantity]"] = qty.toRequestBody("text/plain".toMediaTypeOrNull())
                    validIngredientCount++
                }
            }

            if (validIngredientCount == 0) {
                Toast.makeText(this, "Harap masukkan minimal 1 bahan-bahan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil data Langkah-Langkah dari Layout Dinamis
            var validStepCount = 0
            for (i in 0 until llStepsContainer.childCount) {
                val row = llStepsContainer.getChildAt(i)
                val etStep = row.findViewById<EditText>(R.id.etStepInstruction)

                val stepText = etStep.text.toString().trim()
                if (stepText.isNotEmpty()) {
                    partMap["steps[$validStepCount][instruction]"] = stepText.toRequestBody("text/plain".toMediaTypeOrNull())
                    validStepCount++
                }
            }

            if (validStepCount == 0) {
                Toast.makeText(this, "Harap masukkan minimal 1 langkah cara pembuatan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses Gambar (Hanya jika memilih gambar baru)
            val imagePart = if (selectedImageUri != null) {
                val file = uriToFile(selectedImageUri!!)
                val requestFile = file?.asRequestBody("image/*".toMediaTypeOrNull())
                requestFile?.let { MultipartBody.Part.createFormData("image", file.name, it) }
            } else {
                null
            }

            // Jalankan upload/update ke server
            uploadData(partMap, imagePart)
        }
    }

    private fun addIngredientRow(container: LinearLayout, initialName: String = "", initialQty: String = "") {
        val inflater = LayoutInflater.from(this)
        val rowView = inflater.inflate(R.layout.item_ingredient, container, false)
        val btnRemove = rowView.findViewById<ImageView>(R.id.btnRemoveIngredient)

        val etName = rowView.findViewById<EditText>(R.id.etIngredientName)
        val etQty = rowView.findViewById<EditText>(R.id.etIngredientQuantity)

        etName.setText(initialName)
        etQty.setText(initialQty)

        btnRemove.setOnClickListener {
            container.removeView(rowView)
        }
        container.addView(rowView)
    }

    private fun addStepRow(container: LinearLayout, initialInstruction: String = "") {
        val inflater = LayoutInflater.from(this)
        val rowView = inflater.inflate(R.layout.item_step, container, false)
        val btnRemove = rowView.findViewById<ImageView>(R.id.btnRemoveStep)

        val etStep = rowView.findViewById<EditText>(R.id.etStepInstruction)
        etStep.setText(initialInstruction)

        btnRemove.setOnClickListener {
            container.removeView(rowView)
        }
        container.addView(rowView)
    }

    private fun uploadData(partMap: Map<String, RequestBody>, image: MultipartBody.Part?) {
        lifecycleScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                
                // Tembak endpoint berdasarkan mode (Tambah / Edit)
                val response = if (isEditMode) {
                    RetrofitClient.instance.updateRecipe(token, recipeId, "application/json", "PUT", partMap, image)
                } else {
                    RetrofitClient.instance.createRecipe(token, "application/json", partMap, image)
                }

                if (response.isSuccessful && response.body()?.status == true) {
                    val successMsg = if (isEditMode) "Berhasil Mengubah Resep!" else "Berhasil Menambahkan Resep!"
                    Toast.makeText(this@FormRecipeActivity, successMsg, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val failMsg = if (isEditMode) "Gagal mengubah resep" else "Gagal menambahkan resep"
                    Toast.makeText(this@FormRecipeActivity, failMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormRecipeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(cacheDir, "upload_image.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return tempFile
    }

    private fun fetchCategories(
        spinnerCategory: Spinner,
        etTitle: EditText,
        etDescription: EditText,
        etVideoUrl: EditText,
        etCookingTime: EditText,
        etServings: EditText,
        spinnerDifficulty: Spinner,
        llIngredientsContainer: LinearLayout,
        llStepsContainer: LinearLayout
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                if (response.isSuccessful && response.body()?.status == true) {
                    categoryList = response.body()!!.data
                    val categoryNames = categoryList.map { it.name }
                    spinnerCategory.adapter = ArrayAdapter(
                        this@FormRecipeActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        categoryNames
                    )

                    // Jika masuk mode edit, load data resep SETELAH kategori selesai diload
                    if (isEditMode) {
                        loadRecipeDetail(
                            recipeId,
                            etTitle,
                            etDescription,
                            etVideoUrl,
                            etCookingTime,
                            etServings,
                            spinnerCategory,
                            spinnerDifficulty,
                            llIngredientsContainer,
                            llStepsContainer
                        )
                    }
                } else {
                    Toast.makeText(this@FormRecipeActivity, "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormRecipeActivity, "Error Kategori: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🟢 LOAD DETIL RESEP (DIPANGGIL HANYA SAAT MODE EDIT)
    private fun loadRecipeDetail(
        recipeId: Int,
        etTitle: EditText,
        etDescription: EditText,
        etVideoUrl: EditText,
        etCookingTime: EditText,
        etServings: EditText,
        spinnerCategory: Spinner,
        spinnerDifficulty: Spinner,
        llIngredientsContainer: LinearLayout,
        llStepsContainer: LinearLayout
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getRecipeDetail(recipeId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val recipe = response.body()!!.data

                    // Isi data form utama
                    etTitle.setText(recipe.title)
                    etDescription.setText(recipe.description ?: "")
                    etVideoUrl.setText(recipe.video_url ?: "")
                    etCookingTime.setText(recipe.cooking_time.toString())
                    etServings.setText(recipe.servings.toString())

                    // Tampilkan gambar pratinjau saat ini lewat Custom Image Loader
                    val imgRecipePreview = findViewById<ImageView>(R.id.imgRecipePreview)
                    imgRecipePreview.loadImage(recipe.image)

                    // Pilih kategori resep saat ini
                    val categoryIndex = categoryList.indexOfFirst { it.id == recipe.category_id }
                    if (categoryIndex != -1) {
                        spinnerCategory.setSelection(categoryIndex)
                    }

                    // Pilih tingkat kesulitan resep saat ini
                    val difficultyIndex = when (recipe.difficulty.lowercase()) {
                        "mudah" -> 0
                        "sedang" -> 1
                        "sulit" -> 2
                        else -> 0
                    }
                    spinnerDifficulty.setSelection(difficultyIndex)

                    // Bersihkan container dynamic row sebelum diisi data resep yang ada
                    llIngredientsContainer.removeAllViews()
                    llStepsContainer.removeAllViews()

                    // Masukkan bahan-bahan resep ke container layout
                    if (!recipe.ingredients.isNullOrEmpty()) {
                        recipe.ingredients.forEach { ingredient ->
                            addIngredientRow(llIngredientsContainer, ingredient.name, ingredient.quantity)
                        }
                    } else {
                        addIngredientRow(llIngredientsContainer)
                    }

                    // Masukkan langkah-langkah resep ke container layout
                    if (!recipe.steps.isNullOrEmpty()) {
                        recipe.steps.forEach { step ->
                            addStepRow(llStepsContainer, step.instruction)
                        }
                    } else {
                        addStepRow(llStepsContainer)
                    }

                } else {
                    Toast.makeText(this@FormRecipeActivity, "Gagal memuat detil resep lama", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormRecipeActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
