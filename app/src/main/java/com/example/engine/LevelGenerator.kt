package com.example.engine

import com.example.model.ArrowPiece
import com.example.model.Direction
import com.example.model.GridPoint
import com.example.model.LevelData
import com.example.model.PuzzleShape
import com.example.model.ShapeMaskProvider
import java.util.Random

object LevelGenerator {

    private val levelCache = mutableMapOf<Int, LevelData>()

    fun getLevel(levelNumber: Int): LevelData {
        val clampedLevel = levelNumber.coerceIn(1, 1000)
        levelCache[clampedLevel]?.let { return it }

        val level = generateGuaranteedSolvableShapeLevel(clampedLevel)
        levelCache[clampedLevel] = level
        return level
    }

    fun getDailyLevel(dateSeed: Int): LevelData {
        val seed = dateSeed.toLong() * 90123L + 777L
        val rng = Random(seed)
        return generateLevelWithSeed(
            levelNumber = 0,
            shape = PuzzleShape.HEART,
            cols = 9,
            rows = 9,
            targetArrowCount = 14,
            difficultyLabel = "Daily Challenge",
            rng = rng
        )
    }

    data class LevelConfig(
        val cols: Int,
        val rows: Int,
        val shape: PuzzleShape,
        val targetArrowCount: Int,
        val difficultyLabel: String
    )

    fun getConfigurationForLevel(level: Int): LevelConfig {
        val shape = PuzzleShape.forLevel(level)

        val (cols, rows, targetArrows) = when (level) {
            1 -> Triple(8, 8, 11)   // Level 1: Heart (10-12 arrows)
            2 -> Triple(10, 10, 13) // Level 2: Star (12-14 arrows)
            3 -> Triple(10, 10, 15) // Level 3: Sun (14-16 arrows)
            4 -> Triple(11, 11, 17) // Level 4: Moon (16-18 arrows)
            5 -> Triple(11, 11, 19) // Level 5: Lightning (18-20 arrows)
            6 -> Triple(11, 11, 21) // Level 6: Flower (20-22 arrows)
            7 -> Triple(11, 11, 23) // Level 7: Diamond (22-24 arrows)
            8 -> Triple(12, 12, 24) // Level 8: Crown (22-25 arrows)
            9 -> Triple(12, 12, 25) // Level 9: Butterfly (24-26 arrows)
            10 -> Triple(12, 12, 26)// Level 10: Smile (25-28 arrows)
            in 11..15 -> Triple(12, 12, 28)
            in 16..25 -> Triple(12, 12, 32)
            else -> Triple(13, 13, 36)
        }

        val difficulty = when {
            level <= 3 -> "Easy"
            level <= 8 -> "Medium"
            level <= 15 -> "Hard"
            else -> "Expert"
        }

        return LevelConfig(
            cols = cols,
            rows = rows,
            shape = shape,
            targetArrowCount = targetArrows,
            difficultyLabel = difficulty
        )
    }

    private fun generateGuaranteedSolvableShapeLevel(levelNumber: Int): LevelData {
        val config = getConfigurationForLevel(levelNumber)
        val seed = levelNumber.toLong() * 104729L + 31337L

        for (attempt in 0..120) {
            val rng = Random(seed + attempt * 7919L)
            val level = generateLevelWithSeed(
                levelNumber = levelNumber,
                shape = config.shape,
                cols = config.cols,
                rows = config.rows,
                targetArrowCount = config.targetArrowCount,
                difficultyLabel = config.difficultyLabel,
                rng = rng
            )

            if (level.arrows.size >= config.targetArrowCount) {
                val solveResult = PuzzleSolver.solve(level)
                if (solveResult is PuzzleSolver.SolveResult.Solvable) {
                    val score = PuzzleSolver.calculateDifficulty(
                        cols = level.cols,
                        rows = level.rows,
                        arrowCount = level.arrows.size,
                        solveResult = solveResult
                    )
                    return level.copy(
                        difficultyScore = score,
                        parMoves = level.arrows.size
                    )
                }
            }
        }

        return createFallbackSolvableLevel(levelNumber, config)
    }

