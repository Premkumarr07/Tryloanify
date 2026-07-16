package com.example.tryloanify.presentation.loans

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tryloanify.component.MainTopBar
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.ui.theme.Appcolors

// ─────────────────────────────────────────────────────────────────────────────
// UI model
//
// NOTE: the current domain layer (DashboardViewModel.loanState = single Loan,
// applications = List<Application>) doesn't carry the per-loan fields this
// design needs (EMI number, due date, overdue amount, tenure, total paid).
// Rather than force-fit a mismatch, this screen defines its own UI-only
// LoanItem model. Once the backend/domain layer exposes a real "my loans"
// list with these fields, replace `sampleLoans` with a mapped list from a
// LoansViewModel -- the composables below don't need to change.
// ─────────────────────────────────────────────────────────────────────────────

enum class LoanStatus { DUE_SOON, OVERDUE, COMPLETED }

data class LoanItem(
    val id: String,
    val title: String,
    val amount: String,
    val loanId: String,
    val status: LoanStatus,
    val dateLabel: String,
    val dateValue: String,
    val emiAmount: String,
    val col2Label: String,
    val col2Value: String,
    val col3Label: String,
    val col3Value: String,
    val col3ValueColor: Color,
    val actionLabel: String,
    val iconBg: Color,
    val iconTint: Color,
    val showCheckBadge: Boolean = false,
)

private val sampleLoans = listOf(
    LoanItem(
        id = "ln1", title = "Personal Loan", amount = "₹1,20,000", loanId = "LN12345678",
        status = LoanStatus.DUE_SOON, dateLabel = "Due Date", dateValue = "25 May 2024",
        emiAmount = "₹5,320", col2Label = "EMI No.", col2Value = "5 / 24",
        col3Label = "Next EMI Due", col3Value = "₹5,320", col3ValueColor = Color(0xFFEA580C),
        actionLabel = "Pay EMI", iconBg = Appcolors.iconBgPurple, iconTint = Appcolors.Primary,
    ),
    LoanItem(
        id = "ln2", title = "Business Loan", amount = "₹2,50,000", loanId = "LN87654321",
        status = LoanStatus.OVERDUE, dateLabel = "Due Date", dateValue = "10 May 2024",
        emiAmount = "₹8,750", col2Label = "EMI No.", col2Value = "3 / 18",
        col3Label = "Overdue Amount", col3Value = "₹8,750", col3ValueColor = Color(0xFFE11D48),
        actionLabel = "Pay Now", iconBg = Color(0xFFFEE2E2), iconTint = Color(0xFFE11D48),
    ),
    LoanItem(
        id = "ln3", title = "Two Wheeler Loan", amount = "₹80,000", loanId = "LN11223344",
        status = LoanStatus.COMPLETED, dateLabel = "Closed On", dateValue = "15 Mar 2024",
        emiAmount = "₹3,201", col2Label = "Total Paid", col2Value = "₹80,025",
        col3Label = "Tenure", col3Value = "24 Months", col3ValueColor = Appcolors.Text,
        actionLabel = "View Details", iconBg = Color(0xFFDCFCE7), iconTint = Color(0xFF16A34A),
        showCheckBadge = true,
    ),
)

private enum class LoanTab { PENDING_DUE, COMPLETED }

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LoansScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(LoanTab.PENDING_DUE) }

    val pendingDueLoans = sampleLoans.filter { it.status != LoanStatus.COMPLETED }
    val completedLoans = sampleLoans.filter { it.status == LoanStatus.COMPLETED }

    Scaffold(
        topBar = {
            MainTopBar(
                title = "Loans",
                showMenu = true,
                onMenuClick = { /* open nav drawer */ },
                showBell = true,
                hasUnread = true,
                onBellClick = { /* open notifications */ },
            )
        },
        containerColor = Appcolors.Background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        ) {
            item {
                LoansTabSelector(
                    selectedTab = selectedTab,
                    pendingCount = pendingDueLoans.size,
                    completedCount = completedLoans.size,
                    onTabSelected = { selectedTab = it },
                )
            }

            val (sectionTitle, loansForTab) = when (selectedTab) {
                LoanTab.PENDING_DUE -> "Pending & Due Loans" to pendingDueLoans
                LoanTab.COMPLETED -> "Completed Loans" to completedLoans
            }

            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = sectionTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(12.dp))
            }

            items(loansForTab, key = { it.id }) { loan ->
                LoanCard(loan = loan, onActionClick = { navController.navigate(Screen.Dashboard.createRoute(loan.id)) })
                Spacer(Modifier.height(14.dp))
            }

            item {
                Spacer(Modifier.height(6.dp))
                NeedNewLoanBanner(
                    eligibleAmount = "₹5,00,000",
                    onApplyClick = { navController.navigate(Screen.ApplicationForm.route) },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoansTabSelector(
    selectedTab: LoanTab, pendingCount: Int, completedCount: Int, onTabSelected: (LoanTab) -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)).background(Appcolors.Card).padding(4.dp),
    ) {
        TabPill(
            label = "Pending & Due", count = pendingCount, icon = Icons.Outlined.HourglassEmpty,
            selected = selectedTab == LoanTab.PENDING_DUE, modifier = Modifier.weight(1f),
            onClick = { onTabSelected(LoanTab.PENDING_DUE) },
        )
        Spacer(Modifier.width(4.dp))
        TabPill(
            label = "Completed", count = completedCount, icon = Icons.Default.CheckCircle,
            selected = selectedTab == LoanTab.COMPLETED, modifier = Modifier.weight(1f),
            onClick = { onTabSelected(LoanTab.COMPLETED) },
        )
    }
}

