package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.ErrorState
import com.alejandro.pokesearch.core.designsystem.component.FavoriteButton
import com.alejandro.pokesearch.core.designsystem.component.LoadingState
import com.alejandro.pokesearch.core.designsystem.component.LoadingVariant
import com.alejandro.pokesearch.core.designsystem.component.OfflineBanner
import com.alejandro.pokesearch.core.designsystem.component.STAT_BAR_STAGGER_MILLIS
import com.alejandro.pokesearch.core.designsystem.component.StatBar
import com.alejandro.pokesearch.core.designsystem.component.TypeChip
import com.alejandro.pokesearch.core.designsystem.component.displayNameOf
import com.alejandro.pokesearch.core.designsystem.component.formatEntryNumber
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.PokeTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.detail.domain.BaseStat
import com.alejandro.pokesearch.feature.detail.domain.GenerationDetail
import com.alejandro.pokesearch.feature.detail.domain.ItemCategoryDetail
import com.alejandro.pokesearch.feature.detail.domain.PokemonDetail
import com.alejandro.pokesearch.feature.detail.domain.RegionDetail
import java.util.Locale

private const val FALLBACK_TYPE = "normal"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onOpenEntry: (EntryKey) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                },
                actions = {
                    if (state is DetailUiState.Content) {
                        FavoriteButton(isFavorite = state.isFavorite, onToggle = onToggleFavorite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        when (state) {
            DetailUiState.Loading -> LoadingState(variant = LoadingVariant.Detail, modifier = Modifier.fillMaxSize())
            is DetailUiState.Error -> ErrorState(
                title = stringResource(R.string.detail_error_title),
                message = errorMessage(state.error),
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding,
            )
            is DetailUiState.Content -> Box(Modifier.fillMaxSize()) {
                when (val detail = state.detail) {
                    is PokemonDetail -> PokemonDetailContent(detail = detail, contentPadding = padding)
                    is ItemCategoryDetail -> ItemCategoryDetailContent(detail = detail, contentPadding = padding)
                    is RegionDetail -> RegionDetailContent(detail = detail, contentPadding = padding)
                    is GenerationDetail -> GenerationDetailContent(
                        detail = detail,
                        onSpeciesClick = onOpenEntry,
                        contentPadding = padding,
                    )
                }
                OfflineBanner(
                    visible = state.isOffline,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = padding.calculateTopPadding()),
                )
            }
        }
    }
}

private const val CONTENT_TYPE_HEADER = "header"
private const val CONTENT_TYPE_TITLE = "title"
private val SheetLipHeight = 28.dp

@Composable
internal fun DetailLayout(
    name: String,
    key: EntryKey,
    headerColor: Color,
    contentPadding: PaddingValues,
    header: @Composable () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    val numberDescription = stringResource(R.string.entry_number_description, key.id.value)
    LazyColumn(
        contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + SheetLipHeight),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = CONTENT_TYPE_HEADER, contentType = CONTENT_TYPE_HEADER) {
            DetailHeader(headerColor = headerColor, topPadding = contentPadding.calculateTopPadding(), content = header)
        }
        item(key = CONTENT_TYPE_TITLE, contentType = CONTENT_TYPE_TITLE) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.padding(horizontal = Spacing.xl),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false).semantics { heading() },
                )
                Text(
                    text = formatEntryNumber(key.id),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { contentDescription = numberDescription },
                )
            }
        }
        content()
    }
}
@Composable
private fun DetailHeader(headerColor: Color, topPadding: Dp, content: @Composable () -> Unit) {
    val sheetShape = MaterialTheme.shapes.extraLarge.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
    Column(
        Modifier
            .fillMaxWidth()
            .background(headerColor)
            .padding(top = topPadding + Spacing.xl),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) { content() }
        Spacer(Modifier.height(Spacing.md))
        Box(
            Modifier
                .fillMaxWidth()
                .height(SheetLipHeight)
                .background(MaterialTheme.colorScheme.surface, sheetShape),
        )
    }
}

internal fun LazyListScope.sheetItem(key: String, content: @Composable () -> Unit) {
    item(key = key, contentType = key) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.lg),
        ) {
            content()
        }
    }
}

