package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

// =========================================================================
// 1. BLOSSOM GIRL ("Lily" in Pink Floral Dress & Hair Bow)
// =========================================================================
@Composable
fun CartoonPinkGirlMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val bowWiggle = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CHEERING || mood == MascotMood.CELEBRATING) {
            bowWiggle.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(280, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else bowWiggle.snapTo(0f)
    }

    val tearAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else tearAnim.snapTo(0f)
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Brunette Hair (Back Volume)
            drawCircle(Color(0xFF5D2E14), radius = w * 0.40f, center = Offset(cx, cy - h * 0.04f))
            drawCircle(Color(0xFF7A3E1D), radius = w * 0.38f, center = Offset(cx, cy - h * 0.04f))

            // 2. Chibi Face
            drawCircle(Color(0xFFFFDFBA), radius = w * 0.30f, center = Offset(cx, cy + 2f))

            // 3. Front Bangs
            val bangs = Path().apply {
                moveTo(cx - w * 0.28f, cy - h * 0.12f)
                quadraticTo(cx - w * 0.10f, cy - h * 0.02f, cx, cy - h * 0.10f)
                quadraticTo(cx + w * 0.15f, cy - h * 0.02f, cx + w * 0.28f, cy - h * 0.12f)
                quadraticTo(cx + w * 0.20f, cy - h * 0.24f, cx, cy - h * 0.24f)
                close()
            }
            drawPath(bangs, Color(0xFF7A3E1D))

            // 4. Iconic Red/Pink Ribbon Hair Bow on Top
            val bowY = cy - h * 0.28f + (bowWiggle.value * 3f)
            val bowL = Path().apply {
                moveTo(cx, bowY)
                lineTo(cx - 16f, bowY - 10f)
                lineTo(cx - 16f, bowY + 8f)
                close()
            }
            val bowR = Path().apply {
                moveTo(cx, bowY)
                lineTo(cx + 16f, bowY - 10f)
                lineTo(cx + 16f, bowY + 8f)
                close()
            }
            drawPath(bowL, Color(0xFFF43F5E))
            drawPath(bowR, Color(0xFFF43F5E))
            drawCircle(Color(0xFFFF2A85), radius = 5f, center = Offset(cx, bowY))

            // 5. Expressive Eyes with Lashes
            when (mood) {
                MascotMood.CRYING_SAD -> {
                    val sadL = Path().apply {
                        moveTo(cx - 18f, cy + 2f)
                        quadraticTo(cx - 12f, cy - 4f, cx - 6f, cy + 2f)
                    }
                    val sadR = Path().apply {
                        moveTo(cx + 6f, cy + 2f)
                        quadraticTo(cx + 12f, cy - 4f, cx + 18f, cy + 2f)
                    }
                    drawPath(sadL, Color(0xFF451A03), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    drawPath(sadR, Color(0xFF451A03), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    drawAnimatedCryingTears(cx - 12f, cy + 6f, tearAnim.value, h)
                    drawAnimatedCryingTears(cx + 12f, cy + 6f, tearAnim.value, h)
                }
                MascotMood.CHEERING, MascotMood.CELEBRATING -> {
                    drawCircle(Color(0xFF451A03), radius = 5.5f, center = Offset(cx - 12f, cy - 2f))
                    drawCircle(Color(0xFF451A03), radius = 5.5f, center = Offset(cx + 12f, cy - 2f))
                    drawCircle(Color.White, radius = 2f, center = Offset(cx - 13f, cy - 3f))
                    drawCircle(Color.White, radius = 2f, center = Offset(cx + 11f, cy - 3f))
                    // Lashes
                    drawLine(Color(0xFF451A03), start = Offset(cx - 16f, cy - 6f), end = Offset(cx - 20f, cy - 9f), strokeWidth = 1.5f)
                    drawLine(Color(0xFF451A03), start = Offset(cx + 16f, cy - 6f), end = Offset(cx + 20f, cy - 9f), strokeWidth = 1.5f)
                }
                else -> {
                    drawCircle(Color(0xFF451A03), radius = 5f, center = Offset(cx - 12f, cy - 2f))
                    drawCircle(Color(0xFF451A03), radius = 5f, center = Offset(cx + 12f, cy - 2f))
                    drawCircle(Color.White, radius = 1.8f, center = Offset(cx - 13f, cy - 3f))
                    drawCircle(Color.White, radius = 1.8f, center = Offset(cx + 11f, cy - 3f))
                }
            }

            // 6. Sweet Rosy Cheeks
            drawCircle(Color(0xFFFF8DA1).copy(alpha = 0.65f), radius = 6f, center = Offset(cx - 18f, cy + 8f))
            drawCircle(Color(0xFFFF8DA1).copy(alpha = 0.65f), radius = 6f, center = Offset(cx + 18f, cy + 8f))

            // 7. Smile
            if (mood == MascotMood.CRYING_SAD) {
                val sadM = Path().apply {
                    moveTo(cx - 5f, cy + 16f)
                    quadraticTo(cx, cy + 12f, cx + 5f, cy + 16f)
                }
                drawPath(sadM, Color(0xFF7A3E1D), style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                val happyM = Path().apply {
                    moveTo(cx - 6f, cy + 12f)
                    quadraticTo(cx, cy + 17f, cx + 6f, cy + 12f)
                }
                drawPath(happyM, Color(0xFFE11D48), style = Stroke(width = 2.2f, cap = StrokeCap.Round))
            }

            // 8. Pink Floral Dress Collar
            drawCircle(Color(0xFFF472B6), radius = 14f, center = Offset(cx, cy + h * 0.32f))
            drawCircle(Color.White, radius = 3f, center = Offset(cx, cy + h * 0.30f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.35f, w, h)
            }
        }
    }
}

// =========================================================================
// 2. BRAVE KNIGHT ("Sir Leo" with Sword & Cape)
// =========================================================================
@Composable
fun CartoonKnightMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val swordAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CHEERING || mood == MascotMood.CELEBRATING) {
            swordAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(300, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else swordAnim.snapTo(0f)
    }

    val tearAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else tearAnim.snapTo(0f)
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Blue Hero Cape behind
            val cape = Path().apply {
                moveTo(cx - w * 0.25f, cy + 4f)
                lineTo(cx - w * 0.35f, cy + h * 0.40f)
                lineTo(cx + w * 0.35f, cy + h * 0.40f)
                lineTo(cx + w * 0.25f, cy + 4f)
                close()
            }
            drawPath(cape, Color(0xFF1D4ED8))

            // 2. Brown Spiky Hero Hair
            drawCircle(Color(0xFF5A2A18), radius = w * 0.38f, center = Offset(cx, cy - h * 0.08f))

            // 3. Face
            drawCircle(Color(0xFFFFDFBA), radius = w * 0.28f, center = Offset(cx, cy))

            // 4. Front Hair Spikes
            val hairSpike = Path().apply {
                moveTo(cx - 24f, cy - 14f)
                lineTo(cx - 10f, cy - 28f)
                lineTo(cx, cy - 14f)
                lineTo(cx + 12f, cy - 28f)
                lineTo(cx + 24f, cy - 14f)
                close()
            }
            drawPath(hairSpike, Color(0xFF7A3E1D))

            // 5. Sword Raised High
            val swordY = (swordAnim.value * 6f)
            val swordPath = Path().apply {
                moveTo(cx - w * 0.32f, cy - h * 0.36f - swordY)
                lineTo(cx - w * 0.35f, cy - h * 0.10f - swordY)
                lineTo(cx - w * 0.29f, cy - h * 0.10f - swordY)
                close()
            }
            drawPath(swordPath, Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFCBD5E1))))
            // Sword Guard & Hilt
            drawLine(Color(0xFF92400E), start = Offset(cx - w * 0.38f, cy - h * 0.10f - swordY), end = Offset(cx - w * 0.26f, cy - h * 0.10f - swordY), strokeWidth = 3f)
            drawLine(Color(0xFF78350F), start = Offset(cx - w * 0.32f, cy - h * 0.10f - swordY), end = Offset(cx - w * 0.32f, cy + 4f - swordY), strokeWidth = 3.5f)

            // 6. Eyes
            if (mood == MascotMood.CRYING_SAD) {
                drawAnimatedCryingTears(cx - 10f, cy + 4f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + 10f, cy + 4f, tearAnim.value, h)
            } else {
                drawCircle(Color(0xFF1E293B), radius = 5f, center = Offset(cx - 10f, cy - 2f))
                drawCircle(Color(0xFF1E293B), radius = 5f, center = Offset(cx + 10f, cy - 2f))
                drawCircle(Color.White, radius = 1.8f, center = Offset(cx - 11f, cy - 3f))
                drawCircle(Color.White, radius = 1.8f, center = Offset(cx + 9f, cy - 3f))
            }

            // 7. Confident Smile
            val smile = Path().apply {
                moveTo(cx - 6f, cy + 10f)
                quadraticTo(cx, cy + 15f, cx + 6f, cy + 10f)
            }
            drawPath(smile, Color(0xFF78350F), style = Stroke(width = 2f, cap = StrokeCap.Round))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.35f, w, h)
            }
        }
    }
}

