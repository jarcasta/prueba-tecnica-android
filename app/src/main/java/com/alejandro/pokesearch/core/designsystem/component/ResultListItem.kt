package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme

@Immutable
sealed interface ResultLeading {
    data class Sprite(val imageUrl: String?) : ResultLeading

    data class PocketIcon(val pocketName: String) : ResultLeading

    data class Monogram(val text: String) : ResultLeading
}

@Composable
fun ResultListItem(
    name: String,
    number: String,
    leading: ResultLeading,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = { Text(text = number, maxLines = 1) },
        leadingContent = { ResultLeadingVisual(leading = leading, contentDescription = name) },
        trailingContent = { FavoriteButton(isFavorite = isFavorite, onToggle = onToggleFavorite) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun ResultLeadingVisual(leading: ResultLeading, contentDescription: String) {
    val size = 56.dp
    when (leading) {
        is ResultLeading.Sprite -> AsyncImage(
            model = leading.imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        )
        is ResultLeading.PocketIcon -> Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(size),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = PokeIcons.pocket(leading.pocketName), contentDescription = null)
            }
        }
        is ResultLeading.Monogram -> Monogram(text = leading.text, size = size)
    }
}

@PreviewLightDark
@Composable
private fun ResultListItemPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Column {
            ResultListItem(
                name = "Charizard",
                number = "#006",
                leading = ResultLeading.Sprite(imageUrl = null),
                isFavorite = true,
                onToggleFavorite = {},
                onClick = {},
            )
            ResultListItem(
                name = "Healing",
                number = "#027",
                leading = ResultLeading.PocketIcon(pocketName = "medicine"),
                isFavorite = false,
                onToggleFavorite = {},
                onClick = {},
            )
            ResultListItem(
                name = "Generation VIII",
                number = "#8",
                leading = ResultLeading.Monogram(text = "VIII"),
                isFavorite = false,
                onToggleFavorite = {},
                onClick = {},
            )
        }
    }
}
