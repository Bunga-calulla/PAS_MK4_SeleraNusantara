data class Recipe(
    val image: Int,
    val title: String,
    val author: String,
    val creatorAvatar: Int,
    val description: String,
    val ingredients: List<String>,
    val youtubeLink: String,   // ← TAMBAH INI
    val username: String = "@${author.lowercase().replace(" ", "")}",
    val rating: Double = 4.5
)
