package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.ui.models.SelectablePointOfInterest
import com.openclassrooms.realestatemanagerv2.utils.toSelectable

@Composable
fun PointsOfInterestDropdown(
    allPointOfInterestList: List<PointOfInterest>,
    selectedPointOfInterestSet: Set<PointOfInterest>,
    onSelectionChanged: (PointOfInterest, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedNames = selectedPointOfInterestSet.map { point ->
        stringResource(id = point.displayNameResId)
    }

    // Texte à afficher dans le bouton du spinner
    /*val selectedText = if (selectedNames.isEmpty()) {
        "Choose Points of Interest"
    } else {
        selectedNames.joinToString(", ")
    }*/

    val selectedText = stringResource(R.string.select_points_of_interest) + " " +
            selectedNames.joinToString(", ")


    // Spinner button
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {

        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.points_of_interest)) },
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.DarkGray.copy(alpha = 0.7f),
                disabledIndicatorColor = Color.DarkGray, //  border
                disabledLabelColor = Color.DarkGray, //  label
                unfocusedIndicatorColor = Color.DarkGray,
                focusedIndicatorColor = Color.DarkGray,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.2f), // background color
            )
        )
        /*Text(text = selectedText)*/
    }

    // Dropdown Menu
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {

        allPointOfInterestList.forEach { point ->
            val isSelected = selectedPointOfInterestSet.contains(point)
            DropdownMenuItem(
                onClick = { onSelectionChanged(point, !isSelected) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        val displayName = stringResource(id = point.displayNameResId)
                        Text(text = displayName,
                            modifier = Modifier.padding(8.dp)


                        )
                    }
                })
        }
    }
}

/*@Preview(showBackground = true, backgroundColor = -1, showSystemUi = true)
@Composable
fun PointsOfInterestDropdownPreview () {
    PointsOfInterestDropdown(

        allPointOfInterestList = PointOfInterest.values().asList() ,
        selectedPointOfInterestSet = setOf(PointOfInterest.RESTAURANT, PointOfInterest.PHARMACY),
        onSelectionChanged = {pointOfInterest, isSelected ->   }
    )
}*/
@Preview(showBackground = true, backgroundColor = 0xfff)
@Composable
fun PointsOfInterestDropdownPreview() {
    // Liste complète des points d'intérêt
    val allPoints = PointOfInterest.values().toList()

    // État local pour les points d'intérêt sélectionnés, initialisé avec deux points d'intérêt
    val (selectedPoints, setSelectedPoints) = remember {
        mutableStateOf(setOf(PointOfInterest.RESTAURANT, PointOfInterest.PHARMACY))
    }

    PointsOfInterestDropdown(
        allPointOfInterestList = allPoints,
        selectedPointOfInterestSet = selectedPoints,
        onSelectionChanged = { pointOfInterest, isSelected ->
            // Mettre à jour l'état local en fonction de la sélection
            val newSelectedPoints = if (isSelected) {
                selectedPoints + pointOfInterest
            } else {
                selectedPoints - pointOfInterest
            }
            setSelectedPoints(newSelectedPoints)
        }
    )
}
