package com.example.tryloanify.presentation.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tryloanify.domain.model.Loan
import com.example.tryloanify.domain.model.LoanApplication
import com.example.tryloanify.domain.model.LoanOffer
import com.example.tryloanify.domain.repository.ApplicationRepository
import com.example.tryloanify.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfferViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository,
) : ViewModel() {
    private val _offerState = MutableStateFlow<UiState<LoanOffer>>(UiState.Idle)
    val offerState: StateFlow<UiState<LoanOffer>> = _offerState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    private val _esignState = MutableStateFlow<UiState<LoanApplication>>(UiState.Idle)
    val esignState: StateFlow<UiState<LoanApplication>> = _esignState.asStateFlow()

    private val _disburseState = MutableStateFlow<UiState<Loan>>(UiState.Idle)
    val disburseState: StateFlow<UiState<Loan>> = _disburseState.asStateFlow()

    fun loadOffer(applicationId: String) {
        viewModelScope.launch {
            _offerState.value = UiState.Loading
            val offer = applicationRepository.getOffer(applicationId)
            if (offer != null) _offerState.value = UiState.Success(offer)
            else _offerState.value = UiState.Error("Offer not available yet") { loadOffer(applicationId) }
        }
    }

    fun acceptOffer(applicationId: String, onAccepted: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            applicationRepository.acceptOffer(applicationId)
                .onSuccess {
                    _actionState.value = UiState.Success(Unit)
                    onAccepted()
                }
                .onFailure {
                    _actionState.value = UiState.Error(it.message ?: "Failed") { acceptOffer(applicationId, onAccepted) }
                }
        }
    }

    fun rejectOffer(applicationId: String) {
        viewModelScope.launch {
            applicationRepository.rejectOffer(applicationId)
        }
    }

    fun completeESign(applicationId: String, onDone: (String) -> Unit) {
        viewModelScope.launch {
            _esignState.value = UiState.Loading
            applicationRepository.completeESign(applicationId)
                .onSuccess {
                    _esignState.value = UiState.Success(it)
                    onDone(applicationId)
                }
                .onFailure {
                    _esignState.value = UiState.Error(it.message ?: "E-sign failed") { completeESign(applicationId, onDone) }
                }
        }
    }

    fun triggerDisbursement(applicationId: String, onDone: (String) -> Unit) {
        viewModelScope.launch {
            _disburseState.value = UiState.Loading
            applicationRepository.triggerDisbursement(applicationId)
                .onSuccess {
                    _disburseState.value = UiState.Success(it)
                    onDone(it.id)
                }
                .onFailure {
                    _disburseState.value = UiState.Error(it.message ?: "Disbursement failed") {
                        triggerDisbursement(applicationId, onDone)
                    }
                }
        }
    }
}
