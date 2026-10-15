package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing

private const val MAX_BASE_STAT = 255f
private const val FILL_DURATION_MILLIS = 600

const val STAT_BAR_STAGGER_MILLIS = 40

@Composable
fun StatBar(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    animationDelayMillis: Int = 0,
) {
    var started by rememberSaveable { mutableStateOf(false) }
    val inPreview = LocalInspectionMode.current
    LaunchedEffect(Unit) { started = true }
    val target = (value / MAX_BASE_STAT).coerceIn(0f, 1f)
    val progress by animateFloatAsState(
        targetValue = if (started || inPreview) target else 0f,
        animationSpec = tween(FILL_DURATION_MILLIS, animationDelayMillis, FastOutSlowInEasing),
        label = "statProgress",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        modifier = modifier.height(24.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.width(32.dp),
        )
        LinearProgressIndicator(
            progress = { progress },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun StatBarPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Column(Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                StatBar(label = "PS", value = 35)
                StatBar(label = "Velocidad", value = 90)
                StatBar(label = "At. esp.", value = 255)
            }
        }
    }
}
