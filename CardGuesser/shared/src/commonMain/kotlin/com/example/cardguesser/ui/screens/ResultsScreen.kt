package com.example.cardguesser.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.change_category
import cardguesser.shared.generated.resources.image_failed
import cardguesser.shared.generated.resources.mark_failed
import cardguesser.shared.generated.resources.mark_guessed
import cardguesser.shared.generated.resources.no_image
import cardguesser.shared.generated.resources.play_again
import cardguesser.shared.generated.resources.results_empty
import cardguesser.shared.generated.resources.results_hint
import cardguesser.shared.generated.resources.results_score
import cardguesser.shared.generated.resources.results_title
import com.example.cardguesser.data.Category
import com.example.cardguesser.data.WordEntry
import com.example.cardguesser.game.WordResult
import com.example.cardguesser.platform.PlatformBackHandler
import com.example.cardguesser.ui.components.AppText
import com.example.cardguesser.ui.components.Glyph
import com.example.cardguesser.ui.components.GlyphIcon
import com.example.cardguesser.ui.components.PillButton
import com.example.cardguesser.ui.components.RemoteImage
import com.example.cardguesser.ui.components.pressable
import com.example.cardguesser.ui.theme.LocalPalette
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultsScreen(
    category: Category,
    initialResults: List<WordResult>,
    onPlayAgain: () -> Unit,
    onChangeCategory: () -> Unit,
) {
    val palette = LocalPalette.current
    val results = remember { initialResults.toMutableStateList() }
    var preview by remember { mutableStateOf<WordEntry?>(null) }

    PlatformBackHandler(enabled = preview != null) { preview = null }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    AppText(stringResource(Res.string.results_title), size = 34.sp, weight = FontWeight.Bold)
                    AppText("${category.icon}  ${category.name}", size = 16.sp, color = palette.textMuted, maxLines = 1)
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(palette.surface)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    AppText(
                        stringResource(Res.string.results_score, results.count { it.guessed }, results.size),
                        size = 30.sp,
                        weight = FontWeight.Bold,
                        color = palette.accent,
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            AppText(
                stringResource(Res.string.results_hint),
                size = 13.sp,
                weight = FontWeight.Medium,
                color = palette.textMuted,
            )
            Spacer(Modifier.height(12.dp))

            if (results.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AppText(stringResource(Res.string.results_empty), color = palette.textMuted)
                }
            } else {
                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                ) {
                    itemsIndexed(results) { index, result ->
                        ResultRow(
                            result,
                            onToggle = { results[index] = result.copy(guessed = !result.guessed) },
                            onShowImage = { preview = result.entry },
                        )
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PillButton(
                    stringResource(Res.string.change_category),
                    onClick = onChangeCategory,
                    modifier = Modifier.weight(1f),
                    background = palette.surface,
                    textColor = palette.text,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                )
                PillButton(
                    stringResource(Res.string.play_again),
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f),
                    padding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                )
            }
        }

        AnimatedVisibility(preview != null, enter = fadeIn(), exit = fadeOut()) {
            // Keep the last entry while the popup fades out.
            var shown by remember { mutableStateOf(preview) }
            preview?.let { shown = it }
            shown?.let { ImagePopup(it, onDismiss = { preview = null }) }
        }
    }
}

@Composable
private fun ResultRow(result: WordResult, onToggle: () -> Unit, onShowImage: () -> Unit) {
    val palette = LocalPalette.current
    val guessed = result.guessed
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(palette.surface.copy(alpha = if (guessed) 1f else 0.55f))
            .pressable(role = null, onClick = onShowImage)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val toggleLabel = stringResource(if (guessed) Res.string.mark_failed else Res.string.mark_guessed)
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (guessed) palette.success else palette.surfaceHigh)
                .pressable(onClick = onToggle)
                .semantics { contentDescription = toggleLabel },
            contentAlignment = Alignment.Center,
        ) {
            GlyphIcon(
                if (guessed) Glyph.Check else Glyph.Cross,
                if (guessed) Color.White else palette.textMuted,
                size = 18.dp,
            )
        }
        Spacer(Modifier.width(14.dp))
        AppText(
            result.entry.name,
            modifier = Modifier.weight(1f),
            size = 19.sp,
            weight = if (guessed) FontWeight.Bold else FontWeight.Normal,
            color = if (guessed) palette.text else palette.textMuted,
            maxLines = 2,
        )
    }
}

@Composable
private fun ImagePopup(entry: WordEntry, onDismiss: () -> Unit) {
    val palette = LocalPalette.current
    val image = entry.image
    var failed by remember(entry) { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(palette.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.background),
                contentAlignment = Alignment.Center,
            ) {
                if (image != null && !failed) {
                    RemoteImage(
                        url = image,
                        contentDescription = entry.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        onFailed = { failed = true },
                    )
                } else {
                    AppText(
                        stringResource(if (image == null) Res.string.no_image else Res.string.image_failed),
                        modifier = Modifier.padding(20.dp),
                        color = palette.textMuted,
                        align = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            AppText(entry.name, size = 24.sp, weight = FontWeight.Bold, align = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
        }
    }
}
