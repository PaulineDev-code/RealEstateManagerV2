package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Photo

@Composable
fun DetailsDescriptionContent(description: String) {

    Column(modifier = Modifier.padding(4.dp)) {
        Text(text = stringResource(id = R.string.description),
            fontWeight = FontWeight.ExtraBold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize)


    }
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun DetailsDescriptionContentPreview() {
    DetailsDescriptionContent(

        "blablablabalbalbalablabalbalablabalablabalbalablabalablabalablabalablabalablabalbalablabalablabala"
    )
}