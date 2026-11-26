package com.darkyellowstudios.localflix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.darkyellowstudios.localflix.ui.theme.IndustrialObsidian
import com.darkyellowstudios.localflix.ui.theme.IndustrialYellow

@Composable
fun SplashScreen(onReady: () -> Unit) {
    LaunchedEffect(Unit) { onReady() }

    Box(
        modifier = Modifier.fillMaxSize().background(IndustrialObsidian),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "LOCALFLIX",
            color = IndustrialYellow,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 6.sp,
        )
    }
}
