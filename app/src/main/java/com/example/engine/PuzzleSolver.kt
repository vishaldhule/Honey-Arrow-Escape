package com.example.engine

import com.example.model.ArrowPiece
import com.example.model.LevelData

object PuzzleSolver {

    sealed class SolveResult {
        data class Solvable(
            val steps: List<String>,
            val maxSimultaneousMoves: Int,
            val minDepth: Int
        ) : SolveResult()

        data object Unsolvable : SolveResult()
    }

    /**
     * Determines whether an arrow has an unobstructed exit path.
     */
    fun isArrowClear(arrow: ArrowPiece, activeArrows: List<ArrowPiece>, cols: Int, rows: Int): Boolean {
        return !arrow.isBlockedBy(activeArrows, cols, rows)
    }

    /**
     * Finds all arrows that can currently escape without being blocked.
     */
    fun findClearArrows(activeArrows: List<ArrowPiece>, cols: Int, rows: Int): List<ArrowPiece> {
        return activeArrows.filter { isArrowClear(it, activeArrows, cols, rows) }
    }

    /**
     * Returns a valid next move to suggest as a hint.
     */
    fun findHint(activeArrows: List<ArrowPiece>, cols: Int, rows: Int): ArrowPiece? {
        val clear = findClearArrows(activeArrows, cols, rows)
        return clear.firstOrNull()
    }

    /**
     * Checks if the player is stuck (arrows remain but none can move).
     */
    fun isStuck(activeArrows: List<ArrowPiece>, cols: Int, rows: Int): Boolean {
        return activeArrows.isNotEmpty() && findClearArrows(activeArrows, cols, rows).isEmpty()
    }

    /**
     * Solves the puzzle from the initial state using a dependency solver.
     */
    fun solve(level: LevelData): SolveResult {
        val remaining = level.arrows.toMutableList()
        val solutionOrder = mutableListOf<String>()
        var maxSimultaneous = 0
        var depth = 0

        while (remaining.isNotEmpty()) {
            val clear = findClearArrows(remaining, level.cols, level.rows)
            if (clear.isEmpty()) {
                return SolveResult.Unsolvable
            }
            if (clear.size > maxSimultaneous) {
                maxSimultaneous = clear.size
            }
            // Remove the first clear arrow
            val chosen = clear.first()
            solutionOrder.add(chosen.id)
            remaining.remove(chosen)
            depth++
        }

        return SolveResult.Solvable(
            steps = solutionOrder,
            maxSimultaneousMoves = maxSimultaneous,
            minDepth = depth
        )
    }

    /**
     * Computes a difficulty score from 1 to 100 based on board parameters.
     */
    fun calculateDifficulty(
        cols: Int,
        rows: Int,
        arrowCount: Int,
        solveResult: SolveResult.Solvable
    ): Int {
        val sizeScore = ((cols * rows) / 100.0f) * 25f
        val countScore = (arrowCount / 40.0f).coerceAtMost(1.0f) * 35f
        val depthRatio = (solveResult.minDepth.toFloat() / arrowCount).coerceIn(0.5f, 1.0f) * 20f
        val choiceFactor = (1.0f - (solveResult.maxSimultaneousMoves.toFloat() / arrowCount).coerceIn(0.1f, 0.8f)) * 20f

        val raw = (sizeScore + countScore + depthRatio + choiceFactor).toInt()
        return raw.coerceIn(1, 100)
    }
}