// =========================================================================
// 3. MAGIC WITCH ("Luna" with Purple Hat & Wand)
// =========================================================================
@Composable
fun CartoonWitchMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val wandSparkle = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        wandSparkle.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Long Auburn Hair
            drawCircle(Color(0xFF831843), radius = w * 0.38f, center = Offset(cx, cy + 2f))

            // 2. Chibi Face
            drawCircle(Color(0xFFFFDFBA), radius = w * 0.28f, center = Offset(cx, cy + 6f))

            // 3. Iconic Pointed Witch Hat
            val hatBrim = Path().apply {
                moveTo(cx - w * 0.42f, cy - h * 0.08f)
                quadraticTo(cx, cy - h * 0.16f, cx + w * 0.42f, cy - h * 0.08f)
                quadraticTo(cx, cy - h * 0.04f, cx - w * 0.42f, cy - h * 0.08f)
                close()
            }
            drawPath(hatBrim, Color(0xFF581C87))

            // Hat Cone with bend
            val hatCone = Path().apply {
                moveTo(cx - w * 0.24f, cy - h * 0.10f)
                quadraticTo(cx - 8f, cy - h * 0.28f, cx + 8f, cy - h * 0.44f)
                quadraticTo(cx + w * 0.14f, cy - h * 0.24f, cx + w * 0.24f, cy - h * 0.10f)
                close()
            }
            drawPath(hatCone, Color(0xFF6B21A8))
            // Hat Gold Buckle
            drawCircle(Color(0xFFFBBF24), radius = 5f, center = Offset(cx, cy - h * 0.11f))

            // 4. Magical Wand with Glowing Orb
            val wandX = cx + w * 0.32f
            val wandY = cy - h * 0.18f
            drawLine(Color(0xFF78350F), start = Offset(wandX, wandY + 36f), end = Offset(wandX, wandY), strokeWidth = 3f)
            // Glowing Purple Orb
            drawCircle(Color(0xFFA855F7), radius = 8f + wandSparkle.value * 3f, center = Offset(wandX, wandY))
            drawCircle(Color(0xFFF472B6), radius = 5f, center = Offset(wandX, wandY))
            drawCircle(Color.White, radius = 2f, center = Offset(wandX - 1f, wandY - 1f))

            // 5. Smiling Witch Eyes
            drawCircle(Color(0xFF4C1D95), radius = 5f, center = Offset(cx - 10f, cy + 4f))
            drawCircle(Color(0xFF4C1D95), radius = 5f, center = Offset(cx + 10f, cy + 4f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 11f, cy + 3f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 9f, cy + 3f))

            // 6. Smile
            val smile = Path().apply {
                moveTo(cx - 5f, cy + 15f)
                quadraticTo(cx, cy + 19f, cx + 5f, cy + 15f)
            }
            drawPath(smile, Color(0xFF581C87), style = Stroke(width = 2f, cap = StrokeCap.Round))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx - 12f, cy - h * 0.35f, w, h)
            }
        }
    }
}

