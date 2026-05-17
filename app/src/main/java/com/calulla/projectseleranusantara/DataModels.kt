package com.calulla.projectseleranusantara

// =======================
// REQUEST BODIES (Data yang dikirim ke Laravel)
// =======================
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class CategoryRequest(
    val name: String,
    val icon: String? = null
)

// =======================
// RESPONSES (Data Balasan dari Laravel)
// =======================
data class BaseResponse(val status: Boolean, val message: String)

data class AuthResponse(val status: Boolean, val message: String, val token: String, val data: UserData)

// (Sesuai dengan nama di ApiService kamu: UserResponse)
data class ProfileResponse(val status: Boolean, val message: String, val data: UserData)

// Response untuk List Kategori
data class CategoriesResponse(val status: Boolean, val message: String, val data: List<CategoryData>)

// Response untuk List Resep (Menggunakan Pagination dari Laravel)
data class RecipesResponse(val status: Boolean, val message: String, val data: RecipePagination)
data class RecipePagination(val current_page: Int, val data: List<RecipeData>)

// Response untuk Detail 1 Resep
data class RecipeDetailResponse(val status: Boolean, val message: String, val data: RecipeData)


// =======================
// CORE DATA MODELS (Struktur Objek)
// =======================
data class UserData(
    val id: Int,
    val name: String,
    val email: String? = null,
    val role: String? = null
)

data class CategoryData(
    val id: Int,
    val name: String,
    val icon: String? = null
)

data class RecipeData(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val title: String,
    val description: String?,
    val image: String?,
    val video_url: String?,      // Link Video YouTube
    val cooking_time: Int,
    val servings: Int,
    val difficulty: String,
    val average_rating: Double?,
    val favorites_count: Int?,
    val user: UserData?,         // Info siapa yang buat resep
    val category: CategoryData?, // Info resep ini masuk kategori apa
    val ingredients: List<IngredientData>?, // Daftar Bahan
    val steps: List<StepData>?              // Daftar Cara Membuat
)

data class IngredientData(
    val id: Int,
    val name: String,
    val quantity: String,
    val unit: String?
)

data class StepData(
    val id: Int,
    val step_number: Int,
    val instruction: String,
    val image: String?
)

data class CheckFavoriteResponse(
    val status: Boolean,
    val message: String?,
    val is_favorite: Boolean
)

data class FavoriteRecipesResponse(
    val status: Boolean,
    val message: String,
    val data: List<RecipeData>
)
