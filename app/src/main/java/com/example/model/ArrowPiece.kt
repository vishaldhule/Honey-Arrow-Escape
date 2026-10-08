package com.example.model

enum class ArrowShapeType(val label: String) {
    STRAIGHT("Straight"),
    L_BEND("L-Curve"),
    U_TURN("U-Turn"),
    S_CURVE("S-Curve"),
    HOOK("Hook Loop")
}

data class ArrowPiece(
    val id: String,
    val points: List<GridPoint>,
    val colorIndex: Int = 0
) {
    init {
        require(points.size >= 2) { "Arrow must have at least 2 points (tail and head)" }
    }

    val head: GridPoint get() = points.last()
    val tail: GridPoint get() = points.first()

    val direction: Direction
        get() {
            val prev = points[points.size - 2]
            val h = points.last()
            return when {
                h.col > prev.col -> Direction.RIGHT
                h.col < prev.col -> Direction.LEFT
                h.row > prev.row -> Direction.DOWN
                else -> Direction.UP
            }
        }

    val shapeType: ArrowShapeType
        get() {
            if (points.size <= 2) return ArrowShapeType.STRAIGHT
            val turns = mutableListOf<Direction>()
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val dir = when {
                    p2.col > p1.col -> Direction.RIGHT
                    p2.col < p1.col -> Direction.LEFT
                    p2.row > p1.row -> Direction.DOWN
                    else -> Direction.UP
                }
                if (turns.isEmpty() || turns.last() != dir) {
                    turns.add(dir)
                }
            }
            return when (turns.size) {
                1 -> ArrowShapeType.STRAIGHT
                2 -> ArrowShapeType.L_BEND
                3 -> {
                    // Check if U-turn or S-curve
                    val d1 = turns[0]
                    val d3 = turns[2]
                    if (d1.opposite == d3) ArrowShapeType.U_TURN else ArrowShapeType.S_CURVE
                }
                else -> ArrowShapeType.HOOK
            }
        }

    fun occupies(point: GridPoint): Boolean = points.contains(point)

    /**
     * Returns the straight line of cells in front of the arrowhead until out of board bounds.
     */
    fun getExitRay(cols: Int, rows: Int): List<GridPoint> {
        val ray = mutableListOf<GridPoint>()
        var curr = head + direction
        while (curr.isInside(cols, rows)) {
            ray.add(curr)
            curr += direction
        }
        return ray
    }

    /**
     * Checks if this arrow is blocked by any other active arrow on the board.
     * An arrow is blocked if ANY active arrow occupies ANY cell in its exit ray.
     */
    fun isBlockedBy(activeArrows: List<ArrowPiece>, cols: Int, rows: Int): Boolean {
        val exitRay = getExitRay(cols, rows)
        if (exitRay.isEmpty()) return false // Already at board boundary, free to escape!

        for (other in activeArrows) {
            if (other.id == this.id) continue
            for (rayPoint in exitRay) {
                if (other.occupies(rayPoint)) {
                    return true
                }
            }
        }
        return false
    }
}
