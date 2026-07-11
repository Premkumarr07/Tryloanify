package com.example.tryloanify.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tryloanify.presentation.common.formatCurrency
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Appcolors.TextSecondary, fontSize = 14.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Appcolors.Border, RoundedCornerShape(14.dp))
                .background(Appcolors.Surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Appcolors.Text,
                fontSize = 16.sp,
            ),
            cursorBrush = SolidColor(Appcolors.Primary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
        )
    }
}

@Composable
fun OtpInput(
    otp: String,
    onOtpChange: (String) -> Unit,
    length: Int = 6,
) {
    LaunchedEffect(otp) {
        if (otp.length > length) onOtpChange(otp.take(length))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(length) { index ->
            val char = otp.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, if (char.isNotEmpty()) Appcolors.Primary else Appcolors.Border, RoundedCornerShape(12.dp))
                    .background(Appcolors.Surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = char, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
            }
        }
    }
    BasicTextField(
        value = otp,
        onValueChange = { if (it.length <= length && it.all { c -> c.isDigit() }) onOtpChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    )
}

@Composable
fun StepProgress(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < currentStep) Appcolors.Primary else Appcolors.Border),
            )
        }
    }
}

@Composable
fun AmountSlider(
    amount: Float,
    onAmountChange: (Float) -> Unit,
    min: Float = 10_000f,
    max: Float = 500_000f,
) {
    Column {
        Text(
            text = formatCurrency(amount.toDouble()),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Appcolors.Primary,
        )
        Slider(
            value = amount,
            onValueChange = onAmountChange,
            valueRange = min..max,
            steps = 49,
            colors = SliderDefaults.colors(
                thumbColor = Appcolors.Primary,
                activeTrackColor = Appcolors.Primary,
            ),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatCurrency(min.toDouble()), color = Appcolors.Hint, fontSize = 12.sp)
            Text(formatCurrency(max.toDouble()), color = Appcolors.Hint, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatusBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun LoanSummaryCard(
    title: String,
    amount: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Appcolors.Surface)
            .border(1.dp, Appcolors.Border, RoundedCornerShape(18.dp))
            .padding(20.dp),
    ) {
        Text(title, color = Appcolors.TextSecondary, fontSize = 14.sp)
        Text(amount, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Appcolors.Text)
        Text(subtitle, color = Appcolors.Hint, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ErrorView(message: String, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, color = Appcolors.Error, textAlign = TextAlign.Center)
        if (onRetry != null) {
            AppButton(text = "Retry", onClick = onRetry, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun ConsentCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Appcolors.Primary),
        )
        Text(label, color = Appcolors.TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun KfsScrollPanel(content: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Appcolors.Border, RoundedCornerShape(14.dp))
            .background(Appcolors.Card)
            .padding(16.dp),
    ) {
        Text(text = content, color = Appcolors.Text, fontSize = 13.sp, lineHeight = 20.sp)
    }
}
