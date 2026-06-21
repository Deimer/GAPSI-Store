package com.deymervilla.ds.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.deymervilla.ds.theme.GAPSIStoreTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1200L

@Composable
fun SplashScreenCompose(
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GAPSIStore",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    GAPSIStoreTheme {
        Scaffold {
            SplashScreenCompose(
                onNavigateToHome = {}
            )
        }
    }
}