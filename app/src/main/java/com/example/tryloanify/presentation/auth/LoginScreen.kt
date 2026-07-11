package com.example.tryloanify.presentation.auth

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
import com.example.tryloanify.component.PrimaryTextField
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var phone by remember { mutableStateOf("") }
    val state by viewModel.sendOtpState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            navController.navigate(Screen.Otp.createRoute(phone))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("Welcome to", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Text("TryLoanify", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Appcolors.Primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter your mobile number to continue", color = Appcolors.TextSecondary, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryTextField(
            value = phone,
            onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
            label = "Mobile Number (+91)",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
        )
        if (state is UiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text((state as UiState.Error).message, color = Appcolors.Error, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            text = "Send OTP",
            loading = state is UiState.Loading,
            enabled = phone.length == 10,
            onClick = { viewModel.sendOtp(phone) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Demo OTP: 123456", color = Appcolors.Hint, fontSize = 12.sp)
    }
}
