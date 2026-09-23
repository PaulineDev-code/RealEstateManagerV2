package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.ui.tooling.preview.Preview

/**
 * Rend une preview aux trois largeurs de vérification du projet.
 * 1600 dp est celle qui rend visibles les bornes de largeur.
 */
@Preview(name = "412dp – phone", widthDp = 412, showBackground = true, backgroundColor = -1)
@Preview(name = "840dp – tablet", widthDp = 840, showBackground = true, backgroundColor = -1)
@Preview(name = "1600dp – large window", widthDp = 1600, showBackground = true, backgroundColor = -1)
annotation class ResponsiveWidths
