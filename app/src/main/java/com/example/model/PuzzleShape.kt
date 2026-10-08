package com.example.model

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class PuzzleShape(val displayName: String, val badge: String) {
    HEART("Heart", "♥"),
    STAR("Star", "★"),
    SUN("Sun", "☀"),
    MOON("Moon", "☾"),
    LIGHTNING("Lightning", "⚡"),
    FLOWER("Flower", "🌸"),
    DIAMOND("Diamond", "💎"),
    CROWN("Crown", "👑"),
    BUTTERFLY("Butterfly", "🦋"),
    SMILE("Happy Face", "😊"),
    ROCKET("Rocket", "🚀"),
    CAT("Cat Face", "🐱"),
    MUSIC_NOTE("Music Note", "🎵"),
    KEY("Key", "🗝️"),
    INFINITY("Infinity", "♾️"),
    SPIRAL("Spiral", "🌀"),
    TREE("Tree", "🌲"),
    GIFT("Gift", "🎁"),
    CLOUD("Cloud", "☁️"),
    PLANET("Planet", "🪐"),
    KNIGHT("Horse Knight", "♞"),
    HOUSE("House", "🏠"),
    HOURGLASS("Hourglass", "⌛"),
    DOVE("Dove", "🕊️"),
    APPLE("Apple", "🍎"),
    RECTANGLE("Classic", "⬛");

    companion object {
        fun forLevel(levelNumber: Int): PuzzleShape {
            return when (levelNumber) {
                1 -> HEART
                2 -> STAR
                3 -> SUN
                4 -> MOON
                5 -> LIGHTNING
                6 -> FLOWER
                7 -> DIAMOND
                8 -> CROWN
                9 -> BUTTERFLY
                10 -> SMILE
                11 -> ROCKET
                12 -> CAT
                13 -> MUSIC_NOTE
                14 -> KEY
                15 -> INFINITY
                16 -> SPIRAL
                17 -> TREE
                18 -> GIFT
                19 -> CLOUD
                20 -> PLANET
                else -> {
                    val allShapes = entries.toTypedArray()
                    allShapes[(levelNumber - 1) % allShapes.size]
                }
            }
        }
    }
}

object ShapeMaskProvider {

