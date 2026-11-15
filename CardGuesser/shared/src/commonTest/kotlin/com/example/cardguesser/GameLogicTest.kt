package com.example.cardguesser

import com.example.cardguesser.data.CategoryRepository
import com.example.cardguesser.data.WordEntry
import com.example.cardguesser.game.GameRound
import com.example.cardguesser.game.Tilt
import com.example.cardguesser.game.TiltDetector
import com.example.cardguesser.ui.components.formatTime
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameLogicTest {
    private val words = listOf("a", "b", "c").map { WordEntry(it) }

    @Test
    fun tiltNeedsNeutralBeforeFirstEvent() {
        val detector = TiltDetector()
        // Phone lying face up on a table when the round starts must not count as a pass.
        assertNull(detector.onReading(1f))
        assertNull(detector.onReading(0f))
        assertEquals(Tilt.Up, detector.onReading(0.9f))
    }

    @Test
    fun tiltFiresOncePerGesture() {
        val detector = TiltDetector()
        detector.onReading(0f)
        assertEquals(Tilt.Down, detector.onReading(-0.8f))
        assertNull(detector.onReading(-0.9f))
        assertNull(detector.onReading(-0.5f))
        assertNull(detector.onReading(0.1f))
        assertEquals(Tilt.Down, detector.onReading(-0.7f))
    }

    @Test
    fun smallTiltsAreIgnored() {
        val detector = TiltDetector()
        detector.onReading(0f)
        assertNull(detector.onReading(0.5f))
        assertNull(detector.onReading(-0.5f))
    }

    @Test
    fun roundRecordsAnswersInOrderShown() {
        val round = GameRound(words, Random(1))
        val first = round.current!!
        round.mark(true)
        round.mark(false) // second answer for the same word is ignored
        val second = round.next()!!
        round.mark(false)
        round.next()
        val results = round.finish()
        assertEquals(listOf(first to true, second to false), results.map { it.entry to it.guessed })
    }

    @Test
    fun unansweredWordOnScreenIsLeftOut() {
        val round = GameRound(words, Random(1))
        val answered = round.current!!
        round.mark(true)
        round.next()
        assertEquals(listOf(answered), round.finish().map { it.entry })
    }

    @Test
    fun roundRunsOutOfWords() {
        val round = GameRound(words, Random(1))
        repeat(2) {
            round.mark(true)
            round.next()
        }
        round.mark(true)
        assertNull(round.next())
        assertEquals(3, round.finish().size)
    }

    @Test
    fun parsesWordFile() {
        val json = """
            {"version":1,"language":"en","categories":[
              {"id":"x","name":"X","icon":"🎮","color":"#112233","future":true,
               "entries":[{"name":"One","imageUrl":""},{"name":"Two","imageUrl":"https://example.com/a.png"},{"name":"Three"}]},
              {"id":"empty","name":"Empty","entries":[]}
            ]}
        """.trimIndent()
        val categories = CategoryRepository.parse(json)
        assertEquals(listOf("x"), categories.map { it.id })
        assertEquals(listOf(null, "https://example.com/a.png", null), categories[0].entries.map { it.image })
    }

    @Test
    fun formatsTime() {
        assertEquals("2:00", formatTime(120))
        assertEquals("0:30", formatTime(30))
        assertEquals("1:05", formatTime(65))
        assertEquals("0:00", formatTime(-3))
    }
}
