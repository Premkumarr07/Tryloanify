package com.example.tryloanify.presentation.document

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.StatusBadge
import com.example.tryloanify.domain.model.DocumentType
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun DocumentCaptureScreen(
    applicationId: String,
    navController: NavController,
    viewModel: DocumentViewModel = hiltViewModel(),
) {
    val uploadState by viewModel.uploadState.collectAsState()
    val documents by viewModel.documents.collectAsState()

    LaunchedEffect(applicationId) { viewModel.loadDocuments(applicationId) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Upload Documents", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Capture or upload PAN, Aadhaar, and income proof.", color = Appcolors.TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))

        DocumentType.entries.filter { it != DocumentType.BANK_STATEMENT }.forEach { type ->
            val uploaded = documents.any { it.type == type }
            Text(type.name.replace('_', ' '), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            if (uploaded) {
                StatusBadge("Uploaded", Appcolors.Success)
            } else {
                AppButton(
                    text = "Upload ${type.name}",
                    loading = uploadState is UiState.Loading,
                    onClick = {
                        viewModel.uploadDocument(applicationId, type, "${type.name.lowercase()}.jpg")
                    },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        val requiredDone = documents.any { it.type == DocumentType.PAN } &&
            documents.any { it.type == DocumentType.AADHAAR }

        AppButton(
            text = "Continue to KYC",
            enabled = requiredDone,
            onClick = {
                viewModel.startKyc(applicationId) {
                    navController.navigate(Screen.KycStatus.createRoute(applicationId))
                }
            },
        )
    }
}
