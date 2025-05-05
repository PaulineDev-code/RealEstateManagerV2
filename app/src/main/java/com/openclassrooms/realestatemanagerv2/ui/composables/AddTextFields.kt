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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest

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
    entryDate: String,
    saleDate: String,

    //Error Values
    addressError: String,
    descriptionError: String,
    typeError: String,
    priceError: String,
    areaError: String,
    numberOfRoomsError: String,
    entryDateError: String,
    saleDateError: String,



// OnChange functions
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onNumberOfRoomsChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
    onEntryDateChange: (String) -> Unit,
    onSaleDateChange: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(4.dp)) {

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
            .padding(4.dp),
            text = description,
            onTextChange = onDescriptionChange,
            supportingText = { Text(text = descriptionError, color = Color.Red) }
        )


        Row {

            CustomTextField(
                label = { Text(text = stringResource(id = R.string.type)) },
                placeHolder = { Text(text = "Type of property") },
                text = type,
                onTextChange = onTypeChange,
                supportingText = { Text(text = typeError) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            CustomTextField(
                label = { Text(text = stringResource(id = R.string.price)) },
                placeHolder = { Text(text = "Price of property") },
                suffix = { Text(text = "$") },
                keyboardType = KeyboardType.Decimal,
                text = price,
                onTextChange = onPriceChange,
                supportingText = { Text(text = priceError, color = Color.Red) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        Row {
            CustomTextField(
                label = { Text(text = stringResource(id = R.string.surface)) },
                placeHolder = {
                    Text(
                        text = "Area of property"
                    )
                },
                suffix = { Text(text = "m²") },
                keyboardType = KeyboardType.Decimal,
                text = area,
                onTextChange = onAreaChange,
                supportingText = { Text(text = areaError, color = Color.Red) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
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
                        text = "Rooms of property"
                    )
                },
                keyboardType = KeyboardType.Number,
                text = numberOfRooms,
                onTextChange = onNumberOfRoomsChange,
                supportingText = { Text(text = numberOfRoomsError, color = Color.Red) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        Row {
            CustomTextField(
                label = { Text(text = stringResource(id = R.string.entry_date)) },
                placeHolder = { Text(text = "Date of entry") },
                text = entryDate,
                onTextChange = onEntryDateChange,
                supportingText = { Text(text = entryDateError, color = Color.Red) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            CustomTextField(
                label = { Text(text = stringResource(id = R.string.sale_date)) },
                placeHolder = { Text(text = "Date of sale") },
                text = saleDate,
                onTextChange = onSaleDateChange,
                supportingText = { Text(text = "* Optional field" + saleDateError, color = Color.Red) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )

        }

        CustomTextField(
            label = { Text(text = stringResource(id = R.string.location)) },
            placeHolder = { Text(text = "Address of property") },
            text = address,
            onTextChange = onAddressChange,
            supportingText = { Text(text = addressError, color = Color.Red) },
            modifier = Modifier.padding(4.dp)
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

        PointsOfInterestDropdown(
            allPointOfInterestList = nearbyPointList,
            selectedPointOfInterestSet = nearbyPointSelectedSet,
            onSelectionChanged = onNearbyPointChange
        )

    }

}


@Preview(showBackground = true, showSystemUi = false, backgroundColor = -1)
@Composable
fun AddTextFieldsPreview() {
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
        entryDate = "entryDate",
        saleDate = "saleDate",

        //Errors
        descriptionError = "",
        typeError = "",
        priceError = "",
        addressError = "",
        areaError = "",
        numberOfRoomsError = "",
        entryDateError = "",
        saleDateError = "",

// OnChange functions
        onDescriptionChange = {},
        onTypeChange = {},
        onPriceChange = {},
        onAreaChange = {},
        onNumberOfRoomsChange = {},
        onAddressChange = {},
        onNearbyPointChange = { poi, bool -> },
        onEntryDateChange = {},
        onSaleDateChange = {},

        )
}
