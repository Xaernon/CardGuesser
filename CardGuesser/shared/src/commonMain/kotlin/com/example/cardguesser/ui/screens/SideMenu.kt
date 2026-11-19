package com.example.cardguesser.ui.screens

import cardguesser.shared.generated.resources.theme_graphite
import cardguesser.shared.generated.resources.theme_sunset
import cardguesser.shared.generated.resources.theme_matcha
import cardguesser.shared.generated.resources.theme_mocha
import cardguesser.shared.generated.resources.theme_wine
import cardguesser.shared.generated.resources.theme_berry
import cardguesser.shared.generated.resources.theme_ember
import cardguesser.shared.generated.resources.theme_forest
import com.example.cardguesser.ui.theme.Palette
import cardguesser.shared.generated.resources.theme_lavender
import cardguesser.shared.generated.resources.theme_lime
import cardguesser.shared.generated.resources.theme_coral
import cardguesser.shared.generated.resources.theme_sky
import cardguesser.shared.generated.resources.theme_grape
import cardguesser.shared.generated.resources.theme_lagoon
import cardguesser.shared.generated.resources.theme_midnight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.language_coming_soon
import cardguesser.shared.generated.resources.menu
import cardguesser.shared.generated.resources.setting_font
import cardguesser.shared.generated.resources.setting_language
import cardguesser.shared.generated.resources.setting_theme
import com.example.cardguesser.AppSettings
import com.example.cardguesser.data.AppLanguage
import com.example.cardguesser.ui.components.AppText
import com.example.cardguesser.ui.components.PillButton
import com.example.cardguesser.ui.components.pressable
import com.example.cardguesser.ui.theme.AppFont
import com.example.cardguesser.ui.theme.LocalPalette
import com.example.cardguesser.ui.theme.Palettes
import org.jetbrains.compose.resources.stringResource

/** Menu that slides in from the left edge of the main screen. */
@Composable
fun SideMenu(open: Boolean, settings: AppSettings, onClose: () -> Unit) {
    val palette = LocalPalette.current
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(open, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose,
                    )
            )
        }
        AnimatedVisibility(
            open,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it },
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(0.84f)
                    .clip(RoundedCornerShape(topEnd = 34.dp, bottomEnd = 34.dp))
                    .background(palette.surface)
                    // Swallow taps so they don't reach the scrim behind the menu.
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                    .safeDrawingPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    AppText(stringResource(Res.string.menu), size = 28.sp, weight = FontWeight.Bold)

                    Spacer(Modifier.height(22.dp))
                    AppText(stringResource(Res.string.setting_theme), size = 19.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Palettes.all.forEach { option ->
                            val selected = option.id == settings.palette.id
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .background(option.background)
                                    .border(2.dp, if (selected) palette.text else Color.Transparent, CircleShape)
                                    .pressable { settings.updatePalette(option) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(Modifier.size(18.dp).clip(CircleShape).background(option.accent))
                                Spacer(Modifier.width(8.dp))
                                Box(Modifier.size(18.dp).clip(CircleShape).background(option.surfaceHigh))
                                Spacer(Modifier.width(12.dp))
                                AppText(themeName(option), size = 16.sp, weight = FontWeight.Bold, color = option.text)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    AppText(stringResource(Res.string.setting_font), size = 19.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AppFont.entries.forEach { option ->
                            val selected = option == settings.font
                            PillButton(
                                option.label,
                                onClick = { settings.updateFont(option) },
                                background = if (selected) palette.accent else palette.surfaceHigh,
                                textColor = if (selected) palette.onAccent else palette.text,
                                textSize = 15.sp,
                                padding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Language sits in the bottom-right corner of the menu.
                Column(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.End) {
                    AppText(
                        stringResource(Res.string.setting_language),
                        size = 13.sp,
                        weight = FontWeight.Bold,
                        color = palette.textMuted,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.forEach { option ->
                            val selected = option == settings.language
                            val label = if (option.available) {
                                option.label
                            } else {
                                stringResource(Res.string.language_coming_soon, option.label)
                            }
                            PillButton(
                                option.code.uppercase(),
                                onClick = { settings.updateLanguage(option) },
                                modifier = Modifier.semantics { contentDescription = label },
                                background = if (selected) palette.accent else palette.surfaceHigh,
                                textColor = if (selected) palette.onAccent else palette.text,
                                textSize = 14.sp,
                                padding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                                enabled = option.available,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** The theme's name in the game's language (falls back to its English name). */
@Composable
private fun themeName(palette: Palette): String = when (palette.id) {
    "midnight" -> stringResource(Res.string.theme_midnight)
    "lagoon" -> stringResource(Res.string.theme_lagoon)
    "grape" -> stringResource(Res.string.theme_grape)
    "sky" -> stringResource(Res.string.theme_sky)
    "coral" -> stringResource(Res.string.theme_coral)
    "lime" -> stringResource(Res.string.theme_lime)
    "lavender" -> stringResource(Res.string.theme_lavender)
    "forest" -> stringResource(Res.string.theme_forest)
    "ember" -> stringResource(Res.string.theme_ember)
    "berry" -> stringResource(Res.string.theme_berry)
    "wine" -> stringResource(Res.string.theme_wine)
    "mocha" -> stringResource(Res.string.theme_mocha)
    "matcha" -> stringResource(Res.string.theme_matcha)
    "sunset" -> stringResource(Res.string.theme_sunset)
    "graphite" -> stringResource(Res.string.theme_graphite)
    else -> palette.name
}
