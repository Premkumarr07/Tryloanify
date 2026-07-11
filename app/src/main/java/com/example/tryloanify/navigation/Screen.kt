package com.example.tryloanify.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Otp : Screen("otp/{phone}") {
        fun createRoute(phone: String) = "otp/$phone"
    }
    data object ProfileSetup : Screen("profile_setup")
    data object Consent : Screen("consent")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object ApplicationForm : Screen("application_form")
    data object DocumentCapture : Screen("document_capture/{applicationId}") {
        fun createRoute(applicationId: String) = "document_capture/$applicationId"
    }
    data object KycStatus : Screen("kyc_status/{applicationId}") {
        fun createRoute(applicationId: String) = "kyc_status/$applicationId"
    }
    data object ApplicationStatus : Screen("application_status/{applicationId}") {
        fun createRoute(applicationId: String) = "application_status/$applicationId"
    }
    data object Offer : Screen("offer/{applicationId}") {
        fun createRoute(applicationId: String) = "offer/$applicationId"
    }
    data object ESign : Screen("esign/{applicationId}") {
        fun createRoute(applicationId: String) = "esign/$applicationId"
    }
    data object Disbursement : Screen("disbursement/{applicationId}") {
        fun createRoute(applicationId: String) = "disbursement/$applicationId"
    }
    data object Dashboard : Screen("dashboard/{loanId}") {
        fun createRoute(loanId: String) = "dashboard/$loanId"
    }
    data object Payment : Screen("payment/{loanId}") {
        fun createRoute(loanId: String) = "payment/$loanId"
    }
    data object Loans : Screen("loans")
    data object Profile : Screen("profile")
}
