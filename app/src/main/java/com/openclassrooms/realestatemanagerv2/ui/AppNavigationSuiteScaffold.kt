package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigationSuiteScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit = {}
    ) {
    val navItems = listOf(
        BottomNavItem.List,
        BottomNavItem.Search,
        BottomNavItem.Map
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selected    = MaterialTheme.colorScheme.onPrimaryContainer
    val indicator   = selected.copy(alpha = 0.1f)
    val unselected  = MaterialTheme.colorScheme.primary
    val disabled    = MaterialTheme.colorScheme.error

    NavigationSuiteScaffold(
        navigationSuiteItems = {

            navItems.forEach { item ->
                item(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },

    /*NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemColors(
            selectedIconColor = selected,
            selectedTextColor = selected,
            selectedIndicatorColor = indicator,
            unselectedIconColor = unselected,
            unselectedTextColor = unselected,
            disabledIconColor = disabled,
            disabledTextColor = disabled
        ),
        navigationRailItemColors = NavigationRailItemColors(
            selectedIconColor = selected,
            selectedTextColor = selected,
            selectedIndicatorColor = indicator,
            unselectedIconColor = unselected,
            unselectedTextColor = unselected,
            disabledIconColor = disabled,
            disabledTextColor = disabled
        ),
        navigationDrawerItemColors = NavigationDrawerItemColors(
            selectedIconColor = selected,
            selectedTextColor = selected,
            indicatorColor = indicator,
            unselectedIconColor = unselected,
            unselectedTextColor = unselected,
            disabledIconColor = disabled,
            disabledTextColor = disabled
        )
    ),*/
        content = content)


}

@Preview(showSystemUi = false, showBackground = true, backgroundColor = -1)
@Composable
fun AppNavigationSuiteScaffoldPreview() {
    AppNavigationSuiteScaffold(navController = rememberNavController())
}