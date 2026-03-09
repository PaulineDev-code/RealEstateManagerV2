package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyTypeCheckBox(
    types: List<String>,
    selectedType: Set<String>,
    onTypeSelected: (String, Boolean) -> Unit,
    modifier: Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        types.forEach { type ->
            Card(
                modifier = Modifier
                    .wrapContentSize(),
                shape = MaterialTheme.shapes.medium,
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val isChecked = selectedType.contains(type)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onTypeSelected(type, !isChecked) }
                    )
                    Text(
                        text = type,
                        modifier = Modifier.padding(end = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = -1, showSystemUi = true)
@Composable
fun PropertyTypeCheckBoxPreview() {
    val typesList = listOf("House", "Apartment", "Loft", "Duplex", "Triplex")
    val (selectedTypes, setSelectedTypes) = remember {
        mutableStateOf(setOf("House", "Loft"))
    }
    PropertyTypeCheckBox(
        modifier = Modifier.fillMaxWidth(),
        types = typesList,
        selectedType = selectedTypes,
        onTypeSelected = { type, isSelected ->
            val newSelectedTypes = if (isSelected) {
                selectedTypes + type
            } else {
                selectedTypes - type
            }
            setSelectedTypes(newSelectedTypes)
        })

}
