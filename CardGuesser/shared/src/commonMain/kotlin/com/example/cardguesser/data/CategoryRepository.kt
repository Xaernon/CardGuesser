package com.example.cardguesser.data

import cardguesser.shared.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.MissingResourceException

/**
 * Loads the bundled word list. The game must stay playable offline, so this file is always the
 * baseline; a later version can merge categories fetched from a server on top of it.
 */
object CategoryRepository {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(text: String): List<Category> =
        json.decodeFromString<WordFile>(text).categories.filter { it.entries.isNotEmpty() }

    /** Loads the word list of [language], falling back to English if that file is missing. */
    @OptIn(ExperimentalResourceApi::class)
    suspend fun load(language: AppLanguage = AppLanguage.default): List<Category> {
        val bytes = try {
            Res.readBytes(wordFile(language))
        } catch (e: MissingResourceException) {
            Res.readBytes(wordFile(AppLanguage.default))
        }
        return parse(bytes.decodeToString())
    }

    private fun wordFile(language: AppLanguage) = "files/words_${language.code}.json"
}
