package com.openclassrooms.realestatemanagerv2.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.PropertyListItem
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: PropertySharedViewModel = hiltViewModel()) {

    val viewState by viewModel.uiState.collectAsState()

    val navBarsColor = if (
        viewState is PropertySharedViewModel.PropertyUiState.Success
        && (viewState as PropertySharedViewModel.PropertyUiState.Success).isFiltered
    ) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    //TODO: Create a new composable for scaffold+topbar
    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        navBarsColor = navBarsColor,
        showBottomBar = true
    ) { paddingValues ->
        val backStackEntry = navController.previousBackStackEntry
        val savedState     = backStackEntry?.savedStateHandle
        val criteria       = remember {
            savedState?.get<PropertySearchCriteria>("criterias")
        }

        LaunchedEffect(criteria) {
            criteria?.let {
                viewModel.searchProperties(it)
                savedState?.remove<PropertySearchCriteria>("criterias")
            }
        }

        when (viewState) {
            is PropertySharedViewModel.PropertyUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PropertySharedViewModel.PropertyUiState.Success ->
                LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                    itemsIndexed(items =
                    (viewState as PropertySharedViewModel.PropertyUiState.Success).properties)
                    { _, item ->
                        PropertyListItem(property = item, onItemClick = {
                            navController.navigate("details_screen" + "/" + item.id)
                        })
                    }
                }
            is PropertySharedViewModel.PropertyUiState.Error -> {
                val e = (viewState as PropertySharedViewModel.PropertyUiState.Error).exception
                Text(
                    text = "Erreur : ${e.localizedMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Log.e("UI ERROR", "PropertyListUiState.Error")
            }
        }

        if (viewState is PropertySharedViewModel.PropertyUiState.Success &&
            (viewState as PropertySharedViewModel.PropertyUiState.Success).isFiltered) {
            TextButton(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                onClick = { viewModel.resetProperties() },
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.padding(paddingValues).padding(start = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.erase_filters))
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}