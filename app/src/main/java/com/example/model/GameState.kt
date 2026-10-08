package com.example.model

enum class GameStatus {
    PLAYING,
    VICTORY,
    STUCK,
    GAME_OVER,
    PAUSED
}

enum class GameMode(val label: String, val description: String) {
    NORMAL("Normal", "Relaxed puzzle solving. Unlimited mistakes."),
    HARD("Hard", "Mistakes cost lives. Think before you tap!"),
    EXPERT("Expert", "Complex boards with tight dependencies.")
}

data class LevelReward(
    val totalCoins: Int = 0,
    val baseCoins: Int = 0,
    val starBonus: Int = 0,
    val parBonus: Int = 0,
    val streakBonus: Int = 0,
    val currentStreak: Int = 0,
    val dailyStreakBonus: Int = 0,
    val dailyStreakDays: Int = 0,
    val earnedBonusHint: Boolean = false
)

data class GameState(
    val levelData: LevelData,
    val activeArrows: List<ArrowPiece> = levelData.arrows,
    val history: List<ArrowPiece> = emptyList(),
    val movesTaken: Int = 0,
    val mistakes: Int = 0,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val hintsRemaining: Int = 3,
    val hintedArrowId: String? = null,
    val blockedArrowId: String? = null,
    val escapingArrowId: String? = null,
    val status: GameStatus = GameStatus.PLAYING,
    val gameMode: GameMode = GameMode.NORMAL,
    val earnedStars: Int = 0,
    val reward: LevelReward? = null,
    val comboStreak: Int = 0,
    val timeRemainingSeconds: Int = 120,
    val totalTimeSeconds: Int = 120,
    val timeElapsedSeconds: Int = 0
) {
    val remainingArrowsCount: Int get() = activeArrows.size
    val totalArrowsCount: Int get() = levelData.totalArrows
    val isUndoAvailable: Boolean get() = history.isNotEmpty() && status == GameStatus.PLAYING
    val canUseHint: Boolean get() = hintsRemaining > 0 && status == GameStatus.PLAYING && hintedArrowId == null
}