// =========================================================================
// 4. CYBER BOT ("Sparky" Robot with Digital Eye Screen)
// =========================================================================
@Composable
fun CartoonRobotMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val pulse = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        pulse.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Antenna with glowing cyan orb
            drawLine(Color(0xFF94A3B8), start = Offset(cx, cy - h * 0.28f), end = Offset(cx, cy - h * 0.40f), strokeWidth = 3f)
            drawCircle(Color(0xFF00E5FF), radius = 5f + pulse.value * 2f, center = Offset(cx, cy - h * 0.40f))

            // 2. Cute Ear Pods
            drawCircle(Color(0xFF1E293B), radius = 10f, center = Offset(cx - w * 0.32f, cy))
            drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(cx - w * 0.32f, cy))
            drawCircle(Color(0xFF1E293B), radius = 10f, center = Offset(cx + w * 0.32f, cy))
            drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(cx + w * 0.32f, cy))

            // 3. Rounded White Robot Head
            drawRoundRect(
                color = Color(0xFFF8FAFC),
                topLeft = Offset(cx - w * 0.30f, cy - h * 0.28f),
                size = Size(w * 0.60f, h * 0.52f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
            )
            drawRoundRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(cx - w * 0.30f, cy - h * 0.28f),
                size = Size(w * 0.60f, h * 0.52f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f),
                style = Stroke(width = 2.5f)
            )

            // 4. Black Digital Visor Screen
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(cx - w * 0.24f, cy - h * 0.18f),
                size = Size(w * 0.48f, h * 0.32f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // 5. Glowing Neon Cyan Eyes on Screen
            val eyeCyan = Color(0xFF00E5FF)
            when (mood) {
                MascotMood.CRYING_SAD -> {
                    // Sad angled digital eyes
                    drawLine(eyeCyan, start = Offset(cx - 16f, cy - 2f), end = Offset(cx - 6f, cy - 8f), strokeWidth = 3f, cap = StrokeCap.Round)
                    drawLine(eyeCyan, start = Offset(cx + 6f, cy - 8f), end = Offset(cx + 16f, cy - 2f), strokeWidth = 3f, cap = StrokeCap.Round)
                    // Digital glitch tears
                    drawCircle(Color(0xFF38BDF8), radius = 3f, center = Offset(cx - 12f, cy + 6f))
                    drawCircle(Color(0xFF38BDF8), radius = 3f, center = Offset(cx + 12f, cy + 6f))
                }
                MascotMood.CHEERING, MascotMood.CELEBRATING -> {
                    // Happy inverted curve eyes ^ ^
                    val leftEye = Path().apply {
                        moveTo(cx - 18f, cy - 2f)
                        quadraticTo(cx - 12f, cy - 10f, cx - 6f, cy - 2f)
                    }
                    val rightEye = Path().apply {
                        moveTo(cx + 6f, cy - 2f)
                        quadraticTo(cx + 12f, cy - 10f, cx + 18f, cy - 2f)
                    }
                    drawPath(leftEye, eyeCyan, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                    drawPath(rightEye, eyeCyan, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                }
                else -> {
                    // Big glowing circular eyes
                    drawCircle(eyeCyan, radius = 6f, center = Offset(cx - 12f, cy - 4f))
                    drawCircle(eyeCyan, radius = 6f, center = Offset(cx + 12f, cy - 4f))
                    drawCircle(Color.White, radius = 2f, center = Offset(cx - 13f, cy - 5f))
                    drawCircle(Color.White, radius = 2f, center = Offset(cx + 11f, cy - 5f))
                }
            }

            // 6. Chest Power Core
            drawCircle(Color(0xFF00E5FF), radius = 4f, center = Offset(cx, cy + h * 0.32f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.38f, w, h)
            }
        }
    }
}

