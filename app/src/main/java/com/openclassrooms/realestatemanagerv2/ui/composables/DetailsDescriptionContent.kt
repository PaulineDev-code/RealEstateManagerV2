package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R

@Composable
fun DetailsDescriptionContent(description: String, modifier: Modifier = Modifier) {

    Column(modifier = modifier) {
        Text(text = stringResource(id = R.string.description),
            fontWeight = FontWeight.ExtraBold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize)
    }
}

@ResponsiveWidths
@Composable
fun DetailsDescriptionContentPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        DetailsDescriptionContent(
            description = "Spacious three-bedroom apartment on a quiet tree-lined street, " +
                    "with a south-facing living room, an open kitchen renovated in 2023, " +
                    "oak parquet floors throughout and a private cellar. Two blocks from " +
                    "the subway station and a five-minute walk from the park.",
            modifier = Modifier
                .padding(4.dp)
                .widthIn(max = 480.dp)
        )
    }
}