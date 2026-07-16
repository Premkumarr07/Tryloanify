package com.example.tryloanify.presentation.application

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tryloanify.component.AppButton
import com.example.tryloanify.component.MainTopBar
import com.example.tryloanify.component.PrimaryTextField
import com.example.tryloanify.domain.model.EmploymentType
import com.example.tryloanify.navigation.Screen
import com.example.tryloanify.presentation.common.UiState
import com.example.tryloanify.ui.theme.Appcolors

private const val MIN_AMOUNT = 1_000f
private const val MAX_AMOUNT = 500_000f

private data class QuickAmount(val label: String, val value: Float)
private val quickAmounts = listOf(
    QuickAmount("₹5K", 5_000f),
    QuickAmount("₹10L", 10_000f),
    QuickAmount("₹1L", 100_000f),
    QuickAmount("₹3L", 300_000f),
    QuickAmount("₹5L", 500_000f),
)

private val tenureOptions = listOf(3, 6, 12, 24, 36, 48)

private data class EmploymentOption(val type: EmploymentType, val label: String, val icon: ImageVector)
private val employmentOptions = listOf(
    EmploymentOption(EmploymentType.SALARIED, "Salaried", Icons.Default.Work),
    EmploymentOption(EmploymentType.SELF_EMPLOYED, "Self Employed", Icons.Default.Storefront),
    EmploymentOption(EmploymentType.BUSINESS, "Business", Icons.Default.Person),
)

private data class TrustPoint(val title: String, val subtitle: String, val icon: ImageVector, val bg: Color, val tint: Color)
private val trustPoints = listOf(
    TrustPoint("Instant Approval", "In 5 minutes", Icons.Default.Bolt, Color(0xFFEDE9FE), Color(0xFF7C3AED)),
    TrustPoint("Safe & Secure", "Bank-level security", Icons.Default.VerifiedUser, Color(0xFFDCFCE7), Color(0xFF16A34A)),
    TrustPoint("Low Interest Rates", "Starting 11.99% p.a.", Icons.Default.Percent, Color(0xFFFFEDD5), Color(0xFFEA580C)),
    TrustPoint("Direct Transfer", "In your bank account", Icons.Default.AccountBalance, Color(0xFFDBEAFE), Color(0xFF2563EB)),
)

private val indianStates = listOf(
    "Andhra Pradesh", "Bihar", "Delhi", "Gujarat", "Karnataka", "Kerala",
    "Madhya Pradesh", "Maharashtra", "Punjab", "Rajasthan", "Tamil Nadu",
    "Telangana", "Uttar Pradesh", "West Bengal", "Other",
)