@Composable
private fun TabPill(
    label: String, count: Int, icon: ImageVector, selected: Boolean,
    modifier: Modifier = Modifier, onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Appcolors.iconBgPurple else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (selected) Appcolors.Primary else Appcolors.TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            label, fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Appcolors.Primary else Appcolors.TextSecondary,
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier.clip(CircleShape)
                .background(if (selected) Appcolors.Primary else Appcolors.TextSecondary.copy(alpha = 0.15f))
                .padding(horizontal = 7.dp, vertical = 1.dp),
        ) {
            Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Appcolors.TextSecondary)
        }
    }
}

@Composable
private fun LoanCard(loan: LoanItem, onActionClick: () -> Unit) {
    val (statusBg, statusColor, statusLabel) = statusStyle(loan.status)
    val dateColor = when (loan.status) {
        LoanStatus.DUE_SOON -> Color(0xFFEA580C)
        LoanStatus.OVERDUE -> Color(0xFFE11D48)
        LoanStatus.COMPLETED -> Color(0xFF16A34A)
    }

    Card(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            LoanCardHeader(loan, statusBg, statusColor, statusLabel, dateColor)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Appcolors.TextSecondary.copy(alpha = 0.12f))
            Spacer(Modifier.height(14.dp))
            LoanCardFooter(loan, onActionClick)
        }
    }
}

@Composable
private fun LoanCardHeader(loan: LoanItem, statusBg: Color, statusColor: Color, statusLabel: String, dateColor: Color) {
    Row(verticalAlignment = Alignment.Top) {
        LoanIconBadge(iconBg = loan.iconBg, iconTint = loan.iconTint, showCheck = loan.showCheckBadge)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(loan.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Text)
            Spacer(Modifier.height(4.dp))
            Text(loan.amount, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Appcolors.Text)
            Spacer(Modifier.height(2.dp))
            Text("Loan ID: ${loan.loanId}", fontSize = 12.sp, color = Appcolors.TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(statusBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(statusLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
            Spacer(Modifier.height(8.dp))
            Text(loan.dateLabel, fontSize = 11.sp, color = Appcolors.TextSecondary)
            Text(loan.dateValue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = dateColor)
        }
    }
}

@Composable
private fun LoanIconBadge(iconBg: Color, iconTint: Color, showCheck: Boolean) {
    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(iconBg), contentAlignment = Alignment.Center) {
        Icon(Icons.Default.AccountBalanceWallet, null, tint = iconTint, modifier = Modifier.size(22.dp))
        if (showCheck) {
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).size(16.dp).clip(CircleShape).background(Color(0xFF16A34A)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun LoanCardFooter(loan: LoanItem, onActionClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LoanStatColumn("EMI Amount", loan.emiAmount, Appcolors.Text, Modifier.weight(1f))
        LoanStatColumn(loan.col2Label, loan.col2Value, Appcolors.Text, Modifier.weight(1f))
        LoanStatColumn(loan.col3Label, loan.col3Value, loan.col3ValueColor, Modifier.weight(1.1f))

        if (loan.status == LoanStatus.OVERDUE) {
            Button(
                onClick = onActionClick, shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Appcolors.Primary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(loan.actionLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        } else {
            OutlinedButton(
                onClick = onActionClick, shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Appcolors.Primary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(loan.actionLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Primary)
            }
        }
    }
}

@Composable
private fun LoanStatColumn(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = Appcolors.TextSecondary)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun NeedNewLoanBanner(eligibleAmount: String, onApplyClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)).background(Appcolors.iconBgPurple).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Appcolors.Primary), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Need a new loan?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Appcolors.Primary)
            Text("You're eligible for up to $eligibleAmount", fontSize = 12.sp, color = Appcolors.TextSecondary)
        }
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = onApplyClick, shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Appcolors.Primary),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text("Apply Now", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

private fun statusStyle(status: LoanStatus): Triple<Color, Color, String> = when (status) {
    LoanStatus.DUE_SOON -> Triple(Color(0xFFFFEDD5), Color(0xFFEA580C), "Due Soon")
    LoanStatus.OVERDUE -> Triple(Color(0xFFFFE4E6), Color(0xFFE11D48), "Overdue")
    LoanStatus.COMPLETED -> Triple(Color(0xFFDCFCE7), Color(0xFF16A34A), "Completed")
}