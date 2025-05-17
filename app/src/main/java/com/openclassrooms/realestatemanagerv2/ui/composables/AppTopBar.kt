package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.ui.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    onNavigationClick: () -> Unit,
    onSearchClick: () -> Unit = {navController.navigate("search_screen")},
    onAddClick: () -> Unit = {navController.navigate("add_screen")},
    onModifyClick: () -> Unit,
    showModifyButton: Boolean,
    navBarsColor: Color = MaterialTheme.colorScheme.primaryContainer,
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = navBarsColor,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = navBarsColor
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Localized description"
                        )
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Localized description"
                        )
                    }
                    if (showModifyButton) {
                        IconButton(onClick = onModifyClick) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation(backgroundColor = navBarsColor,
                    contentColor = MaterialTheme.colorScheme.primary) {
                    val items = listOf(
                        BottomNavItem.List,
                        BottomNavItem.Search,
                        BottomNavItem.Map
                    )
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {

                                navController.apply {
                                    navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                    }
                                }
                                /*
                                // Get DetailsScreen out of the stack if we are using it and want
                                // to go back to home screen
                                if (navController.currentDestination?.route?.startsWith("details_screen") == true) {
                                    // Tente de dépiler jusqu'à home_screen
                                    val popped = navController.popBackStack("home_screen", false)
                                    if (!popped) {
                                        // Si le dépilement n'a rien retiré (home_screen n'est pas dans la pile), on navigue vers home_screen
                                        navController.navigate("home_screen") {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                } else if (navController.currentDestination?.route != item.route) {
                                    // Pour tous les autres cas, navigue normalement
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                    }
                                }*/
                            }
                        )
                    }
                }
            }
        },
        content = content
    )
}

/*{innerPadding ->
        ScrollContent(innerPadding)
        // Contenu sous la barre supérieure (par exemple, LazyColumn)
        val navController = rememberNavController()
        PropertyList(navController = navController)
    }*/

/*
@Preview
@Composable
fun PreviewAppTopBar() {
    AppTopBar(
        onNavigationClick = { */
/*TODO*//*
  },
        onSearchClick = { */
/*TODO*//*
 },
        onAddClick = { */
/*TODO*//*
 },
        onModifyClick = { */
/*TODO*//*
 },
        showModifyButton = true)
}*/

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun AppTopBarPreview() {
    AppTopBar(
        navController = rememberNavController(),
        onNavigationClick = { /*TODO*/ },
        onSearchClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false
    ) {

    }
}
