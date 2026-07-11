package com.example.tryloanify.presentation.common

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>
}

fun formatCurrency(amount: Double): String {
    return "₹%,.0f".format(amount)
}

fun isValidPan(pan: String): Boolean =
    pan.matches(Regex("^[A-Z]{5}[0-9]{4}[A-Z]$"))

fun isValidPhone(phone: String): Boolean =
    phone.matches(Regex("^[6-9]\\d{9}$"))
