package com.example.tryloanify.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tryloanify.domain.model.Loan
import com.example.tryloanify.domain.model.LoanApplication
import com.example.tryloanify.domain.model.RepaymentInstallment
import com.example.tryloanify.domain.model.Transaction
import com.example.tryloanify.domain.repository.ApplicationRepository
import com.example.tryloanify.domain.repository.LoanRepository
import com.example.tryloanify.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val applicationRepository: ApplicationRepository,
) : ViewModel() {
    private val _loanState = MutableStateFlow<UiState<Loan>>(UiState.Idle)
    val loanState: StateFlow<UiState<Loan>> = _loanState.asStateFlow()

    private val _schedule = MutableStateFlow<List<RepaymentInstallment>>(emptyList())
    val schedule: StateFlow<List<RepaymentInstallment>> = _schedule.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _applications = MutableStateFlow<List<LoanApplication>>(emptyList())
    val applications: StateFlow<List<LoanApplication>> = _applications.asStateFlow()

    private val _paymentState = MutableStateFlow<UiState<Transaction>>(UiState.Idle)
    val paymentState: StateFlow<UiState<Transaction>> = _paymentState.asStateFlow()

    fun loadDashboard(loanId: String?) {
        viewModelScope.launch {
            _loanState.value = UiState.Loading
            val loan = if (!loanId.isNullOrBlank()) loanRepository.getLoan(loanId)
            else loanRepository.getActiveLoan()
            if (loan != null) {
                _loanState.value = UiState.Success(loan)
                _schedule.value = loanRepository.getSchedule(loan.id)
                _transactions.value = loanRepository.getTransactions(loan.id)
            } else {
                _loanState.value = UiState.Error("No active loan")
            }
        }
    }

    fun loadHome() {
        viewModelScope.launch {
            _applications.value = applicationRepository.getApplications()
            val loan = loanRepository.getActiveLoan()
            if (loan != null) _loanState.value = UiState.Success(loan)
        }
    }

    fun payEmi(loanId: String, amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            loanRepository.payEmi(loanId, amount)
                .onSuccess {
                    _paymentState.value = UiState.Success(it)
                    loadDashboard(loanId)
                    onSuccess()
                }
                .onFailure {
                    _paymentState.value = UiState.Error(it.message ?: "Payment failed") { payEmi(loanId, amount, onSuccess) }
                }
        }
    }

    fun cancelCoolingOff(loanId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            loanRepository.cancelWithinCoolingOff(loanId)
                .onSuccess { onDone() }
        }
    }
}
