package com.example.tryloanify.presentation.auth

import androidx.compose.foundation.background
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
import com.example.tryloanify.component.AuthTopBar
import com.example.tryloanify.component.OtpInput
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.data.remote.sms.OtpBus
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun OtpScreen(
    phone: String,
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var otp by remember { mutableStateOf("") }
    val state by viewModel.verifyOtpState.collectAsState()

    LaunchedEffect(Unit) {
        OtpBus.otp.collect { code ->
            if (!code.isNullOrBlank()) otp = code
        }
    }

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            navController.navigate(Screen.ProfileSetup.route) {
                popUpTo(Screen.Login.route) { inclusive = false }
            }
        }
    }

    LaunchedEffect(otp) {
        if (otp.length == 6) viewModel.verifyOtp(phone, otp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Appcolors.white),
    ) {

        AuthTopBar(
            onBackClick = { navController.popBackStack() },
            onHelpClick = { /* help */ },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("Verify OTP", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Sent to +91 $phone", color = Appcolors.TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))
        OtpInput(otp = otp, onOtpChange = { otp = it })
        if (state is UiState.Error) {
            Spacer(modifier = Modifier.height(12.dp))
            Text((state as UiState.Error).message, color = Appcolors.Error, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            text = "Verify",
            loading = state is UiState.Loading,
            enabled = otp.length == 6,
            onClick = { viewModel.verifyOtp(phone, otp) },
        )}
    }
}
