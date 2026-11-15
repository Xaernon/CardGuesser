package com.example.cardguesser.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardguesser.ui.theme.LocalFontFamily
import com.example.cardguesser.ui.theme.LocalPalette

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 16.sp,
    weight: FontWeight = FontWeight.SemiBold,
    color: Color = LocalPalette.current.text,
    align: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = LocalFontFamily.current,
            fontSize = size,
            fontWeight = weight,
            // The fonts are variable, so every weight is real; never let the platform fake bold on top.
            fontSynthesis = FontSynthesis.None,
            color = color,
            textAlign = align,
            lineHeight = lineHeight,
        ),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Click handling with a gentle shrink on press instead of a ripple. */
@Composable
fun Modifier.pressable(
    enabled: Boolean = true,
    role: Role? = Role.Button,
    onClick: () -> Unit,
): Modifier {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f)
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = source,
            indication = null,
            enabled = enabled,
            role = role,
            onClick = onClick,
        )
}

enum class Glyph { Check, Cross, Plus, Minus, Back, Menu }

/** Line icons drawn by hand so they look the same on every platform. */
@Composable
fun GlyphIcon(glyph: Glyph, color: Color, modifier: Modifier = Modifier, size: Dp = 20.dp, stroke: Dp = 3.dp) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(color, Offset(w * x1, h * y1), Offset(w * x2, h * y2), style.width, StrokeCap.Round)
        when (glyph) {
            Glyph.Check -> drawPath(
                Path().apply {
                    moveTo(w * 0.14f, h * 0.54f)
                    lineTo(w * 0.40f, h * 0.80f)
                    lineTo(w * 0.86f, h * 0.24f)
                },
                color,
                style = style,
            )
            Glyph.Cross -> {
                line(0.2f, 0.2f, 0.8f, 0.8f)
                line(0.8f, 0.2f, 0.2f, 0.8f)
            }
            Glyph.Plus -> {
                line(0.5f, 0.14f, 0.5f, 0.86f)
                line(0.14f, 0.5f, 0.86f, 0.5f)
            }
            Glyph.Minus -> line(0.14f, 0.5f, 0.86f, 0.5f)
            Glyph.Menu -> {
                line(0.12f, 0.22f, 0.88f, 0.22f)
                line(0.12f, 0.5f, 0.88f, 0.5f)
                line(0.12f, 0.78f, 0.88f, 0.78f)
            }
            Glyph.Back -> drawPath(
                Path().apply {
                    moveTo(w * 0.62f, h * 0.16f)
                    lineTo(w * 0.30f, h * 0.50f)
                    lineTo(w * 0.62f, h * 0.84f)
                },
                color,
                style = style,
            )
        }
    }
}

@Composable
fun CircleButton(
    glyph: Glyph,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 56.dp,
    background: Color = LocalPalette.current.surfaceHigh,
    tint: Color = LocalPalette.current.text,
) {
    Box(
        modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.35f)
            .clip(CircleShape)
            .background(background)
            .pressable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        GlyphIcon(glyph, tint, size = size * 0.38f)
    }
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = LocalPalette.current.accent,
    textColor: Color = LocalPalette.current.onAccent,
    textSize: TextUnit = 18.sp,
    padding: PaddingValues = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
    enabled: Boolean = true,
) {
    Box(
        modifier
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .background(background)
            .pressable(enabled = enabled, onClick = onClick)
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        AppText(text, size = textSize, weight = FontWeight.Bold, color = textColor, maxLines = 1)
    }
}

@Composable
fun ToggleSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    val track by animateColorAsState(if (checked) palette.accent else palette.surfaceHigh)
    val thumbOffset by animateDpAsState(if (checked) 26.dp else 4.dp)
    Box(
        modifier
            .size(width = 58.dp, height = 36.dp)
            .clip(CircleShape)
            .background(track)
            .toggleable(
                value = checked,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = thumbOffset)
                .size(28.dp)
                .clip(CircleShape)
                .background(if (checked) palette.onAccent else palette.text)
        )
    }
}

fun formatTime(totalSeconds: Int): String {
    val seconds = totalSeconds.coerceAtLeast(0)
    return "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
}