    private fun generateLevelWithSeed(
        levelNumber: Int,
        shape: PuzzleShape,
        cols: Int,
        rows: Int,
        targetArrowCount: Int,
        difficultyLabel: String,
        rng: Random
    ): LevelData {
        val validCells = ShapeMaskProvider.getValidCells(shape, cols, rows).toMutableSet()
        val totalValid = validCells.size

        val desiredArrowCount = maxOf(targetArrowCount, 10)
        val avgLength = (totalValid.toDouble() / desiredArrowCount).coerceIn(2.0, 2.7)

        val occupied = Array(cols) { Array<String?>(rows) { null } }
        val placedArrows = mutableListOf<ArrowPiece>()
        val unfilled = validCells.toMutableSet()

        val directions = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
        var arrowIndex = 1

        var loop = 0
        while (unfilled.isNotEmpty() && loop < 2000) {
            loop++
            val start = unfilled.shuffled(rng).firstOrNull() ?: break
            val path = mutableListOf(start)
            val neededArrows = targetArrowCount - placedArrows.size
            val remainingCells = unfilled.size

            val targetLen = if (neededArrows > 0 && remainingCells <= neededArrows * 2 + 2) {
                2
            } else if (rng.nextDouble() < 0.65) {
                avgLength.toInt().coerceAtLeast(2)
            } else {
                (avgLength + 0.6).toInt().coerceIn(2, 3)
            }
            var curr = start
            var prevDir: Direction? = null

            for (step in 1 until targetLen) {
                val neighbors = directions
                    .map { dir -> Pair(curr + dir, dir) }
                    .filter { (pt, _) -> unfilled.contains(pt) && !path.contains(pt) }

                if (neighbors.isEmpty()) break

                val chosen = if (prevDir != null && rng.nextDouble() < 0.45) {
                    neighbors.firstOrNull { it.second != prevDir } ?: neighbors.first()
                } else {
                    neighbors.shuffled(rng).first()
                }

                path.add(chosen.first)
                curr = chosen.first
                prevDir = chosen.second
            }

            if (path.size >= 2) {
                val arrowId = "arrow_${arrowIndex++}"
                val finalPoints = if (rng.nextBoolean()) path.reversed() else path
                val arrow = ArrowPiece(id = arrowId, points = finalPoints, colorIndex = 0)

                for (pt in arrow.points) {
                    occupied[pt.col][pt.row] = arrowId
                    unfilled.remove(pt)
                }
                placedArrows.add(arrow)
            }
        }

        // 1. Pair any remaining orphan cells into new 2-cell arrows FIRST to maximize distinct paths
        if (unfilled.size >= 2) {
            val remainingList = unfilled.toList()
            for (i in remainingList.indices) {
                val p1 = remainingList[i]
                if (!unfilled.contains(p1)) continue
                for (j in i + 1 until remainingList.size) {
                    val p2 = remainingList[j]
                    if (!unfilled.contains(p2)) continue
                    val isAdjacent = (kotlin.math.abs(p1.col - p2.col) + kotlin.math.abs(p1.row - p2.row)) == 1
                    if (isAdjacent) {
                        val arrowId = "arrow_${arrowIndex++}"
                        val arrow = ArrowPiece(id = arrowId, points = listOf(p1, p2), colorIndex = 0)
                        occupied[p1.col][p1.row] = arrowId
                        occupied[p2.col][p2.row] = arrowId
                        unfilled.remove(p1)
                        unfilled.remove(p2)
                        placedArrows.add(arrow)
                        break
                    }
                }
            }
        }

        // 2. Absorption pass: absorbs any single leftover cells into adjacent arrow tails/heads
        if (unfilled.isNotEmpty() && placedArrows.isNotEmpty()) {
            val leftovers = unfilled.toList()
            for (orphan in leftovers) {
                var absorbed = false
                for (i in placedArrows.indices) {
                    val arrow = placedArrows[i]
                    if (arrow.points.size >= 4) continue
                    val tail = arrow.tail
                    val isAdjacent = (kotlin.math.abs(tail.col - orphan.col) + kotlin.math.abs(tail.row - orphan.row)) == 1
                    if (isAdjacent && !arrow.points.contains(orphan)) {
                        placedArrows[i] = arrow.copy(points = listOf(orphan) + arrow.points)
                        occupied[orphan.col][orphan.row] = arrow.id
                        unfilled.remove(orphan)
                        absorbed = true
                        break
                    }
                }
                if (!absorbed) {
                    for (i in placedArrows.indices) {
                        val arrow = placedArrows[i]
                        if (arrow.points.size >= 4) continue
                        val head = arrow.head
                        val isAdjacent = (kotlin.math.abs(head.col - orphan.col) + kotlin.math.abs(head.row - orphan.row)) == 1
                        if (isAdjacent && !arrow.points.contains(orphan)) {
                            placedArrows[i] = arrow.copy(points = arrow.points + listOf(orphan))
                            occupied[orphan.col][orphan.row] = arrow.id
                            unfilled.remove(orphan)
                            absorbed = true
                            break
                        }
                    }
                }
            }
        }

        val finalValidCells = placedArrows.flatMap { it.points }.toSet()
        resolveSolvability(placedArrows, cols, rows)

        return LevelData(
            levelNumber = levelNumber,
            cols = cols,
            rows = rows,
            arrows = placedArrows,
            difficulty = difficultyLabel,
            difficultyScore = 40,
            parMoves = placedArrows.size,
            shape = shape,
            validCells = finalValidCells
        )
    }