// =========================================================================
// 5. SAFARI EXPLORER ("Finn" with Pith Helmet & Vest)
// =========================================================================
@Composable
fun CartoonSafariExplorerMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Chibi Face
            drawCircle(Color(0xFFFFDFBA), radius = w * 0.30f, center = Offset(cx, cy + 4f))

            // 2. Safari Pith Helmet Brim
            val brim = Path().apply {
                moveTo(cx - w * 0.44f, cy - h * 0.10f)
                quadraticTo(cx, cy - h * 0.22f, cx + w * 0.44f, cy - h * 0.10f)
                quadraticTo(cx, cy - h * 0.05f, cx - w * 0.44f, cy - h * 0.10f)
                close()
            }
            drawPath(brim, Color(0xFFD4B996))
            // Helmet Dome
            val dome = Path().apply {
                moveTo(cx - w * 0.28f, cy - h * 0.12f)
                quadraticTo(cx, cy - h * 0.42f, cx + w * 0.28f, cy - h * 0.12f)
                close()
            }
            drawPath(dome, Color(0xFFE2C9A9))
            // Leather band
            drawLine(Color(0xFF78350F), start = Offset(cx - w * 0.28f, cy - h * 0.12f), end = Offset(cx + w * 0.28f, cy - h * 0.12f), strokeWidth = 3f)

            // 3. Eyes
            drawCircle(Color(0xFF451A03), radius = 5f, center = Offset(cx - 10f, cy + 2f))
            drawCircle(Color(0xFF451A03), radius = 5f, center = Offset(cx + 10f, cy + 2f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 11f, cy + 1f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 9f, cy + 1f))

            // 4. Smile
            val smile = Path().apply {
                moveTo(cx - 6f, cy + 14f)
                quadraticTo(cx, cy + 19f, cx + 6f, cy + 14f)
            }
            drawPath(smile, Color(0xFF78350F), style = Stroke(width = 2f, cap = StrokeCap.Round))

            // 5. Khaki Vest Collar
            drawCircle(Color(0xFF65A30D), radius = 12f, center = Offset(cx, cy + h * 0.34f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.38f, w, h)
            }
        }
    }
}

