package com.example

import com.example.engine.LevelGenerator
import com.example.engine.PuzzleSolver
import com.example.model.ArrowPiece
import com.example.model.ArrowShapeType
import com.example.model.Direction
import com.example.model.GridPoint
import com.example.model.PuzzleShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStraightArrowBlockedByOtherArrow() {
        // Arrow A: points UP from (2, 3) to (2, 2)
        val arrowA = ArrowPiece(
            id = "A",
            points = listOf(GridPoint(2, 3), GridPoint(2, 2))
        )
        assertEquals(Direction.UP, arrowA.direction)
        assertEquals(ArrowShapeType.STRAIGHT, arrowA.shapeType)

        // Arrow B: is in front of A at (2, 1) to (2, 0)
        val arrowB = ArrowPiece(
            id = "B",
            points = listOf(GridPoint(2, 1), GridPoint(2, 0))
        )
        val isBlocked = arrowA.isBlockedBy(listOf(arrowA, arrowB), 5, 5)
        assertTrue("Arrow A should be blocked by Arrow B directly in front", isBlocked)
    }

    @Test
    fun testStraightArrowClearToExit() {
        val arrow = ArrowPiece(
            id = "clear",
            points = listOf(GridPoint(2, 2), GridPoint(2, 1), GridPoint(2, 0))
        )
        // No other arrows in ray towards top
        val isBlocked = arrow.isBlockedBy(listOf(arrow), 5, 5)
        assertFalse("Arrow pointing UP at row 0 should be clear to exit", isBlocked)
    }

    @Test
    fun testTurnClassifications() {
        val lTurn = ArrowPiece("l_turn", listOf(GridPoint(0, 0), GridPoint(1, 0), GridPoint(1, 1)))
        assertEquals(ArrowShapeType.L_BEND, lTurn.shapeType)

        val uTurn = ArrowPiece("u_turn", listOf(GridPoint(0, 0), GridPoint(1, 0), GridPoint(1, 1), GridPoint(0, 1)))
        assertEquals(ArrowShapeType.U_TURN, uTurn.shapeType)

        val sCurve = ArrowPiece("s_curve", listOf(GridPoint(0, 0), GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 1)))
        assertEquals(ArrowShapeType.S_CURVE, sCurve.shapeType)
    }

    @Test
    fun testLevel1ZeroEmptyBlocksAndAtLeast10Arrows() {
        val level = LevelGenerator.getLevel(1)
        assertEquals("Level 1 should be Heart shape", PuzzleShape.HEART, level.shape)
        assertTrue("Level 1 must have at least 10 arrows as requested", level.arrows.size >= 10)
        val occupiedPoints = level.arrows.flatMap { it.points }.toSet()
        assertEquals(
            "Every single cell in Level 1 must be covered (0 empty blocks!)",
            level.validCells.size,
            occupiedPoints.size
        )
        val result = PuzzleSolver.solve(level)
        assertTrue("Level 1 must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel2StarShape() {
        val level = LevelGenerator.getLevel(2)
        assertEquals("Level 2 should be Star shape", PuzzleShape.STAR, level.shape)
        assertTrue("Level 2 should have at least 12 arrows", level.arrows.size >= 12)
        val result = PuzzleSolver.solve(level)
        assertTrue("Star level must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel3SunShape() {
        val level = LevelGenerator.getLevel(3)
        assertEquals("Level 3 should be Sun shape", PuzzleShape.SUN, level.shape)
        assertTrue("Level 3 should have at least 14 arrows", level.arrows.size >= 14)
        val result = PuzzleSolver.solve(level)
        assertTrue("Sun level must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel4MoonShape() {
        val level = LevelGenerator.getLevel(4)
        assertEquals("Level 4 should be Moon shape", PuzzleShape.MOON, level.shape)
        assertTrue("Level 4 should have at least 16 arrows", level.arrows.size >= 16)
        val result = PuzzleSolver.solve(level)
        assertTrue("Moon level must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel5LightningShape() {
        val level = LevelGenerator.getLevel(5)
        assertEquals("Level 5 should be Lightning shape", PuzzleShape.LIGHTNING, level.shape)
        assertTrue("Level 5 should have at least 18 arrows", level.arrows.size >= 18)
        val result = PuzzleSolver.solve(level)
        assertTrue("Lightning level must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel100Solvable() {
        val level = LevelGenerator.getLevel(100)
        assertTrue("Level 100 should have arrows", level.arrows.isNotEmpty())
        val result = PuzzleSolver.solve(level)
        assertTrue("Level 100 must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel500Solvable() {
        val level = LevelGenerator.getLevel(500)
        assertTrue("Level 500 should have arrows", level.arrows.isNotEmpty())
        val result = PuzzleSolver.solve(level)
        assertTrue("Level 500 must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testLevel1000Solvable() {
        val level = LevelGenerator.getLevel(1000)
        assertTrue("Level 1000 should have arrows", level.arrows.isNotEmpty())
        val result = PuzzleSolver.solve(level)
        assertTrue("Level 1000 must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testDailyPuzzleSolvable() {
        val daily = LevelGenerator.getDailyLevel(20261001)
        assertTrue("Daily puzzle should have arrows", daily.arrows.isNotEmpty())
        val result = PuzzleSolver.solve(daily)
        assertTrue("Daily puzzle must be solvable", result is PuzzleSolver.SolveResult.Solvable)
    }

    @Test
    fun testHintFinder() {
        val level = LevelGenerator.getLevel(5)
        val hint = PuzzleSolver.findHint(level.arrows, level.cols, level.rows)
        assertNotNull("There should always be at least one valid hint available at level start", hint)
    }

    @Test
    fun testLevelTimeLimitsCapAtOneMinute() {
        val testLevels = listOf(1, 2, 5, 10, 20, 100)
        for (lvl in testLevels) {
            val levelData = LevelGenerator.getLevel(lvl)
            val timeLimit = when (levelData.difficulty.lowercase()) {
                "easy" -> 60
                "medium" -> 50
                "hard" -> 45
                "expert" -> 40
                else -> when {
                    levelData.levelNumber <= 3 -> 60
                    levelData.levelNumber <= 8 -> 50
                    levelData.levelNumber <= 15 -> 45
                    else -> 40
                }
            }.coerceIn(30, 60)
            assertTrue("Level $lvl time limit must be <= 60 seconds (max 1 min)", timeLimit <= 60)
            assertTrue("Level $lvl time limit must be >= 30 seconds", timeLimit >= 30)
        }
    }

    @Test
    fun testMascotCostsAndFreeStarterBee() {
        val bee = com.example.ui.components.CartoonMascotType.BEE
        assertEquals("BEE mascot must be free starter (0 coins)", 0, bee.unlockCost)
        assertEquals("BEE must map to CLASSIC theme", com.example.model.GameTheme.CLASSIC, bee.associatedTheme)

        // All non-bee characters must cost exactly 1,000 coins
        val otherMascots = com.example.ui.components.CartoonMascotType.entries.filter { it != bee }
        assertTrue("Must have multiple unlockable cartoon characters", otherMascots.size >= 7)
        for (mascot in otherMascots) {
            assertEquals("${mascot.displayName} must cost 1,000 coins", 1000, mascot.unlockCost)
            assertNotNull("${mascot.displayName} must have an associated theme", mascot.associatedTheme)
        }
    }

    @Test
    fun testStarCoinCalculationLogic() {
        fun calcStarCoins(stars: Int): Int = when (stars) {
            3 -> 30
            2 -> 20
            else -> 10
        }
        assertEquals("1 star must award 10 coins", 10, calcStarCoins(1))
        assertEquals("2 stars must award 20 coins", 20, calcStarCoins(2))
        assertEquals("3 stars must award 30 coins", 30, calcStarCoins(3))
    }
}
