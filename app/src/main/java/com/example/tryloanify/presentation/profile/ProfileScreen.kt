package com.example.tryloanify.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tryloanify.component.MainTopBar          // ← new separate bar
import com.example.tryloanify.domain.model.Customer
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.auth.AuthViewModel
import com.example.tryloanify.presentation.profile.components.ProfileCard
import com.example.tryloanify.presentation.profile.components.ReferEarnCard
import com.example.tryloanify.presentation.profile.components.SettingsMenuItem
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

    Scaffold(

        topBar = {
            MainTopBar(
                title       = "My Profile",
                showMenu    = true,
                onMenuClick = { /* open nav drawer */ },
                showBell    = true,
                hasUnread   = true,
                onBellClick = { /* open notifications */ },
            )
        },
        containerColor = Appcolors.Background,
    ) { innerPadding ->

        // ── Scrollable body — starts right below the fixed top bar ────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),           // respects top bar height
            contentPadding = PaddingValues(
                start  = 20.dp,
                end    = 20.dp,
                top    = 12.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            item {
                ProfileCard(
                    name        = customer?.fullName ?: "Prem Kumar",
                    phone       = customer?.let { "+91 ${it.phone}" } ?: "+91 98765 43210",
                    email       = customer?.email ?: "premkumar@email.com",
                    onCardClick = { },
                    onEditClick = { },
                )
            }

            item {
                Text(
                    text       = "Account & Settings",
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Appcolors.Text,
                )
            }

            // ── Settings menu card ────────────────────────────────────────
            item {
                Card(
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = Appcolors.Card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SettingsMenuItem(
                            title          = "Personal Information",
                            subtitle       = "Update your personal details",
                            icon           = Icons.Filled.Person,
                            iconBackground = Appcolors.iconBgBlue,
                            iconTint       = Appcolors.iconTintBlue,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "KYC & Verification",
                            subtitle       = "View and update KYC details",
                            icon           = Icons.Filled.Shield,
                            iconBackground = Appcolors.iconBgGreen,
                            iconTint       = Appcolors.iconTintGreen,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "Security",
                            subtitle       = "Change password, PIN & biometrics",
                            icon           = Icons.Filled.Lock,
                            iconBackground = Appcolors.iconBgPurple,
                            iconTint       = Appcolors.iconTintPurple,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "Notifications",
                            subtitle       = "Manage your notification preferences",
                            icon           = Icons.Filled.Notifications,
                            iconBackground = Appcolors.iconBgYellow,
                            iconTint       = Appcolors.iconTintYellow,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "Bank Accounts",
                            subtitle       = "View and manage linked accounts",
                            icon           = Icons.Filled.AccountBalance,
                            iconBackground = Appcolors.iconBgBlue,
                            iconTint       = Appcolors.iconTintBlue,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "Documents",
                            subtitle       = "View uploaded documents",
                            icon           = Icons.Filled.Description,
                            iconBackground = Appcolors.iconBgPurple,
                            iconTint       = Appcolors.iconTintPurple,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "Help & Support",
                            subtitle       = "Get help and contact support",
                            icon           = Icons.Filled.HelpOutline,
                            iconBackground = Appcolors.iconBgTeal,
                            iconTint       = Appcolors.iconTintTeal,
                            onClick        = { },
                        )
                        SettingsMenuItem(
                            title          = "Logout",
                            subtitle       = "Securely logout from your account",
                            icon           = Icons.Filled.Logout,
                            iconBackground = Appcolors.iconBgRed,
                            iconTint       = Appcolors.iconTintRed,
                            onClick        = {
                                viewModel.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            },
                        )
                    }
                }
            }

            item {
                ReferEarnCard(onReferClick = { })
            }
        }
    }
}