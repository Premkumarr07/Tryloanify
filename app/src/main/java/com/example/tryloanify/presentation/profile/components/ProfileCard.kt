package com.example.tryloanify.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun ProfileCard(
    name: String,
    phone: String,
    email: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Appcolors.Card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar + edit badge overlaid in ONE box
            Box(modifier = Modifier.size(72.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(color = Appcolors.avatar, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitials(name),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Appcolors.white
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .background(color = Appcolors.nametext, CircleShape)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile",
                        tint = Appcolors.white,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Appcolors.profilename,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = phone,
                    fontSize = 14.sp,
                    color = Appcolors.cloudyindigo,
                    maxLines = 1
                )
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Appcolors.cloudyindigo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    StatusChip(
                        text = "KYC Verified",
                        icon = Icons.Outlined.VerifiedUser,
                        background = Appcolors.backgroudcard,
                        textColor = Appcolors.greencolor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(
                        text = "Secure",
                        icon = Icons.Outlined.Shield,
                        background = Appcolors.backgroudcard,
                        textColor = Appcolors.origin
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Appcolors.rockblue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun getInitials(name: String): String =
    name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }