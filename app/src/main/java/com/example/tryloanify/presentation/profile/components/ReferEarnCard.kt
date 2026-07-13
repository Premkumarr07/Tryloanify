package com.example.tryloanify.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun ReferEarnCard(
    onReferClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Appcolors.referBackground, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Refer & Earn Rewards",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Appcolors.Primary
            )
            Text(
                text = "Refer your friends and earn exciting rewards",
                fontSize = 12.sp,
                color = Appcolors.TextSecondary
            )
        }

        Button(
            onClick = onReferClick,
            colors = ButtonDefaults.buttonColors(containerColor = Appcolors.Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Refer Now", color = Appcolors.white, fontSize = 13.sp)
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Appcolors.white,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}