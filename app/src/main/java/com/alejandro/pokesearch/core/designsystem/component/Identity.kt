package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun Wordmark(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
) {
    val prefix = stringResource(R.string.wordmark_prefix)
    val suffix = stringResource(R.string.wordmark_suffix)
    val primary = MaterialTheme.colorScheme.primary
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(prefix) }
            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = primary)) { append(suffix) }
        },
        style = style,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

@Composable
fun UserAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.size(size),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = initials, style = textStyle)
        }
    }
}

@Composable
fun Monogram(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    textStyle: TextStyle = monogramStyleFor(text),
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier.size(size),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, style = textStyle, maxLines = 1)
        }
    }
}

@Composable
private fun monogramStyleFor(text: String): TextStyle =
    if (text.length > 3) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge

@PreviewLightDark
@Composable
private fun IdentityPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            Column(Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Wordmark(style = MaterialTheme.typography.displaySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    UserAvatar(initials = "AS")
                    UserAvatar(initials = "MI", size = 32.dp, textStyle = MaterialTheme.typography.labelLarge)
                    Monogram(text = "K")
                    Monogram(text = "VIII")
                }
            }
        }
    }
}