    private fun resolveSolvability(arrows: MutableList<ArrowPiece>, cols: Int, rows: Int) {
        if (PuzzleSolver.solve(LevelData(1, cols, rows, arrows)) is PuzzleSolver.SolveResult.Solvable) {
            return
        }

        val unsolved = arrows.indices.toMutableSet()

        while (unsolved.isNotEmpty()) {
            var found = false
            for (idx in unsolved.toList()) {
                val arrow = arrows[idx]
                val currentRemaining = unsolved.filter { it != idx }.map { arrows[it] }

                if (!arrow.isBlockedBy(currentRemaining, cols, rows)) {
                    unsolved.remove(idx)
                    found = true
                    break
                }

                val flipped = arrow.copy(points = arrow.points.reversed())
                if (!flipped.isBlockedBy(currentRemaining, cols, rows)) {
                    arrows[idx] = flipped
                    unsolved.remove(idx)
                    found = true
                    break
                }
            }

            if (!found) {
                val bestIdx = unsolved.minByOrNull { idx ->
                    val a = arrows[idx]
                    val others = unsolved.filter { it != idx }.map { arrows[it] }
                    val b1 = a.getExitRay(cols, rows).count { pt -> others.any { it.occupies(pt) } }
                    val flipped = a.copy(points = a.points.reversed())
                    val b2 = flipped.getExitRay(cols, rows).count { pt -> others.any { it.occupies(pt) } }
                    minOf(b1, b2)
                } ?: break

                val a = arrows[bestIdx]
                val others = unsolved.filter { it != bestIdx }.map { arrows[it] }
                val b1 = a.getExitRay(cols, rows).count { pt -> others.any { it.occupies(pt) } }
                val flipped = a.copy(points = a.points.reversed())
                val b2 = flipped.getExitRay(cols, rows).count { pt -> others.any { it.occupies(pt) } }

                arrows[bestIdx] = if (b2 < b1) flipped else a
                unsolved.remove(bestIdx)
            }
        }
    }

    private fun createFallbackSolvableLevel(levelNumber: Int, config: LevelConfig): LevelData {
        val cols = config.cols
        val rows = config.rows
        val shapeMask = ShapeMaskProvider.getValidCells(config.shape, cols, rows)
        val arrows = mutableListOf<ArrowPiece>()
        val visited = mutableSetOf<GridPoint>()
        var count = 0

        // Tile horizontally from edges inward
        for (r in 0 until rows) {
            var c = 0
            while (c < cols - 1) {
                val p1 = GridPoint(c, r)
                val p2 = GridPoint(c + 1, r)
                if (shapeMask.contains(p1) && shapeMask.contains(p2) &&
                    !visited.contains(p1) && !visited.contains(p2)
                ) {
                    val pts = if (c < cols / 2) listOf(p2, p1) else listOf(p1, p2)
                    arrows.add(ArrowPiece(id = "arrow_${++count}", points = pts, colorIndex = 0))
                    visited.add(p1)
                    visited.add(p2)
                    c += 2
                } else {
                    c++
                }
            }
        }

        // Tile vertically
        for (c in 0 until cols) {
            var r = 0
            while (r < rows - 1) {
                val p1 = GridPoint(c, r)
                val p2 = GridPoint(c, r + 1)
                if (shapeMask.contains(p1) && shapeMask.contains(p2) &&
                    !visited.contains(p1) && !visited.contains(p2)
                ) {
                    val pts = if (r < rows / 2) listOf(p2, p1) else listOf(p1, p2)
                    arrows.add(ArrowPiece(id = "arrow_${++count}", points = pts, colorIndex = 0))
                    visited.add(p1)
                    visited.add(p2)
                    r += 2
                } else {
                    r++
                }
            }
        }

        val finalValidCells = arrows.flatMap { it.points }.toSet()
        resolveSolvability(arrows, cols, rows)

        return LevelData(
            levelNumber = levelNumber,
            cols = cols,
            rows = rows,
            arrows = arrows,
            difficulty = config.difficultyLabel,
            difficultyScore = 40,
            parMoves = arrows.size,
            shape = config.shape,
            validCells = finalValidCells
        )
    }
}
