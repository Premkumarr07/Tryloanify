package com.example.tryloanify.presentation.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tryloanify.domain.model.EmploymentType
import com.example.tryloanify.domain.model.LoanApplication
import com.example.tryloanify.domain.repository.ApplicationRepository
import com.example.tryloanify.domain.repository.AuthRepository
import com.example.tryloanify.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ApplicationFormState(
    val amount: Float = 100_000f,
    val tenure: Int = 24,
    val employmentType: EmploymentType = EmploymentType.SALARIED,
    val monthlyIncome: String = "",
    val step: Int = 1,
    val applicationId: String = "",
)

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _formState = MutableStateFlow(ApplicationFormState())
    val formState: StateFlow<ApplicationFormState> = _formState.asStateFlow()

    private val _submitState = MutableStateFlow<UiState<LoanApplication>>(UiState.Idle)
    val submitState: StateFlow<UiState<LoanApplication>> = _submitState.asStateFlow()

    private val _applicationState = MutableStateFlow<UiState<LoanApplication>>(UiState.Idle)
    val applicationState: StateFlow<UiState<LoanApplication>> = _applicationState.asStateFlow()

    fun updateAmount(amount: Float) {
        _formState.value = _formState.value.copy(amount = amount)
    }

    fun updateTenure(tenure: Int) {
        _formState.value = _formState.value.copy(tenure = tenure)
    }

    fun updateEmployment(type: EmploymentType) {
        _formState.value = _formState.value.copy(employmentType = type)
    }

    fun updateIncome(income: String) {
        _formState.value = _formState.value.copy(monthlyIncome = income)
    }

    fun nextStep() {
        _formState.value = _formState.value.copy(step = (_formState.value.step + 1).coerceAtMost(3))
    }

    fun prevStep() {
        _formState.value = _formState.value.copy(step = (_formState.value.step - 1).coerceAtLeast(1))
    }

    fun saveDraft() {
        viewModelScope.launch {
            val state = _formState.value
            val income = state.monthlyIncome.toDoubleOrNull() ?: return@launch
            val customer = authRepository.getCurrentCustomer() ?: return@launch
            val app = LoanApplication(
                id = state.applicationId.ifBlank { UUID.randomUUID().toString() },
                trackingId = "",
                customerId = customer.id,
                requestedAmount = state.amount.toDouble(),
                requestedTenure = state.tenure,
                employmentType = state.employmentType,
                monthlyIncome = income,
                status = com.example.tryloanify.domain.model.ApplicationStatus.DRAFT,
            )
            val result = if (state.applicationId.isBlank()) {
                applicationRepository.createDraft(app)
            } else {
                applicationRepository.updateDraft(app)
            }
            result.onSuccess {
                _formState.value = state.copy(applicationId = it.id)
            }
        }
    }

    fun submitApplication(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val state = _formState.value
            val income = state.monthlyIncome.toDoubleOrNull()
            if (income == null || income <= 0) {
                _submitState.value = UiState.Error("Enter a valid monthly income")
                return@launch
            }
            _submitState.value = UiState.Loading
            saveDraftInternal(state, income)
            val appId = _formState.value.applicationId
            applicationRepository.submitApplication(appId)
                .onSuccess {
                    _submitState.value = UiState.Success(it)
                    onSuccess(it.id)
                }
                .onFailure {
                    _submitState.value = UiState.Error(it.message ?: "Submit failed") { submitApplication(onSuccess) }
                }
        }
    }

    private suspend fun saveDraftInternal(state: ApplicationFormState, income: Double) {
        val customer = authRepository.getCurrentCustomer() ?: return
        val app = LoanApplication(
            id = state.applicationId.ifBlank { UUID.randomUUID().toString() },
            trackingId = "",
            customerId = customer.id,
            requestedAmount = state.amount.toDouble(),
            requestedTenure = state.tenure,
            employmentType = state.employmentType,
            monthlyIncome = income,
            status = com.example.tryloanify.domain.model.ApplicationStatus.DRAFT,
        )
        val result = if (state.applicationId.isBlank()) applicationRepository.createDraft(app)
        else applicationRepository.updateDraft(app)
        result.onSuccess { _formState.value = state.copy(applicationId = it.id) }
    }

    fun loadApplication(applicationId: String) {
        viewModelScope.launch {
            _applicationState.value = UiState.Loading
            val app = applicationRepository.getApplication(applicationId)
            if (app != null) _applicationState.value = UiState.Success(app)
            else _applicationState.value = UiState.Error("Application not found")
        }
    }

    fun pollKyc(applicationId: String, onApproved: (String) -> Unit) {
        viewModelScope.launch {
            _applicationState.value = UiState.Loading
            applicationRepository.pollKyc(applicationId)
                .onSuccess {
                    _applicationState.value = UiState.Success(it)
                    if (it.status == com.example.tryloanify.domain.model.ApplicationStatus.APPROVED) {
                        onApproved(applicationId)
                    }
                }
                .onFailure {
                    _applicationState.value = UiState.Error(it.message ?: "KYC failed") { pollKyc(applicationId, onApproved) }
                }
        }
    }
}
