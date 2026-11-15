package com.example.cardguesser.game

import com.example.cardguesser.data.WordEntry
import kotlin.random.Random

data class WordResult(val entry: WordEntry, val guessed: Boolean)

/** One round: a shuffled deck, the word currently shown and the answers given so far. */
class GameRound(entries: List<WordEntry>, random: Random = Random.Default) {
    private val deck = entries.shuffled(random)
    private var index = 0
    private var answered = false
    private val results = mutableListOf<WordResult>()

    /** The word currently on screen, or null once the deck is exhausted. */
    val current: WordEntry? get() = deck.getOrNull(index)

    /** Records the answer for the current word. Ignored if it was already answered. */
    fun mark(guessed: Boolean) {
        val word = current ?: return
        if (answered) return
        results += WordResult(word, guessed)
        answered = true
    }

    /** Moves to the next word and returns it, or null when there are no words left. */
    fun next(): WordEntry? {
        if (index < deck.size) index++
        answered = false
        return current
    }

    /**
     * Ends the round. Only words the player answered are listed: a word still on screen when
     * the time runs out is neither guessed nor failed, so it is left out.
     */
    fun finish(): List<WordResult> = results.toList()
}
