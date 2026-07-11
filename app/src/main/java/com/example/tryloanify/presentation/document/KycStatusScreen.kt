package com.example.tryloanify.presentation.document

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.domain.model.ApplicationStatus
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.application.ApplicationViewModel
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun KycStatusScreen(
    applicationId: String,
    navController: NavController,
    viewModel: ApplicationViewModel = hiltViewModel(),
) {
    val state by viewModel.applicationState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.pollKyc(applicationId) { id ->
            navController.navigate(Screen.Offer.createRoute(id)) {
                popUpTo(Screen.ApplicationForm.route)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("KYC Verification", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(24.dp))
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator(color = Appcolors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Verifying your documents...", color = Appcolors.TextSecondary)
            }
            is UiState.Success -> {
                val app = (state as UiState.Success).data
                Text("Status: ${app.kycStatus.name}", color = Appcolors.Primary)
                if (app.status == ApplicationStatus.APPROVED) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AppButton(
                        text = "View Offer",
                        onClick = { navController.navigate(Screen.Offer.createRoute(applicationId)) },
                    )
                }
            }
            is UiState.Error -> {
                Text((state as UiState.Error).message, color = Appcolors.Error)
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(text = "Retry", onClick = {
                    viewModel.pollKyc(applicationId) { id ->
                        navController.navigate(Screen.Offer.createRoute(id))
                    }
                })
            }
            else -> Unit
        }
    }
}
