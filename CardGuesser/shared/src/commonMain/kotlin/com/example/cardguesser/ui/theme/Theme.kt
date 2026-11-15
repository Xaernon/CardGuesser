package com.example.cardguesser.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.fredoka
import cardguesser.shared.generated.resources.nunito
import org.jetbrains.compose.resources.Font

/**
 * Colours of one theme. Every theme keeps a dark blue [background] so white words stay readable;
 * themes differ in the shade of blue and in the accent.
 */
data class Palette(
    val id: String,
    val name: String,
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val accent: Color,
    val onAccent: Color,
    val text: Color = Color.White,
    val textMuted: Color,
    val success: Color = Color(0xFF3DB57E),
    val danger: Color = Color(0xFFE5645F),
)

object Palettes {
    val Midnight = Palette(
        id = "midnight",
        name = "Midnight",
        background = Color(0xFF131B3A),
        surface = Color(0xFF1F2A57),
        surfaceHigh = Color(0xFF2C3A73),
        accent = Color(0xFFFFC857),
        onAccent = Color(0xFF1A1F3D),
        textMuted = Color(0xFF8C97BF),
    )
    val Lagoon = Palette(
        id = "lagoon",
        name = "Lagoon",
        background = Color(0xFF0E2438),
        surface = Color(0xFF183851),
        surfaceHigh = Color(0xFF234C6C),
        accent = Color(0xFF6ED6C5),
        onAccent = Color(0xFF0C2230),
        textMuted = Color(0xFF86A3B8),
    )
    val Grape = Palette(
        id = "grape",
        name = "Grape",
        background = Color(0xFF1B1842),
        surface = Color(0xFF2A2562),
        surfaceHigh = Color(0xFF3A3484),
        accent = Color(0xFFFF9FB6),
        onAccent = Color(0xFF241A3D),
        textMuted = Color(0xFF9A94C6),
    )

    val Sky = Palette(
        id = "sky",
        name = "Sky",
        background = Color(0xFF0F1E3D),
        surface = Color(0xFF1A2F5C),
        surfaceHigh = Color(0xFF26417D),
        accent = Color(0xFF7CC4FF),
        onAccent = Color(0xFF0D1B36),
        textMuted = Color(0xFF8AA0C8),
    )
    val Coral = Palette(
        id = "coral",
        name = "Coral",
        background = Color(0xFF171A3B),
        surface = Color(0xFF242958),
        surfaceHigh = Color(0xFF333975),
        accent = Color(0xFFFF8E78),
        onAccent = Color(0xFF231A33),
        textMuted = Color(0xFF9497C2),
    )
    val Lime = Palette(
        id = "lime",
        name = "Lime",
        background = Color(0xFF101F38),
        surface = Color(0xFF1B3154),
        surfaceHigh = Color(0xFF274472),
        accent = Color(0xFFC6E76F),
        onAccent = Color(0xFF14233A),
        textMuted = Color(0xFF8CA2BF),
    )
    val Lavender = Palette(
        id = "lavender",
        name = "Lavender",
        background = Color(0xFF16173F),
        surface = Color(0xFF23255F),
        surfaceHigh = Color(0xFF32357F),
        accent = Color(0xFFC4A8FF),
        onAccent = Color(0xFF1C1840),
        textMuted = Color(0xFF9796C8),
    )

    val all = listOf(Midnight, Lagoon, Grape, Sky, Coral, Lime, Lavender)
    val default = Midnight

    fun byId(id: String): Palette = all.firstOrNull { it.id == id } ?: default
}

enum class AppFont(val id: String, val label: String) {
    Nunito("nunito", "Nunito"),
    Fredoka("fredoka", "Fredoka");

    companion object {
        fun byId(id: String): AppFont = entries.firstOrNull { it.id == id } ?: Nunito
    }
}

val LocalPalette = staticCompositionLocalOf { Palettes.default }
val LocalFontFamily = staticCompositionLocalOf<FontFamily> { FontFamily.Default }

/** Both fonts are variable fonts, so each weight is the same file with a different weight axis. */
@Composable
private fun fontFamilyOf(font: AppFont): FontFamily {
    val resource = when (font) {
        AppFont.Nunito -> Res.font.nunito
        AppFont.Fredoka -> Res.font.fredoka
    }
    return FontFamily(
        Font(resource, FontWeight.Normal),
        Font(resource, FontWeight.Medium),
        Font(resource, FontWeight.SemiBold),
        Font(resource, FontWeight.Bold),
    )
}

@Composable
fun AppTheme(palette: Palette, font: AppFont, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalPalette provides palette,
        LocalFontFamily provides fontFamilyOf(font),
        content = content,
    )
}

/** Parses `#RRGGBB`; returns [fallback] for anything else. */
fun parseHexColor(hex: String, fallback: Color): Color {
    val digits = hex.removePrefix("#")
    if (digits.length != 6) return fallback
    val rgb = digits.toLongOrNull(16) ?: return fallback
    return Color(0xFF000000 or rgb)
}
