package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TitleText(text: String,
              modifier: Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        fontWeight = FontWeight.ExtraBold,
    )

}