package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import kotlinx.coroutines.launch

private const val POP_SCALE = 1.3f

/** Icono de favorito*/
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val actionLabel = stringResource(if (isFavorite) R.string.favorite_remove else R.string.favorite_add)
    val state = stringResource(if (isFavorite) R.string.favorite_state_on else R.string.favorite_state_off)

    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { checked ->
            onToggle()
            if (checked) {
                scope.launch {
                    scale.animateTo(POP_SCALE, spring(stiffness = Spring.StiffnessMedium))
                    scale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium))
                }
            }
        },
        enabled = enabled,
        modifier = modifier.semantics { stateDescription = state },
    ) {
        Crossfade(targetState = isFavorite, label = "favoriteIcon") { favorite ->
            Icon(
                imageVector = if (favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = actionLabel,
                tint = if (favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FavoriteButtonPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Row {
                FavoriteButton(isFavorite = false, onToggle = {})
                FavoriteButton(isFavorite = true, onToggle = {})
                FavoriteButton(isFavorite = true, onToggle = {}, enabled = false)
            }
        }
    }
}
