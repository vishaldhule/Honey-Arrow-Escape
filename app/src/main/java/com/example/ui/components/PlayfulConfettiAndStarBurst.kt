package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

enum class ParticleShape {
    STAR,
    DIAMOND,
    RECTANGLE,
    CIRCLE
}

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    var flipAngle: Float,
    val flipSpeed: Float,
    val size: Float,
    val color: Color,
    val shape: ParticleShape,
    val wobbleSpeed: Float,
    var wobbleOffset: Float,
    var alpha: Float = 1.0f,
    val maxLife: Float,
    var life: Float = 0f
)

@Composable
fun PlayfulConfettiAndStarBurst(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val particles = remember { mutableStateListOf<ConfettiParticle>() }

    LaunchedEffect(trigger) {
        val rng = Random()
        particles.clear()

        val confettiColors = listOf(
            Color(0xFFFF2D55), // Hot Rose
            Color(0xFFFF9500), // Tangerine Orange
            Color(0xFFFFCC00), // Sun Gold
            Color(0xFF34C759), // Emerald Lime
            Color(0xFF00C7BE), // Bright Cyan
            Color(0xFF007AFF), // Electric Blue
            Color(0xFFAF52DE), // Lavender Purple
            Color(0xFFFF375F), // Coral Ruby
            Color(0xFFFEF08A)  // Soft Butter Yellow
        )

        // 1. Center Star-Burst Explosion (35 radiant stars & diamonds)
        for (i in 0 until 40) {
            val angle = rng.nextFloat() * 2f * PI.toFloat()
            val speed = 9f + rng.nextFloat() * 16f
            val shape = if (rng.nextBoolean()) ParticleShape.STAR else ParticleShape.DIAMOND
            val color = if (rng.nextFloat() < 0.6f) Color(0xFFFBBF24) else confettiColors[rng.nextInt(confettiColors.size)]

            particles.add(
                ConfettiParticle(
                    x = 0.5f, // normalized center (updated in draw)
                    y = 0.45f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 6f, // slight upward bias
                    rotation = rng.nextFloat() * 360f,
                    rotationSpeed = (rng.nextFloat() - 0.5f) * 16f,
                    flipAngle = rng.nextFloat() * 360f,
                    flipSpeed = (rng.nextFloat() - 0.5f) * 12f,
                    size = 14f + rng.nextFloat() * 12f,
                    color = color,
                    shape = shape,
                    wobbleSpeed = 2f + rng.nextFloat() * 3f,
                    wobbleOffset = rng.nextFloat() * 10f,
                    maxLife = 140f + rng.nextFloat() * 60f
                )
            )
        }

        // 2. Left and Right Cannon Confetti Showers (60 flutter ribbons)
        for (i in 0 until 65) {
            val isLeftCannon = i % 2 == 0
            val startX = if (isLeftCannon) 0.12f else 0.88f
            val startY = 0.85f

            val baseAngle = if (isLeftCannon) -0.95f else -2.19f // angled up & inward
            val spreadAngle = baseAngle + (rng.nextFloat() - 0.5f) * 0.75f
            val speed = 14f + rng.nextFloat() * 18f

            val shape = when (rng.nextInt(4)) {
                0 -> ParticleShape.STAR
                1 -> ParticleShape.DIAMOND
                2 -> ParticleShape.RECTANGLE
                else -> ParticleShape.CIRCLE
            }

            particles.add(
                ConfettiParticle(
                    x = startX,
                    y = startY,
                    vx = cos(spreadAngle) * speed,
                    vy = sin(spreadAngle) * speed,
                    rotation = rng.nextFloat() * 360f,
                    rotationSpeed = (rng.nextFloat() - 0.5f) * 14f,
                    flipAngle = rng.nextFloat() * 360f,
                    flipSpeed = (rng.nextFloat() - 0.5f) * 15f,
                    size = 11f + rng.nextFloat() * 14f,
                    color = confettiColors[rng.nextInt(confettiColors.size)],
                    shape = shape,
                    wobbleSpeed = 3f + rng.nextFloat() * 4f,
                    wobbleOffset = rng.nextFloat() * 10f,
                    maxLife = 160f + rng.nextFloat() * 70f
                )
            )
        }

        // Animation frame loop
        var lastTime = withFrameMillis { it }
        while (particles.isNotEmpty()) {
            val currentTime = withFrameMillis { it }
            val dt = ((currentTime - lastTime) / 16.666f).coerceIn(0.5f, 2.0f)
            lastTime = currentTime

            for (p in particles) {
                p.life += dt
                p.x += (p.vx * dt)
                p.y += (p.vy * dt)
                p.vy += (0.42f * dt) // Gravity
                p.vx *= (1f - 0.015f * dt) // Air drag
                p.rotation += (p.rotationSpeed * dt)
                p.flipAngle += (p.flipSpeed * dt)
                p.wobbleOffset += (p.wobbleSpeed * 0.05f * dt)

                // Gentle horizontal wobble (flutter breeze effect)
                p.x += (sin(p.wobbleOffset) * 0.85f * dt)

                // Fade out near end of life
                val progress = p.life / p.maxLife
                if (progress > 0.65f) {
                    p.alpha = ((1f - progress) / 0.35f).coerceIn(0f, 1f)
                }
            }

            // Remove expired particles
            particles.removeAll { it.life >= it.maxLife || it.alpha <= 0.01f }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_particle_canvas")
    ) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            // If coordinates were initial normalized fraction, scale to pixels
            val px = if (p.x in 0f..1.0f && p.life <= 1f) p.x * w else p.x
            val py = if (p.y in 0f..1.0f && p.life <= 1f) p.y * h else p.y

            val currentAlpha = p.alpha.coerceIn(0f, 1f)
            val pColor = p.color.copy(alpha = currentAlpha)

            // 3D tumble flip scale: cos(flipAngle) mimics paper flipping over in the wind!
            val flipScale = cos(p.flipAngle * (PI / 180f)).toFloat()
            val effectiveHeight = (p.size * abs(flipScale)).coerceAtLeast(1.5f)

            rotate(degrees = p.rotation, pivot = Offset(px, py)) {
                when (p.shape) {
                    ParticleShape.STAR -> {
                        drawStar(
                            center = Offset(px, py),
                            radius = p.size * 0.75f,
                            color = pColor
                        )
                    }

                    ParticleShape.DIAMOND -> {
                        val diamondPath = Path().apply {
                            moveTo(px, py - effectiveHeight)
                            lineTo(px + p.size * 0.65f, py)
                            lineTo(px, py + effectiveHeight)
                            lineTo(px - p.size * 0.65f, py)
                            close()
                        }
                        drawPath(diamondPath, color = pColor)
                    }

                    ParticleShape.RECTANGLE -> {
                        drawRoundRect(
                            color = pColor,
                            topLeft = Offset(px - p.size * 0.5f, py - effectiveHeight * 0.5f),
                            size = Size(p.size, effectiveHeight)
                        )
                    }

                    ParticleShape.CIRCLE -> {
                        drawCircle(
                            color = pColor,
                            radius = p.size * 0.45f,
                            center = Offset(px, py)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws a crisp 5-point celebratory star at the given center.
 */
private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    val innerRadius = radius * 0.42f
    val points = 5
    var angle = -PI / 2.0
    val step = PI / points

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        angle += step
    }
    path.close()
    drawPath(path, color = color)
}
