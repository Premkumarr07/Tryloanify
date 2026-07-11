package com.example.tryloanify.presentation.home

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
import androidx.navigation.NavGraph.Companion.findStartDestination
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
fun HomeScreen(
    navController: NavController,
    tabNavController: NavController? = null,
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
        Text("TryLoanify", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Appcolors.Primary)
        Text("Your lending hub", color = Appcolors.TextSecondary, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(24.dp))

        if (loanState is UiState.Success) {
            val loan = (loanState as UiState.Success).data
            LoanSummaryCard(
                title = "Active Loan",
                amount = formatCurrency(loan.outstandingPrincipal),
                subtitle = "Next EMI ${formatCurrency(loan.emiAmount)} · Due ${loan.nextEmiDate}",
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(
                text = "View Loan Dashboard",
                onClick = { navController.navigate(Screen.Dashboard.createRoute(loan.id)) },
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            LoanSummaryCard(
                title = "Get started",
                amount = "Up to ₹5,00,000",
                subtitle = "Quick approval · Transparent rates",
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Apply for Loan",
                onClick = {
                    tabNavController?.navigate(Screen.ApplicationForm.route) {
                        popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        if (applications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(28.dp))
            Text("Recent Applications", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            applications.take(3).forEach { app ->
                LoanSummaryCard(
                    title = app.trackingId,
                    amount = formatCurrency(app.requestedAmount),
                    subtitle = "Status: ${app.status.name.replace('_', ' ')}",
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                if (app.status == ApplicationStatus.APPROVED) {
                    AppButton(
                        text = "View Offer",
                        onClick = { navController.navigate(Screen.Offer.createRoute(app.id)) },
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }
            }
        }
    }
}
