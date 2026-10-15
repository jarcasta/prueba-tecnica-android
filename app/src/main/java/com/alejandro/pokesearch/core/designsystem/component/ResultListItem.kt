package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing

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
    numberDescription: String? = null,
) {
    ListItem(
        headlineContent = {
            Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(
                text = number,
                maxLines = 1,
                modifier = if (numberDescription != null) {
                    Modifier.semantics { contentDescription = numberDescription }
                } else {
                    Modifier
                },
            )
        },
        leadingContent = { ResultLeadingVisual(leading = leading) },
        trailingContent = { FavoriteButton(isFavorite = isFavorite, onToggle = onToggleFavorite) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .clickable(onClick = onClick),
    )
}

@Composable
fun ResultListItemPlaceholder(modifier: Modifier = Modifier) {
    val block = MaterialTheme.colorScheme.surfaceContainerHigh
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = Spacing.md)
            .clearAndSetSemantics {},
    ) {
        Box(Modifier.size(56.dp).background(block, MaterialTheme.shapes.medium))
        Box(Modifier.fillMaxWidth(0.4f).height(14.dp).background(block, CircleShape))
    }
}

@Composable
private fun ResultLeadingVisual(leading: ResultLeading) {
    val size = 56.dp
    when (leading) {
        is ResultLeading.Sprite -> AsyncImage(
            model = leading.imageUrl,
            contentDescription = null,
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
        is ResultLeading.Monogram -> Monogram(text = leading.text, size = size, modifier = Modifier.clearAndSetSemantics {})
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
