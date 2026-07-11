package com.example.tryloanify.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.auth.AuthViewModel
import com.example.tryloanify.ui.theme.Appcolors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        delay(2000)
        val loggedIn = viewModel.isLoggedIn()
        val hasProfile = if (loggedIn) viewModel.hasProfile() else false
        when {
            loggedIn && hasProfile -> navController.navigate(Screen.Main.route) { popUpTo(0) }
            loggedIn -> navController.navigate(Screen.ProfileSetup.route) { popUpTo(0) }
            else -> navController.navigate(Screen.Login.route) { popUpTo(0) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Appcolors.Background, Appcolors.PrimaryLight.copy(alpha = 0.25f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TryLoanify",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Appcolors.Primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Smart loans. Trusted always.",
                fontSize = 16.sp,
                color = Appcolors.TextSecondary,
            )
        }
    }
}
