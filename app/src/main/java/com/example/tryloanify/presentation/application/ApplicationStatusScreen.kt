package com.example.tryloanify.presentation.application

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.StatusBadge
import com.example.tryloanify.domain.model.ApplicationStatus
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun ApplicationStatusScreen(
    applicationId: String,
    navController: NavController,
    viewModel: ApplicationViewModel = hiltViewModel(),
) {
    val state by viewModel.applicationState.collectAsState()

    LaunchedEffect(applicationId) { viewModel.loadApplication(applicationId) }

    val statuses = listOf(
        ApplicationStatus.DRAFT,
        ApplicationStatus.SUBMITTED,
        ApplicationStatus.KYC_VERIFIED,
        ApplicationStatus.UNDER_REVIEW,
        ApplicationStatus.APPROVED,
    )

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Application Status", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(24.dp))
        if (state is UiState.Success) {
            val app = (state as UiState.Success).data
            Text("Tracking: ${app.trackingId}", color = Appcolors.TextSecondary)
            Spacer(modifier = Modifier.height(20.dp))
            statuses.forEach { status ->
                val reached = status.ordinal <= app.status.ordinal ||
                    (app.status == ApplicationStatus.REJECTED && status != ApplicationStatus.APPROVED)
                StatusBadge(
                    text = status.name.replace('_', ' '),
                    color = if (reached) Appcolors.Success else Appcolors.Hint,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (app.status == ApplicationStatus.APPROVED) {
                Spacer(modifier = Modifier.height(16.dp))
                com.example.tryloanify.component.AppButton(
                    text = "View Offer",
                    onClick = { navController.navigate(Screen.Offer.createRoute(applicationId)) },
                )
            }
        }
    }
}
