package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.Monogram
import com.alejandro.pokesearch.core.designsystem.component.PokeIcons
import com.alejandro.pokesearch.core.designsystem.component.displayNameOf
import com.alejandro.pokesearch.core.designsystem.component.formatEntryNumber
import com.alejandro.pokesearch.core.designsystem.component.monogramOf
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.detail.domain.CategoryItem
import com.alejandro.pokesearch.feature.detail.domain.GenerationDetail
import com.alejandro.pokesearch.feature.detail.domain.ItemCategoryDetail
import com.alejandro.pokesearch.feature.detail.domain.RegionDetail
import com.alejandro.pokesearch.feature.detail.domain.SpeciesSummary


private val HeaderBadgeSize = 120.dp

@Composable
internal fun ItemCategoryDetailContent(detail: ItemCategoryDetail, contentPadding: PaddingValues) {
    val pocketIcon = PokeIcons.pocket(detail.pocket)
    val pocketName = displayNameOf(PokeCategory.ITEM_CATEGORY, detail.pocket)
    val pocketDescription = stringResource(R.string.detail_pocket_description, pocketName)
    DetailLayout(
        name = displayNameOf(PokeCategory.ITEM_CATEGORY, detail.name),
        number = formatEntryNumber(detail.key.id),
        headerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = contentPadding,
        header = { PocketBadge(pocketIcon) },
    ) {
        AssistChip(
            onClick = {},
            label = { Text(pocketName) },
            leadingIcon = {
                Icon(imageVector = pocketIcon, contentDescription = null, modifier = Modifier.size(AssistChipDefaults.IconSize))
            },
            modifier = Modifier.clearAndSetSemantics { contentDescription = pocketDescription },
        )
        DetailSection(title = stringResource(R.string.detail_items_title, detail.items.size)) {
            Column {
                detail.items.forEachIndexed { index, item ->
                    key(item.name) {
                        if (index > 0) HorizontalDivider()
                        ItemRow(item = item, fallbackIcon = pocketIcon)
                    }
                }
            }
        }
    }
}

@Composable
internal fun RegionDetailContent(detail: RegionDetail, contentPadding: PaddingValues) {
    DetailLayout(
        name = displayNameOf(PokeCategory.REGION, detail.name),
        number = formatEntryNumber(detail.key.id),
        headerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = contentPadding,
        header = { HeaderMonogram(monogramOf(PokeCategory.REGION, detail.name)) },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            InfoCard(
                label = stringResource(R.string.detail_main_generation),
                value = detail.mainGeneration?.let { displayNameOf(PokeCategory.GENERATION, it) }
                    ?: stringResource(R.string.detail_no_value),
                modifier = Modifier.weight(1f),
            )
            InfoCard(
                label = stringResource(R.string.detail_locations),
                value = detail.locationCount.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        if (detail.pokedexes.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.detail_pokedexes)) {
                Column {
                    detail.pokedexes.forEach { pokedex ->
                        key(pokedex) { PokedexRow(displayNameOf(PokeCategory.REGION, pokedex)) }
                    }
                }
            }
        }
        VersionGroupsSection(detail.versionGroups)
    }
}

@Composable
internal fun GenerationDetailContent(
    detail: GenerationDetail,
    onSpeciesClick: (EntryKey) -> Unit,
    contentPadding: PaddingValues,
) {
    DetailLayout(
        name = displayNameOf(PokeCategory.GENERATION, detail.name),
        number = formatEntryNumber(detail.key.id),
        headerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = contentPadding,
        header = { HeaderMonogram(monogramOf(PokeCategory.GENERATION, detail.name)) },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            InfoCard(
                label = stringResource(R.string.detail_main_region),
                value = displayNameOf(PokeCategory.REGION, detail.mainRegion),
                modifier = Modifier.weight(1f),
            )
            InfoCard(
                label = stringResource(R.string.detail_new_species),
                value = detail.newSpeciesCount.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        VersionGroupsSection(detail.versionGroups)
        if (detail.speciesSample.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.detail_species_sample)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(items = detail.speciesSample, key = { it.key.id.value }) { species ->
                        SpeciesCard(species = species, onClick = { onSpeciesClick(species.key) })
                    }
                }
            }
        }
    }
}