// =========================================================================
// 6. CAP MONKEY ("Kiki" with Backwards Cap & Blue Hoodie)
// =========================================================================
@Composable
fun CartoonMonkeyMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Big Round Monkey Ears
            drawCircle(Color(0xFF78350F), radius = 15f, center = Offset(cx - w * 0.32f, cy))
            drawCircle(Color(0xFFFFDFBA), radius = 9f, center = Offset(cx - w * 0.32f, cy))
            drawCircle(Color(0xFF78350F), radius = 15f, center = Offset(cx + w * 0.32f, cy))
            drawCircle(Color(0xFFFFDFBA), radius = 9f, center = Offset(cx + w * 0.32f, cy))

            // 2. Brown Head
            drawCircle(Color(0xFF78350F), radius = w * 0.30f, center = Offset(cx, cy))

            // 3. Heart-shaped Peach Muzzle
            drawCircle(Color(0xFFFFDFBA), radius = 18f, center = Offset(cx - 8f, cy + 4f))
            drawCircle(Color(0xFFFFDFBA), radius = 18f, center = Offset(cx + 8f, cy + 4f))
            drawCircle(Color(0xFFFFDFBA), radius = 14f, center = Offset(cx, cy + 12f))

            // 4. Backwards Red Cap
            val cap = Path().apply {
                moveTo(cx - w * 0.28f, cy - 8f)
                quadraticTo(cx, cy - h * 0.34f, cx + w * 0.28f, cy - 8f)
                close()
            }
            drawPath(cap, Color(0xFFDC2626))
            // Cap Visor sticking to side/back
            val visor = Path().apply {
                moveTo(cx + w * 0.20f, cy - 8f)
                lineTo(cx + w * 0.38f, cy - 14f)
                lineTo(cx + w * 0.26f, cy - 2f)
                close()
            }
            drawPath(visor, Color(0xFF991B1B))

            // 5. Monkey Eyes
            drawCircle(Color(0xFF1E293B), radius = 5f, center = Offset(cx - 8f, cy - 2f))
            drawCircle(Color(0xFF1E293B), radius = 5f, center = Offset(cx + 8f, cy - 2f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 9f, cy - 3f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 7f, cy - 3f))

            // 6. Wide Happy Grin
            val grin = Path().apply {
                moveTo(cx - 8f, cy + 10f)
                quadraticTo(cx, cy + 18f, cx + 8f, cy + 10f)
            }
            drawPath(grin, Color(0xFF78350F), style = Stroke(width = 2.5f, cap = StrokeCap.Round))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.35f, w, h)
            }
        }
    }
}

