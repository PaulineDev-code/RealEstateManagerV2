package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.utils.formatMillisToLocal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextFields(
//Values
    description: String,
    type: String,
    price: String,
    area: String,
    numberOfRooms: String,
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
    descriptionError: String,
    typeError: String,
    priceError: String,
    areaError: String,
    numberOfRoomsError: String,


// OnChange functions
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onNumberOfRoomsChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
) {

    TitleText(
        text = stringResource(id = R.string.description_for_the_property),
        modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
    )

    CustomTextField(label = {
        Text(
            text = stringResource(id = R.string.description)
        )
    }, placeHolder = {
        Text(
            text = "Your description"
        )
    }, modifier = Modifier
        .fillMaxWidth()
        .height(128.dp)
        .padding(8.dp),
        text = description,
        onTextChange = onDescriptionChange,
        supportingText = { Text(text = descriptionError, color = Color.Red) }
    )

    TitleText(
        text = stringResource(id = R.string.type_and_price),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
    )

    Row {

        CustomTextField(
            label = { Text(text = stringResource(id = R.string.type)) },
            placeHolder = { Text(text = stringResource(id = R.string.type)) },
            text = type,
            onTextChange = onTypeChange,
            supportingText = { Text(text = typeError, color = Color.Red) },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
        CustomTextField(
            label = { Text(text = stringResource(id = R.string.price)) },
            placeHolder = { Text(text = stringResource(R.string.price)) },
            suffix = { Text(text = "$") },
            keyboardType = KeyboardType.Decimal,
            text = price,
            onTextChange = onPriceChange,
            supportingText = { Text(text = priceError, color = Color.Red) },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
    }

    TitleText(
        text = stringResource(id = R.string.area_and_number_of_rooms),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
    )

    Row {
        CustomTextField(
            label = { Text(text = stringResource(id = R.string.area)) },
            placeHolder = {
                Text(
                    text = stringResource(id = R.string.area)
                )
            },
            suffix = { Text(text = "m²") },
            keyboardType = KeyboardType.Decimal,
            text = area,
            onTextChange = onAreaChange,
            supportingText = { Text(text = areaError, color = Color.Red) },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
        CustomTextField(
            label = {
                Text(
                    text = stringResource(id = R.string.number_of_rooms),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            placeHolder = {
                Text(
                    text = stringResource(id = R.string.number_of_rooms)
                )
            },
            keyboardType = KeyboardType.Number,
            text = numberOfRooms,
            onTextChange = onNumberOfRoomsChange,
            supportingText = { Text(text = numberOfRoomsError, color = Color.Red) },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
    }

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

    /*Row(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically) {
        CustomTextField(
            label = { Text(text = "Point of interest") },
            placeHolder = { Text(text = "Add a nearby point of interest") },
            text = nearbyPoint,
            onTextChange = onNearbyPointChange,
            modifier = Modifier
                .padding(4.dp)
                .weight(2f)
        )*/

    /*  Button( modifier = Modifier
          .padding(4.dp)
          .weight(1f),
          onClick =  onAddNearbyPoint ) {
          Text(text = "Add")
      }
  }*/

    /*LazyRow(modifier = Modifier.wrapContentWidth()) {
        itemsIndexed(items = nearbyPointList) { _, nearbyPoint ->
            RemovableCard(
                content = { Text(
                    text = nearbyPoint,
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(start = 8.dp),
                    textAlign = TextAlign.Center
                )},
                onDelete = { onDeleteNearbyPoint(nearbyPoint) }
                )

        }
    }*/
    /*
    val nearbyPointList2 :List<PointOfInterest> = emptyList()*/

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


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false, backgroundColor = -1)
@Composable
fun AddTextFieldsPreview() {
    Column(modifier = Modifier.padding(4.dp)) {
        AddTextFields(
            //Values
            description = "description",
            type = "type",
            price = "price",
            area = "area",
            numberOfRooms = "numberOfRooms",
            address = "address",
            nearbyPointSelectedSet = emptySet(),
            nearbyPointList = emptyList(),
            selectedEntryDate = null,
            onEntryDateSelected = {},
            isEntryDateDialogShown = false,
            onShowEntryDateDialog = {},
            onDismissEntryDateDialog = {},
            entryDatePickerState = rememberDatePickerState(),
            selectedSaleDate = null,
            onSaleDateSelected = {},
            isSaleDateDialogShown = false,
            onShowSaleDateDialog = {},
            onDismissSaleDateDialog = {},
            saleDatePickerState = rememberDatePickerState(),

            //Errors
            descriptionError = "",
            typeError = "",
            priceError = "",
            addressError = "",
            areaError = "",
            numberOfRoomsError = "",

// OnChange functions
            onDescriptionChange = {},
            onTypeChange = {},
            onPriceChange = {},
            onAreaChange = {},
            onNumberOfRoomsChange = {},
            onAddressChange = {},
            onNearbyPointChange = { poi, bool -> },

            )
    }
}
