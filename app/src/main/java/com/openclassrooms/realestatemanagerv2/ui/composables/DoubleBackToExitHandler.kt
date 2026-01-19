package com.openclassrooms.realestatemanagerv2.ui.composables

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoubleBackToExitHandler(
    enabled: Boolean = true,
    message: String = "Press again to exit application",
    exit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backPressedOnce : Boolean by remember { mutableStateOf<Boolean>(false) }

    BackHandler(enabled = enabled) {
        if (backPressedOnce == false) {
            backPressedOnce = true

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            scope.launch {
                delay(2000)
                backPressedOnce = false
            }
        } else {
            exit()
        }
    }
}

