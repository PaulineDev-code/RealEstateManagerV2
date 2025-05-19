package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomRangeSlider(
    modifier: Modifier,
    valueRange: ClosedFloatingPointRange<Float>,
    selectedRange: ClosedFloatingPointRange<Float>,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var currentRange by remember { mutableStateOf(selectedRange) }

    RangeSlider(
        value = currentRange,
        onValueChange = { range ->
            currentRange = range
            onRangeChanged(range)
        },
        valueRange = valueRange,
        modifier = modifier
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = currentRange.start.toInt().toString())
        Text(text = currentRange.endInclusive.toInt().toString())
    }
}


@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun CustomRangeSliderPreview() {
    Column {
        CustomRangeSlider(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            valueRange = 0f..1000f,
            selectedRange = 100f..400f,
            onRangeChanged = {})
    }
}