@Composable
fun ApplicationFormScreen(
    navController: NavController,
    viewModel: ApplicationViewModel = hiltViewModel(),
) {
    val form by viewModel.formState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    // NOTE: These fields are not yet part of ApplicationViewModel.formState.
    // Kept as local screen state for now so the UI matches the design;
    // wire these into the ViewModel (and persist via saveDraft()) once the
    // form model is extended with personal-details fields.
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }

    var address by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("") }

    var companyName by remember { mutableStateOf("") }
    var workExperience by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MainTopBar(
                title = "Apply for Loan",
                showMenu = true,
                onMenuClick = { /* open nav drawer */ },
                showBell = true,
                hasUnread = true,
                onBellClick = { /* open notifications */ },
            )
        },
        containerColor = Appcolors.Background,
        bottomBar = {
            ApplicationBottomBar(
                step = form.step,
                loading = submitState is UiState.Loading,
                onPrimaryClick = {
                    when {
                        form.step < 3 -> {
                            viewModel.nextStep()
                            viewModel.saveDraft()
                        }
                        else -> viewModel.submitApplication { appId ->
                            navController.navigate(Screen.DocumentCapture.createRoute(appId))
                        }
                    }
                },
                onBackClick = viewModel::prevStep,
            )
        },
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { ApplicationStepTracker(currentStep = form.step) }

            when (form.step) {
                1 -> {
                    item {
                        LoanAmountSection(
                            amount = form.amount,
                            onAmountChange = viewModel::updateAmount,
                        )
                    }
                    item {
                        TenureSection(
                            selectedTenure = form.tenure,
                            onTenureSelect = viewModel::updateTenure,
                        )
                    }
                    item {
                        EmploymentTypeSection(
                            selected = form.employmentType,
                            onSelect = viewModel::updateEmployment,
                        )
                    }
                    item { WhyChooseSection() }
                }

                2 -> {
                    item {
                        SectionCard(title = "Personal Information") {
                            PrimaryTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = "Full Name",
                            )
                            Spacer(Modifier.height(14.dp))

                            IconTrailingField(
                                value = dateOfBirth,
                                onValueChange = { dateOfBirth = it },
                                label = "Date of Birth",
                                icon = Icons.Default.CalendarMonth,
                                placeholder = "DD/MM/YYYY",
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = mobileNumber,
                                onValueChange = { if (it.length <= 10) mobileNumber = it },
                                label = "Mobile Number",
                                keyboardType = KeyboardType.Phone,
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Email Address",
                                keyboardType = KeyboardType.Email,
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = panNumber,
                                onValueChange = { if (it.length <= 10) panNumber = it.uppercase() },
                                label = "PAN Number",
                            )
                            Spacer(Modifier.height(14.dp))

                            IconTrailingField(
                                value = aadhaarNumber,
                                onValueChange = { if (it.length <= 14) aadhaarNumber = it },
                                label = "Aadhaar Number",
                                icon = Icons.Default.Shield,
                                keyboardType = KeyboardType.Number,
                            )
                        }
                    }

                    item {
                        SectionCard(title = "Current Address") {
                            PrimaryTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = "Address",
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = pincode,
                                onValueChange = { if (it.length <= 6) pincode = it },
                                label = "Pincode",
                                keyboardType = KeyboardType.Number,
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = "City",
                            )
                            Spacer(Modifier.height(14.dp))

                            StateDropdownField(
                                selected = selectedState,
                                onSelect = { selectedState = it },
                            )
                        }
                    }

                    item {
                        SectionCard(title = "Work Information") {
                            PrimaryTextField(
                                value = companyName,
                                onValueChange = { companyName = it },
                                label = "Company Name",
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = workExperience,
                                onValueChange = { if (it.length <= 2) workExperience = it },
                                label = "Work Experience (Years)",
                                keyboardType = KeyboardType.Number,
                            )
                            Spacer(Modifier.height(14.dp))

                            PrimaryTextField(
                                value = form.monthlyIncome,
                                onValueChange = viewModel::updateIncome,
                                label = "Monthly Income (₹)",
                                keyboardType = KeyboardType.Number,
                            )
                        }
                    }

                    item { SafeInfoNotice() }
                }

                else -> {
                    item {
                        SectionCard(title = "Document & Review") {
                            Text(
                                "Review your details and continue to document upload.",
                                fontSize = 13.sp,
                                color = Appcolors.TextSecondary,
                            )
                        }
                    }
                }
            }

            if (submitState is UiState.Error) {
                item {
                    Text(
                        (submitState as UiState.Error).message,
                        color = Appcolors.Error,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }
}

private data class StepDef(val index: Int, val label: String)
private val stepDefs = listOf(
    StepDef(1, "Loan Details"),
    StepDef(2, "Personal Details"),
    StepDef(3, "Document & Review"),
)

@Composable
private fun ApplicationStepTracker(currentStep: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            stepDefs.forEach { step ->
                val isCompleted = step.index < currentStep
                val isCurrent = step.index == currentStep

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (step.index != stepDefs.first().index) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(if (isCompleted || isCurrent) Appcolors.Primary else Appcolors.TextSecondary.copy(alpha = 0.2f)),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted || isCurrent) Appcolors.Primary else Appcolors.TextSecondary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    "${step.index}", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else Appcolors.TextSecondary,
                                )
                            }
                        }

                        if (step.index != stepDefs.last().index) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(if (isCompleted) Appcolors.Primary else Appcolors.TextSecondary.copy(alpha = 0.2f)),
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        step.label,
                        fontSize = 11.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) Appcolors.Primary else Appcolors.TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Loan amount
