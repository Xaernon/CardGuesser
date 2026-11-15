package com.example.cardguesser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cardguesser.shared.generated.resources.Res
import cardguesser.shared.generated.resources.app_name
import cardguesser.shared.generated.resources.categories_subtitle
import cardguesser.shared.generated.resources.words_count
import com.example.cardguesser.data.Category
import com.example.cardguesser.ui.components.AppText
import com.example.cardguesser.ui.components.pressable
import com.example.cardguesser.ui.theme.LocalPalette
import com.example.cardguesser.ui.theme.parseHexColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoriesScreen(categories: List<Category>, onSelect: (Category) -> Unit) {
    val palette = LocalPalette.current
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(28.dp))
        AppText(stringResource(Res.string.app_name), size = 36.sp, weight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        AppText(stringResource(Res.string.categories_subtitle), size = 17.sp, color = palette.textMuted)
        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryCard(category, onClick = { onSelect(category) })
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    val color = parseHexColor(category.color, LocalPalette.current.surfaceHigh)
    Column(
        Modifier
            .fillMaxWidth()
            .aspectRatio(0.84f)
            .clip(RoundedCornerShape(30.dp))
            .background(color)
            .pressable(onClick = onClick)
            .padding(16.dp),
    ) {
        Box(
            Modifier.size(58.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center,
        ) {
            AppText(category.icon, size = 30.sp)
        }
        Spacer(Modifier.weight(1f))
        AppText(category.name, size = 21.sp, weight = FontWeight.Bold, maxLines = 2, lineHeight = 24.sp)
        if (category.description.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            AppText(
                category.description,
                size = 14.sp,
                weight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 2,
                lineHeight = 18.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        AppText(
            stringResource(Res.string.words_count, category.entries.size),
            size = 12.sp,
            weight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun CategoryBadge(category: Category, modifier: Modifier = Modifier) {
    val color = parseHexColor(category.color, LocalPalette.current.surfaceHigh)
    Box(
        modifier.size(52.dp).clip(CircleShape).background(color),
        contentAlignment = Alignment.Center,
    ) {
        AppText(category.icon, size = 26.sp)
    }
}
