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

    /** Holds the phone at [z] for a few readings (a real tilt), returning the event it produced, if any. */
    private fun TiltDetector.hold(z: Float, readings: Int = 4): Tilt? =
        (1..readings).firstNotNullOfOrNull { onReading(z) }

    @Test
    fun tiltNeedsNeutralBeforeFirstEvent() {
        val detector = TiltDetector()
        // Phone lying face up on a table when the round starts must not count as a pass.
        assertNull(detector.hold(1f))
        assertNull(detector.hold(0f))
        assertEquals(Tilt.Up, detector.hold(0.95f))
    }

    @Test
    fun tiltFiresOncePerGesture() {
        val detector = TiltDetector()
        detector.hold(0f)
        assertEquals(Tilt.Down, detector.hold(-0.8f))
        assertNull(detector.hold(-0.9f))
        assertNull(detector.hold(-0.5f))
        assertNull(detector.hold(0.1f))
        assertEquals(Tilt.Down, detector.hold(-0.7f))
    }

    @Test
    fun smallTiltsAreIgnored() {
        val detector = TiltDetector()
        detector.hold(0f)
        assertNull(detector.hold(0.4f))   // 24 degrees up
        assertNull(detector.hold(-0.4f))  // 24 degrees down
    }

    @Test
    fun aQuickJoltIsIgnored() {
        val detector = TiltDetector()
        detector.hold(0f)
        assertNull(detector.onReading(0.97f))  // one reading of a head jerk
        assertNull(detector.onReading(0f))
        assertNull(detector.hold(0f))
    }

    @Test
    fun upNeedsABitMoreThanDown() {
        val detector = TiltDetector()
        detector.hold(0f)
        assertNull(detector.hold(0.55f))                // 33 degrees up: not yet a pass
        detector.hold(0f)
        assertEquals(Tilt.Down, detector.hold(-0.55f))  // 33 degrees down: guessed
    }

    @Test
    fun learnsHowThePlayerHoldsThePhone() {
        val detector = TiltDetector()
        // Phone held leaning back by ~15 degrees for a while (z = sin 15 = 0.26)
        repeat(200) { detector.onReading(0.26f) }
        // A small head movement backwards from there is no longer a pass...
        assertNull(detector.hold(0.7f))                 // 44 degrees: only ~29 past their upright
        detector.hold(0.26f)
        // ...while tipping forward 30 degrees from their upright still counts as guessed.
        assertEquals(Tilt.Down, detector.hold(-0.3f))   // -17 degrees: 32 below their upright
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
