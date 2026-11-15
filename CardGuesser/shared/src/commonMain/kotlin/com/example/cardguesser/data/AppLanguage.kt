package com.example.cardguesser.data

/**
 * Languages the game is planned in. Each one has its own word file, `files/words_<code>.json`,
 * because the words themselves differ per language (not only their spelling).
 *
 * To ship a language: add its word file, add `values-<code>/strings.xml` for the interface
 * texts, and set [available] to true.
 */
enum class AppLanguage(val code: String, val label: String, val available: Boolean) {
    English("en", "English", available = true),
    French("fr", "Français", available = false),
    Spanish("es", "Español", available = false);

    companion object {
        val default = English

        fun byCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code && it.available } ?: default
    }
}
