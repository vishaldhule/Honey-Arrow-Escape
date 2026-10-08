package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D Pixar/Disney style animated baby honey bee mascot matching the user's reference character sheet.
 * Features:
 * - Chubby golden head with 3D gradient lighting & soft peach-pink blushing cheeks
 * - Huge soulful glossy amber/chocolate Pixar eyes with dual white specular highlights & eyelashes
 * - Fluffy white/cream neck collar ruff between head and abdomen
 * - Chubby striped bee body with glossy black and golden honey stripes
 * - Curving antennae with glowing yellow round bulb tips
 * - Animated fluttering translucent cyan/sky-blue fairy wings
 * - Animated cheerful waving hand
 * - Floating hover bobbing & natural eye blinking
 */
@Composable
fun PixarHoneyBeeMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 140.dp,
    onClick: (() -> Unit)? = null
) {
    // 1. Hovering Bobbing Animation
    val hoverAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        val duration = when (mood) {
            MascotMood.CELEBRATING -> 360
            MascotMood.CHEERING -> 480
            MascotMood.CRYING_SAD -> 1400
            else -> 1100
        }
        hoverAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // 2. High-speed Wing Fluttering Animation
    val wingFlutter = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        wingFlutter.animateTo(
            targetValue = 0.28f,
            animationSpec = infiniteRepeatable(
                animation = tween(90, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // 3. Cute Hand Waving Animation
    val waveAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood != MascotMood.CRYING_SAD) {
            waveAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(420, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            waveAnim.snapTo(0f)
        }
    }

    // 4. Springy Antenna Sway Animation
    val antennaSway = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        antennaSway.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(750, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // 5. Natural Eye Blinking Animation
    val blinkAnim = remember { Animatable(1f) }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(3200)
            blinkAnim.animateTo(0.08f, tween(70, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(90, easing = FastOutSlowInEasing))
        }
    }

    // 6. Tap Bounce Reaction
    val tapScale = remember { Animatable(1f) }
    val tapRotation = remember { Animatable(0f) }

    val isSad = mood == MascotMood.CRYING_SAD
    val isCelebrating = mood == MascotMood.CELEBRATING
    val isCheering = mood == MascotMood.CHEERING

    val verticalFloat = when {
        isCelebrating -> (-hoverAnim.value * 16f).dp
        isCheering -> (-hoverAnim.value * 12f).dp
        isSad -> (hoverAnim.value * 4f).dp
        else -> (-hoverAnim.value * 8f).dp
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick()
        }
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .offset(y = verticalFloat)
            .scale(tapScale.value * (if (isCelebrating) 1.05f else 1.0f))
            .then(clickModifier)
            .testTag("pixar_honey_bee_mascot"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.50f
            val cy = h * 0.52f

            // -------------------------------------------------------------
            // 0. Soft Ground Shadow
            // -------------------------------------------------------------
            val shadowWidth = w * (0.55f + hoverAnim.value * 0.05f)
            val shadowHeight = h * 0.08f
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x35000000), Color(0x10000000), Color.Transparent),
                    center = Offset(cx, h * 0.94f),
                    radius = shadowWidth * 0.5f
                ),
                topLeft = Offset(cx - shadowWidth * 0.5f, h * 0.90f),
                size = Size(shadowWidth, shadowHeight)
            )

            // -------------------------------------------------------------
            // 1. Translucent Fairy Wings (Behind Body)
            // -------------------------------------------------------------
            drawPixarBeeWings(
                cx = cx,
                cy = cy - h * 0.08f,
                w = w,
                h = h,
                flutter = if (isSad) 0.5f else wingFlutter.value,
                isSad = isSad
            )

            // -------------------------------------------------------------
            // 2. Chubby Striped Bee Abdomen / Body
            // -------------------------------------------------------------
            val bodyW = w * 0.46f
            val bodyH = h * 0.44f
            val bodyCenterY = cy + h * 0.16f

            // A. Cute Little Pointed Stinger
            val stingerPath = Path().apply {
                moveTo(cx - w * 0.04f, bodyCenterY + bodyH * 0.42f)
                lineTo(cx, bodyCenterY + bodyH * 0.56f)
                lineTo(cx + w * 0.04f, bodyCenterY + bodyH * 0.42f)
                close()
            }
            drawPath(
                path = stingerPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF261C14), Color(0xFF150F0B)),
                    startY = bodyCenterY + bodyH * 0.42f,
                    endY = bodyCenterY + bodyH * 0.56f
                )
            )

            // B. Chubby Abdomen with 3D Radial Honey Shading
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFE65100)),
                    center = Offset(cx - bodyW * 0.15f, bodyCenterY - bodyH * 0.15f),
                    radius = bodyW * 0.65f
                ),
                topLeft = Offset(cx - bodyW * 0.5f, bodyCenterY - bodyH * 0.48f),
                size = Size(bodyW, bodyH)
            )

            // C. Curved 3D Black Bumblebee Stripes
            val stripeHeight = bodyH * 0.20f
            // Stripe 1 (Middle)
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1E1712), Color(0xFF3E2D22), Color(0xFF1E1712))
                ),
                topLeft = Offset(cx - bodyW * 0.46f, bodyCenterY - bodyH * 0.10f),
                size = Size(bodyW * 0.92f, stripeHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )
            // Stripe 2 (Lower)
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1E1712), Color(0xFF3E2D22), Color(0xFF1E1712))
                ),
                topLeft = Offset(cx - bodyW * 0.38f, bodyCenterY + bodyH * 0.16f),
                size = Size(bodyW * 0.76f, stripeHeight * 0.85f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // D. Cute Little Dark Feet
            val footR = w * 0.055f
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF4E342E), Color(0xFF211510)),
                    center = Offset(cx - bodyW * 0.28f, bodyCenterY + bodyH * 0.42f),
                    radius = footR * 1.2f
                ),
                topLeft = Offset(cx - bodyW * 0.38f, bodyCenterY + bodyH * 0.34f),
                size = Size(footR * 2f, footR * 1.4f)
            )
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF4E342E), Color(0xFF211510)),
                    center = Offset(cx + bodyW * 0.28f, bodyCenterY + bodyH * 0.42f),
                    radius = footR * 1.2f
                ),
                topLeft = Offset(cx + bodyW * 0.18f, bodyCenterY + bodyH * 0.34f),
                size = Size(footR * 2f, footR * 1.4f)
            )

            // -------------------------------------------------------------
            // 3. Fluffy Cream/White Fur Neck Ruff Collar
            // -------------------------------------------------------------
            val collarY = cy - h * 0.02f
            val ruffW = w * 0.52f
            val ruffH = h * 0.14f

            // Soft white/cream cloud-like scalloped fur
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFF9C4), Color(0xFFFFE082)),
                    center = Offset(cx, collarY),
                    radius = ruffW * 0.5f
                ),
                topLeft = Offset(cx - ruffW * 0.5f, collarY - ruffH * 0.5f),
                size = Size(ruffW, ruffH)
            )
            // Delicate fur tufts
            drawCircle(Color(0xFFFFFFFF), radius = ruffH * 0.48f, center = Offset(cx - ruffW * 0.28f, collarY + 2f))
            drawCircle(Color(0xFFFFFDE7), radius = ruffH * 0.52f, center = Offset(cx, collarY + 3f))
            drawCircle(Color(0xFFFFFFFF), radius = ruffH * 0.48f, center = Offset(cx + ruffW * 0.28f, collarY + 2f))

            // -------------------------------------------------------------
            // 4. Oversized Chubby Round Head (3D Pixar Gradient)
            // -------------------------------------------------------------
            val headW = w * 0.62f
            val headH = h * 0.52f
            val headCenterY = cy - h * 0.16f

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF176), // Bright top-left highlight
                        Color(0xFFFFD54F), // Warm honey yellow
                        Color(0xFFFFB300), // Rich golden bee
                        Color(0xFFF57C00)  // Deep warm amber shadow
                    ),
                    center = Offset(cx - headW * 0.18f, headCenterY - headH * 0.20f),
                    radius = headW * 0.65f
                ),
                topLeft = Offset(cx - headW * 0.5f, headCenterY - headH * 0.5f),
                size = Size(headW, headH)
            )

            // -------------------------------------------------------------
            // 5. Cute Curving Antennae with Glossy Golden Spheres
            // -------------------------------------------------------------
            val antennaBaseY = headCenterY - headH * 0.42f
            val swayOffset = (antennaSway.value - 0.5f) * 6f

            // Left Antenna
            val leftAntennaTip = Offset(cx - headW * 0.32f + swayOffset, headCenterY - headH * 0.78f)
            val leftPath = Path().apply {
                moveTo(cx - headW * 0.14f, antennaBaseY)
                cubicTo(
                    cx - headW * 0.16f, headCenterY - headH * 0.58f,
                    cx - headW * 0.28f, headCenterY - headH * 0.68f,
                    leftAntennaTip.x, leftAntennaTip.y
                )
            }
            drawPath(
                path = leftPath,
                color = Color(0xFF261C14),
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )
            // Left Antenna Golden Bulb with 3D highlight
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFCA28), Color(0xFFF57F17)),
                    center = Offset(leftAntennaTip.x - 2f, leftAntennaTip.y - 2f),
                    radius = 9f
                ),
                radius = 8.5f,
                center = leftAntennaTip
            )
            drawCircle(Color.White, radius = 2.5f, center = Offset(leftAntennaTip.x - 2.5f, leftAntennaTip.y - 2.5f))

            // Right Antenna
            val rightAntennaTip = Offset(cx + headW * 0.32f + swayOffset, headCenterY - headH * 0.78f)
            val rightPath = Path().apply {
                moveTo(cx + headW * 0.14f, antennaBaseY)
                cubicTo(
                    cx + headW * 0.16f, headCenterY - headH * 0.58f,
                    cx + headW * 0.28f, headCenterY - headH * 0.68f,
                    rightAntennaTip.x, rightAntennaTip.y
                )
            }
            drawPath(
                path = rightPath,
                color = Color(0xFF261C14),
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )
            // Right Antenna Golden Bulb with 3D highlight
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFCA28), Color(0xFFF57F17)),
                    center = Offset(rightAntennaTip.x - 2f, rightAntennaTip.y - 2f),
                    radius = 9f
                ),
                radius = 8.5f,
                center = rightAntennaTip
            )
            drawCircle(Color.White, radius = 2.5f, center = Offset(rightAntennaTip.x - 2.5f, rightAntennaTip.y - 2.5f))

            // -------------------------------------------------------------
            // 6. Chubby Rosy Cheeks Blush
            // -------------------------------------------------------------
            val blushR = headW * 0.14f
            val blushY = headCenterY + headH * 0.12f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFF8A80).copy(alpha = 0.65f), Color(0xFFFF8A80).copy(alpha = 0.20f), Color.Transparent),
                    center = Offset(cx - headW * 0.32f, blushY),
                    radius = blushR
                ),
                radius = blushR,
                center = Offset(cx - headW * 0.32f, blushY)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFF8A80).copy(alpha = 0.65f), Color(0xFFFF8A80).copy(alpha = 0.20f), Color.Transparent),
                    center = Offset(cx + headW * 0.32f, blushY),
                    radius = blushR
                ),
                radius = blushR,
                center = Offset(cx + headW * 0.32f, blushY)
            )

            // -------------------------------------------------------------
            // 7. Huge Soulful Pixar 3D Eyes (The Star of the Character Sheet!)
            // -------------------------------------------------------------
            val eyeSpacing = headW * 0.22f
            val eyeY = headCenterY - headH * 0.02f
            val eyeWidth = headW * 0.26f
            val eyeHeight = headH * 0.38f

            drawPixarEye(
                centerX = cx - eyeSpacing,
                centerY = eyeY,
                width = eyeWidth,
                height = eyeHeight,
                blink = blinkAnim.value,
                isLeft = true,
                isSad = isSad,
                isCheering = isCheering || isCelebrating
            )

            drawPixarEye(
                centerX = cx + eyeSpacing,
                centerY = eyeY,
                width = eyeWidth,
                height = eyeHeight,
                blink = blinkAnim.value,
                isLeft = false,
                isSad = isSad,
                isCheering = isCheering || isCelebrating
            )

            // Cute Arched Little Brown Eyebrows
            val browY = eyeY - eyeHeight * 0.62f
            val browPathLeft = Path().apply {
                moveTo(cx - eyeSpacing - eyeWidth * 0.40f, browY + (if (isSad) 4f else 0f))
                quadraticTo(cx - eyeSpacing, browY - (if (isSad) -2f else 6f), cx - eyeSpacing + eyeWidth * 0.35f, browY)
            }
            drawPath(browPathLeft, Color(0xFF5D4037), style = Stroke(width = 3.2f, cap = StrokeCap.Round))

            val browPathRight = Path().apply {
                moveTo(cx + eyeSpacing - eyeWidth * 0.35f, browY)
                quadraticTo(cx + eyeSpacing, browY - (if (isSad) -2f else 6f), cx + eyeSpacing + eyeWidth * 0.40f, browY + (if (isSad) 4f else 0f))
            }
            drawPath(browPathRight, Color(0xFF5D4037), style = Stroke(width = 3.2f, cap = StrokeCap.Round))

            // -------------------------------------------------------------
            // 8. Sweet Smiling Open Mouth
            // -------------------------------------------------------------
            val mouthY = headCenterY + headH * 0.20f
            if (isSad) {
                val sadMouth = Path().apply {
                    moveTo(cx - headW * 0.12f, mouthY + 8f)
                    quadraticTo(cx, mouthY, cx + headW * 0.12f, mouthY + 8f)
                }
                drawPath(sadMouth, Color(0xFF4E342E), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            } else {
                // Wide open happy Disney smile with pink tongue
                val mouthW = headW * 0.24f
                val mouthH = headH * 0.16f
                val mouthPath = Path().apply {
                    moveTo(cx - mouthW * 0.5f, mouthY)
                    cubicTo(
                        cx - mouthW * 0.5f, mouthY + mouthH * 1.1f,
                        cx + mouthW * 0.5f, mouthY + mouthH * 1.1f,
                        cx + mouthW * 0.5f, mouthY
                    )
                    close()
                }
                // Mouth cavity
                drawPath(
                    path = mouthPath,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF880E4F), Color(0xFF4A0022)),
                        startY = mouthY,
                        endY = mouthY + mouthH
                    )
                )
                // Cute little pink tongue
                val tonguePath = Path().apply {
                    moveTo(cx - mouthW * 0.28f, mouthY + mouthH * 0.45f)
                    cubicTo(
                        cx - mouthW * 0.15f, mouthY + mouthH * 1.05f,
                        cx + mouthW * 0.15f, mouthY + mouthH * 1.05f,
                        cx + mouthW * 0.28f, mouthY + mouthH * 0.45f
                    )
                    close()
                }
                drawPath(tonguePath, Color(0xFFFF8A80))

                // Upper mouth line with dimple ends
                val mouthLip = Path().apply {
                    moveTo(cx - mouthW * 0.55f, mouthY - 1f)
                    quadraticTo(cx, mouthY + 2f, cx + mouthW * 0.55f, mouthY - 1f)
                }
                drawPath(mouthLip, Color(0xFF3E2723), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            }

            // -------------------------------------------------------------
            // 9. Cute Chubby Arms (One Waving Cheerfully!)
            // -------------------------------------------------------------
            // Left Arm (Resting cutely on belly/hip)
            val leftArmPath = Path().apply {
                moveTo(cx - bodyW * 0.42f, bodyCenterY - bodyH * 0.15f)
                quadraticTo(cx - bodyW * 0.62f, bodyCenterY, cx - bodyW * 0.40f, bodyCenterY + bodyH * 0.18f)
            }
            drawPath(
                path = leftArmPath,
                color = Color(0xFF261C14),
                style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
            )
            // Left little paw / hand
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF3E2D22), Color(0xFF1E1712)),
                    center = Offset(cx - bodyW * 0.40f, bodyCenterY + bodyH * 0.18f),
                    radius = w * 0.045f
                ),
                radius = w * 0.045f,
                center = Offset(cx - bodyW * 0.40f, bodyCenterY + bodyH * 0.18f)
            )

            // Right Arm (WAVING UP TO GREET THE PLAYER! 👋)
            val waveAngle = (waveAnim.value - 0.5f) * 22f
            rotate(degrees = waveAngle, pivot = Offset(cx + bodyW * 0.38f, bodyCenterY - bodyH * 0.15f)) {
                val waveArmPath = Path().apply {
                    moveTo(cx + bodyW * 0.38f, bodyCenterY - bodyH * 0.15f)
                    quadraticTo(cx + bodyW * 0.66f, bodyCenterY - bodyH * 0.32f, cx + bodyW * 0.72f, bodyCenterY - bodyH * 0.52f)
                }
                drawPath(
                    path = waveArmPath,
                    color = Color(0xFF261C14),
                    style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
                )
                // Waving Hand with cute rounded baby fingers
                val handCenter = Offset(cx + bodyW * 0.74f, bodyCenterY - bodyH * 0.54f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF4E342E), Color(0xFF1E1712)),
                        center = handCenter,
                        radius = w * 0.055f
                    ),
                    radius = w * 0.055f,
                    center = handCenter
                )
                // Little thumb
                drawCircle(
                    color = Color(0xFF3E2D22),
                    radius = w * 0.024f,
                    center = Offset(handCenter.x - w * 0.035f, handCenter.y + 1f)
                )
            }
        }
    }
}

