package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Candy-Crush style 3D Glossy Jelly Button with top shine, inner bevel, and bouncy press physics.
 */
@Composable
fun CandyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: List<Color> = listOf(Color(0xFFFF2E93), Color(0xFFD81B60), Color(0xFFAD1457)),
    borderColor: Color = Color(0xFFFF94C2),
    shadowColor: Color = Color(0xFF880E4F),
    cornerRadius: Dp = 22.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scaleAnim.animateTo(0.92f, tween(60, easing = FastOutSlowInEasing))
        } else {
            scaleAnim.animateTo(1.04f, tween(100, easing = FastOutSlowInEasing))
            scaleAnim.animateTo(1.0f, tween(80, easing = LinearEasing))
        }
    }

    Box(
        modifier = modifier
            .scale(scaleAnim.value)
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = shadowColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Brush.verticalGradient(gradient))
            .border(2.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Top 3D glossy reflection highlight arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val glossPath = Path().apply {
                moveTo(cornerRadius.toPx(), 2f)
                lineTo(w - cornerRadius.toPx(), 2f)
                quadraticBezierTo(w - 2f, 2f, w - 2f, h * 0.45f)
                quadraticBezierTo(w * 0.5f, h * 0.58f, 2f, h * 0.45f)
                quadraticBezierTo(2f, 2f, cornerRadius.toPx(), 2f)
                close()
            }
            drawPath(
                path = glossPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.50f),
                        Color.White.copy(alpha = 0.08f)
                    )
                )
            )
        }

        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Circular 3D Glossy Candy Button for Back, Settings, Restart.
 */
@Composable
fun CandyCircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    iconSize: Dp = 24.dp,
    gradient: List<Color> = listOf(Color(0xFFFF3399), Color(0xFFD81B60), Color(0xFF9C1349)),
    iconColor: Color = Color.White,
    borderColor: Color = Color(0xFFFF99CC),
    testTag: String = ""
) {
    CandyButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .testTag(testTag),
        gradient = gradient,
        borderColor = borderColor,
        cornerRadius = size / 2
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Candy-Crush style Glossy Pill Badge for Level Header, Paths Left, and Timer.
 */
@Composable
fun CandyPillBadge(
    modifier: Modifier = Modifier,
    gradient: List<Color> = listOf(Color(0xFFFF2A85), Color(0xFFD81B60), Color(0xFF880E4F)),
    borderColor: Color = Color(0xFFFFB6D9),
    cornerRadius: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = Color(0x44000000)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Brush.verticalGradient(gradient))
            .border(1.8.dp, borderColor, RoundedCornerShape(cornerRadius))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Top 3D glossy highlight
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                topLeft = Offset(2f, 2f),
                size = Size(w - 4f, h * 0.48f),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )
        }

        content()
    }
}

/**
 * 3D Candy Jelly Hearts with glossy shine reflection and floating life animation!
 */
@Composable
fun CandyJellyHeart(
    isAlive: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val floatAnim = remember { Animatable(0f) }

    LaunchedEffect(isAlive) {
        if (isAlive) {
            floatAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200 + index * 200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    val yOffset = if (isAlive) (-floatAnim.value * 3.5f).dp else 0.dp
    val scale = if (isAlive) 1f + floatAnim.value * 0.06f else 0.88f

    Box(
        modifier = modifier
            .offset(y = yOffset)
            .scale(scale)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        if (isAlive) {
            // Candy Glow & 3D Layered Jelly Heart
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = this.size.width
                val h = this.size.height

                // Draw outer 3D shadow heart
                val heartPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.88f)
                    cubicTo(w * 0.10f, h * 0.60f, 0f, h * 0.32f, 0f, h * 0.20f)
                    cubicTo(0f, 0f, w * 0.28f, 0f, w * 0.5f, h * 0.24f)
                    cubicTo(w * 0.72f, 0f, w, 0f, w, h * 0.20f)
                    cubicTo(w, h * 0.32f, w * 0.90f, h * 0.60f, w * 0.5f, h * 0.88f)
                    close()
                }

                // Vibrant 3D Jelly Gradient
                drawPath(
                    path = heartPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF4081),
                            Color(0xFFE91E63),
                            Color(0xFFB71C1C)
                        )
                    )
                )

                // Inner Gloss Highlight Crescent (Top Left)
                drawCircle(
                    color = Color.White.copy(alpha = 0.75f),
                    radius = w * 0.12f,
                    center = Offset(w * 0.32f, h * 0.24f)
                )

                // White rim highlight
                drawPath(
                    path = heartPath,
                    color = Color(0xFFFF80AB),
                    style = Stroke(width = 2.5f)
                )
            }
        } else {
            // Frosted / Broken Grey Heart
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Lost Heart",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(size)
            )
        }
    }
}
