package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.realestatemanagerv2.utils.formatMillisToLocal
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(selectedDateMillis: Long?,
                     isDialogShown: Boolean,
                     onShowDialog: () -> Unit,
                     onDismissDialog: () -> Unit,
                     onDateSelected: (Long?) -> Unit,
                     datePickerState : DatePickerState,
                     modifier: Modifier = Modifier
) {
    val displayText = selectedDateMillis?.formatMillisToLocal(FormatStyle.LONG) ?: "Click to select"

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = { Text("Select Date") },
        leadingIcon = {  Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = Color.DarkGray)  },
        modifier = modifier
            .wrapContentSize()
            .clickable { onShowDialog() },
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.DarkGray.copy(alpha = 0.7f),
            disabledIndicatorColor = Color.DarkGray, //  border
            disabledLabelColor = Color.DarkGray, //  label
            unfocusedIndicatorColor = Color.DarkGray,
            focusedIndicatorColor = Color.DarkGray,
            disabledContainerColor = Color.LightGray.copy(alpha = 0.2f) // background color
        ),
                trailingIcon = {
            Icon(imageVector = Icons.Outlined.Clear,
                contentDescription = "clear date",
                //Clear previous written text
                modifier = Modifier.clickable {
                    onDateSelected(null)
                    datePickerState.selectedDateMillis = null
                    datePickerState.displayedMonthMillis = System.currentTimeMillis()
                }
            )
        }

    )


    if (isDialogShown) {

        DatePickerDialog(
            onDismissRequest = onDismissDialog,
            confirmButton = {
                Button(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismissDialog()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = {
                    onDismissDialog()
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(modifier = Modifier.verticalScroll(rememberScrollState()),
                state = datePickerState)
        }
    }
}

@Preview (showBackground = true, backgroundColor = -1)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerPreview() {
    val isDialog = remember { mutableStateOf(false) }
    val selectedDate : MutableState<Long?> = remember { mutableStateOf(null) }
    CustomDatePicker(
        selectedDateMillis = selectedDate.value,
        isDialogShown = isDialog.value,
        onShowDialog = { isDialog.value = true},
        onDismissDialog = { isDialog.value = false},
        datePickerState = DatePickerState(initialSelectedDateMillis = System.currentTimeMillis(),
            locale = Locale.getDefault()), onDateSelected = {date -> selectedDate.value = date})

}
