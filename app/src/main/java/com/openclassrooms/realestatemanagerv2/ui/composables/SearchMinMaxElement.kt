package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R

@Composable
fun SearchMinMaxElement(
    modifier: Modifier = Modifier,
    title: String,
    keyboardType: KeyboardType = KeyboardType.Number,
    minValue: String,
    maxValue: String,
    onMinValueChange: (String) -> Unit,
    onMaxValueChange: (String) -> Unit,
    minValueLabel: String,
    maxValueLabel: String,
    minValuePlaceHolder: String,
    maxValuePlaceHolder: String,
    minValueError: String,
    maxValueError: String,
) {
    Column(modifier = modifier) {

        TitleText(
            text = title,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {

            CustomTextField(
                label = {
                    Text(
                        text = minValueLabel,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                },
                placeHolder = { Text(text = minValuePlaceHolder) },
                supportingText = { Text(text = minValueError, color = Color.Red) },
                keyboardType = keyboardType,
                text = minValue,
                onTextChange = onMinValueChange,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )

            CustomTextField(
                label = {
                    Text(
                        text = maxValueLabel,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                },
                placeHolder = { Text(text = maxValuePlaceHolder) },
                supportingText = { Text(text = maxValueError, color = Color.Red) },
                keyboardType = keyboardType,
                text = maxValue,
                onTextChange = onMaxValueChange,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun SearchMinMaxElementPreview() {
    SearchMinMaxElement(
        title = stringResource(id = R.string.price),
        onMinValueChange = {},
        onMaxValueChange = {},
        minValue = "8",
        maxValue = "maxPrice",
        minValueLabel = stringResource(id = R.string.min_price),
        maxValueLabel = stringResource(id = R.string.max_price),
        minValuePlaceHolder = stringResource(id = R.string.min_price),
        maxValuePlaceHolder = stringResource(id = R.string.max_price),
        minValueError = "minPriceError",
        maxValueError = "maxPriceError"
    )
}