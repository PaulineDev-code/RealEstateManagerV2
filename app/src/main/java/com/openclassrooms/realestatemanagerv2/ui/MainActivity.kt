package com.openclassrooms.realestatemanagerv2.ui

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.openclassrooms.realestatemanagerv2.ui.theme.RealEstateManagerV2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            supportActionBar?.hide()

            val windowAdaptiveInfo = currentWindowAdaptiveInfo()

            RealEstateManagerV2Theme {
                val navController = rememberNavController()

                AppNavigation(windowAdaptiveInfo = windowAdaptiveInfo)
            }
        }
    }
}
