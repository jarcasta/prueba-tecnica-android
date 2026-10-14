package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.FilterAltOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing

enum class LoadingVariant { List, Detail }

@Composable
fun LoadingState(
    variant: LoadingVariant,
    modifier: Modifier = Modifier,
) {
    val loadingLabel = stringResource(R.string.loading)
    val pulse by rememberInfiniteTransition(label = "skeletonPulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 900), RepeatMode.Reverse),
        label = "skeletonAlpha",
    )
    Box(
        modifier = modifier
            .semantics { contentDescription = loadingLabel }
            .graphicsLayer { alpha = pulse },
    ) {
        when (variant) {
            LoadingVariant.List -> ListSkeleton()
            LoadingVariant.Detail -> DetailSkeleton()
        }
    }
}

private const val SKELETON_ROWS = 6

@Composable
private fun ListSkeleton() {
    val strong = MaterialTheme.colorScheme.surfaceContainerHigh
    val soft = MaterialTheme.colorScheme.surfaceContainer
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.height(36.dp).padding(horizontal = Spacing.md), contentAlignment = Alignment.CenterStart) {
            SkeletonBlock(width = 140.dp, height = 12.dp, color = strong)
        }
        repeat(SKELETON_ROWS) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.height(72.dp).padding(horizontal = Spacing.md),
            ) {
                SkeletonBlock(width = 56.dp, height = 56.dp, color = strong, shape = MaterialTheme.shapes.medium)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    SkeletonBlock(fraction = 0.55f, height = 14.dp, color = strong)
                    SkeletonBlock(fraction = 0.35f, height = 12.dp, color = soft)
                }
                SkeletonBlock(width = 24.dp, height = 24.dp, color = soft, modifier = Modifier.padding(end = Spacing.sm))
            }
        }
    }
}

@Composable
private fun DetailSkeleton() {
    val strong = MaterialTheme.colorScheme.surfaceContainerHigh
    val soft = MaterialTheme.colorScheme.surfaceContainer
    Column(Modifier.fillMaxWidth()) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .background(soft)
                .padding(top = 96.dp, bottom = 44.dp),
        ) {
            SkeletonBlock(width = 200.dp, height = 200.dp, color = strong, shape = CircleShape)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier
                .offset(y = (-28).dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.extraLarge)
                .padding(horizontal = Spacing.xl, vertical = 28.dp),
        ) {
            SkeletonBlock(fraction = 0.55f, height = 28.dp, color = strong)
            SkeletonBlock(width = 96.dp, height = 32.dp, color = soft, shape = MaterialTheme.shapes.small)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SkeletonBlock(height = 72.dp, color = soft, shape = MaterialTheme.shapes.medium, modifier = Modifier.weight(1f))
                SkeletonBlock(height = 72.dp, color = soft, shape = MaterialTheme.shapes.medium, modifier = Modifier.weight(1f))
            }
            repeat(SKELETON_ROWS) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    SkeletonBlock(width = 80.dp, height = 12.dp, color = strong)
                    SkeletonBlock(height = 8.dp, color = soft, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SkeletonBlock(
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    fraction: Float? = null,
    shape: Shape = CircleShape,
) {
    val sized = when {
        width != null -> modifier.width(width)
        fraction != null -> modifier.fillMaxWidth(fraction)
        else -> modifier
    }
    Box(sized.height(height).background(color, shape))
}

@Composable
fun ErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    StatusMessage(
        icon = Icons.Rounded.CloudOff,
        iconContainerColor = MaterialTheme.colorScheme.errorContainer,
        iconContentColor = MaterialTheme.colorScheme.onErrorContainer,
        title = title,
        message = message,
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        Button(onClick = onRetry, contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
            ButtonIcon(Icons.Rounded.Refresh)
            Text(stringResource(R.string.action_retry))
        }
    }
}


@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    highlighted: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val colors = MaterialTheme.colorScheme
    StatusMessage(
        icon = icon,
        iconContainerColor = if (highlighted) colors.primaryContainer else colors.surfaceContainerHigh,
        iconContentColor = if (highlighted) colors.onPrimaryContainer else colors.onSurfaceVariant,
        title = title,
        message = message,
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        if (actionLabel != null && onAction != null) {
            if (highlighted) {
                Button(
                    onClick = onAction,
                    contentPadding = if (actionIcon != null) {
                        ButtonDefaults.ButtonWithIconContentPadding
                    } else {
                        ButtonDefaults.ContentPadding
                    },
                ) {
                    actionIcon?.let { ButtonIcon(it) }
                    Text(actionLabel)
                }
            } else {
                OutlinedButton(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

@Composable
private fun ButtonIcon(icon: ImageVector) {
    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
}

@Composable
private fun StatusMessage(
    icon: ImageVector,
    iconContainerColor: Color,
    iconContentColor: Color,
    title: String,
    message: String,
    modifier: Modifier,
    contentPadding: PaddingValues,
    action: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterVertically),
        modifier = modifier
            .padding(contentPadding)
            .padding(horizontal = 40.dp),
    ) {
        Surface(shape = CircleShape, color = iconContainerColor, contentColor = iconContentColor, modifier = Modifier.size(80.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(40.dp))
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Box(Modifier.padding(top = Spacing.xs)) { action() }
    }
}

@Composable
fun OfflineBanner(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(start = Spacing.md, end = Spacing.md, top = Spacing.xxs),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Spacing.touchTarget)
                    .padding(horizontal = Spacing.md),
            ) {
                Icon(imageVector = Icons.Rounded.CloudOff, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(text = stringResource(R.string.offline_banner), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun PagingFooter(
    isError: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(targetState = isError, label = "pagingFooter", modifier = modifier) { error ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            if (error) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(start = Spacing.md, end = Spacing.xs),
                ) {
                    Text(
                        text = stringResource(R.string.paging_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onRetry, contentPadding = ButtonDefaults.TextButtonWithIconContentPadding) {
                        ButtonIcon(Icons.Rounded.Refresh)
                        Text(stringResource(R.string.action_retry))
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingListPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface { LoadingState(variant = LoadingVariant.List) }
    }
}

@PreviewLightDark
@Composable
private fun LoadingDetailPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface { LoadingState(variant = LoadingVariant.Detail) }
    }
}

@PreviewLightDark
@Composable
private fun ErrorStatePreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            ErrorState(
                title = "No pudimos cargar las regiones",
                message = "No hay conexión y todavía no hay datos guardados de esta categoría en el dispositivo.",
                onRetry = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyStatePreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Column {
                EmptyState(
                    icon = Icons.Rounded.FilterAltOff,
                    title = "Sin resultados guardados",
                    message = "No hay Pokémon de tipo ice guardados en el dispositivo.",
                    actionLabel = "Ver todos los tipos",
                    onAction = {},
                    modifier = Modifier.padding(vertical = Spacing.xl),
                )
                EmptyState(
                    icon = Icons.Rounded.Explore,
                    title = "Aún no tienes favoritos",
                    message = "Toca el corazón en cualquier Pokémon para guardarlo aquí.",
                    actionLabel = "Explorar",
                    onAction = {},
                    actionIcon = Icons.Rounded.Explore,
                    highlighted = true,
                    modifier = Modifier.padding(vertical = Spacing.xl),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun OfflineAndPagingPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Column {
                OfflineBanner(visible = true)
                PagingFooter(isError = false, onRetry = {})
                PagingFooter(isError = true, onRetry = {})
            }
        }
    }
}