/**
 * Draws the high-detail Pixar 3D soulful eyes with dual catchlights.
 */
private fun DrawScope.drawPixarEye(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    blink: Float,
    isLeft: Boolean,
    isSad: Boolean,
    isCheering: Boolean
) {
    if (isCheering) {
        // Joyful smiling eye arcs (^_^)
        val arcPath = Path().apply {
            moveTo(centerX - width * 0.45f, centerY + 2f)
            cubicTo(
                centerX - width * 0.2f, centerY - height * 0.45f,
                centerX + width * 0.2f, centerY - height * 0.45f,
                centerX + width * 0.45f, centerY + 2f
            )
        }
        drawPath(arcPath, Color(0xFF211510), style = Stroke(width = 5f, cap = StrokeCap.Round))
        // Upper eyelash
        drawLine(
            Color(0xFF211510),
            start = Offset(if (isLeft) centerX - width * 0.40f else centerX + width * 0.40f, centerY),
            end = Offset(if (isLeft) centerX - width * 0.55f else centerX + width * 0.55f, centerY - 6f),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )
        return
    }

    val currentH = height * blink.coerceIn(0.08f, 1.0f)
    val eyeTop = centerY - currentH * 0.5f

    // 1. Sclera (Eye White with soft 3D shading)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0)),
            center = Offset(centerX, centerY),
            radius = width * 0.55f
        ),
        topLeft = Offset(centerX - width * 0.5f, eyeTop),
        size = Size(width, currentH)
    )

    // 2. Thick Outer Eyeliner / Contour
    drawOval(
        color = Color(0xFF1E1712),
        topLeft = Offset(centerX - width * 0.5f, eyeTop),
        size = Size(width, currentH),
        style = Stroke(width = 2.5f)
    )

    // Upper eyelash flick
    if (blink > 0.4f) {
        val lashStart = Offset(if (isLeft) centerX - width * 0.45f else centerX + width * 0.45f, centerY - currentH * 0.35f)
        val lashEnd = Offset(if (isLeft) centerX - width * 0.65f else centerX + width * 0.65f, centerY - currentH * 0.55f)
        drawLine(Color(0xFF1E1712), start = lashStart, end = lashEnd, strokeWidth = 3.5f, cap = StrokeCap.Round)
    }

    if (blink > 0.35f) {
        // 3. Rich Amber/Chocolate Iris
        val irisSize = width * 0.78f
        val irisX = if (isLeft) centerX + width * 0.04f else centerX - width * 0.04f
        val irisY = centerY + 1f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF8D6E63), // Light warm chocolate
                    Color(0xFF4E342E), // Rich coffee brown
                    Color(0xFF1B0000)  // Deep dark border
                ),
                center = Offset(irisX - 2f, irisY - 2f),
                radius = irisSize * 0.5f
            ),
            radius = irisSize * 0.5f,
            center = Offset(irisX, irisY)
        )

        // 4. Glossy Black Pupil
        val pupilSize = irisSize * 0.68f
        drawCircle(
            color = Color(0xFF0F0B08),
            radius = pupilSize * 0.5f,
            center = Offset(irisX, irisY)
        )

        // 5. TRADEMARK DUAL WHITE SPECULAR CATCHLIGHTS (Just like the Pixar reference!)
        // Main large glossy catchlight (Top-Left)
        drawCircle(
            color = Color.White,
            radius = irisSize * 0.22f,
            center = Offset(irisX - irisSize * 0.16f, irisY - irisSize * 0.16f)
        )
        // Secondary soft bounce catchlight (Bottom-Right)
        drawCircle(
            color = Color.White.copy(alpha = 0.90f),
            radius = irisSize * 0.11f,
            center = Offset(irisX + irisSize * 0.18f, irisY + irisSize * 0.18f)
        )
    }
}

