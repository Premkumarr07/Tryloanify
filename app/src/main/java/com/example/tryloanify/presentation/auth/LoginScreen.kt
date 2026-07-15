package com.example.tryloanify.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.R
import com.example.tryloanify.component.AuthTopBar
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState



private val Primary        = Color(0xFF4C3BCA)
private val TextPrimary    = Color(0xFF0F1523)
private val TextSecondary  = Color(0xFF6B7280)
private val TextHint       = Color(0xFFADB5BD)
private val InputBorder    = Color(0xFFD1D5DB)
private val DividerColor   = Color(0xFFE4E6F0)
private val ErrorColor     = Color(0xFFDC2626)
private val CountryCodeBg  = Color(0xFFF4F5FB)
private val White          = Color(0xFFFFFFFF)

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
            .background(White),
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
            Spacer(Modifier.height(24.dp))

            Text("Welcome to", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("TryLoanify", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Primary)

            Spacer(Modifier.height(20.dp))

            Text("Enter your mobile number", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("We will send you an OTP to verify", fontSize = 13.sp, color = TextSecondary)

            Spacer(Modifier.height(20.dp))

            PhoneInputRow(
                value = phone,
                onValueChange = {
                    if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it
                },
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_check),
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp),
                )
                Text("Your data is safe and secure with us", fontSize = 12.sp, color = TextSecondary)
            }

            if (state is UiState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((state as UiState.Error).message, color = ErrorColor, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.sendOtp(phone) },
                enabled = phone.length == 10 && state !is UiState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.5f),
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                if (state is UiState.Loading) {
                    CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
                }
            }

            Spacer(Modifier.weight(1f))
            TermsFooter()
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PhoneInputRow(value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, InputBorder), RoundedCornerShape(12.dp))
            .background(White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text("+91", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }

        }
        Divider(
            modifier = Modifier.padding(horizontal = 12.dp).height(24.dp).width(1.dp),
            color = DividerColor,
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Mobile Number", color = TextHint, fontSize = 14.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Primary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
            ),
        )
    }
}


@Composable
private fun TermsFooter() {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = TextSecondary, fontSize = 12.sp)) { append("By continuing, you agree to our ") }
        withStyle(SpanStyle(color = Primary, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) { append("Terms & Conditions") }
        withStyle(SpanStyle(color = TextSecondary, fontSize = 12.sp)) { append(" and # ") }
        withStyle(SpanStyle(color = Primary, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) { append("Privacy Policy") }
    }
    Text(text = text, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, lineHeight = 18.sp)
}