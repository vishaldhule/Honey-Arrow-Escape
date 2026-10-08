package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.model.GameTheme
import kotlin.math.min

@Composable
fun PlayfulKidsBackground(
    theme: GameTheme,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (!theme.isDark) {
                // 1. Gentle warm vanilla cream cloud backdrop (lets the golden-yellow board shine!)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFDF5), // Pure warm vanilla
                            Color(0xFFFFFBEB), // Soft milky cream
                            Color(0xFFFEF3C7)  // Gentle warm pastel tone
                        )
                    )
                )

                // 2. Tactile subtle playful paper-grain fibers & micro-dots
                val dotSpacing = 28f
                var x = 14f
                while (x < w) {
                    var y = 14f
                    while (y < h) {
                        val hash = ((x.toInt() * 73856093) xor (y.toInt() * 19349663) xor 31337)
                        val isFleck = (hash and 7) == 0
                        val isHighlight = (hash and 13) == 1
                        val offsetX = (((hash shr 8) and 7) - 3.5f) * 0.8f
                        val offsetY = (((hash shr 11) and 7) - 3.5f) * 0.8f

                        if (isFleck) {
                            drawCircle(
                                color = Color(0xFFD97706).copy(alpha = 0.065f),
                                radius = 1.15f,
                                center = Offset(x + offsetX, y + offsetY)
                            )
                        } else if (isHighlight) {
                            drawCircle(
                                color = Color(0xFFFFFFFF).copy(alpha = 0.25f),
                                radius = 1.3f,
                                center = Offset(x + offsetX, y + offsetY)
                            )
                        }

                        // Organic paper-grain fiber
                        if ((hash and 11) == 0) {
                            val fiberLength = 6f
                            val angle = if ((hash and 1) == 0) 0.785f else -0.785f
                            val dx = kotlin.math.cos(angle) * fiberLength
                            val dy = kotlin.math.sin(angle) * fiberLength
                            drawLine(
                                color = Color(0xFFB45309).copy(alpha = 0.045f),
                                start = Offset(x + offsetX, y + offsetY),
                                end = Offset(x + offsetX + dx, y + offsetY + dy),
                                strokeWidth = 1f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                        y += dotSpacing
                    }
                    x += dotSpacing
                }

                // 3. Playful illustrated soft cartoon clouds in corners
                drawCuteCloud(
                    centerX = w * 0.14f,
                    centerY = h * 0.07f,
                    baseRadius = min(w, h) * 0.065f,
                    cloudColor = Color.White.copy(alpha = 0.70f)
                )
                drawCuteCloud(
                    centerX = w * 0.86f,
                    centerY = h * 0.10f,
                    baseRadius = min(w, h) * 0.060f,
                    cloudColor = Color.White.copy(alpha = 0.65f)
                )
                drawCuteCloud(
                    centerX = w * 0.10f,
                    centerY = h * 0.92f,
                    baseRadius = min(w, h) * 0.065f,
                    cloudColor = Color.White.copy(alpha = 0.60f)
                )
                drawCuteCloud(
                    centerX = w * 0.88f,
                    centerY = h * 0.90f,
                    baseRadius = min(w, h) * 0.070f,
                    cloudColor = Color.White.copy(alpha = 0.65f)
                )

                // 4. Whimsical 4-point golden sparkle stars
                val starColor = Color(0xFFF59E0B).copy(alpha = 0.45f)
                drawSparkleStar(Offset(w * 0.28f, h * 0.05f), size = 16f, color = starColor)
                drawSparkleStar(Offset(w * 0.72f, h * 0.06f), size = 14f, color = starColor)
                drawSparkleStar(Offset(w * 0.92f, h * 0.20f), size = 18f, color = starColor)
                drawSparkleStar(Offset(w * 0.07f, h * 0.25f), size = 16f, color = starColor)
                drawSparkleStar(Offset(w * 0.22f, h * 0.95f), size = 18f, color = starColor)
                drawSparkleStar(Offset(w * 0.78f, h * 0.94f), size = 16f, color = starColor)

                // 5. Cheerful soft pastel confetti bubbles
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.18f),
                    radius = 12f,
                    center = Offset(w * 0.06f, h * 0.15f)
                )
                drawCircle(
                    color = Color(0xFFFB7185).copy(alpha = 0.18f),
                    radius = 10f,
                    center = Offset(w * 0.93f, h * 0.16f)
                )
                drawCircle(
                    color = Color(0xFF34D399).copy(alpha = 0.18f),
                    radius = 14f,
                    center = Offset(w * 0.82f, h * 0.85f)
                )
                drawCircle(
                    color = Color(0xFFA78BFA).copy(alpha = 0.18f),
                    radius = 11f,
                    center = Offset(w * 0.16f, h * 0.85f)
                )
            } else {
                // Dark Theme: Deep celestial cosmos texture
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            theme.backgroundColor,
                            theme.surfaceColor,
                            theme.backgroundColor
                        )
                    )
                )

                val cosmicStarColor = Color(0xFFE0F2FE).copy(alpha = 0.25f)
                drawSparkleStar(Offset(w * 0.15f, h * 0.10f), size = 14f, color = cosmicStarColor)
                drawSparkleStar(Offset(w * 0.85f, h * 0.15f), size = 18f, color = cosmicStarColor)
                drawSparkleStar(Offset(w * 0.20f, h * 0.88f), size = 16f, color = cosmicStarColor)
                drawSparkleStar(Offset(w * 0.80f, h * 0.85f), size = 14f, color = cosmicStarColor)
            }
        }

        content()
    }
}

private fun DrawScope.drawCuteCloud(
    centerX: Float,
    centerY: Float,
    baseRadius: Float,
    cloudColor: Color
) {
    drawCircle(
        color = cloudColor,
        radius = baseRadius,
        center = Offset(centerX, centerY)
    )
    drawCircle(
        color = cloudColor,
        radius = baseRadius * 0.80f,
        center = Offset(centerX - baseRadius * 0.75f, centerY + baseRadius * 0.15f)
    )
    drawCircle(
        color = cloudColor,
        radius = baseRadius * 0.85f,
        center = Offset(centerX + baseRadius * 0.75f, centerY + baseRadius * 0.15f)
    )
}

private fun DrawScope.drawSparkleStar(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticTo(center.x, center.y, center.x + size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + size)
        quadraticTo(center.x, center.y, center.x - size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path = path, color = color)
}
