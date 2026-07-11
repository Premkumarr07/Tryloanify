package com.example.tryloanify.presentation.payment

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
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.presentation.common.formatCurrency
import com.example.tryloanify.presentation.dashboard.DashboardViewModel
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun PaymentScreen(
    loanId: String,
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val loanState by viewModel.loanState.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    LaunchedEffect(loanId) { viewModel.loadDashboard(loanId) }

    LaunchedEffect(paymentState) {
        if (paymentState is UiState.Success) navController.popBackStack()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Pay EMI", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(24.dp))
        if (loanState is UiState.Success) {
            val loan = (loanState as UiState.Success).data
            Text("Amount due", color = Appcolors.TextSecondary)
            Text(formatCurrency(loan.emiAmount), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Appcolors.Primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Due date: ${loan.nextEmiDate}", color = Appcolors.TextSecondary)
            Spacer(modifier = Modifier.height(32.dp))
            AppButton(
                text = "Pay Now",
                loading = paymentState is UiState.Loading,
                onClick = { viewModel.payEmi(loanId, loan.emiAmount) {} },
            )
            if (paymentState is UiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text((paymentState as UiState.Error).message, color = Appcolors.Error)
            }
        }
    }
}
