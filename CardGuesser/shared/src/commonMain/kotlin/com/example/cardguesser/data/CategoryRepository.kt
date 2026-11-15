package com.example.cardguesser.data

import cardguesser.shared.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Loads the bundled word list. The game must stay playable offline, so this file is always the
 * baseline; a later version can merge categories fetched from a server on top of it.
 */
object CategoryRepository {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(text: String): List<Category> =
        json.decodeFromString<WordFile>(text).categories.filter { it.entries.isNotEmpty() }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun load(language: String = "en"): List<Category> =
        parse(Res.readBytes("files/words_$language.json").decodeToString())
}
