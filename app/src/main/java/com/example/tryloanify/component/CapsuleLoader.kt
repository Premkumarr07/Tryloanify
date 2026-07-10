package com.example.tryloanify.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CapsuleLoader(
    modifier: Modifier = Modifier,
    width: Dp = 110.dp,
    height: Dp = 8.dp
) {

    val transition = rememberInfiniteTransition(label = "loader")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = FastOutSlowInEasing            )
        ),
        label = ""
    )

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .width(width)
            .height(height)
    ) {

        val totalWidth = with(density) { maxWidth.toPx() }
        val totalHeight = with(density) { maxHeight.toPx() }
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val radius = size.height / 2

            // Dark Track
            drawRoundRect(
                color = Color(0xFF2A236B),
                cornerRadius = CornerRadius(radius, radius)
            )

            // Animated Gradient
            val gradientOffset = size.width * offset

            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4C1DFF),
                        Color(0xFF6E33FF),
                        Color(0xFF8B5CF6),
                        Color(0xFFA78BFA),
                        Color(0xFF8B5CF6),
                        Color(0xFF6E33FF),
                        Color(0xFF4C1DFF)
                    ),
                    start = Offset(
                        gradientOffset - size.width,
                        0f
                    ),
                    end = Offset(
                        gradientOffset + size.width,
                        0f
                    ),
                    tileMode = TileMode.Mirror
                ),
                cornerRadius = CornerRadius(radius, radius)
            )

            // Moving White Capsule
            val capsuleWidth = size.width * 0.18f

            val capsuleHeight = size.height * .65f

            val capsuleY = (size.height - capsuleHeight) / 2

            val capsuleX =
                ((size.width + capsuleWidth) * offset) - capsuleWidth

            // Glow
            drawRoundRect(
                color = Color.White.copy(alpha = .25f),
                topLeft = Offset(
                    capsuleX - 2.dp.toPx(),
                    capsuleY - 1.dp.toPx()
                ),
                size = Size(
                    capsuleWidth + 4.dp.toPx(),
                    capsuleHeight + 2.dp.toPx()
                ),
                cornerRadius = CornerRadius(radius, radius)
            )

            // Highlight
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(
                    capsuleX,
                    capsuleY
                ),
                size = Size(
                    capsuleWidth,
                    capsuleHeight
                ),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}