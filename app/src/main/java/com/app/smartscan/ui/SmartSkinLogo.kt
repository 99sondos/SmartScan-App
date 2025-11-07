package com.app.smartscan.ui
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.app.smartscan.R


@Composable
fun SmartSkinLogo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8F2)), // soft beige background
        contentAlignment = Alignment.Center
    ) {
        // --- Logo Text ---
        Text(
            text = "SmartSkin",
            fontSize = 36.sp,
            color = Color(0xFF6B8E6E),
            fontFamily = FontFamily.Serif
        )

        // --- Animation setup ---
        val scope = rememberCoroutineScope()
        val offsetY = remember { Animatable(-120f) }  // start above
        val offsetX = remember { Animatable(60f) }    // start to the right

        // Gentle infinite side-to-side sway (rotation)
        val infiniteRotation = rememberInfiniteTransition(label = "")
        val rotationAngle by infiniteRotation.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )

        // --- Launch animations once ---
        LaunchedEffect(Unit) {
            // Falling motion
            scope.launch {
                offsetY.animateTo(
                    targetValue = 25f, // landing Y
                    animationSpec = tween(durationMillis = 2200, easing = LinearOutSlowInEasing)
                )

                // Small bounce after landing
                delay(100)
                offsetY.animateTo(
                    targetValue = 15f,
                    animationSpec = tween(durationMillis = 400, easing = EaseOutBounce)
                )
            }

            // Horizontal movement (towards "i")
            scope.launch {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 2200, easing = LinearOutSlowInEasing)
                )
            }
        }

        // --- Leaf Image ---
        Image(
            painter = painterResource(id = R.drawable.leaf),
            contentDescription = "Falling leaf",
            modifier = Modifier
                .size(40.dp)
                .offset(
                    x = offsetX.value.dp + 70.dp, // adjust horizontal position to land on "i"
                    y = offsetY.value.dp - 30.dp  // adjust vertical offset
                )
                .rotate(rotationAngle) // gentle sway rotation
        )
    }
}
