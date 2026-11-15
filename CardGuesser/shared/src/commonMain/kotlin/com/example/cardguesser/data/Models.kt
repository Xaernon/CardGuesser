package com.example.cardguesser.data

import kotlinx.serialization.Serializable

/** Root of a word file such as `files/words_en.json`. */
@Serializable
data class WordFile(
    val version: Int = 1,
    val language: String = "en",
    val categories: List<Category> = emptyList(),
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String = "",
    /** Emoji shown on the category card. */
    val icon: String = "",
    /** Card colour as `#RRGGBB`. */
    val color: String = "",
    val entries: List<WordEntry> = emptyList(),
)

@Serializable
data class WordEntry(
    val name: String,
    val imageUrl: String? = null,
) {
    /** The image URL, or null when the entry has none. */
    val image: String? get() = imageUrl?.trim()?.takeIf { it.isNotEmpty() }
}
