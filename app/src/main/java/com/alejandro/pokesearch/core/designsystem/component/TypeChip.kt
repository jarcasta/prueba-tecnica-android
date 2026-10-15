package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.PokeTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing

@Composable
fun TypeChip(typeName: String, modifier: Modifier = Modifier) {
    val colors = PokeTheme.typeColors[typeName]
    Surface(
        shape = MaterialTheme.shapes.small,
        color = colors.container,
        contentColor = colors.onContainer,
        modifier = modifier.height(32.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.padding(start = 10.dp, end = Spacing.md),
        ) {
            TypeDot(color = colors.base)
            Text(text = typeName, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun TypeDot(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .background(color = color, shape = CircleShape),
    )
}

@PreviewLightDark
@Composable
private fun TypeChipPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Column(Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    listOf("fire", "water", "grass", "electric").forEach { TypeChip(it) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    listOf("fire", "flying").forEach { TypeChip(it) }
                }
            }
        }
    }
}