// =========================================================================
// 7. BABY RED DRAGON ("Draco" with Golden Wings)
// =========================================================================
@Composable
fun CartoonBabyDragonMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val wingFlap = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        wingFlap.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(450, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Golden Wings Spreading Wide
            val flapAngle = wingFlap.value * 6f
            val leftWing = Path().apply {
                moveTo(cx - 10f, cy + 4f)
                lineTo(cx - w * 0.44f, cy - h * 0.22f + flapAngle)
                lineTo(cx - w * 0.26f, cy - 4f)
                close()
            }
            val rightWing = Path().apply {
                moveTo(cx + 10f, cy + 4f)
                lineTo(cx + w * 0.44f, cy - h * 0.22f + flapAngle)
                lineTo(cx + w * 0.26f, cy - 4f)
                close()
            }
            drawPath(leftWing, Color(0xFFF59E0B))
            drawPath(rightWing, Color(0xFFF59E0B))

            // 2. Cute Red Chubby Dragon Head
            drawCircle(Color(0xFFEF4444), radius = w * 0.32f, center = Offset(cx, cy))

            // 3. Golden Horns on Top
            val hornL = Path().apply {
                moveTo(cx - 16f, cy - h * 0.22f)
                lineTo(cx - 24f, cy - h * 0.40f)
                lineTo(cx - 8f, cy - h * 0.26f)
                close()
            }
            val hornR = Path().apply {
                moveTo(cx + 16f, cy - h * 0.22f)
                lineTo(cx + 24f, cy - h * 0.40f)
                lineTo(cx + 8f, cy - h * 0.26f)
                close()
            }
            drawPath(hornL, Color(0xFFFBBF24))
            drawPath(hornR, Color(0xFFFBBF24))

            // 4. Yellow Snout & Cheeks
            drawCircle(Color(0xFFFEF08A), radius = 14f, center = Offset(cx, cy + 10f))
            drawCircle(Color(0xFFB45309), radius = 2f, center = Offset(cx - 4f, cy + 8f))
            drawCircle(Color(0xFFB45309), radius = 2f, center = Offset(cx + 4f, cy + 8f))

            // 5. Emerald Green Eyes
            drawCircle(Color(0xFF10B981), radius = 6f, center = Offset(cx - 12f, cy - 4f))
            drawCircle(Color(0xFF10B981), radius = 6f, center = Offset(cx + 12f, cy - 4f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 13f, cy - 5f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 11f, cy - 5f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 8. WATER NYMPH ("Marina" with Ocean Blue Wave Hair)
// =========================================================================
@Composable
fun CartoonWaterNymphMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val waveAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        waveAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Flowing Wave Hair
            val waveOffset = waveAnim.value * 4f
            drawCircle(Color(0xFF0284C7), radius = w * 0.38f, center = Offset(cx - waveOffset, cy))
            drawCircle(Color(0xFF38BDF8), radius = w * 0.36f, center = Offset(cx + waveOffset, cy))

            // 2. Cute Fairy Face
            drawCircle(Color(0xFFE0F2FE), radius = w * 0.28f, center = Offset(cx, cy + 2f))

            // 3. Water Flower in Hair
            drawCircle(Color(0xFFBAE6FD), radius = 6f, center = Offset(cx - w * 0.24f, cy - h * 0.16f))
            drawCircle(Color.White, radius = 3f, center = Offset(cx - w * 0.24f, cy - h * 0.16f))

            // 4. Sparkling Ocean Blue Eyes
            drawCircle(Color(0xFF0369A1), radius = 5f, center = Offset(cx - 10f, cy))
            drawCircle(Color(0xFF0369A1), radius = 5f, center = Offset(cx + 10f, cy))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 11f, cy - 1f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 9f, cy - 1f))

            // 5. Water droplet necklace
            drawCircle(Color(0xFF0284C7), radius = 4f, center = Offset(cx, cy + h * 0.28f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 9. FLAME SPRITE ("Blaze" with Fire Hair & Sparks)
// =========================================================================
@Composable
fun CartoonFireSpriteMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val fireDance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fireDance.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Dancing Flame Hair
            val fireTopY = cy - h * 0.38f - (fireDance.value * 6f)
            val flamePath = Path().apply {
                moveTo(cx - w * 0.32f, cy - 4f)
                quadraticTo(cx - w * 0.20f, fireTopY + 12f, cx - 10f, fireTopY)
                quadraticTo(cx, fireTopY + 8f, cx + 10f, fireTopY - 4f)
                quadraticTo(cx + w * 0.20f, fireTopY + 12f, cx + w * 0.32f, cy - 4f)
                close()
            }
            drawPath(flamePath, Brush.verticalGradient(listOf(Color(0xFFFACC15), Color(0xFFEA580C))))

            // 2. Chibi Face
            drawCircle(Color(0xFFFFEDD5), radius = w * 0.28f, center = Offset(cx, cy + 4f))

            // 3. Golden Fiery Eyes
            drawCircle(Color(0xFFEA580C), radius = 5.5f, center = Offset(cx - 10f, cy + 2f))
            drawCircle(Color(0xFFEA580C), radius = 5.5f, center = Offset(cx + 10f, cy + 2f))
            drawCircle(Color(0xFFFBBF24), radius = 2.5f, center = Offset(cx - 10f, cy + 2f))
            drawCircle(Color(0xFFFBBF24), radius = 2.5f, center = Offset(cx + 10f, cy + 2f))

            // 4. Confident Grin
            val grin = Path().apply {
                moveTo(cx - 6f, cy + 14f)
                quadraticTo(cx, cy + 19f, cx + 6f, cy + 14f)
            }
            drawPath(grin, Color(0xFFC2410C), style = Stroke(width = 2.2f, cap = StrokeCap.Round))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.38f, w, h)
            }
        }
    }
}

