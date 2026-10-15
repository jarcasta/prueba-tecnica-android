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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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

private const val CONTENT_TYPE_ITEM = "item"
private const val CONTENT_TYPE_POKEDEX = "pokedex"
private const val CONTENT_TYPE_SPECIES = "species"
private val ItemSpriteSize = 40.dp
private const val ITEM_SPRITE_VIEWPORT = 40f

@Composable
internal fun ItemCategoryDetailContent(detail: ItemCategoryDetail, contentPadding: PaddingValues) {
    val pocketIcon = PokeIcons.pocket(detail.pocket)
    val pocketName = displayNameOf(PokeCategory.ITEM_CATEGORY, detail.pocket)
    val pocketDescription = stringResource(R.string.detail_pocket_description, pocketName)
    val itemsTitle = stringResource(R.string.detail_items_title, detail.items.size)
    val spriteFallback = rememberItemSpriteFallback(pocketIcon)
    DetailLayout(
        name = displayNameOf(PokeCategory.ITEM_CATEGORY, detail.name),
        key = detail.key,
        headerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = contentPadding,
        header = { PocketBadge(pocketIcon) },
    ) {
        sheetItem("pocket") {
            AssistChip(
                onClick = {},
                label = { Text(pocketName) },
                leadingIcon = {
                    Icon(imageVector = pocketIcon, contentDescription = null, modifier = Modifier.size(AssistChipDefaults.IconSize))
                },
                modifier = Modifier.clearAndSetSemantics { contentDescription = pocketDescription },
            )
        }
        if (detail.items.isNotEmpty()) {
            sheetItem("items_title") { SectionTitle(itemsTitle, Modifier.padding(bottom = Spacing.xs)) }
            itemsIndexed(items = detail.items, key = { _, item -> "item:${item.name}" }, contentType = { _, _ -> CONTENT_TYPE_ITEM }) { index, item ->
                Column(Modifier.padding(horizontal = Spacing.xl)) {
                    if (index > 0) HorizontalDivider()
                    ItemRow(item = item, spriteFallback = spriteFallback)
                }
            }
        }
    }
}

@Composable
internal fun RegionDetailContent(detail: RegionDetail, contentPadding: PaddingValues) {
    val pokedexesTitle = stringResource(R.string.detail_pokedexes)
    DetailLayout(
        name = displayNameOf(PokeCategory.REGION, detail.name),
        key = detail.key,
        headerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = contentPadding,
        header = { HeaderMonogram(monogramOf(PokeCategory.REGION, detail.name)) },
    ) {
        sheetItem("summary") {
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
        }
        if (detail.pokedexes.isNotEmpty()) {
            sheetItem("pokedexes_title") { SectionTitle(pokedexesTitle, Modifier.padding(bottom = Spacing.xs)) }
            items(items = detail.pokedexes, key = { "pokedex:$it" }, contentType = { CONTENT_TYPE_POKEDEX }) { pokedex ->
                PokedexRow(displayNameOf(PokeCategory.REGION, pokedex))
            }
        }
        versionGroupsSection(detail.versionGroups)
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
        key = detail.key,
        headerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = contentPadding,
        header = { HeaderMonogram(monogramOf(PokeCategory.GENERATION, detail.name)) },
    ) {
        sheetItem("summary") {
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
        }
        versionGroupsSection(detail.versionGroups)
        if (detail.speciesSample.isNotEmpty()) {
            sheetItem("species") {
                DetailSection(title = stringResource(R.string.detail_species_sample)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        items(
                            items = detail.speciesSample,
                            key = { it.key.id.value },
                            contentType = { CONTENT_TYPE_SPECIES },
                        ) { species ->
                            SpeciesCard(species = species, onClick = { onSpeciesClick(species.key) })
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.versionGroupsSection(versionGroups: List<String>) {
    if (versionGroups.isEmpty()) return
    sheetItem("version_groups") {
        DetailSection(title = stringResource(R.string.detail_version_groups)) {
            InfoChipRow(names = versionGroups.map { displayNameOf(PokeCategory.GENERATION, it) })
        }
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
private fun ItemRow(item: CategoryItem, spriteFallback: Painter) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
    ) {
        AsyncImage(
            model = item.spriteUrl,
            contentDescription = null,
            placeholder = spriteFallback,
            error = spriteFallback,
            fallback = spriteFallback,
            modifier = Modifier.size(ItemSpriteSize),
        )
        Text(
            text = displayNameOf(PokeCategory.ITEM_CATEGORY, item.name),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun rememberItemSpriteFallback(icon: ImageVector): Painter {
    val inset = (ITEM_SPRITE_VIEWPORT - icon.viewportWidth) / 2
    return rememberVectorPainter(
        defaultWidth = ItemSpriteSize,
        defaultHeight = ItemSpriteSize,
        viewportWidth = ITEM_SPRITE_VIEWPORT,
        viewportHeight = ITEM_SPRITE_VIEWPORT,
        name = icon.name,
        tintColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tintBlendMode = BlendMode.SrcIn,
        autoMirror = icon.autoMirror,
    ) { _, _ ->
        Group(translationX = inset, translationY = inset) { RenderVectorGroup(group = icon.root) }
    }
}

@Composable
private fun PokedexRow(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.xl).heightIn(min = 48.dp),
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
            val numberDescription = stringResource(R.string.entry_number_description, species.key.id.value)
            Text(
                text = formatEntryNumber(species.key.id),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { contentDescription = numberDescription },
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