@Composable
private fun PokemonDetailContent(detail: PokemonDetail, contentPadding: PaddingValues) {
    val name = displayNameOf(PokeCategory.POKEMON, detail.name)
    DetailLayout(
        name = name,
        key = detail.key,
        headerColor = PokeTheme.typeColors.detailTint(detail.types.firstOrNull() ?: FALLBACK_TYPE),
        contentPadding = contentPadding,
        header = { AsyncImage(model = detail.artworkUrl, contentDescription = name, modifier = Modifier.size(200.dp)) },
    ) {
        sheetItem("types") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                detail.types.forEach { TypeChip(typeName = it) }
            }
        }
        sheetItem("measures") {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                InfoCard(
                    label = stringResource(R.string.detail_height),
                    value = stringResource(R.string.detail_height_value, formatMeasure(detail.heightMeters)),
                    modifier = Modifier.weight(1f),
                )
                InfoCard(
                    label = stringResource(R.string.detail_weight),
                    value = stringResource(R.string.detail_weight_value, formatMeasure(detail.weightKg)),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        sheetItem("abilities") {
            DetailSection(title = stringResource(R.string.detail_abilities)) {
                InfoChipRow(names = detail.abilities.map { displayNameOf(PokeCategory.POKEMON, it) })
            }
        }
        sheetItem("stats") {
            DetailSection(title = stringResource(R.string.detail_base_stats)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    detail.stats.forEachIndexed { index, stat ->
                        StatBar(
                            label = statLabel(stat.name),
                            value = stat.value,
                            animationDelayMillis = index * STAT_BAR_STAGGER_MILLIS,
                        )
                    }
                }
            }
        }
    }
}
@Composable
internal fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.height(72.dp),
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(horizontal = Spacing.md)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
internal fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        SectionTitle(title)
        content()
    }
}

@Composable
internal fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.semantics { heading() },
    )
}

@Composable
internal fun InfoChipRow(names: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        names.forEach { InfoChip(it) }
    }
}

@Composable
private fun InfoChip(name: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.height(32.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = Spacing.md)) {
            Text(text = name, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun formatMeasure(value: Double): String = String.format(Locale.getDefault(), "%.1f", value)

@Composable
private fun statLabel(statName: String): String = when (statName) {
    "hp" -> stringResource(R.string.stat_hp)
    "attack" -> stringResource(R.string.stat_attack)
    "defense" -> stringResource(R.string.stat_defense)
    "special-attack" -> stringResource(R.string.stat_special_attack)
    "special-defense" -> stringResource(R.string.stat_special_defense)
    "speed" -> stringResource(R.string.stat_speed)
    else -> displayNameOf(PokeCategory.POKEMON, statName)
}

@Composable
private fun errorMessage(error: DomainError): String = when (error) {
    DomainError.NoConnection -> stringResource(R.string.detail_error_no_connection)
    DomainError.NotFound -> stringResource(R.string.detail_error_not_found)
    is DomainError.Server -> stringResource(R.string.error_server, error.code)
    DomainError.Unexpected -> stringResource(R.string.error_unexpected)
}

private val previewPikachu = PokemonDetail(
    key = EntryKey(PokeCategory.POKEMON, requireNotNull(EntryId.of(25).getOrNull())),
    name = "pikachu",
    artworkUrl = null,
    types = listOf("electric"),
    heightMeters = 0.4,
    weightKg = 6.0,
    abilities = listOf("static", "lightning-rod"),
    stats = listOf(
        BaseStat("hp", 35),
        BaseStat("attack", 55),
        BaseStat("defense", 40),
        BaseStat("special-attack", 50),
        BaseStat("special-defense", 50),
        BaseStat("speed", 90),
    ),
)

@Composable
private fun DetailPreview(state: DetailUiState) {
    PokeSearchTheme(dynamicColor = false) {
        DetailScreen(state = state, onBack = {}, onToggleFavorite = {}, onRetry = {}, snackbarHostState = SnackbarHostState())
    }
}

@PreviewLightDark
@Composable
private fun DetailContentPreview() = DetailPreview(DetailUiState.Content(previewPikachu, isFavorite = true, isStale = false))

@PreviewLightDark
@Composable
private fun DetailDualTypePreview() = DetailPreview(
    DetailUiState.Content(
        previewPikachu.copy(name = "charizard", types = listOf("fire", "flying"), heightMeters = 1.7, weightKg = 90.5),
        isFavorite = false,
        isStale = true,
    ),
)

@PreviewLightDark
@Composable
private fun DetailLoadingPreview() = DetailPreview(DetailUiState.Loading)

@PreviewLightDark
@Composable
private fun DetailErrorPreview() = DetailPreview(DetailUiState.Error(DomainError.NoConnection))