// ─────────────────────────────────────────────────────────────────────────────

@Composable

private fun LoanAmountSection(amount: Float, onAmountChange: (Float) -> Unit) {
    // Local text state so user can type freely; syncs to Float on done/unfocus
    var textValue by remember(amount) {
        mutableStateOf(amount.toLong().toString())
    }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Parse raw text → clamped Float, update parent
    fun commitText(raw: String) {
        val parsed = raw.filter { it.isDigit() }.toLongOrNull()?.toFloat() ?: MIN_AMOUNT
        val clamped = parsed.coerceIn(MIN_AMOUNT, MAX_AMOUNT)
        onAmountChange(clamped)
        textValue = clamped.toLong().toString()
    }

    SectionCard(
        title = "Loan Amount",
        trailing = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Appcolors.iconBgPurple)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text("Min ₹1,000", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Primary)
            }
        },
    ) {
        // ── Editable amount display ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isFocused) Appcolors.iconBgPurple else Appcolors.Background)
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) Appcolors.Primary else Appcolors.BorderColor,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // ₹ prefix
                Text(
                    text       = "₹",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Appcolors.Primary,
                )
                Spacer(Modifier.width(4.dp))
                // Editable number
                BasicTextField(
                    value        = textValue,
                    onValueChange = { raw ->
                        // Only digits, max 7 chars (5,00,000)
                        val digits = raw.filter { it.isDigit() }.take(7)
                        textValue = digits
                    },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { state ->
                            isFocused = state.isFocused
                            if (!state.isFocused) commitText(textValue)
                        },
                    textStyle = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Appcolors.Text,
                    ),
                    cursorBrush   = SolidColor(Appcolors.Primary),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            commitText(textValue)
                            focusManager.clearFocus()
                        },
                    ),
                )
                // Edit pencil hint
                if (!isFocused) {
                    Icon(
                        imageVector        = Icons.Default.Edit,
                        contentDescription = "Edit amount",
                        tint               = Appcolors.Primary.copy(alpha = 0.6f),
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }
        }

        if (isFocused) {
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "Type any amount between ₹50,000 – ₹5,00,000",
                fontSize = 11.sp,
                color    = Appcolors.TextSecondary,
            )
        }

        Spacer(Modifier.height(16.dp))

        // Slider stays in sync with amount Float
        Slider(
            value          = amount.coerceIn(MIN_AMOUNT, MAX_AMOUNT),
            onValueChange  = { newVal ->
                onAmountChange(newVal)
                if (!isFocused) textValue = newVal.toLong().toString()
            },
            valueRange     = MIN_AMOUNT..MAX_AMOUNT,
            colors         = SliderDefaults.colors(
                thumbColor         = Appcolors.Primary,
                activeTrackColor   = Appcolors.Primary,
                inactiveTrackColor = Appcolors.TextSecondary.copy(alpha = 0.15f),
            ),
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("₹50,000",   fontSize = 12.sp, color = Appcolors.TextSecondary)
            Text("₹5,00,000", fontSize = 12.sp, color = Appcolors.TextSecondary)
        }

        Spacer(Modifier.height(16.dp))

        // Quick chips
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            quickAmounts.forEach { option ->
                SelectableChip(
                    label    = option.label,
                    selected = amount == option.value,
                    modifier = Modifier.weight(1f),
                    onClick  = {
                        onAmountChange(option.value)
                        textValue = option.value.toLong().toString()
                        focusManager.clearFocus()
                    },
                )
            }
        }
    }
}

