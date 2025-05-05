package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomTextField(modifier: Modifier = Modifier,
                    label: @Composable (() -> Unit),
                    placeHolder: @Composable (() -> Unit),
                    suffix: @Composable (() -> Unit) = {},
                    supportingText: @Composable (() -> Unit) = {},
                    keyboardType: KeyboardType = KeyboardType.Text,
                    onTextChange: (String) -> Unit,
                    text: String

) {

    OutlinedTextField(
        value = text,
        onValueChange = {
            onTextChange(it)
                        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Left
        ),
        label = label,
        placeholder = placeHolder,
        trailingIcon = {
            Icon(imageVector = Icons.Outlined.Clear,
                contentDescription = "clear text",
                //Clear previous written text
                modifier = Modifier.clickable { onTextChange("") }
            )
        },
        suffix = suffix,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType,
            imeAction = ImeAction.Next),
        modifier = modifier,
        supportingText = supportingText
    )
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun CustomTextFieldsPreview() {
    CustomTextField(label = {Text(text ="testLabel")},
        placeHolder = {Text("testPlaceHolder")},
        text = "",
        onTextChange = {})
}