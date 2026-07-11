package com.example.tryloanify.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tryloanify.domain.model.Customer
import com.example.tryloanify.domain.repository.AuthRepository
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.presentation.common.isValidPan
import com.example.tryloanify.presentation.common.isValidPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _sendOtpState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val sendOtpState: StateFlow<UiState<Unit>> = _sendOtpState.asStateFlow()

    private val _verifyOtpState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val verifyOtpState: StateFlow<UiState<Unit>> = _verifyOtpState.asStateFlow()

    private val _profileState = MutableStateFlow<UiState<Customer>>(UiState.Idle)
    val profileState: StateFlow<UiState<Customer>> = _profileState.asStateFlow()

    fun sendOtp(phone: String) {
        if (!isValidPhone(phone)) {
            _sendOtpState.value = UiState.Error("Enter a valid 10-digit mobile number")
            return
        }
        viewModelScope.launch {
            _sendOtpState.value = UiState.Loading
            authRepository.sendOtp(phone)
                .onSuccess { _sendOtpState.value = UiState.Success(Unit) }
                .onFailure { _sendOtpState.value = UiState.Error(it.message ?: "Failed to send OTP") { sendOtp(phone) } }
        }
    }

    fun verifyOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _verifyOtpState.value = UiState.Loading
            authRepository.verifyOtp(phone, otp)
                .onSuccess { _verifyOtpState.value = UiState.Success(Unit) }
                .onFailure { _verifyOtpState.value = UiState.Error(it.message ?: "Invalid OTP") { verifyOtp(phone, otp) } }
        }
    }

    fun saveProfile(fullName: String, email: String, pan: String, phone: String) {
        if (fullName.isBlank()) {
            _profileState.value = UiState.Error("Full name is required")
            return
        }
        if (!isValidPan(pan.uppercase())) {
            _profileState.value = UiState.Error("Enter a valid PAN (e.g. ABCDE1234F)")
            return
        }
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            val existing = authRepository.getCurrentCustomer()
            val customer = Customer(
                id = existing?.id.orEmpty(),
                fullName = fullName.trim(),
                phone = phone,
                email = email.trim(),
                panNumber = pan.uppercase(),
            )
            authRepository.saveProfile(customer)
                .onSuccess { _profileState.value = UiState.Success(it) }
                .onFailure { _profileState.value = UiState.Error(it.message ?: "Failed to save profile") { saveProfile(fullName, email, pan, phone) } }
        }
    }

    suspend fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    suspend fun hasProfile(): Boolean {
        val customer = authRepository.getCurrentCustomer()
        return !customer?.fullName.isNullOrBlank() && !customer?.panNumber.isNullOrBlank()
    }

    suspend fun getCurrentCustomer() = authRepository.getCurrentCustomer()

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
