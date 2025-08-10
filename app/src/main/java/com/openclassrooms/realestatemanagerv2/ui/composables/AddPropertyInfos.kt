package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyInfos(
    //Values
    address: String,
    nearbyPointSelectedSet: Set<PointOfInterest>,
    nearbyPointList: List<PointOfInterest>,
    //EntryDatePicker
    selectedEntryDate: Long?,
    isEntryDateDialogShown: Boolean,
    onShowEntryDateDialog: () -> Unit,
    onDismissEntryDateDialog: () -> Unit,
    onEntryDateSelected: (Long?) -> Unit,
    entryDatePickerState: DatePickerState,
    //SaleDatePicker
    selectedSaleDate: Long?,
    isSaleDateDialogShown: Boolean,
    onShowSaleDateDialog: () -> Unit,
    onDismissSaleDateDialog: () -> Unit,
    onSaleDateSelected: (Long?) -> Unit,
    saleDatePickerState: DatePickerState,
    //Error Values
    addressError: String,
    //OnChange functions
    onAddressChange: (String) -> Unit,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
    ) {

    TitleText(
        text = stringResource(id = R.string.entry_date),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
    )

    CustomDatePicker(
        selectedDateMillis = selectedEntryDate,
        isDialogShown = isEntryDateDialogShown,
        onShowDialog = onShowEntryDateDialog,
        onDismissDialog = onDismissEntryDateDialog,
        onDateSelected = onEntryDateSelected,
        datePickerState = entryDatePickerState,
        modifier = Modifier.padding(8.dp)
    )

    TitleText(
        text = stringResource(id = R.string.sale_date_optional),
        modifier = Modifier.padding(top = 24.dp, start = 8.dp, end = 8.dp)
    )

    CustomDatePicker(
        selectedDateMillis = selectedSaleDate,
        isDialogShown = isSaleDateDialogShown,
        onShowDialog = onShowSaleDateDialog,
        onDismissDialog = onDismissSaleDateDialog,
        onDateSelected = onSaleDateSelected,
        datePickerState = saleDatePickerState,
        modifier = Modifier.padding(8.dp)
    )


    TitleText(
        text = stringResource(id = R.string.address),
        modifier = Modifier.padding(top = 24.dp, start = 8.dp, end = 8.dp)
    )

    CustomTextField(
        label = { Text(text = stringResource(id = R.string.location)) },
        placeHolder = { Text(text = stringResource(id = R.string.address)) },
        text = address,
        onTextChange = onAddressChange,
        supportingText = { Text(text = addressError, color = Color.Red) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )

    TitleText(
        text = stringResource(id = R.string.nearby_points_of_interest),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
    )

    PointsOfInterestDropdown(
        modifier = Modifier.padding(8.dp),
        allPointOfInterestList = nearbyPointList,
        selectedPointOfInterestSet = nearbyPointSelectedSet,
        onSelectionChanged = onNearbyPointChange
    )
}