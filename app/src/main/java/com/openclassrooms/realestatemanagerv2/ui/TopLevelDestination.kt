package com.openclassrooms.realestatemanagerv2.ui

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val route: TopLevelRoute,
    @StringRes val labelRes: Int,
    val icon: ImageVector
)