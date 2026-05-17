package com.calulla.projectseleranusantara

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface ApiService {

    // ✅ REGISTER (tidak butuh token)
    @POST("register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<AuthResponse>

    // ✅ LOGIN (tidak butuh token)
    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<AuthResponse>

    // 🔒 LOGOUT (butuh token)
    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<BaseResponse>

    // 🔒 GET PROFILE (butuh token)
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    // ✅ GET SEMUA RESEP (tidak butuh token)
    @GET("recipes")
    suspend fun getRecipes(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null
    ): Response<RecipesResponse>

    // ✅ GET SEMUA KATEGORI
    @GET("categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    // 🔥 ADMIN: CREATE KATEGORI
    @POST("categories")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Body request: CategoryRequest
    ): Response<BaseResponse>

    // 🔥 ADMIN: UPDATE KATEGORI
    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CategoryRequest
    ): Response<BaseResponse>

    // 🔥 ADMIN: DELETE KATEGORI
    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse>

    // ✅ GET DETAIL RESEP (tidak butuh token)
    @GET("recipes/{id}")
    suspend fun getRecipeDetail(
        @Path("id") id: Int
    ): Response<RecipeDetailResponse>

    // 🔒 TOGGLE FAVORIT (butuh token)
    @POST("favorites/{recipe_id}/toggle")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Path("recipe_id") recipeId: Int
    ): Response<CheckFavoriteResponse>

    // 🔒 CEK FAVORIT (butuh token)
    @GET("favorites/{recipe_id}/check")
    suspend fun checkFavorite(
        @Header("Authorization") token: String,
        @Path("recipe_id") recipeId: Int
    ): Response<CheckFavoriteResponse>

    // 🔒 GET ALL FAVORITES (butuh token)
    @GET("favorites")
    suspend fun getFavoriteRecipes(
        @Header("Authorization") token: String
    ): Response<FavoriteRecipesResponse>

    // 🔥 ADMIN: CREATE RESEP (BUTUH TOKEN & MULTIPART UNTUK GAMBAR)
    @Multipart
    @POST("recipes")
    suspend fun createRecipe(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String = "application/json",
        @PartMap partMap: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<BaseResponse>

    // 🔥 ADMIN: UPDATE RESEP (BUTUH TOKEN & MULTIPART UNTUK GAMBAR)
    @Multipart
    @POST("recipes/{id}")
    suspend fun updateRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
        @Query("_method") method: String = "PUT",
        @PartMap partMap: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<BaseResponse>

    // 🔥 ADMIN: DELETE RESEP (BUTUH TOKEN)
    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse>
}