@Composable
private fun VersionGroupsSection(versionGroups: List<String>) {
    if (versionGroups.isEmpty()) return
    DetailSection(title = stringResource(R.string.detail_version_groups)) {
        InfoChipRow(names = versionGroups.map { displayNameOf(PokeCategory.GENERATION, it) })
    }
}

@Composable
private fun HeaderMonogram(text: String) {
    Monogram(text = text, size = HeaderBadgeSize, textStyle = MaterialTheme.typography.displayMedium)
}

@Composable
private fun PocketBadge(icon: ImageVector) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.size(HeaderBadgeSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
private fun ItemRow(item: CategoryItem, fallbackIcon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
    ) {
        SubcomposeAsyncImage(
            model = item.spriteUrl,
            contentDescription = null,
            loading = { ItemSpriteFallback(fallbackIcon) },
            error = { ItemSpriteFallback(fallbackIcon) },
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = displayNameOf(PokeCategory.ITEM_CATEGORY, item.name),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ItemSpriteFallback(icon: ImageVector) {
    Box(contentAlignment = Alignment.Center) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PokedexRow(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SpeciesCard(species: SpeciesSummary, onClick: () -> Unit) {
    val name = displayNameOf(PokeCategory.POKEMON, species.name)
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.width(104.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacing.sm),
        ) {
            AsyncImage(model = species.artworkUrl, contentDescription = null, modifier = Modifier.size(80.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = formatEntryNumber(species.key.id),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun previewKey(category: PokeCategory, id: Int) = EntryKey(category, requireNotNull(EntryId.of(id).getOrNull()))

private val previewItemCategory = ItemCategoryDetail(
    key = previewKey(PokeCategory.ITEM_CATEGORY, 27),
    name = "healing",
    pocket = "medicine",
    items = listOf("potion", "super-potion", "hyper-potion", "max-potion").map { CategoryItem(it, spriteUrl = "") },
)

private val previewRegion = RegionDetail(
    key = previewKey(PokeCategory.REGION, 1),
    name = "kanto",
    mainGeneration = "generation-i",
    locationCount = 107,
    pokedexes = listOf("kanto", "letsgo-kanto"),
    versionGroups = listOf("red-blue", "yellow", "firered-leafgreen", "lets-go-pikachu-lets-go-eevee"),
)

private val previewGeneration = GenerationDetail(
    key = previewKey(PokeCategory.GENERATION, 1),
    name = "generation-i",
    mainRegion = "kanto",
    newSpeciesCount = 151,
    versionGroups = listOf("red-blue", "yellow"),
    speciesSample = listOf("bulbasaur", "ivysaur", "venusaur", "charmander").mapIndexed { index, name ->
        SpeciesSummary(previewKey(PokeCategory.POKEMON, index + 1), name, artworkUrl = "")
    },
)

@Composable
private fun CategoryDetailPreview(state: DetailUiState) {
    PokeSearchTheme(dynamicColor = false) {
        DetailScreen(state = state, onBack = {}, onToggleFavorite = {}, onRetry = {}, snackbarHostState = SnackbarHostState())
    }
}

@PreviewLightDark
@Composable
private fun ItemCategoryDetailPreview() =
    CategoryDetailPreview(DetailUiState.Content(previewItemCategory, isFavorite = false, isStale = false))

@PreviewLightDark
@Composable
private fun RegionDetailPreview() =
    CategoryDetailPreview(DetailUiState.Content(previewRegion, isFavorite = true, isStale = false))

@PreviewLightDark
@Composable
private fun RegionWithoutGenerationPreview() = CategoryDetailPreview(
    DetailUiState.Content(previewRegion.copy(name = "hisui", mainGeneration = null, pokedexes = emptyList()), false, false),
)

@PreviewLightDark
@Composable
private fun GenerationDetailPreview() =
    CategoryDetailPreview(DetailUiState.Content(previewGeneration, isFavorite = false, isStale = false))
