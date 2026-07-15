package com.example.tryloanify.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tryloanify.R
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun AuthTopBar(
    onBackClick: () -> Unit = {},
    showBack: Boolean = true,
    onHelpClick: () -> Unit = {},
    showHelp: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()                 // standalone — no Scaffold
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // ── LEFT: Back button ─────────────────────────────────────────────
        if (showBack) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp, Appcolors.BorderColor), RoundedCornerShape(10.dp)),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Appcolors.TextPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        } else {
            Spacer(Modifier.size(36.dp))
        }

        // ── RIGHT: Help chip ──────────────────────────────────────────────
        if (showHelp) {
            HelpChip(onClick = onHelpClick)
        } else {
            Spacer(Modifier.size(36.dp))
        }
    }
}

@Composable
fun MainTopBar(
    title: String = "",
    // Left — Menu (default) or Back for inner screens
    showMenu: Boolean = true,
    onMenuClick: () -> Unit = {},
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    // Right — Bell notification
    showBell: Boolean = true,
    onBellClick: () -> Unit = {},
    hasUnread: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            // No statusBarsPadding here — Scaffold handles it via WindowInsets
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // ── LEFT: Menu or Back ────────────────────────────────────────────
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            when {
                showBack -> {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, Appcolors.BorderColor), RoundedCornerShape(10.dp)),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Appcolors.TextPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                showMenu -> {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, Appcolors.BorderColor), RoundedCornerShape(10.dp)),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.menu),
                            contentDescription = "Menu",
                            tint = Appcolors.TextPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                else -> Spacer(Modifier.size(36.dp))
            }
        }

        // ── CENTER: Title ─────────────────────────────────────────────────
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Appcolors.TextPrimary,
        )

        // ── RIGHT: Bell ───────────────────────────────────────────────────
        if (showBell) {
            Box {
                IconButton(
                    onClick = onBellClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp, Appcolors.BorderColor), RoundedCornerShape(10.dp)),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notification),
                        contentDescription = "Notifications",
                        tint = Appcolors.TextPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                if (hasUnread) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Appcolors.white, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp),
                    ) {
                        Surface(
                            color = Color(0xFFDC2626),
                            shape = CircleShape,
                            modifier = Modifier.fillMaxSize(),
                        ) {}
                    }
                }
            }
        } else {
            Spacer(Modifier.size(36.dp))
        }
    }
}


// ─── Shared Help chip (used only by AuthTopBar) ────────────────────────────
@Composable
private fun HelpChip(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .height(34.dp)
            .border(BorderStroke(1.dp, Appcolors.BorderColor), RoundedCornerShape(18.dp)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.textButtonColors(containerColor = Appcolors.white),
    ) {
        Text(
            text = "Help",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Appcolors.TextSecondary,
        )
        Spacer(Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .border(1.dp, Appcolors.PrimaryAppbar, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "?",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Appcolors.PrimaryAppbar,
            )
        }
    }
}