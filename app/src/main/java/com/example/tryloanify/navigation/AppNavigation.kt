package com.example.tryloanify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tryloanify.presentation.application.ApplicationFormScreen
import com.example.tryloanify.presentation.application.ApplicationStatusScreen
import com.example.tryloanify.presentation.auth.LoginScreen
import com.example.tryloanify.presentation.auth.OtpScreen
import com.example.tryloanify.presentation.auth.ProfileSetupScreen
import com.example.tryloanify.presentation.consent.ConsentScreen
import com.example.tryloanify.presentation.dashboard.DashboardScreen
import com.example.tryloanify.presentation.document.DocumentCaptureScreen
import com.example.tryloanify.presentation.document.KycStatusScreen
import com.example.tryloanify.presentation.offer.DisbursementStatusScreen
import com.example.tryloanify.presentation.offer.ESignScreen
import com.example.tryloanify.presentation.offer.OfferScreen
import com.example.tryloanify.presentation.payment.PaymentScreen
import com.example.tryloanify.presentation.splash.SplashScreen
import com.example.tryloanify.ui.theme.TryLoanifyTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    TryLoanifyTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
        ) {
            composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(
                route = Screen.Otp.route,
                arguments = listOf(navArgument("phone") { type = NavType.StringType }),
            ) {
                entry ->
                OtpScreen(phone = entry.arguments?.getString("phone").orEmpty(), navController = navController)
            }
            composable(Screen.ProfileSetup.route) { ProfileSetupScreen(navController) }
            composable(Screen.Consent.route) { ConsentScreen(navController) }
            composable(Screen.Main.route) { MainShell(navController) }

            composable(
                route = Screen.DocumentCapture.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { entry ->
                DocumentCaptureScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.KycStatus.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { entry ->
                KycStatusScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.ApplicationStatus.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { entry ->
                ApplicationStatusScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.Offer.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { entry ->
                OfferScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.ESign.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { entry ->
                ESignScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.Disbursement.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { entry ->
                DisbursementStatusScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.Dashboard.route,
                arguments = listOf(navArgument("loanId") { type = NavType.StringType }),
            ) { entry ->
                DashboardScreen(
                    loanId = entry.arguments?.getString("loanId").orEmpty(),
                    navController = navController,
                )
            }
            composable(
                route = Screen.Payment.route,
                arguments = listOf(navArgument("loanId") { type = NavType.StringType }),
            ) { entry ->
                PaymentScreen(
                    loanId = entry.arguments?.getString("loanId").orEmpty(),
                    navController = navController,
                )
            }
        }
    }
}
