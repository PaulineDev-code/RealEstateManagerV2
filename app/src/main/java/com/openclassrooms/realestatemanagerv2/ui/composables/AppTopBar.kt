package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showEraseFiltersButton: Boolean = false,
    onEraseFiltersClick: () -> Unit = {},
    showUpButton: Boolean = false,
    onUpClick: () -> Unit = {},
    showAddButton: Boolean = true,
    onAddClick: () -> Unit,
    showModifyButton: Boolean = false,
    onModifyClick: () -> Unit = {},
    showNetworkWarning: Boolean = false,
    navBarsColor: Color = MaterialTheme.colorScheme.primaryContainer,
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
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    when {
                        showEraseFiltersButton ->
                            TextButton(
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                onClick = onEraseFiltersClick,
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(text = stringResource(id = R.string.erase_filters))
                            }

                        showUpButton ->
                            IconButton(onClick = onUpClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Arrow Back Icon"
                                )
                            }
                    }
                },
                actions = {
                    if(showNetworkWarning) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "Offline Icon"
                        )
                    }
                    if (showAddButton) {
                        IconButton(onClick = onAddClick) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Icon"
                            )
                        }
                    }
                    if (showModifyButton) {
                        IconButton(onClick = onModifyClick) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Icon"
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = content
    )
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun AppTopBarPreview() {
    AppTopBar(
        title = stringResource(R.string.app_name),
        onUpClick = {},
        showUpButton = false,
        onEraseFiltersClick = {},
        showEraseFiltersButton = true,
        onAddClick = {},
        onModifyClick = {},
        showModifyButton = true
    ) {

    }
}
