package com.example.tryloanify.presentation.dashboard

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
import com.example.tryloanify.component.StatusBadge
import com.example.tryloanify.domain.model.InstallmentStatus
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.presentation.common.formatCurrency
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun DashboardScreen(
    loanId: String,
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val loanState by viewModel.loanState.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    LaunchedEffect(loanId) { viewModel.loadDashboard(loanId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text("Loan Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)

        if (loanState is UiState.Success) {
            val loan = (loanState as UiState.Success).data
            Spacer(modifier = Modifier.height(20.dp))
            LoanSummaryCard(
                title = "Outstanding Principal",
                amount = formatCurrency(loan.outstandingPrincipal),
                subtitle = "EMI ${formatCurrency(loan.emiAmount)} · Next due ${loan.nextEmiDate}",
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Pay EMI",
                onClick = { navController.navigate(Screen.Payment.createRoute(loan.id)) },
            )
            val inCoolingOff = loan.coolingOffEndsAt?.let { System.currentTimeMillis() < it } == true
            if (inCoolingOff) {
                Spacer(modifier = Modifier.height(12.dp))
                AppButton(
                    text = "Cancel Loan (Cooling-off)",
                    onClick = { viewModel.cancelCoolingOff(loan.id) { navController.popBackStack() } },
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Repayment Schedule", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            schedule.take(6).forEach { inst ->
                val color = when (inst.status) {
                    InstallmentStatus.PAID -> Appcolors.Success
                    InstallmentStatus.OVERDUE -> Appcolors.Error
                    InstallmentStatus.PENDING -> Appcolors.Warning
                }
                LoanSummaryCard(
                    title = "EMI #${inst.installmentNo} · ${inst.dueDate}",
                    amount = formatCurrency(inst.totalAmount),
                    subtitle = "Principal ${formatCurrency(inst.principalComponent)} · Interest ${formatCurrency(inst.interestComponent)}",
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                StatusBadge(inst.status.name, color)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Transactions", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            transactions.forEach { txn ->
                Text("${txn.type}: ${formatCurrency(txn.amount)} · ${txn.status} · ${txn.createdAt}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Grievance Officer", fontWeight = FontWeight.SemiBold)
            Text("grievance@tryloanify.com · 1800-123-4567", color = Appcolors.TextSecondary, fontSize = 14.sp)
            Text("RBI Ombudsman: 14448", color = Appcolors.TextSecondary, fontSize = 14.sp)
        }
    }
}
