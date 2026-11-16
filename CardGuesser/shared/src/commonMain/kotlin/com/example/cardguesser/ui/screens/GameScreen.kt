package com.example.cardguesser.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.correct
import cardguesser.shared.generated.resources.end_round
import cardguesser.shared.generated.resources.pass
import cardguesser.shared.generated.resources.time_up
import com.example.cardguesser.data.Category
import com.example.cardguesser.data.WordEntry
import com.example.cardguesser.game.GameRound
import com.example.cardguesser.game.Tilt
import com.example.cardguesser.game.TiltDetector
import com.example.cardguesser.game.WordResult
import com.example.cardguesser.platform.TiltSensorEffect
import com.example.cardguesser.platform.rememberGameFeedback
import com.example.cardguesser.ui.components.AppText
import com.example.cardguesser.ui.components.CircleButton
import com.example.cardguesser.ui.components.Glyph
import com.example.cardguesser.ui.components.GlyphIcon
import com.example.cardguesser.ui.components.RemoteImage
import com.example.cardguesser.ui.components.formatTime
import com.example.cardguesser.ui.theme.LocalFontFamily
import com.example.cardguesser.ui.theme.LocalPalette
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val FLASH_MILLIS = 700L
private const val TIME_UP_MILLIS = 1500L
private const val FINAL_SECONDS = 5

