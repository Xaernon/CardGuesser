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
    /** Id of the parent category for a sub-category (e.g. "anime" for Shōnen), empty for top level. */
    val parent: String = "",
    val entries: List<WordEntry> = emptyList(),
)

@Serializable
data class WordEntry(
    val name: String,
    val imageUrl: String? = null,
    /** "easy", "medium", "hard", or empty when not rated. Set in CardGuesser Studio. */
    val difficulty: String = "",
    /** Other names for the same thing, shown small under the main one (e.g. "Enen no Shouboutai" for Fire Force). */
    val aliases: List<String> = emptyList(),
) {
    /** The image URL, or null when the entry has none. */
    val image: String? get() = imageUrl?.trim()?.takeIf { it.isNotEmpty() }
}
