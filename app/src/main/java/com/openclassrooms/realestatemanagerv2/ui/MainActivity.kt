package com.openclassrooms.realestatemanagerv2.ui

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.di.DatabaseModule
import com.openclassrooms.realestatemanagerv2.di.DatabaseModule_ProvideMyDatabaseFactory.provideMyDatabase
import com.openclassrooms.realestatemanagerv2.ui.theme.RealEstateManagerV2Theme
import dagger.hilt.android.AndroidEntryPoint
import dagger.internal.DaggerCollections

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
            val isTablet = screenWidthDp > 600

            supportActionBar?.hide()

            RealEstateManagerV2Theme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
