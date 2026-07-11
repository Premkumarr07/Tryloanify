package com.example.tryloanify.presentation.consent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.ConsentCheckbox
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun ConsentScreen(navController: NavController) {
    var loanConsent by remember { mutableStateOf(false) }
    var bureauConsent by remember { mutableStateOf(false) }
    var marketingConsent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text("Consent & Privacy", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "We need your consent to process your loan application in compliance with RBI and DPDP Act 2023.",
            color = Appcolors.TextSecondary,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        ConsentCheckbox(
            checked = loanConsent,
            onCheckedChange = { loanConsent = it },
            label = "I consent to processing my personal data for loan origination and servicing.",
        )
        Spacer(modifier = Modifier.height(12.dp))
        ConsentCheckbox(
            checked = bureauConsent,
            onCheckedChange = { bureauConsent = it },
            label = "I consent to credit bureau data pull (CIBIL/Experian) for credit assessment.",
        )
        Spacer(modifier = Modifier.height(12.dp))
        ConsentCheckbox(
            checked = marketingConsent,
            onCheckedChange = { marketingConsent = it },
            label = "I consent to receive marketing communications (optional, separate from loan consent).",
        )
        Spacer(modifier = Modifier.height(32.dp))
        AppButton(
            text = "I Agree & Continue",
            enabled = loanConsent && bureauConsent,
            onClick = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
        )
    }
}