// =========================================================================
// 10. NATURE NYMPH ("Flora" with Leaf Crown & Flower Blossoms)
// =========================================================================
@Composable
fun CartoonNatureFairyMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Lush Green Leaf Hair
            drawCircle(Color(0xFF059669), radius = w * 0.38f, center = Offset(cx, cy))
            drawCircle(Color(0xFF10B981), radius = w * 0.34f, center = Offset(cx, cy))

            // 2. Chibi Face
            drawCircle(Color(0xFFFFDFBA), radius = w * 0.28f, center = Offset(cx, cy + 4f))

            // 3. Flower Crown on Top
            drawCircle(Color(0xFFF472B6), radius = 6f, center = Offset(cx - 18f, cy - h * 0.16f))
            drawCircle(Color(0xFFF472B6), radius = 6f, center = Offset(cx, cy - h * 0.20f))
            drawCircle(Color(0xFFF472B6), radius = 6f, center = Offset(cx + 18f, cy - h * 0.16f))
            drawCircle(Color(0xFFFEF08A), radius = 3f, center = Offset(cx - 18f, cy - h * 0.16f))
            drawCircle(Color(0xFFFEF08A), radius = 3f, center = Offset(cx, cy - h * 0.20f))
            drawCircle(Color(0xFFFEF08A), radius = 3f, center = Offset(cx + 18f, cy - h * 0.16f))

            // 4. Cheerful Green Eyes
            drawCircle(Color(0xFF047857), radius = 5f, center = Offset(cx - 10f, cy + 2f))
            drawCircle(Color(0xFF047857), radius = 5f, center = Offset(cx + 10f, cy + 2f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 11f, cy + 1f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 9f, cy + 1f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.38f, w, h)
            }
        }
    }
}

