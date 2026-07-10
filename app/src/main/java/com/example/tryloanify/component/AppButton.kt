package com.example.tryloanify.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AppButton(

    text: String,

    modifier: Modifier = Modifier,

    loading: Boolean = false,

    enabled: Boolean = true,

    onClick: () -> Unit

) {

    val interaction = remember {
        MutableInteractionSource()
    }

    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(

        targetValue = if (pressed) .97f else 1f,

        label = ""

    )

    val gradient = Brush.horizontalGradient(

        colors = listOf(

            Color(0xFF5B2EFF),

            Color(0xFF6C3DFF),

            Color(0xFF7E52FF)

        )

    )

    Box(

        modifier = modifier

            .fillMaxWidth()

            .height(58.dp)

            .scale(scale)

            .clip(RoundedCornerShape(16.dp))

            .background(

                if (enabled)

                    gradient

                else

                    Brush.horizontalGradient(

                        listOf(

                            Color.LightGray,

                            Color.Gray

                        )

                    )

            )

            .clickable(

                enabled = enabled && !loading,

                interactionSource = interaction,

                indication = null

            ) {

                onClick()

            },

        contentAlignment = Alignment.Center

    ) {

        if (loading) {

            CircularProgressIndicator(

                modifier = Modifier.size(22.dp),

                strokeWidth = 2.dp,

                color = Color.White

            )

        }

        else {

            Text(

                text = text,

                color = Color.White,

                fontWeight = FontWeight.Bold,

                fontSize = 17.sp

            )

        }

    }

}