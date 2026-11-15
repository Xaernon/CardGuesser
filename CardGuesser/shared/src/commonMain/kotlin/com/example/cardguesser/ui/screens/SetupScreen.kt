package com.example.cardguesser.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.back
import cardguesser.shared.generated.resources.close
import cardguesser.shared.generated.resources.decrease_time
import cardguesser.shared.generated.resources.increase_time
import cardguesser.shared.generated.resources.round_time
import cardguesser.shared.generated.resources.setting_images
import cardguesser.shared.generated.resources.setting_images_description
import cardguesser.shared.generated.resources.settings
import cardguesser.shared.generated.resources.start
import com.example.cardguesser.AppSettings
import com.example.cardguesser.data.Category
import com.example.cardguesser.platform.PlatformBackHandler
import com.example.cardguesser.ui.components.AppText
import com.example.cardguesser.ui.components.CircleButton
import com.example.cardguesser.ui.components.Glyph
import com.example.cardguesser.ui.components.PillButton
import com.example.cardguesser.ui.components.ToggleSwitch
import com.example.cardguesser.ui.components.formatTime
import com.example.cardguesser.ui.theme.LocalPalette
import org.jetbrains.compose.resources.stringResource

@Composable
fun SetupScreen(category: Category, settings: AppSettings, onBack: () -> Unit, onStart: () -> Unit) {
    val palette = LocalPalette.current
    var showSettings by remember { mutableStateOf(false) }

    PlatformBackHandler(enabled = showSettings) { showSettings = false }

    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp)) {
            // Reminder of the chosen category.
            Column(Modifier.align(Alignment.TopStart).padding(top = 12.dp)) {
                CircleButton(
                    Glyph.Back,
                    stringResource(Res.string.back),
                    onClick = onBack,
                    size = 48.dp,
                    background = palette.surface,
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(palette.surface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CategoryBadge(category)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        AppText(category.name, size = 22.sp, weight = FontWeight.Bold, maxLines = 1)
                        if (category.description.isNotBlank()) {
                            AppText(category.description, size = 15.sp, color = palette.textMuted, maxLines = 2)
                        }
                    }
                }
            }

            // Timer sits just below the centre of the screen, Start right under it.
            Column(
                Modifier.align(Alignment.Center).offset(y = 76.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AppText(stringResource(Res.string.round_time), size = 16.sp, color = palette.textMuted)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleButton(
                        Glyph.Minus,
                        stringResource(Res.string.decrease_time),
                        onClick = { settings.changeDuration(-1) },
                        enabled = settings.canDecreaseDuration,
                    )
                    AppText(
                        formatTime(settings.durationSeconds),
                        modifier = Modifier.width(170.dp),
                        size = 64.sp,
                        weight = FontWeight.Bold,
                        align = TextAlign.Center,
                        maxLines = 1,
                    )
                    CircleButton(
                        Glyph.Plus,
                        stringResource(Res.string.increase_time),
                        onClick = { settings.changeDuration(1) },
                        enabled = settings.canIncreaseDuration,
                    )
                }
                Spacer(Modifier.height(26.dp))
                PillButton(
                    stringResource(Res.string.start),
                    onClick = onStart,
                    modifier = Modifier.widthIn(min = 220.dp),
                    textSize = 22.sp,
                    padding = PaddingValues(horizontal = 40.dp, vertical = 18.dp),
                )
                Spacer(Modifier.height(14.dp))
                PillButton(
                    stringResource(Res.string.settings),
                    onClick = { showSettings = true },
                    background = palette.surface,
                    textColor = palette.text,
                    textSize = 16.sp,
                    padding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                )
            }
        }

        AnimatedVisibility(showSettings, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showSettings = false },
                    )
            )
        }
        AnimatedVisibility(
            showSettings,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            SettingsSheet(settings, onClose = { showSettings = false })
        }
    }
}

@Composable
private fun SettingsSheet(settings: AppSettings, onClose: () -> Unit) {
    val palette = LocalPalette.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
            .background(palette.surface)
            // Swallow taps so they don't reach the scrim behind the sheet.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 22.dp),
    ) {
        AppText(stringResource(Res.string.settings), size = 24.sp, weight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AppText(
                stringResource(Res.string.setting_images),
                modifier = Modifier.weight(1f),
                size = 19.sp,
                weight = FontWeight.Bold,
            )
            ToggleSwitch(settings.imagesEnabled, settings::updateImagesEnabled)
        }
        Spacer(Modifier.height(6.dp))
        AppText(
            stringResource(Res.string.setting_images_description),
            size = 14.sp,
            weight = FontWeight.Medium,
            color = palette.textMuted,
            lineHeight = 19.sp,
        )

        Spacer(Modifier.height(26.dp))
        PillButton(
            stringResource(Res.string.close),
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
