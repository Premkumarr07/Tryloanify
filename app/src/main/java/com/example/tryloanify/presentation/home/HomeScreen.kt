package com.example.tryloanify.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.LoanSummaryCard
import com.example.tryloanify.component.MainTopBar
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

    Column(modifier = Modifier.fillMaxSize()) {

        MainTopBar(
            title = "TryLoanify",
            onMenuClick = { /* drawer */ },
            hasUnread = true,
            onBellClick = { /* notifications */ },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {


            Spacer(Modifier.height(20.dp))

            if (loanState is UiState.Success) {
                val loan = (loanState as UiState.Success).data
                ActiveLoanBanner(
                    outstanding = formatCurrency(loan.outstandingPrincipal),
                    emiText = "Next EMI ${formatCurrency(loan.emiAmount)} · Due ${loan.nextEmiDate}",
                    onViewDashboard = { navController.navigate(Screen.Dashboard.createRoute(loan.id)) },
                )
            } else {
                HeroBanner(
                    onApplyClick = {
                        tabNavController?.navigate(Screen.ApplicationForm.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }

            Spacer(Modifier.height(20.dp))
            FeatureGrid()

            Spacer(Modifier.height(20.dp))
            CibilScoreSection(score = 782, onCheckClick = { })

            // ── Recent applications, from real data ────────────────────
            if (applications.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Recent Applications", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
                    Spacer(Modifier.height(10.dp))

                    applications.take(3).forEach { app ->
                        RecentApplicationCard(
                            trackingId = app.trackingId,
                            amountText = formatCurrency(app.requestedAmount),
                            statusText = app.status.name.replace('_', ' '),
                            onClick = {
                                if (app.status == ApplicationStatus.APPROVED) {
                                    navController.navigate(Screen.Offer.createRoute(app.id))
                                }
                            },
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            SmartServicesSection()
        }
    }
}


data class FeatureItem(val label: String, val subLabel: String, val icon: ImageVector, val bg: Color, val tint: Color)
data class SmartService(val label: String, val icon: ImageVector, val bg: Color, val tint: Color)

private val features = listOf(
    FeatureItem("Instant Approval", "In 5 minutes", Icons.Default.Bolt, Color(0xFFEDE9FE), Color(0xFF7C3AED)),
    FeatureItem("Minimal Documents", "100% Paperless", Icons.Default.Description, Color(0xFFDCFCE7), Color(0xFF16A34A)),
    FeatureItem("Low Interest Rates", "Starting 11.99% p.a.", Icons.Default.Percent, Color(0xFFFFEDD5), Color(0xFFEA580C)),
    FeatureItem("Direct Transfer", "In Bank Account", Icons.Default.AccountBalance, Color(0xFFDBEAFE), Color(0xFF2563EB)),
)

private val smartServices = listOf(
    SmartService("EMI Calculator", Icons.Default.Calculate, Appcolors.iconBgPurple, Appcolors.Primary),
    SmartService("Mutual Funds", Icons.Default.TrendingUp, Color(0xFFDCFCE7), Color(0xFF16A34A)),
    SmartService("Digital Gold", Icons.Default.Wifi, Color(0xFFFFEDD5), Color(0xFFEA580C)),
    SmartService("More Loans", Icons.Default.Description, Color(0xFFFFE4E6), Color(0xFFE11D48)),
    SmartService("More", Icons.Default.GridView, Appcolors.iconBgPurple, Appcolors.Primary),
)

@Composable
private fun HeroBanner(onApplyClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Appcolors.Primary, Appcolors.PrimaryDark))),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Get started", fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
                Spacer(Modifier.height(6.dp))
                Text("Up to ₹5,00,000", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text("Quick approval  •  Transparent rates", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(16.dp))
                Surface(onClick = onApplyClick, shape = RoundedCornerShape(10.dp), color = Color.White) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("Apply for Loan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Primary)
                        Icon(Icons.Default.ChevronRight, null, tint = Appcolors.Primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            WalletIllustration(modifier = Modifier.size(110.dp).align(Alignment.CenterVertically))
        }
    }
}

@Composable
private fun ActiveLoanBanner(outstanding: String, emiText: String, onViewDashboard: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        LoanSummaryCard(title = "Active Loan", amount = outstanding, subtitle = emiText)
        Spacer(Modifier.height(12.dp))
        AppButton(text = "View Loan Dashboard", onClick = onViewDashboard)
    }
}

/**
 * Layered vector wallet illustration (coins + wallet + shield-check badge).
 * Swap this composable's body for Image(painterResource(R.drawable.wallet_hero))
 * if/when you have the real illustration asset — same Modifier, one-line change.
 */
@Composable
private fun WalletIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(start = 2.dp, bottom = 4.dp)) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .padding(top = if (i == 0) 0.dp else 3.dp)
                        .size(width = 30.dp, height = 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFBBF24)),
                )
            }
        }
        Box(
            modifier = Modifier.align(Alignment.Center).size(width = 74.dp, height = 62.dp)
                .clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-14).dp)
                    .size(width = 46.dp, height = 26.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                    .background(Color.White),
            )
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Appcolors.Primary))
        }
        Box(modifier = Modifier.align(Alignment.BottomEnd).size(38.dp), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(38.dp))
            Icon(Icons.Default.Check, null, tint = Appcolors.Primary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun FeatureGrid() {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            features.forEach { feature ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp)) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(feature.bg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(feature.icon, feature.label, tint = feature.tint, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(feature.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Text,
                        textAlign = TextAlign.Center, maxLines = 2, lineHeight = 13.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(feature.subLabel, fontSize = 10.sp, color = Appcolors.TextSecondary, textAlign = TextAlign.Center, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun CibilScoreSection(score: Int, onCheckClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(30.dp).clip(CircleShape).background(Appcolors.iconBgPurple),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Speed, null, tint = Appcolors.Primary, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("Check Your CIBIL Score", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
            }
            Spacer(Modifier.height(2.dp))
            Text("Higher score, better loan offers", fontSize = 13.sp, color = Appcolors.TextSecondary,
                modifier = Modifier.padding(start = 38.dp))

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                CibilGauge(score = score, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1.1f)) {
                    listOf(
                        "Get accurate CIBIL score instantly",
                        "Know your loan eligibility",
                        "Get best interest rates",
                        "No impact on your credit score",
                    ).forEach { line ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                            Box(
                                modifier = Modifier.size(18.dp).clip(CircleShape).background(Appcolors.iconBgPurple),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Check, null, tint = Appcolors.Primary, modifier = Modifier.size(11.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(line, fontSize = 12.sp, color = Appcolors.Text, lineHeight = 15.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCheckClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Appcolors.Primary),
            ) {
                Text("Check CIBIL Score", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Primary)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ChevronRight, null, tint = Appcolors.Primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun CibilGauge(score: Int, modifier: Modifier = Modifier) {
    val fraction = ((score - 300f) / (900f - 300f)).coerceIn(0f, 1f)
    val label = when {
        score >= 750 -> "Good"
        score >= 700 -> "Fair"
        else -> "Needs Work"
    }
    val labelColor = when {
        score >= 750 -> Appcolors.greencolor
        score >= 700 -> Color(0xFFF57C00)
        else -> Color(0xFFE53935)
    }

    Box(modifier = modifier.aspectRatio(1.4f).padding(top = 4.dp), contentAlignment = Alignment.BottomCenter) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.width.coerceAtMost(size.height * 2f) - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2f, size.height - diameter / 2f - strokeWidth / 2f)
            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

            drawArc(
                color = Appcolors.referBackground,
                startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                brush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.5f to Color(0xFFE53935),
                        0.625f to Color(0xFFF57C00),
                        0.75f to Color(0xFFFBC02D),
                        0.875f to Color(0xFF8BC34A),
                        1.0f to Appcolors.greencolor,
                    ),
                    center = Offset(topLeft.x + diameter / 2f, topLeft.y + diameter / 2f),
                ),
                startAngle = 180f, sweepAngle = 180f * fraction, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 4.dp)) {
            Text("$score", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Appcolors.Text)
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = labelColor)
            Text("(300 - 900)", fontSize = 10.sp, color = Appcolors.TextSecondary)
        }
    }
}

@Composable
private fun RecentApplicationCard(
    trackingId: String,
    amountText: String,
    statusText: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Appcolors.iconBgPurple),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Description, null, tint = Appcolors.Primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(trackingId, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Text)
                Text(amountText, fontSize = 12.sp, color = Appcolors.TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Appcolors.iconBgPurple)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(statusText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Primary)
                }
                Spacer(Modifier.height(6.dp))
                Icon(Icons.Default.ChevronRight, null, tint = Appcolors.TextSecondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun SmartServicesSection() {
    Column {
        Text("Smart Services", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text,
            modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
            elevation = CardDefaults.cardElevation(1.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                smartServices.forEach { service ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(60.dp).clickable { },
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(service.bg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(service.icon, service.label, tint = service.tint, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(service.label, fontSize = 10.sp, color = Appcolors.Text, textAlign = TextAlign.Center,
                            maxLines = 2, lineHeight = 12.sp)
                    }
                }
            }
        }
    }
}