package com.example.tryloanify.presentation.offer

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.ConsentCheckbox
import com.example.tryloanify.component.KfsScrollPanel
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.presentation.common.formatCurrency
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun OfferScreen(
    applicationId: String,
    navController: NavController,
    viewModel: OfferViewModel = hiltViewModel(),
) {
    val state by viewModel.offerState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var kfsViewed by remember { mutableStateOf(false) }

    LaunchedEffect(applicationId) { viewModel.loadOffer(applicationId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text("Your Loan Offer", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(20.dp))

        when (state) {
            is UiState.Success -> {
                val offer = (state as UiState.Success).data
                Text("Sanctioned Amount", color = Appcolors.TextSecondary)
                Text(formatCurrency(offer.sanctionedAmount), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Appcolors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Interest: ${offer.interestRate}% p.a.", fontSize = 16.sp)
                Text("APR: ${offer.apr}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Appcolors.Warning)
                Text("EMI: ${formatCurrency(offer.emiAmount)} / ${offer.tenureMonths} months")
                Text("Risk Grade: ${offer.riskGrade}", color = Appcolors.TextSecondary)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Key Fact Statement (KFS)", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                KfsScrollPanel(content = offer.kfsContent)
                Spacer(modifier = Modifier.height(12.dp))
                ConsentCheckbox(
                    checked = kfsViewed,
                    onCheckedChange = { kfsViewed = it },
                    label = "I have read and understood the Key Fact Statement",
                )
                Spacer(modifier = Modifier.height(20.dp))
                AppButton(
                    text = "Accept Offer",
                    loading = actionState is UiState.Loading,
                    enabled = kfsViewed,
                    onClick = {
                        viewModel.acceptOffer(applicationId) {
                            navController.navigate(Screen.ESign.createRoute(applicationId))
                        }
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppButton(
                    text = "Reject Offer",
                    onClick = {
                        viewModel.rejectOffer(applicationId)
                        navController.popBackStack()
                    },
                )
            }
            is UiState.Loading -> Text("Loading offer...", color = Appcolors.TextSecondary)
            is UiState.Error -> Text((state as UiState.Error).message, color = Appcolors.Error)
            else -> Unit
        }
    }
}
