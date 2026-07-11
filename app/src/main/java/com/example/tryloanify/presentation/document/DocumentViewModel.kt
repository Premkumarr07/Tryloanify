package com.example.tryloanify.presentation.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tryloanify.domain.model.DocumentType
import com.example.tryloanify.domain.model.UploadedDocument
import com.example.tryloanify.domain.repository.ApplicationRepository
import com.example.tryloanify.domain.repository.DocumentRepository
import com.example.tryloanify.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val applicationRepository: ApplicationRepository,
) : ViewModel() {
    private val _uploadState = MutableStateFlow<UiState<UploadedDocument>>(UiState.Idle)
    val uploadState: StateFlow<UiState<UploadedDocument>> = _uploadState.asStateFlow()

    private val _documents = MutableStateFlow<List<UploadedDocument>>(emptyList())
    val documents: StateFlow<List<UploadedDocument>> = _documents.asStateFlow()

    fun loadDocuments(applicationId: String) {
        viewModelScope.launch {
            _documents.value = documentRepository.getDocuments(applicationId)
        }
    }

    fun uploadDocument(applicationId: String, type: DocumentType, fileName: String) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            documentRepository.uploadDocument(applicationId, type, fileName)
                .onSuccess {
                    _uploadState.value = UiState.Success(it)
                    loadDocuments(applicationId)
                }
                .onFailure {
                    _uploadState.value = UiState.Error(it.message ?: "Upload failed") {
                        uploadDocument(applicationId, type, fileName)
                    }
                }
        }
    }

    fun startKyc(applicationId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            applicationRepository.startKyc(applicationId)
            onDone()
        }
    }
}
