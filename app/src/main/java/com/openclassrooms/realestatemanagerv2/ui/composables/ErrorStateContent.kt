package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R

@Composable
fun ErrorStateContent(
    message: String?,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    val displayMessage = if (message.isNullOrBlank()) {
        stringResource(R.string.error_unknown)
    } else {
        message
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = displayMessage,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            if (onRetry != null) {
                Button(
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onRetry
                ) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun ErrorStateContentPreview() {
    ErrorStateContent(message = "An error occurred while loading properties.",
        onRetry = {})
}