/**
 * Draws the shimmering translucent fairy wings with animated flutter.
 */
private fun DrawScope.drawPixarBeeWings(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    flutter: Float,
    isSad: Boolean
) {
    val wingW = w * 0.32f
    val wingH = h * 0.44f * flutter
    val drop = if (isSad) 12f else 0f

    // Wing Gradient: Pale sparkling cyan with crystalline edge
    val wingBrush = Brush.radialGradient(
        colors = listOf(Color(0xE6E0F7FA), Color(0xBB80DEEA), Color(0x774DD0E1)),
        center = Offset(cx, cy),
        radius = wingW * 1.5f
    )

    // Left Wing (Main Upper)
    drawOval(
        brush = wingBrush,
        topLeft = Offset(cx - wingW * 1.35f, cy - wingH * 0.95f + drop),
        size = Size(wingW, wingH)
    )
    drawOval(
        color = Color(0xFF00ACC1).copy(alpha = 0.70f),
        topLeft = Offset(cx - wingW * 1.35f, cy - wingH * 0.95f + drop),
        size = Size(wingW, wingH),
        style = Stroke(width = 2.2f)
    )
    // Left Wing (Secondary Lower)
    drawOval(
        brush = wingBrush,
        topLeft = Offset(cx - wingW * 1.15f, cy - wingH * 0.35f + drop),
        size = Size(wingW * 0.75f, wingH * 0.70f)
    )

    // Right Wing (Main Upper)
    drawOval(
        brush = wingBrush,
        topLeft = Offset(cx + wingW * 0.35f, cy - wingH * 0.95f + drop),
        size = Size(wingW, wingH)
    )
    drawOval(
        color = Color(0xFF00ACC1).copy(alpha = 0.70f),
        topLeft = Offset(cx + wingW * 0.35f, cy - wingH * 0.95f + drop),
        size = Size(wingW, wingH),
        style = Stroke(width = 2.2f)
    )
    // Right Wing (Secondary Lower)
    drawOval(
        brush = wingBrush,
        topLeft = Offset(cx + wingW * 0.40f, cy - wingH * 0.35f + drop),
        size = Size(wingW * 0.75f, wingH * 0.70f)
    )
}
