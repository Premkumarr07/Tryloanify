package com.example.tryloanify.presentation.loans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.LoanSummaryCard
import com.example.tryloanify.domain.model.ApplicationStatus
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.presentation.common.formatCurrency
import com.example.tryloanify.presentation.dashboard.DashboardViewModel
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun LoansScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val loanState by viewModel.loanState.collectAsState()
    val applications by viewModel.applications.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadHome() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text("My Loans", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(20.dp))

        if (loanState is UiState.Success) {
            val loan = (loanState as UiState.Success).data
            LoanSummaryCard(
                title = "Active Loan",
                amount = formatCurrency(loan.outstandingPrincipal),
                subtitle = "Disbursed ${loan.disbursementDate}",
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(
                text = "Open Dashboard",
                onClick = { navController.navigate(Screen.Dashboard.createRoute(loan.id)) },
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text("Applications", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        if (applications.isEmpty()) {
            Text("No applications yet", color = Appcolors.TextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Apply Now",
                onClick = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                    }
                },
            )
        } else {
            applications.forEach { app ->
                LoanSummaryCard(
                    title = app.trackingId,
                    amount = formatCurrency(app.requestedAmount),
                    subtitle = app.status.name.replace('_', ' '),
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                when (app.status) {
                    ApplicationStatus.APPROVED, ApplicationStatus.OFFER_ACCEPTED ->
                        AppButton(
                            text = "View Offer",
                            onClick = { navController.navigate(Screen.Offer.createRoute(app.id)) },
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    else ->
                        AppButton(
                            text = "Track Status",
                            onClick = { navController.navigate(Screen.ApplicationStatus.createRoute(app.id)) },
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                }
            }
        }
    }
}
