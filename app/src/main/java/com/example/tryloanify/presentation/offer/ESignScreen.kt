package com.example.tryloanify.presentation.offer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun ESignScreen(
    applicationId: String,
    navController: NavController,
    viewModel: OfferViewModel = hiltViewModel(),
) {
    val state by viewModel.esignState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Digital Agreement", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Appcolors.Card),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Aadhaar eSign WebView\n(Digio / eMudhra integration)\n\nReview loan agreement and sign digitally.",
                textAlign = TextAlign.Center,
                color = Appcolors.TextSecondary,
                modifier = Modifier.padding(24.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppButton(
            text = "Simulate Successful eSign",
            loading = state is UiState.Loading,
            onClick = {
                viewModel.completeESign(applicationId) {
                    navController.navigate(Screen.Disbursement.createRoute(applicationId))
                }
            },
        )
    }
}
