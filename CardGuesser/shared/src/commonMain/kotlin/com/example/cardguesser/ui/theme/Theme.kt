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
 * Colours of one theme. Every theme keeps a dark [background] so white words stay readable;
 * themes differ in the background's hue (blues, greens, warm browns, plums…) and in the accent.
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

    // Not blue: warm, green and neutral themes
    val Forest = Palette(
        id = "forest",
        name = "Forest",
        background = Color(0xFF0F2A22),
        surface = Color(0xFF1A3D32),
        surfaceHigh = Color(0xFF255243),
        accent = Color(0xFFF2C14E),
        onAccent = Color(0xFF10261E),
        textMuted = Color(0xFF8FB3A4),
    )
    val Ember = Palette(
        id = "ember",
        name = "Ember",
        background = Color(0xFF241713),
        surface = Color(0xFF382520),
        surfaceHigh = Color(0xFF4B3129),
        accent = Color(0xFFFF8A4C),
        onAccent = Color(0xFF2A1710),
        textMuted = Color(0xFFB59C90),
    )
    val Berry = Palette(
        id = "berry",
        name = "Berry",
        background = Color(0xFF2A1230),
        surface = Color(0xFF3E1D47),
        surfaceHigh = Color(0xFF53295F),
        accent = Color(0xFFFF7EB6),
        onAccent = Color(0xFF2A1230),
        textMuted = Color(0xFFB89BC1),
    )
    val Wine = Palette(
        id = "wine",
        name = "Wine",
        background = Color(0xFF2B0F17),
        surface = Color(0xFF411B26),
        surfaceHigh = Color(0xFF572635),
        accent = Color(0xFFF7B48C),
        onAccent = Color(0xFF2B1218),
        textMuted = Color(0xFFC19CA6),
    )
    val Mocha = Palette(
        id = "mocha",
        name = "Mocha",
        background = Color(0xFF1E1916),
        surface = Color(0xFF2F2823),
        surfaceHigh = Color(0xFF403730),
        accent = Color(0xFFE8A85C),
        onAccent = Color(0xFF231A12),
        textMuted = Color(0xFFAB9D90),
    )
    val Matcha = Palette(
        id = "matcha",
        name = "Matcha",
        background = Color(0xFF1C2316),
        surface = Color(0xFF2B3522),
        surfaceHigh = Color(0xFF3A472E),
        accent = Color(0xFFB8E07A),
        onAccent = Color(0xFF1A2312),
        textMuted = Color(0xFFA2AD8E),
    )
    val Sunset = Palette(
        id = "sunset",
        name = "Sunset",
        background = Color(0xFF2A1A2E),
        surface = Color(0xFF3E2742),
        surfaceHigh = Color(0xFF533457),
        accent = Color(0xFFFFA552),
        onAccent = Color(0xFF2A1A20),
        textMuted = Color(0xFFB9A0BC),
    )
    val Graphite = Palette(
        id = "graphite",
        name = "Graphite",
        background = Color(0xFF17181C),
        surface = Color(0xFF24262C),
        surfaceHigh = Color(0xFF33363E),
        accent = Color(0xFFFFD166),
        onAccent = Color(0xFF1A1A1E),
        textMuted = Color(0xFF9C9FA8),
    )

    val all = listOf(
        Midnight, Lagoon, Grape, Sky, Coral, Lime, Lavender,
        Forest, Ember, Berry, Wine, Mocha, Matcha, Sunset, Graphite,
    )
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
