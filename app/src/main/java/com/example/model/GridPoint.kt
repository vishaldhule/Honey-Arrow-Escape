package com.example.model

data class GridPoint(val col: Int, val row: Int) {
    operator fun plus(dir: Direction): GridPoint {
        return GridPoint(col + dir.dx, row + dir.dy)
    }

    operator fun plus(other: GridPoint): GridPoint {
        return GridPoint(col + other.col, row + other.row)
    }

    operator fun minus(other: GridPoint): GridPoint {
        return GridPoint(col - other.col, row - other.row)
    }

    fun isInside(cols: Int, rows: Int): Boolean {
        return col in 0 until cols && row in 0 until rows
    }

    fun manhattanDistance(other: GridPoint): Int {
        return kotlin.math.abs(col - other.col) + kotlin.math.abs(row - other.row)
    }
}
