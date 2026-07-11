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
import androidx.compose.ui.text.input.KeyboardType
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
fun ProfileSetupScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pan by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val state by viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCurrentCustomer()?.let {
            fullName = it.fullName
            email = it.email
            pan = it.panNumber
            phone = it.phone
        }
    }

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            navController.navigate(Screen.Consent.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Complete your profile", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryTextField(
            value = pan,
            onValueChange = { if (it.length <= 10) pan = it.uppercase() },
            label = "PAN Number",
        )
        if (state is UiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text((state as UiState.Error).message, color = Appcolors.Error, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            text = "Continue",
            loading = state is UiState.Loading,
            onClick = { viewModel.saveProfile(fullName, email, pan, phone) },
        )
    }
}