    /**
     * Generates a precise, recognizable shape silhouette mask on a grid of (cols x rows).
     * The arrow paths in LevelGenerator are packed strictly within these valid cells,
     * so the complete network of arrows visually and structurally forms the target shape!
     */
    fun getValidCells(shape: PuzzleShape, cols: Int, rows: Int): Set<GridPoint> {
        val valid = mutableSetOf<GridPoint>()
        val cx = (cols - 1) / 2.0
        val cy = (rows - 1) / 2.0

        for (c in 0 until cols) {
            for (r in 0 until rows) {
                val nx = (c - cx) / (cols / 2.0)
                val ny = (r - cy) / (rows / 2.0)

                val isInside = when (shape) {
                    PuzzleShape.HEART -> {
                        // Cardioid heart mathematical silhouette
                        val hx = (c - cx) / (cols * 0.44)
                        val hy = -(r - (rows - 1) * 0.42) / (rows * 0.44)
                        val term = hx * hx + hy * hy - 1.0
                        (term * term * term - hx * hx * hy * hy * hy) <= 0.08
                    }

                    PuzzleShape.STAR -> {
                        // 5-pointed star
                        val dx = c - cx
                        val dy = r - cy
                        val dist = sqrt(dx * dx + dy * dy)
                        val angle = atan2(dy, dx)
                        val maxR = (minOf(cols, rows) - 1) / 2.0
                        val starR = maxR * (0.68 + 0.32 * cos(5.0 * angle))
                        dist <= starR + 0.4
                    }

                    PuzzleShape.SUN -> {
                        // Sun: Central circular disc + radiant sun rays
                        val dist = sqrt(nx * nx + ny * ny)
                        val angle = atan2(ny, nx)
                        val rayR = 0.52 + 0.40 * abs(cos(4.0 * angle))
                        dist <= rayR
                    }

                    PuzzleShape.MOON -> {
                        // Crescent Moon: Outer disc minus offset inner circle
                        val dOuter = sqrt(nx * nx + ny * ny)
                        val innerX = nx + 0.40
                        val innerY = ny - 0.10
                        val dInner = sqrt(innerX * innerX + innerY * innerY)
                        dOuter <= 0.90 && dInner >= 0.56
                    }

                    PuzzleShape.LIGHTNING -> {
                        // Sharp lightning bolt zigzag
                        val isTopDiag = (ny in -0.92..0.08 && (nx - ny * 0.75) in -0.46..0.46)
                        val isMidBar = (ny in -0.22..0.22 && nx in -0.80..0.72)
                        val isBottomDiag = (ny in 0.02..0.94 && (nx - ny * 0.85) in -0.46..0.38)
                        isTopDiag || isMidBar || isBottomDiag
                    }

                    PuzzleShape.FLOWER -> {
                        // 6-petal flower
                        val dist = sqrt(nx * nx + ny * ny)
                        val angle = atan2(ny, nx)
                        val petalR = 0.48 + 0.38 * cos(6.0 * angle)
                        dist <= petalR || dist <= 0.35
                    }

                    PuzzleShape.DIAMOND -> {
                        // Gem Diamond: Faceted top + tapered bottom
                        val isTop = (ny in -0.85..-0.20 && abs(nx) <= (ny + 0.95) * 1.5 && abs(nx) <= 0.88)
                        val isBottom = (ny in -0.20..0.85 && abs(nx) <= (0.85 - ny) * 0.95)
                        isTop || isBottom
                    }

                    PuzzleShape.CROWN -> {
                        // 3-Pointed Royal Crown
                        val isBase = (nx in -0.85..0.85 && ny in 0.25..0.82)
                        val isLeftPeak = (nx in -0.85..-0.40 && ny in -0.65..0.30)
                        val isCenterPeak = (nx in -0.28..0.28 && ny in -0.90..0.30)
                        val isRightPeak = (nx in 0.40..0.85 && ny in -0.65..0.30)
                        isBase || isLeftPeak || isCenterPeak || isRightPeak
                    }

                    PuzzleShape.BUTTERFLY -> {
                        // Symmetrical butterfly wings + thorax
                        val mx = abs(nx)
                        val isBody = (mx <= 0.18 && ny in -0.85..0.85)
                        val isUpper = (mx in 0.12..0.88 && ny in -0.80..-0.02 && (mx + ny) <= 0.68)
                        val isLower = (mx in 0.12..0.76 && ny in 0.00..0.78 && (mx - ny) <= 0.48)
                        isBody || isUpper || isLower
                    }

                    PuzzleShape.SMILE -> {
                        // Happy face silhouette: round head with smile and eyes
                        val dist = sqrt(nx * nx + ny * ny)
                        val isFace = dist <= 0.88
                        val isEyeLeft = (nx in -0.45..-0.15 && ny in -0.50..-0.20)
                        val isEyeRight = (nx in 0.15..0.45 && ny in -0.50..-0.20)
                        isFace && !(isEyeLeft || isEyeRight)
                    }

                    PuzzleShape.ROCKET -> {
                        // Rocket fuselage + nosecone + side fins
                        val isNose = (ny in -0.92..-0.45 && abs(nx) <= (ny + 0.95) * 0.95)
                        val isBody = (abs(nx) <= 0.45 && ny in -0.45..0.55)
                        val isLeftFin = (nx in -0.88..-0.40 && ny in 0.25..0.85 && (nx + ny) >= -0.3)
                        val isRightFin = (nx in 0.40..0.88 && ny in 0.25..0.85 && (ny - nx) >= -0.3)
                        isNose || isBody || isLeftFin || isRightFin
                    }

                    PuzzleShape.CAT -> {
                        // Cat face with pointed ears and round head
                        val isEars = (ny in -0.92..-0.45 && (nx in -0.82..-0.25 || nx in 0.25..0.82))
                        val isHead = (sqrt(nx * nx + (ny - 0.15) * (ny - 0.15)) <= 0.78)
                        isEars || isHead
                    }

                    PuzzleShape.MUSIC_NOTE -> {
                        // Eighth note: dual note heads + stems + crossbar
                        val isNote1 = (nx in -0.80..-0.25 && ny in 0.25..0.80)
                        val isStem1 = (nx in -0.40..-0.18 && ny in -0.75..0.40)
                        val isNote2 = (nx in 0.20..0.75 && ny in 0.10..0.65)
                        val isStem2 = (nx in 0.55..0.78 && ny in -0.75..0.25)
                        val isBar = (nx in -0.40..0.78 && ny in -0.85..-0.55)
                        isNote1 || isStem1 || isNote2 || isStem2 || isBar
                    }

                    PuzzleShape.KEY -> {
                        // Skeleton key: round bow handle + long shaft + teeth
                        val isBow = (sqrt(nx * nx + (ny + 0.50) * (ny + 0.50)) in 0.22..0.48)
                        val isShaft = (abs(nx) <= 0.18 && ny in -0.20..0.85)
                        val isTooth1 = (nx in 0.15..0.55 && ny in 0.50..0.65)
                        val isTooth2 = (nx in 0.15..0.45 && ny in 0.72..0.85)
                        isBow || isShaft || isTooth1 || isTooth2
                    }

                    PuzzleShape.INFINITY -> {
                        // Lemniscate infinity figure-8
                        val x = nx * 1.2
                        val y = ny * 1.2
                        val a = 0.8
                        val d = (x * x + y * y) * (x * x + y * y) - 2.0 * a * a * (x * x - y * y)
                        abs(d) <= 0.38
                    }

                    PuzzleShape.SPIRAL -> {
                        // Archimedean spiral loop
                        val dist = sqrt(nx * nx + ny * ny)
                        val angle = (atan2(ny, nx) + 2 * Math.PI) % (2 * Math.PI)
                        val expectedR = 0.15 + (angle / (2 * Math.PI)) * 0.70
                        abs(dist - expectedR) <= 0.26 && dist <= 0.90
                    }

                    PuzzleShape.TREE -> {
                        // Evergreen pine tree: 3 tiers + trunk
                        val isTier1 = (ny in -0.90..-0.45 && abs(nx) <= (ny + 0.92) * 1.1)
                        val isTier2 = (ny in -0.45..0.05 && abs(nx) <= (ny + 0.55) * 1.3)
                        val isTier3 = (ny in 0.05..0.55 && abs(nx) <= (ny + 0.15) * 1.5)
                        val isTrunk = (abs(nx) <= 0.22 && ny in 0.55..0.88)
                        isTier1 || isTier2 || isTier3 || isTrunk
                    }

                    PuzzleShape.GIFT -> {
                        // Gift box with ribbon knot
                        val isBox = (abs(nx) <= 0.78 && ny in -0.45..0.82)
                        val isBow = (ny in -0.85..-0.45 && (abs(nx) in 0.08..0.48))
                        isBox || isBow
                    }

                    PuzzleShape.CLOUD -> {
                        // Puffy cloud
                        val isBase = (abs(nx) <= 0.82 && ny in 0.05..0.55)
                        val isPuff1 = (sqrt((nx + 0.42) * (nx + 0.42) + (ny + 0.10) * (ny + 0.10)) <= 0.38)
                        val isPuff2 = (sqrt(nx * nx + (ny + 0.25) * (ny + 0.25)) <= 0.46)
                        val isPuff3 = (sqrt((nx - 0.42) * (nx - 0.42) + (ny + 0.05) * (ny + 0.05)) <= 0.38)
                        isBase || isPuff1 || isPuff2 || isPuff3
                    }

                    PuzzleShape.PLANET -> {
                        // Saturn-like planet with ring
                        val isSphere = (sqrt(nx * nx + ny * ny) <= 0.55)
                        val isRing = (abs(ny + nx * 0.38) <= 0.16 && abs(nx) <= 0.92)
                        isSphere || isRing
                    }

                    PuzzleShape.KNIGHT -> {
                        val isEars = (nx in -0.30..0.30 && ny in -0.95..-0.65)
                        val isSnout = (nx in -0.90..-0.25 && ny in -0.65..-0.20)
                        val isMane = (nx in 0.10..0.75 && ny in -0.70..0.30)
                        val isNeck = (nx in -0.45..0.45 && ny in -0.25..0.55)
                        val isBase = (nx in -0.80..0.80 && ny in 0.55..0.90)
                        isEars || isSnout || isMane || isNeck || isBase
                    }

                    PuzzleShape.HOUSE -> {
                        val isRoof = (ny in -0.85..-0.15 && abs(nx) <= (ny + 0.90) * 1.25)
                        val isChimney = (nx in 0.35..0.65 && ny in -0.85..-0.35)
                        val isWalls = (abs(nx) <= 0.78 && ny in -0.15..0.85)
                        isRoof || isChimney || isWalls
                    }

                    PuzzleShape.HOURGLASS -> {
                        val maxAllowedWidth = 0.22 + 0.65 * abs(ny)
                        val isBody = (abs(nx) <= maxAllowedWidth && abs(ny) <= 0.88)
                        val isBase = (abs(nx) <= 0.85 && (abs(ny) in 0.75..0.92))
                        isBody || isBase
                    }

                    PuzzleShape.DOVE -> {
                        val isHead = (nx in -0.85..-0.45 && ny in -0.65..-0.20)
                        val isBody = (nx in -0.50..0.40 && ny in -0.30..0.35)
                        val isWing = (nx in -0.25..0.75 && ny in -0.90..-0.20)
                        isHead || isBody || isWing
                    }

                    PuzzleShape.APPLE -> {
                        val isFruit = (sqrt(nx * nx + ny * ny) <= 0.78)
                        val isStem = (abs(nx) <= 0.12 && ny in -0.95..-0.70)
                        isFruit || isStem
                    }

                    PuzzleShape.RECTANGLE -> true
                }

                if (isInside) {
                    valid.add(GridPoint(c, r))
                }
            }
        }

        // Safety fallback: ensure at least 24 cells are present for rich 10+ arrow tessellation
        if (valid.size < 24) {
            for (c in 1 until cols - 1) {
                for (r in 1 until rows - 1) {
                    valid.add(GridPoint(c, r))
                }
            }
        }

        return valid
    }
}
