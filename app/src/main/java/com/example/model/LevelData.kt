package com.example.model

data class LevelData(
    val levelNumber: Int,
    val cols: Int,
    val rows: Int,
    val arrows: List<ArrowPiece>,
    val difficulty: String = "Normal",
    val difficultyScore: Int = 10,
    val parMoves: Int = arrows.size,
    val shape: PuzzleShape = PuzzleShape.RECTANGLE,
    val validCells: Set<GridPoint> = emptySet()
) {
    val totalArrows: Int get() = arrows.size

    fun isCellValid(point: GridPoint): Boolean {
        return if (validCells.isEmpty()) {
            point.isInside(cols, rows)
        } else {
            validCells.contains(point)
        }
    }

    fun calculateStars(movesTaken: Int, mistakes: Int): Int {
        return when {
            mistakes == 0 && movesTaken <= parMoves -> 3
            mistakes <= 2 && movesTaken <= parMoves + 3 -> 2
            else -> 1
        }
    }
}
