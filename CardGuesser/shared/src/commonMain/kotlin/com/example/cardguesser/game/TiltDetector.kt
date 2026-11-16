package com.example.cardguesser.game

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin

enum class Tilt {
    /** Screen tipped towards the floor: the word was guessed. */
    Down,

    /** Screen tipped towards the ceiling: pass. */
    Up,
}

/**
 * Turns raw tilt readings into single tilt events.
 *
 * A reading is the gravity component along the axis coming out of the screen, in g:
 * +1 when the screen faces the sky, -1 when it faces the floor, about 0 when the phone is
 * upright on a forehead. It is turned into an angle (degrees from upright).
 *
 * - **Your own "upright":** people rarely hold the phone perfectly vertical; most lean it back a
 *   little, which used to make "up" fire far more easily than "down". While the phone is roughly
 *   still, the detector slowly learns how this player holds it (within ±[maxBaselineDegrees]) and
 *   measures tilts from there.
 * - **Up is a bit harder than down:** heads move backwards (laughing, thinking) much more than
 *   forwards, so a pass needs [upDegrees] while a guess needs [downDegrees].
 * - **No jolts:** a tilt must last [holdReadings] readings in a row (about 60–100 ms) to count.
 * - **One event per gesture:** after each event (and at the start) the phone has to come back
 *   near upright before another can fire.
 */
class TiltDetector(
    private val downDegrees: Float = 30f,
    private val upDegrees: Float = 38f,
    private val neutralDegrees: Float = 20f,
    private val holdReadings: Int = 3,
    private val maxBaselineDegrees: Float = 25f,
    private val learnRate: Float = 0.03f,
) {
    private var armed = false
    private var baseline = 0f  // degrees: how this player holds the phone when it's "upright"
    private var pending: Tilt? = null
    private var held = 0

    fun onReading(z: Float): Tilt? {
        val angle = (asin(z.coerceIn(-1f, 1f).toDouble()) * 180.0 / PI).toFloat()
        val offset = angle - baseline

        if (abs(offset) < neutralDegrees) {
            armed = true
            pending = null
            held = 0
            // drift the baseline towards how the phone is actually held
            baseline = (baseline + (angle - baseline) * learnRate).coerceIn(-maxBaselineDegrees, maxBaselineDegrees)
            return null
        }
        if (!armed) return null

        val tilt = when {
            offset <= -downDegrees -> Tilt.Down
            offset >= upDegrees -> Tilt.Up
            else -> null
        }
        if (tilt == null || tilt != pending) {
            pending = tilt
            held = if (tilt == null) 0 else 1
        } else {
            held++
        }
        if (tilt == null || held < holdReadings) return null

        armed = false
        pending = null
        held = 0
        return tilt
    }
}