@Composable
fun GameScreen(
    category: Category,
    durationSeconds: Int,
    imagesEnabled: Boolean,
    onFinished: (List<WordResult>) -> Unit,
    onExit: (List<WordResult>) -> Unit,
) {
    val palette = LocalPalette.current
    val feedback = rememberGameFeedback()
    val scope = rememberCoroutineScope()
    val finish by rememberUpdatedState(onFinished)

    val round = remember { GameRound(category.entries) }
    val detector = remember { TiltDetector() }
    var word by remember { mutableStateOf(round.current) }
    var remaining by remember { mutableIntStateOf(durationSeconds) }
    var flash by remember { mutableStateOf<Tilt?>(null) }
    var ended by remember { mutableStateOf(false) }

    // Round timer, measured against a monotonic clock so it cannot drift.
    LaunchedEffect(Unit) {
        val start = TimeSource.Monotonic.markNow()
        while (!ended) {
            val left = (durationSeconds - start.elapsedNow().inWholeSeconds.toInt()).coerceAtLeast(0)
            if (left != remaining) {
                remaining = left
                if (left in 1..FINAL_SECONDS) feedback.tick()
            }
            if (left == 0) ended = true else delay(100)
        }
    }

    LaunchedEffect(ended) {
        if (ended) {
            feedback.timeUp()
            delay(TIME_UP_MILLIS)
            finish(round.finish())
        }
    }

    TiltSensorEffect { z ->
        val tilt = detector.onReading(z)
        if (tilt != null && flash == null && !ended) {
            val guessed = tilt == Tilt.Down
            round.mark(guessed)
            if (guessed) feedback.correct() else feedback.pass()
            flash = tilt
            scope.launch {
                delay(FLASH_MILLIS)
                if (!ended) {
                    val next = round.next()
                    // Running out of words ends the round early.
                    if (next == null) ended = true else word = next
                }
                flash = null
            }
        }
    }

    val background by animateColorAsState(
        when {
            ended -> palette.background
            flash == Tilt.Down -> palette.success
            flash == Tilt.Up -> palette.danger
            else -> palette.background
        }
    )

    Box(Modifier.fillMaxSize().background(background)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 28.dp, vertical = 16.dp)) {
            val currentFlash = flash
            when {
                ended -> AppText(
                    stringResource(Res.string.time_up),
                    modifier = Modifier.align(Alignment.Center),
                    size = 64.sp,
                    weight = FontWeight.Bold,
                    align = TextAlign.Center,
                )

                currentFlash != null -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val guessed = currentFlash == Tilt.Down
                    GlyphIcon(if (guessed) Glyph.Check else Glyph.Cross, Color.White, size = 84.dp, stroke = 12.dp)
                    Spacer(Modifier.height(10.dp))
                    AppText(
                        stringResource(if (guessed) Res.string.correct else Res.string.pass),
                        size = 56.sp,
                        weight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                else -> {
                    val urgent = remaining <= FINAL_SECONDS
                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .clip(CircleShape)
                            .background(if (urgent) palette.danger else palette.surface)
                            .padding(horizontal = 22.dp, vertical = 6.dp),
                    ) {
                        AppText(formatTime(remaining), size = 24.sp, weight = FontWeight.Bold, maxLines = 1)
                    }
                    // Ends the round right away, keeping the answers given so far.
                    CircleButton(
                        Glyph.Cross,
                        stringResource(Res.string.end_round),
                        onClick = { onExit(round.finish()) },
                        modifier = Modifier.align(Alignment.TopStart),
                        size = 46.dp,
                        background = palette.surface,
                        tint = palette.textMuted,
                    )
                    word?.let { current ->
                        WordDisplay(
                            current,
                            showImage = imagesEnabled,
                            modifier = Modifier.fillMaxSize().padding(top = 52.dp, bottom = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * The word and, when images are on, its picture side by side. The picture gets a fixed slot (about 40% of
 * the width, full height) so the word never jumps when it loads; inside the slot the frame takes the
 * picture's own shape (tall poster, wide flag, square logo) so nothing is cropped away.
 */
@Composable
private fun WordDisplay(entry: WordEntry, showImage: Boolean, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    val image = entry.image.takeIf { showImage }
    key(entry) {
        var imageFailed by remember { mutableStateOf(false) }
        var aspect by remember { mutableStateOf<Float?>(null) }  // width / height, once loaded
        BoxWithConstraints(modifier) {
            val slotWidth = maxWidth * IMAGE_SLOT_SHARE
            val slotHeight = maxHeight
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (image != null && !imageFailed) {
                    Box(Modifier.width(slotWidth).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        // largest frame of the picture's shape that fits the slot (3:4 until it has loaded).
                        // Any shape is allowed, even a 1:3 strip, so nothing gets cut off.
                        val ratio = (aspect ?: 0.75f).coerceIn(0.1f, 10f)
                        val frameHeight = minOf(slotHeight, slotWidth / ratio)
                        RemoteImage(
                            url = image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = frameHeight * ratio, height = frameHeight)
                                .clip(RoundedCornerShape(24.dp))
                                .background(palette.surface),
                            contentScale = ContentScale.Fit,  // never crop, even if the frame is off by a pixel
                            onFailed = { imageFailed = true },
                            onAspectRatio = { aspect = it },
                        )
                    }
                    Spacer(Modifier.width(28.dp))
                }
                WordText(entry, Modifier.weight(1f).fillMaxSize())
            }
        }
    }
}

/** Share of the screen width the picture may use; the word always keeps the rest. */
private const val IMAGE_SLOT_SHARE = 0.4f

/** The word, as big as fits (never below 30 sp), with its other names small underneath. */
@Composable
private fun WordText(entry: WordEntry, modifier: Modifier) {
    val palette = LocalPalette.current
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.weight(1f, fill = false), contentAlignment = Alignment.Center) {
            BasicText(
                text = entry.name,
                style = TextStyle(
                    fontFamily = LocalFontFamily.current,
                    fontWeight = FontWeight.Bold,
                    fontSynthesis = FontSynthesis.None,
                    color = palette.text,
                    textAlign = TextAlign.Center,
                ),
                autoSize = TextAutoSize.StepBased(minFontSize = 30.sp, maxFontSize = 104.sp, stepSize = 2.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Other names for the same thing (Enen no Shouboutai under Fire Force), small and quiet
        if (entry.aliases.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            BasicText(
                text = entry.aliases.joinToString(" · "),
                style = TextStyle(
                    fontFamily = LocalFontFamily.current,
                    fontWeight = FontWeight.SemiBold,
                    fontSynthesis = FontSynthesis.None,
                    color = palette.textMuted,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
