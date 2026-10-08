package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.model.ArrowPiece
import com.example.model.Direction
import com.example.model.GameState
import com.example.model.GameTheme
import com.example.model.GridPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

@Composable
fun GameBoardCanvas(
    gameState: GameState,
    theme: GameTheme,
    onArrowTapped: (ArrowPiece) -> Unit,
    modifier: Modifier = Modifier
) {
    val level = gameState.levelData
    val cols = level.cols
    val rows = level.rows

    // Pulsing glow animation for hinted arrow (Electric Blue)
    val hintPulse = remember { Animatable(0.4f) }
    LaunchedEffect(gameState.hintedArrowId) {
        if (gameState.hintedArrowId != null) {
            hintPulse.animateTo(
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(450, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            hintPulse.snapTo(0.4f)
        }
    }

    // Spring collision jelly nudge on blocked arrow
    val blockedNudge = remember { Animatable(0f) }
    val blockedRedFlash = remember { Animatable(0f) }

    LaunchedEffect(gameState.blockedArrowId) {
        if (gameState.blockedArrowId != null) {
            launch {
                blockedNudge.snapTo(0f)
                blockedNudge.animateTo(0.85f, tween(80, easing = FastOutSlowInEasing))
                blockedNudge.animateTo(-0.4f, tween(90, easing = FastOutSlowInEasing))
                blockedNudge.animateTo(0f, tween(70, easing = LinearEasing))
            }
            launch {
                blockedRedFlash.snapTo(1f)
                blockedRedFlash.animateTo(0f, tween(260, easing = LinearEasing))
            }
        }
    }

    // Smooth uncoiling escape animation
    val escapeProgress = remember { Animatable(0f) }
    val particleController = remember { ParticleController() }

    LaunchedEffect(gameState.escapingArrowId) {
        if (gameState.escapingArrowId != null) {
            escapeProgress.snapTo(0f)
            escapeProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
            )
            delay(20)
            escapeProgress.snapTo(0f)
        }
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_board_canvas")
                .pointerInput(gameState.activeArrows, cols, rows) {
                    detectTapGestures { tapOffset ->
                        val canvasW = size.width.toFloat()
                        val canvasH = size.height.toFloat()

                        val marginX = 14f
                        val marginY = 14f
                        val availW = (canvasW - marginX * 2f).coerceAtLeast(10f)
                        val availH = (canvasH - marginY * 2f).coerceAtLeast(10f)

                        val cellSize = min(availW / cols, availH / rows)
                        val boardW = cellSize * cols
                        val boardH = cellSize * rows
                        val originX = (canvasW - boardW) / 2f
                        val originY = (canvasH - boardH) / 2f

                        val hitThreshold = (cellSize * 0.76f).coerceAtLeast(26f)
                        var bestArrow: ArrowPiece? = null
                        var bestDist = Float.MAX_VALUE

                        for (arrow in gameState.activeArrows) {
                            val pixelPoints = arrow.points.map { pt ->
                                Offset(
                                    originX + (pt.col + 0.5f) * cellSize,
                                    originY + (pt.row + 0.5f) * cellSize
                                )
                            }

                            for (i in 0 until pixelPoints.size - 1) {
                                val p1 = pixelPoints[i]
                                val p2 = pixelPoints[i + 1]
                                val dist = distancePointToSegment(tapOffset, p1, p2)
                                if (dist < hitThreshold && dist < bestDist) {
                                    bestDist = dist
                                    bestArrow = arrow
                                }
                            }
                        }

                        bestArrow?.let { arrow ->
                            val isBlocked = arrow.isBlockedBy(gameState.activeArrows, cols, rows)
                            if (!isBlocked) {
                                // Trigger satisfying juicy cartoon particle burst like Candy Crush!
                                particleController.triggerJuicyPop(tapOffset, gameState.comboStreak + 1)
                            }
                            onArrowTapped(arrow)
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            val marginX = 14f
            val marginY = 14f
            val availW = (canvasW - marginX * 2f).coerceAtLeast(10f)
            val availH = (canvasH - marginY * 2f).coerceAtLeast(10f)

            val cellSize = min(availW / cols, availH / rows)
            val boardW = cellSize * cols
            val boardH = cellSize * rows
            val originX = (canvasW - boardW) / 2f
            val originY = (canvasH - boardH) / 2f

            // =========================================================================
            // ARROW STYLING (MATCHING IMAGE 1 EXACTLY):
            // - Bold solid stroke width with rounded caps and rounded joints
            // - Deep dark navy paths: #0B132B / #111827
            // - Electric blue free / escaping path: #2563EB / #3B82F6
            // - Clear, sharp triangular arrowhead with sharp side barbs
            // =========================================================================
            val strokeWidth = (cellSize * 0.28f).coerceIn(8.5f, 15.0f)
            val headLength = (cellSize * 0.48f).coerceIn(16f, 26f)
            val headWidth = (cellSize * 0.44f).coerceIn(15f, 24f)
            val curveRadius = cellSize * 0.25f

            // Clean background
            drawRect(color = Color.White)

            // Blueprint grid dots in valid cells
            val dotRadius = 2.0f
            val dotColor = Color(0xFFE2E8F0)
            for (c in 0 until cols) {
                for (r in 0 until rows) {
                    val pt = GridPoint(c, r)
                    if (level.validCells.contains(pt)) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(originX + (c + 0.5f) * cellSize, originY + (r + 0.5f) * cellSize)
                        )
                    }
                }
            }

            // COLOR PALETTE MATCHING IMAGE 1:
            val primaryNavyColor = Color(0xFF0F172A)     // Dark navy path from image 1
            val electricBlueColor = Color(0xFF2563EB)    // Vibrant royal electric blue path from image 1
            val subtleRedWarning = Color(0xFFEF4444)     // Warning red

            // RENDER ALL ARROWS
            for (arrow in gameState.activeArrows) {
                val isHinted = arrow.id == gameState.hintedArrowId
                val isBlocked = arrow.id == gameState.blockedArrowId
                val isEscaping = arrow.id == gameState.escapingArrowId

                var alpha = 1f
                val currentPixelPoints: List<Offset>

                when {
                    isBlocked -> {
                        val forwardDist = blockedNudge.value * (cellSize * 0.22f)
                        val offX = arrow.direction.dx * forwardDist
                        val offY = arrow.direction.dy * forwardDist

                        currentPixelPoints = arrow.points.map { pt ->
                            Offset(
                                originX + (pt.col + 0.5f) * cellSize + offX,
                                originY + (pt.row + 0.5f) * cellSize + offY
                            )
                        }
                    }

                    isEscaping -> {
                        val fullExitDistance = max(canvasW, canvasH) * 1.4f
                        val prog = escapeProgress.value.coerceIn(0f, 1f)
                        val distTraveled = (prog * (0.82f + prog * 1.18f)) * fullExitDistance

                        alpha = if (prog > 0.84f) {
                            ((1f - prog) / 0.16f).coerceIn(0f, 1f)
                        } else {
                            1f
                        }

                        currentPixelPoints = getSlitheredArrowPoints(
                            arrow = arrow,
                            originX = originX,
                            originY = originY,
                            cellSize = cellSize,
                            distTraveled = distTraveled
                        )
                    }

                    else -> {
                        currentPixelPoints = arrow.points.map { pt ->
                            Offset(
                                originX + (pt.col + 0.5f) * cellSize,
                                originY + (pt.row + 0.5f) * cellSize
                            )
                        }
                    }
                }

                val arrowColor = when {
                    isEscaping -> electricBlueColor
                    isHinted -> electricBlueColor
                    isBlocked -> lerp(primaryNavyColor, subtleRedWarning, blockedRedFlash.value)
                    else -> primaryNavyColor
                }

                drawMazeArrow(
                    pixelPoints = currentPixelPoints,
                    direction = arrow.direction,
                    strokeWidth = strokeWidth,
                    headLength = headLength,
                    headWidth = headWidth,
                    curveRadius = curveRadius,
                    color = arrowColor,
                    alpha = alpha,
                    isHinted = isHinted,
                    hintPulseAlpha = hintPulse.value,
                    isEscaping = isEscaping
                )
            }
        }

        // Satisfying juicy cartoon particle explosion overlay
        JuicyParticleOverlay(
            controller = particleController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun getSlitheredArrowPoints(
    arrow: ArrowPiece,
    originX: Float,
    originY: Float,
    cellSize: Float,
    distTraveled: Float
): List<Offset> {
    val basePoints = arrow.points.map { pt ->
        Offset(
            originX + (pt.col + 0.5f) * cellSize,
            originY + (pt.row + 0.5f) * cellSize
        )
    }

    if (distTraveled <= 0f) return basePoints

    val cumDists = FloatArray(basePoints.size)
    cumDists[0] = 0f
    for (i in 1 until basePoints.size) {
        val dx = basePoints[i].x - basePoints[i - 1].x
        val dy = basePoints[i].y - basePoints[i - 1].y
        cumDists[i] = cumDists[i - 1] + hypot(dx.toDouble(), dy.toDouble()).toFloat()
    }

    val arrowBodyLength = cumDists.last()
    val headDir = arrow.direction
    val dirVec = Offset(headDir.dx.toFloat(), headDir.dy.toFloat())

    val currentHeadDist = arrowBodyLength + distTraveled
    val currentTailDist = distTraveled

    fun pointAtDistance(dist: Float): Offset {
        if (dist <= 0f) return basePoints.first()
        if (dist >= arrowBodyLength) {
            val extra = dist - arrowBodyLength
            return Offset(
                basePoints.last().x + dirVec.x * extra,
                basePoints.last().y + dirVec.y * extra
            )
        }
        for (i in 1 until basePoints.size) {
            if (dist <= cumDists[i]) {
                val segLen = cumDists[i] - cumDists[i - 1]
                if (segLen <= 0.0001f) return basePoints[i]
                val frac = (dist - cumDists[i - 1]) / segLen
                val p0 = basePoints[i - 1]
                val p1 = basePoints[i]
                return Offset(
                    p0.x + (p1.x - p0.x) * frac,
                    p0.y + (p1.y - p0.y) * frac
                )
            }
        }
        return basePoints.last()
    }

    val result = mutableListOf<Offset>()
    result.add(pointAtDistance(currentTailDist))

    for (i in 1 until basePoints.size - 1) {
        if (cumDists[i] > currentTailDist) {
            result.add(basePoints[i])
        }
    }

    if (currentTailDist < arrowBodyLength) {
        result.add(basePoints.last())
    }

    result.add(pointAtDistance(currentHeadDist))
    return result
}

private fun buildCurvedPath(
    pixelPoints: List<Offset>,
    cornerRadius: Float
): Path {
    val path = Path()
    if (pixelPoints.size < 2) return path

    if (pixelPoints.size == 2) {
        path.moveTo(pixelPoints[0].x, pixelPoints[0].y)
        path.lineTo(pixelPoints[1].x, pixelPoints[1].y)
        return path
    }

    path.moveTo(pixelPoints[0].x, pixelPoints[0].y)

    for (i in 1 until pixelPoints.size - 1) {
        val prev = pixelPoints[i - 1]
        val curr = pixelPoints[i]
        val next = pixelPoints[i + 1]

        val vPrevX = prev.x - curr.x
        val vPrevY = prev.y - curr.y
        val lenPrev = hypot(vPrevX.toDouble(), vPrevY.toDouble()).toFloat()

        val vNextX = next.x - curr.x
        val vNextY = next.y - curr.y
        val lenNext = hypot(vNextX.toDouble(), vNextY.toDouble()).toFloat()

        val actualR = minOf(cornerRadius, lenPrev * 0.45f, lenNext * 0.45f)

        if (actualR > 1f && lenPrev > 0.1f && lenNext > 0.1f) {
            val startCurveX = curr.x + (vPrevX / lenPrev) * actualR
            val startCurveY = curr.y + (vPrevY / lenPrev) * actualR

            val endCurveX = curr.x + (vNextX / lenNext) * actualR
            val endCurveY = curr.y + (vNextY / lenNext) * actualR

            path.lineTo(startCurveX, startCurveY)
            path.quadraticTo(curr.x, curr.y, endCurveX, endCurveY)
        } else {
            path.lineTo(curr.x, curr.y)
        }
    }

    path.lineTo(pixelPoints.last().x, pixelPoints.last().y)
    return path
}

private fun DrawScope.drawMazeArrow(
    pixelPoints: List<Offset>,
    direction: Direction,
    strokeWidth: Float,
    headLength: Float,
    headWidth: Float,
    curveRadius: Float,
    color: Color,
    alpha: Float,
    isHinted: Boolean,
    hintPulseAlpha: Float,
    isEscaping: Boolean
) {
    if (pixelPoints.size < 2) return

    val curvedPath = buildCurvedPath(
        pixelPoints = pixelPoints,
        cornerRadius = curveRadius
    )

    // Vibrant glowing aura when hinted or escaping (Electric Blue)
    if (isHinted || isEscaping) {
        val glowAlpha = if (isHinted) 0.55f * hintPulseAlpha * alpha else 0.45f * alpha
        drawPath(
            path = curvedPath,
            color = Color(0xFF60A5FA).copy(alpha = glowAlpha),
            style = Stroke(
                width = strokeWidth * 2.4f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // Main solid maze path line (matching Image 1)
    drawPath(
        path = curvedPath,
        color = color.copy(alpha = alpha),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Sharp clean arrowhead at head position (matching Image 1)
    val headCenter = pixelPoints.last()
    drawMazeArrowHead(
        headCenter = headCenter,
        direction = direction,
        headLength = headLength,
        headWidth = headWidth,
        color = color.copy(alpha = alpha)
    )
}

/**
 * Draws a sharp triangular maze arrowhead matching Image 1:
 * - Crisp triangular tip pointing in direction
 * - Symmetrical barb wings angled slightly back
 * - Small inner notch for crisp mechanical feel
 */
private fun DrawScope.drawMazeArrowHead(
    headCenter: Offset,
    direction: Direction,
    headLength: Float,
    headWidth: Float,
    color: Color
) {
    val tipX = headCenter.x + direction.dx * (headLength * 0.55f)
    val tipY = headCenter.y + direction.dy * (headLength * 0.55f)

    val perpX = -direction.dy
    val perpY = direction.dx

    val wing1X = headCenter.x - direction.dx * (headLength * 0.40f) + perpX * (headWidth * 0.50f)
    val wing1Y = headCenter.y - direction.dy * (headLength * 0.40f) + perpY * (headWidth * 0.50f)

    val wing2X = headCenter.x - direction.dx * (headLength * 0.40f) - perpX * (headWidth * 0.50f)
    val wing2Y = headCenter.y - direction.dy * (headLength * 0.40f) - perpY * (headWidth * 0.50f)

    val notchX = headCenter.x - direction.dx * (headLength * 0.12f)
    val notchY = headCenter.y - direction.dy * (headLength * 0.12f)

    val headPath = Path().apply {
        moveTo(tipX, tipY)
        lineTo(wing1X, wing1Y)
        lineTo(notchX, notchY)
        lineTo(wing2X, wing2Y)
        close()
    }

    drawPath(path = headPath, color = color)
}

private fun distancePointToSegment(p: Offset, a: Offset, b: Offset): Float {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val l2 = dx * dx + dy * dy
    if (l2 == 0f) return hypot((p.x - a.x).toDouble(), (p.y - a.y).toDouble()).toFloat()

    val t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / l2
    val clampedT = t.coerceIn(0f, 1f)
    val projX = a.x + clampedT * dx
    val projY = a.y + clampedT * dy
    return hypot((p.x - projX).toDouble(), (p.y - projY).toDouble()).toFloat()
}
