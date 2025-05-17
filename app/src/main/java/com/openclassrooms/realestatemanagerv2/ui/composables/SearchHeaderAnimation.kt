package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.openclassrooms.realestatemanagerv2.R

@Composable
fun SearchHeaderAnimation(
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever
) {
    // 1) Charger la composition depuis le raw resource
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animation_search_property)
    )
    // 2) Piloter l’animation
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = 1.0f,            // 1x speed
        isPlaying = true
    )

    // 3) Afficher
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)     // ajuste selon ton design
    )
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun SearchHeaderAnimationPreview(){
    SearchHeaderAnimation(modifier = Modifier)
}