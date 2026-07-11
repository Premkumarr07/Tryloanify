package com.example.tryloanify.presentation.offer

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
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.presentation.common.formatCurrency
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun DisbursementStatusScreen(
    applicationId: String,
    navController: NavController,
    viewModel: OfferViewModel = hiltViewModel(),
) {
    val state by viewModel.disburseState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.triggerDisbursement(applicationId) { loanId ->
            navController.navigate(Screen.Dashboard.createRoute(loanId)) {
                popUpTo(Screen.Main.route)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("Disbursement", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(24.dp))
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator(color = Appcolors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Transferring funds to your account...", color = Appcolors.TextSecondary)
            }
            is UiState.Success -> {
                val loan = (state as UiState.Success).data
                Text("Loan disbursed successfully!", color = Appcolors.Success, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(formatCurrency(loan.principal), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Appcolors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    text = "Go to Dashboard",
                    onClick = { navController.navigate(Screen.Dashboard.createRoute(loan.id)) },
                )
            }
            is UiState.Error -> {
                Text((state as UiState.Error).message, color = Appcolors.Error)
                AppButton(text = "Retry", onClick = {
                    viewModel.triggerDisbursement(applicationId) { loanId ->
                        navController.navigate(Screen.Dashboard.createRoute(loanId))
                    }
                })
            }
            else -> Unit
        }
    }
}
