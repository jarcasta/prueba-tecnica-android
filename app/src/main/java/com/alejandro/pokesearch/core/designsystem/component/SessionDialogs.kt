package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme

@Composable
fun LogoutDialog(
    userName: String,
    favoriteCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val message = if (favoriteCount == 0) {
        stringResource(R.string.logout_message_no_favorites, userName)
    } else {
        pluralStringResource(R.plurals.logout_message, favoriteCount, favoriteCount, userName)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
        },
        title = { Text(stringResource(R.string.logout_title)) },
        text = { Text(emphasize(message, userName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.logout_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

@Composable
fun ResetDataDialog(
    userName: String,
    favoriteCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val message = pluralStringResource(R.plurals.reset_message, favoriteCount, favoriteCount, userName)
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = { Text(stringResource(R.string.reset_title)) },
        text = { Text(emphasize(message, userName)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text(stringResource(R.string.reset_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

@Composable
private fun emphasize(text: String, highlight: String): AnnotatedString {
    val color = MaterialTheme.colorScheme.onSurface
    return buildAnnotatedString {
        append(text)
        val start = text.indexOf(highlight)
        if (start >= 0) {
            addStyle(SpanStyle(fontWeight = FontWeight.Medium, color = color), start, start + highlight.length)
        }
    }
}

@PreviewLightDark
@Composable
private fun ResetDataDialogPreview() {
    PokeSearchTheme(dynamicColor = false) {
        ResetDataDialog(userName = "ashketchum", favoriteCount = 12, onConfirm = {}, onDismiss = {})
    }
}

@PreviewLightDark
@Composable
private fun LogoutDialogPreview() {
    PokeSearchTheme(dynamicColor = false) {
        LogoutDialog(userName = "ashketchum", favoriteCount = 12, onConfirm = {}, onDismiss = {})
    }
}
