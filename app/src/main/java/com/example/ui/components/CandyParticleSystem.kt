package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

enum class CartoonParticleType {
    CANDY_STAR,
    JELLY_BEAD,
    SPARKLE,
    CANDY_HEART,
    CANDY_CONFETTI
}

data class CartoonParticle(
    val id: Long,
    val type: CartoonParticleType,
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val targetWord: String? = null
)

class ParticleController {
    val particles = mutableStateListOf<CartoonParticle>()
    var popText by mutableStateOf<String?>(null)
    var popTextPos by mutableStateOf(Offset.Zero)
    private val rng = Random()
    private var nextId = 0L

    fun triggerJuicyPop(origin: Offset, comboStreak: Int = 1) {
        val count = 18 + rng.nextInt(8)
        val candyColors = listOf(
            Color(0xFFFF2A85), // Raspberry Pink
            Color(0xFFFFB703), // Golden Yellow
            Color(0xFF06D6A0), // Sweet Mint Green
            Color(0xFF118AB2), // Vibrant Blue
            Color(0xFF8338EC), // Candy Purple
            Color(0xFFFF5400)  // Sweet Orange
        )

        val words = listOf("SWEET!", "TASTY!", "AWESOME!", "GREAT!", "POP!", "CLEARED!")
        popText = words[(comboStreak - 1).coerceIn(0, words.size - 1)]
        popTextPos = origin

        for (i in 0 until count) {
            val angle = rng.nextDouble() * 2 * Math.PI
            val speed = (180f + rng.nextFloat() * 320f)
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed - 90f).toFloat() // Bias upwards

            val type = when (rng.nextInt(5)) {
                0 -> CartoonParticleType.CANDY_STAR
                1 -> CartoonParticleType.CANDY_HEART
                2 -> CartoonParticleType.SPARKLE
                3 -> CartoonParticleType.JELLY_BEAD
                else -> CartoonParticleType.CANDY_CONFETTI
            }

            val pSize = 12f + rng.nextFloat() * 16f
            val color = candyColors[rng.nextInt(candyColors.size)]
            val rotSpeed = (rng.nextFloat() - 0.5f) * 720f

            particles.add(
                CartoonParticle(
                    id = nextId++,
                    type = type,
                    startX = origin.x,
                    startY = origin.y,
                    vx = vx,
                    vy = vy,
                    size = pSize,
                    color = color,
                    rotationSpeed = rotSpeed
                )
            )
        }
    }

    fun clear() {
        particles.clear()
        popText = null
    }
}

@Composable
fun JuicyParticleOverlay(
    controller: ParticleController,
    modifier: Modifier = Modifier
) {
    if (controller.particles.isEmpty() && controller.popText == null) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(controller.particles.size) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = LinearEasing)
        )
        controller.clear()
    }

    val t = progress.value
    if (t >= 1f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val gravity = 480f

        // Draw Juicy Floating Cartoon Particles
        for (p in controller.particles) {
            val currentX = p.startX + p.vx * t
            val currentY = p.startY + p.vy * t + 0.5f * gravity * t * t

            val alpha = (1f - t).coerceIn(0f, 1f)
            val pScale = if (t < 0.2f) (t / 0.2f) * 1.2f else ((1f - t) / 0.8f)
            val currentRotation = p.rotationSpeed * t

            rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                scale(scale = pScale, pivot = Offset(currentX, currentY)) {
                    when (p.type) {
                        CartoonParticleType.CANDY_STAR -> {
                            drawCandyStar(
                                center = Offset(currentX, currentY),
                                radius = p.size,
                                color = p.color.copy(alpha = alpha)
                            )
                        }
                        CartoonParticleType.CANDY_HEART -> {
                            drawMiniCandyHeart(
                                center = Offset(currentX, currentY),
                                radius = p.size,
                                color = p.color.copy(alpha = alpha)
                            )
                        }
                        CartoonParticleType.JELLY_BEAD -> {
                            drawJellyBead(
                                center = Offset(currentX, currentY),
                                radius = p.size * 0.75f,
                                color = p.color.copy(alpha = alpha)
                            )
                        }
                        CartoonParticleType.SPARKLE -> {
                            drawSparkleGlimmer(
                                center = Offset(currentX, currentY),
                                radius = p.size * 0.85f,
                                color = Color.White.copy(alpha = alpha)
                            )
                        }
                        CartoonParticleType.CANDY_CONFETTI -> {
                            drawRoundRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(currentX - p.size * 0.5f, currentY - p.size * 0.3f),
                                size = Size(p.size, p.size * 0.6f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCandyStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val numPoints = 5
    val outerR = radius
    val innerR = radius * 0.45f

    for (i in 0 until numPoints * 2) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = i * Math.PI / numPoints - Math.PI / 2.0
        val x = center.x + (cos(angle) * r).toFloat()
        val y = center.y + (sin(angle) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
    // White center highlight
    drawCircle(Color.White.copy(alpha = color.alpha * 0.7f), radius = radius * 0.22f, center = center)
}

private fun DrawScope.drawMiniCandyHeart(center: Offset, radius: Float, color: Color) {
    val w = radius * 1.8f
    val h = radius * 1.8f
    val path = Path().apply {
        moveTo(center.x, center.y + h * 0.45f)
        cubicTo(center.x - w * 0.5f, center.y + h * 0.15f, center.x - w * 0.5f, center.y - h * 0.35f, center.x - w * 0.22f, center.y - h * 0.45f)
        cubicTo(center.x - w * 0.05f, center.y - h * 0.45f, center.x, center.y - h * 0.25f, center.x, center.y - h * 0.25f)
        cubicTo(center.x, center.y - h * 0.25f, center.x + w * 0.05f, center.y - h * 0.45f, center.x + w * 0.22f, center.y - h * 0.45f)
        cubicTo(center.x + w * 0.5f, center.y - h * 0.35f, center.x + w * 0.5f, center.y + h * 0.15f, center.x, center.y + h * 0.45f)
        close()
    }
    drawPath(path, color)
    drawCircle(Color.White.copy(alpha = color.alpha * 0.8f), radius = radius * 0.18f, center = Offset(center.x - radius * 0.22f, center.y - radius * 0.18f))
}

private fun DrawScope.drawJellyBead(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = color.alpha), color),
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawSparkleGlimmer(center: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x, center.y, center.x + radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + radius)
        quadraticTo(center.x, center.y, center.x - radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - radius)
        close()
    }
    drawPath(path, color)
}
