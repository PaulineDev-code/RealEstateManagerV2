package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.utils.formatToLocalCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextFields(
    //Values
    description: String,
    type: String,
    price: String,
    area: String,
    numberOfRooms: String,
    //Error Values
    descriptionError: String,
    typeError: String,
    priceError: String,
    areaError: String,
    numberOfRoomsError: String,
    //OnChange functions
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onNumberOfRoomsChange: (String) -> Unit,
) {
    var pricePretty by remember(price) { mutableStateOf(price) }
    var hasFocus by remember { mutableStateOf(false) }

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
            keyboardType = KeyboardType.Decimal,
            text = pricePretty,
            onTextChange = onPriceChange,
            supportingText = { Text(text = priceError, color = Color.Red) },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .onFocusChanged { f ->
                    if (!f.isFocused && price != "") {
                        hasFocus = true
                        pricePretty = price.formatToLocalCurrency()
                    } else {
                        hasFocus = false
                        pricePretty = price
                    }
                }
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

            //Errors
            descriptionError = "",
            typeError = "",
            priceError = "",
            areaError = "",
            numberOfRoomsError = "",

// OnChange functions
            onDescriptionChange = {},
            onTypeChange = {},
            onPriceChange = {},
            onAreaChange = {},
            onNumberOfRoomsChange = {},
            )
    }
}
