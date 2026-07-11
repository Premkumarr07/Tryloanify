package com.example.tryloanify.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.domain.model.Customer
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.auth.AuthViewModel
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var customer by remember { mutableStateOf<Customer?>(null) }

    LaunchedEffect(Unit) {
        customer = viewModel.getCurrentCustomer()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Profile", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(24.dp))
        customer?.let {
            Text(it.fullName, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text("+91 ${it.phone}", color = Appcolors.TextSecondary)
            Text(it.email, color = Appcolors.TextSecondary)
            Text("PAN: ${it.panNumber.take(2)}****${it.panNumber.takeLast(2)}", color = Appcolors.TextSecondary)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Legal & Support", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Privacy Policy", color = Appcolors.Primary)
        Text("Fair Practices Code", color = Appcolors.Primary)
        Text("Grievance: grievance@tryloanify.com", color = Appcolors.TextSecondary, fontSize = 14.sp)
        Text("RBI Ombudsman: 14448", color = Appcolors.TextSecondary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        AppButton(
            text = "Logout",
            onClick = {
                viewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            },
        )
    }
}
