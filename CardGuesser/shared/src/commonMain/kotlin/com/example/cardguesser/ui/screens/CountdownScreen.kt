package com.example.cardguesser.ui.screens

import com.example.cardguesser.ui.components.LocalCompactHeight
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.forehead_prompt
import cardguesser.shared.generated.resources.tilt_hint
import com.example.cardguesser.platform.rememberGameFeedback
import com.example.cardguesser.ui.components.AppText
import com.example.cardguesser.ui.theme.LocalPalette
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val COUNTDOWN_SECONDS = 5

@Composable
fun CountdownScreen(onFinished: () -> Unit) {
    val palette = LocalPalette.current
    val feedback = rememberGameFeedback()
    val finish by rememberUpdatedState(onFinished)
    var count by remember { mutableIntStateOf(COUNTDOWN_SECONDS) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (count > 0) {
            if (count <= 3) feedback.tick()
            scale.snapTo(1.3f)
            scale.animateTo(1f, tween(350))
            delay(650)
            count--
        }
        finish()
    }

    val compact = LocalCompactHeight.current
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(if (compact) 12.dp else 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppText(
            stringResource(Res.string.forehead_prompt),
            size = if (compact) 26.sp else 34.sp,
            weight = FontWeight.Bold,
            align = TextAlign.Center,
            maxLines = 2,
        )
        AppText(
            count.coerceAtLeast(1).toString(),
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
            size = if (compact) 84.sp else 110.sp,
            weight = FontWeight.Bold,
            color = palette.accent,
        )
        AppText(stringResource(Res.string.tilt_hint), size = if (compact) 14.sp else 17.sp, color = palette.textMuted, align = TextAlign.Center)
    }
}
