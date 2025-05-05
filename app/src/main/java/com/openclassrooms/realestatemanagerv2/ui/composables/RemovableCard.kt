package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RemovableCard(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .wrapContentWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content() // Contenu de la carte

            /*Spacer(modifier = Modifier.width(4.dp))*/

            // Bouton de suppression
            IconButton(
                onClick = { onDelete() },
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Delete nearby point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.background(Color.White, CircleShape)

                )
            }
        }

    }
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun RemovableCardPreview() {
    RemovableCard(//TODO modifier pour ajuster la taille de la carte,
        onDelete = { /*TODO*/ }
    ) {
        Text(text = "RemovableCard")
    }
}