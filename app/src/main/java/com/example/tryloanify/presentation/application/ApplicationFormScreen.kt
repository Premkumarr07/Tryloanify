package com.example.tryloanify.presentation.application

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AmountSlider
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.PrimaryTextField
import com.example.tryloanify.component.StepProgress
import com.example.tryloanify.domain.model.EmploymentType
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationFormScreen(
    navController: NavController,
    viewModel: ApplicationViewModel = hiltViewModel(),
) {
    val form by viewModel.formState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val tenures = listOf(6, 12, 24, 36, 48, 60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text("Loan Application", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Spacer(modifier = Modifier.height(16.dp))
        StepProgress(currentStep = form.step, totalSteps = 3)
        Spacer(modifier = Modifier.height(24.dp))

        when (form.step) {
            1 -> {
                Text("Loan amount", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                AmountSlider(amount = form.amount, onAmountChange = viewModel::updateAmount)
            }
            2 -> {
                Text("Tenure (months)", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tenures.forEach { t ->
                        FilterChip(
                            selected = form.tenure == t,
                            onClick = { viewModel.updateTenure(t) },
                            label = { Text("$t mo") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Appcolors.Primary.copy(0.15f)),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Employment type", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EmploymentType.entries.forEach { type ->
                        FilterChip(
                            selected = form.employmentType == type,
                            onClick = { viewModel.updateEmployment(type) },
                            label = { Text(type.name.replace('_', ' ')) },
                        )
                    }
                }
            }
            else -> {
                PrimaryTextField(
                    value = form.monthlyIncome,
                    onValueChange = viewModel::updateIncome,
                    label = "Monthly Income (₹)",
                    keyboardType = KeyboardType.Number,
                )
            }
        }

        if (submitState is UiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text((submitState as UiState.Error).message, color = Appcolors.Error, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        if (form.step < 3) {
            AppButton(text = "Continue", onClick = { viewModel.nextStep(); viewModel.saveDraft() })
            if (form.step > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                AppButton(text = "Back", onClick = viewModel::prevStep)
            }
        } else {
            AppButton(
                text = "Submit Application",
                loading = submitState is UiState.Loading,
                onClick = {
                    viewModel.submitApplication { appId ->
                        navController.navigate(Screen.DocumentCapture.createRoute(appId))
                    }
                },
            )
        }
    }
}
