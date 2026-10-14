package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.PokeTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import com.alejandro.pokesearch.core.domain.AttributeKind
import com.alejandro.pokesearch.core.domain.PokeCategory

@Composable
fun CategorySelector(
    selected: PokeCategory?,
    onSelect: (PokeCategory?) -> Unit,
    modifier: Modifier = Modifier,
    includeAll: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    ) {
        if (includeAll) {
            CheckFilterChip(
                label = stringResource(R.string.category_all),
                selected = selected == null,
                onClick = { onSelect(null) },
            )
        }
        PokeCategory.entries.forEach { category ->
            CheckFilterChip(
                label = categoryLabel(category),
                selected = selected == category,
                onClick = { onSelect(category) },
            )
        }
    }
}

/** Localized name of a category. */
@Composable
fun categoryLabel(category: PokeCategory): String = stringResource(
    when (category) {
        PokeCategory.POKEMON -> R.string.category_pokemon
        PokeCategory.ITEM_CATEGORY -> R.string.category_item_category
        PokeCategory.REGION -> R.string.category_region
        PokeCategory.GENERATION -> R.string.category_generation
    },
)

@Composable
fun AttributeFilterRow(
    kind: AttributeKind,
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        contentPadding = PaddingValues(start = Spacing.md, end = Spacing.md, bottom = Spacing.xs),
        modifier = modifier,
    ) {
        item(key = ALL_OPTION_KEY) {
            CheckFilterChip(
                label = stringResource(R.string.filter_all),
                selected = selected == null,
                onClick = { onSelect(null) },
            )
        }
        items(items = options, key = { it }) { option ->
            when (kind) {
                AttributeKind.TYPE -> TypeFilterChip(
                    typeName = option,
                    selected = option == selected,
                    onClick = { onSelect(option) },
                )
                AttributeKind.POCKET -> FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                    leadingIcon = {
                        Icon(
                            imageVector = PokeIcons.pocket(option),
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    },
                )
            }
        }
    }
}

private const val ALL_OPTION_KEY = "__all__"

@Composable
private fun CheckFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        } else {
            null
        },
    )
}

@Composable
private fun TypeFilterChip(typeName: String, selected: Boolean, onClick: () -> Unit) {
    val colors = PokeTheme.typeColors[typeName]
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(typeName) },
        leadingIcon = { TypeDot(color = colors.base) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.container,
            selectedLabelColor = colors.onContainer,
        ),
    )
}

@PreviewLightDark
@Composable
private fun FiltersPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Column {
                CategorySelector(selected = PokeCategory.ITEM_CATEGORY, onSelect = {})
                CategorySelector(selected = null, onSelect = {}, includeAll = true)
                AttributeFilterRow(
                    kind = AttributeKind.TYPE,
                    options = listOf("normal", "fire", "water", "grass", "electric"),
                    selected = "water",
                    onSelect = {},
                )
                AttributeFilterRow(
                    kind = AttributeKind.POCKET,
                    options = listOf("medicine", "pokeballs", "machines", "berries"),
                    selected = "medicine",
                    onSelect = {},
                )
            }
        }
    }
}