@Composable
private fun TenureSection(selectedTenure: Int, onTenureSelect: (Int) -> Unit) {
    SectionCard(title = "Tenure (Months)") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tenureOptions.forEach { months ->
                SelectableChip(
                    label = "$months",
                    modifier = Modifier.weight(1f),
                    selected = selectedTenure == months,
                    onClick = { onTenureSelect(months) },
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDCFCE7))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "EMI will be calculated after entering your details",
                fontSize = 12.sp,
                color = Color(0xFF15803D),
            )
        }
    }
}

@Composable
private fun EmploymentTypeSection(selected: EmploymentType, onSelect: (EmploymentType) -> Unit) {
    SectionCard(title = "Employment Type") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            employmentOptions.forEach { option ->
                val isSelected = selected == option.type
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Appcolors.iconBgPurple else Appcolors.Background)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Appcolors.Primary else Appcolors.TextSecondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelect(option.type) }
                        .padding(vertical = 18.dp),
                ) {
                    Icon(
                        option.icon, null,
                        tint = if (isSelected) Appcolors.Primary else Appcolors.TextSecondary,
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        option.label, fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Appcolors.Primary else Appcolors.TextSecondary,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Trust strip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WhyChooseSection() {
    SectionCard(title = "Why Choose TryLoanify?") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            trustPoints.forEach { point ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp)) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(point.bg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(point.icon, null, tint = point.tint, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        point.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Appcolors.Text,
                        textAlign = TextAlign.Center, maxLines = 2, lineHeight = 13.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        point.subtitle, fontSize = 10.sp, color = Appcolors.TextSecondary,
                        textAlign = TextAlign.Center, maxLines = 1,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Personal Details helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Text field with a trailing decorative icon (calendar / shield in the design).
 * Uses OutlinedTextField directly since PrimaryTextField's public signature
 * (as used elsewhere in this file) doesn't expose a trailingIcon slot.
 * If PrimaryTextField already supports one, prefer that instead.
 */
@Composable
private fun IconTrailingField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it, color = Appcolors.TextSecondary.copy(alpha = 0.6f)) } },
        trailingIcon = { Icon(icon, null, tint = Appcolors.TextSecondary, modifier = Modifier.size(18.dp)) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Appcolors.Primary,
            unfocusedBorderColor = Appcolors.TextSecondary.copy(alpha = 0.25f),
            focusedLabelColor = Appcolors.Primary,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StateDropdownField(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("State") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Appcolors.Primary,
                unfocusedBorderColor = Appcolors.TextSecondary.copy(alpha = 0.25f),
                focusedLabelColor = Appcolors.Primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            indianStates.forEach { state ->
                DropdownMenuItem(
                    text = { Text(state) },
                    onClick = {
                        onSelect(state)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SafeInfoNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Appcolors.iconBgPurple)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            Icons.Default.Shield, null,
            tint = Appcolors.Primary,
            modifier = Modifier.size(18.dp).padding(top = 2.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "Your information is safe with us",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Appcolors.Text,
            )
            Text(
                "We use bank-level encryption to keep your data secure.",
                fontSize = 11.sp,
                color = Appcolors.TextSecondary,
            )
        }
    }
}

@Composable
private fun ApplicationBottomBar(
    step: Int,
    loading: Boolean,
    onPrimaryClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Appcolors.Card)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        if (step > 1) {
            AppButton(text = "Back", onClick = onBackClick)
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = onPrimaryClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Appcolors.Primary),
            enabled = !loading,
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (step < 3) "Continue" else "Submit Application",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Lock, null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                "100% Secure  •  No Hidden Charges  •  RBI Compliant",
                fontSize = 11.sp,
                color = Appcolors.TextSecondary,
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
                trailing?.invoke()
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Appcolors.Primary else Appcolors.Background)
            .border(
                width = 1.dp,
                color = if (selected) Appcolors.Primary else Appcolors.TextSecondary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp),
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else Appcolors.Text,
        )
    }
}