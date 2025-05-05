package com.openclassrooms.realestatemanagerv2.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar


//To be deleted as AppTopBar has already a Scaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScaffold(navController: NavController,
    title: String, @DrawableRes backButtonIcon: Int = R.drawable.ic_back_arrow,
    @DrawableRes firstMenuIcon: Int? = null, onMenuIconClick: () -> Unit = {}, onBackPressed: () -> Unit = {},
    content: @Composable (modifier: Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                onNavigationClick = { onBackPressed() },
                onSearchClick = { /*TODO*/ },
                onAddClick = { /*TODO*/ },
                onModifyClick = { /*TODO*/ },
                showModifyButton = false
            ) {

            }
        },


        content = {
            val modifier = if (false) Modifier.padding(it) else Modifier
            content(modifier)

        }

    )
}