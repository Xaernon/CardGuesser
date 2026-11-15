package com.example.cardguesser.game

import kotlin.math.abs

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
 * upright on a forehead. After each event (and at the start) the phone has to come back to
 * upright before another event can fire, so one gesture never counts twice.
 */
class TiltDetector(
    private val triggerThreshold: Float = 0.65f,
    private val neutralThreshold: Float = 0.35f,
) {
    private var armed = false

    fun onReading(z: Float): Tilt? {
        if (!armed) {
            if (abs(z) < neutralThreshold) armed = true
            return null
        }
        val tilt = when {
            z <= -triggerThreshold -> Tilt.Down
            z >= triggerThreshold -> Tilt.Up
            else -> return null
        }
        armed = false
        return tilt
    }
}