// =========================================================================
// 11. CLOUD SPRITE ("Nimbus" Fluffy Cloud with Sparkles)
// =========================================================================
@Composable
fun CartoonCloudSpriteMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val cloudFloat = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        cloudFloat.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f + (cloudFloat.value * 3f)
            val w = this.size.width
            val h = this.size.height

            // 1. Billowing Cloud Puffs
            drawCircle(Color(0xFFEFF6FF), radius = w * 0.24f, center = Offset(cx - w * 0.20f, cy))
            drawCircle(Color(0xFFEFF6FF), radius = w * 0.24f, center = Offset(cx + w * 0.20f, cy))
            drawCircle(Color(0xFFDBEAFE), radius = w * 0.26f, center = Offset(cx, cy - h * 0.16f))
            drawCircle(Color(0xFFEFF6FF), radius = w * 0.24f, center = Offset(cx, cy - h * 0.16f))
            drawCircle(Color(0xFFDBEAFE), radius = w * 0.26f, center = Offset(cx, cy + h * 0.08f))
            drawCircle(Color(0xFFEFF6FF), radius = w * 0.24f, center = Offset(cx, cy + h * 0.08f))

            // 2. Cute Celestial Blue Eyes
            drawCircle(Color(0xFF1D4ED8), radius = 5.5f, center = Offset(cx - 12f, cy - 2f))
            drawCircle(Color(0xFF1D4ED8), radius = 5.5f, center = Offset(cx + 12f, cy - 2f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx - 13f, cy - 3f))
            drawCircle(Color.White, radius = 2f, center = Offset(cx + 11f, cy - 3f))

            // 3. Pink Cloud Cheeks
            drawCircle(Color(0xFFF472B6).copy(alpha = 0.5f), radius = 6f, center = Offset(cx - 18f, cy + 6f))
            drawCircle(Color(0xFFF472B6).copy(alpha = 0.5f), radius = 6f, center = Offset(cx + 18f, cy + 6f))

            // 4. Smile
            val smile = Path().apply {
                moveTo(cx - 5f, cy + 8f)
                quadraticTo(cx, cy + 13f, cx + 5f, cy + 8f)
            }
            drawPath(smile, Color(0xFF1E40AF), style = Stroke(width = 2f, cap = StrokeCap.Round))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 12. STONE GOLEM ("Rocky" Earth Guardian with Moss)
// =========================================================================
@Composable
fun CartoonStoneGolemMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Carved Boulder Head
            drawRoundRect(
                color = Color(0xFF64748B),
                topLeft = Offset(cx - w * 0.30f, cy - h * 0.28f),
                size = Size(w * 0.60f, h * 0.56f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(26f, 26f)
            )

            // 2. Soft Green Moss Patches
            drawCircle(Color(0xFF10B981), radius = 10f, center = Offset(cx - w * 0.22f, cy - h * 0.24f))
            drawCircle(Color(0xFF059669), radius = 8f, center = Offset(cx + w * 0.20f, cy - h * 0.22f))
            drawCircle(Color(0xFF10B981), radius = 6f, center = Offset(cx - w * 0.16f, cy + h * 0.20f))

            // 3. Glowing Gentle Stone Eyes
            drawCircle(Color(0xFFE2E8F0), radius = 6f, center = Offset(cx - 12f, cy - 4f))
            drawCircle(Color(0xFFE2E8F0), radius = 6f, center = Offset(cx + 12f, cy - 4f))
            drawCircle(Color(0xFF0F172A), radius = 4f, center = Offset(cx - 12f, cy - 4f))
            drawCircle(Color(0xFF0F172A), radius = 4f, center = Offset(cx + 12f, cy - 4f))

            // 4. Carved Rock Smile
            val smile = Path().apply {
                moveTo(cx - 8f, cy + 12f)
                quadraticTo(cx, cy + 18f, cx + 8f, cy + 12f)
            }
            drawPath(smile, Color(0xFF1E293B), style = Stroke(width = 3f, cap = StrokeCap.Round))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.38f, w, h)
            }
        }
    }
}
