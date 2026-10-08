package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobConfig
import com.example.ads.AdMobManager
import com.example.ads.RewardedAdRewardType
import com.example.audio.HapticManager
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.GamePreferences
import com.example.data.LevelProgressEntity
import com.example.data.MysteryChestReward
import com.example.engine.LevelGenerator
import com.example.engine.PuzzleSolver
import com.example.model.ArrowPiece
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.GameStatus
import com.example.model.LevelData
import com.example.ui.components.CartoonMascotType
import com.example.ui.components.MascotMood
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val preferences = GamePreferences(application)
    val hapticManager = HapticManager(application)
    private val database = AppDatabase.getInstance(application)
    private val progressDao = database.levelProgressDao()

    val allProgressFlow = progressDao.getAllProgress()
    val totalStarsFlow = progressDao.getTotalStarsFlow()
    val completedCountFlow = progressDao.getCompletedCountFlow()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _isDailyMode = MutableStateFlow(false)
    val isDailyMode: StateFlow<Boolean> = _isDailyMode.asStateFlow()

    private val _mascotMood = MutableStateFlow(MascotMood.HAPPY)
    val mascotMood: StateFlow<MascotMood> = _mascotMood.asStateFlow()

    private val _selectedMascot = MutableStateFlow(
        CartoonMascotType.fromId(preferences.selectedMascot.value)
    )
    val selectedMascot: StateFlow<CartoonMascotType> = _selectedMascot.asStateFlow()

    val dailyStreak: StateFlow<Int> = preferences.dailyStreak
    val isStreakClaimedToday: StateFlow<Boolean> = preferences.isStreakClaimedToday

    fun claimDailyStreak(doubleReward: Boolean = false): com.example.data.DailyStreakClaimResult? {
        val multiplier = if (doubleReward) 2 else 1
        val result = preferences.claimDailyStreakReward(multiplier)
        if (result != null) {
            SoundManager.playCoinReward()
            hapticManager.victory()
            _mascotMood.value = MascotMood.CELEBRATING
            viewModelScope.launch {
                delay(2200)
                if (_mascotMood.value == MascotMood.CELEBRATING) {
                    _mascotMood.value = MascotMood.HAPPY
                }
            }
        }
        return result
    }

    private var moodResetJob: kotlinx.coroutines.Job? = null
    private var timerJob: kotlinx.coroutines.Job? = null

    fun isMascotUnlocked(type: CartoonMascotType): Boolean {
        return type == CartoonMascotType.BEE ||
                type.unlockCost == 0 ||
                preferences.isMascotUnlocked(type.id)
    }

    fun setSelectedMascot(type: CartoonMascotType) {
        if (!isMascotUnlocked(type)) return
        preferences.setSelectedMascot(type.id)
        preferences.setTheme(type.associatedTheme)
        _selectedMascot.value = type
        _mascotMood.value = MascotMood.CHEERING
        viewModelScope.launch {
            delay(1200)
            if (_mascotMood.value == MascotMood.CHEERING) {
                _mascotMood.value = MascotMood.HAPPY
            }
        }
    }

    fun unlockMascot(type: CartoonMascotType): Boolean {
        val success = preferences.unlockMascot(type.id, type.unlockCost)
        if (success) {
            SoundManager.playVictory()
            hapticManager.victory()
            setSelectedMascot(type)
        } else {
            SoundManager.playBlocked()
            hapticManager.blocked()
        }
        return success
    }

    fun cycleNextMascot() {
        val unlocked = CartoonMascotType.entries.filter { isMascotUnlocked(it) }
        if (unlocked.isEmpty()) return
        val currentIdx = unlocked.indexOf(_selectedMascot.value)
        val next = unlocked[(currentIdx + 1) % unlocked.size]
        setSelectedMascot(next)
    }

    fun onMascotInteract() {
        SoundManager.playTap()
        hapticManager.tap()
        _mascotMood.value = MascotMood.CHEERING
        viewModelScope.launch {
            delay(1200)
            if (_mascotMood.value == MascotMood.CHEERING) {
                _mascotMood.value = MascotMood.HAPPY
            }
        }
    }

    init {
        // Sync preferences with audio & haptics
        SoundManager.isSoundEnabled = preferences.soundEnabled.value
        SoundManager.isMusicEnabled = preferences.musicEnabled.value
        hapticManager.isHapticsEnabled = preferences.hapticEnabled.value

        viewModelScope.launch {
            preferences.soundEnabled.collect {
                SoundManager.isSoundEnabled = it
            }
        }
        viewModelScope.launch {
            preferences.musicEnabled.collect {
                SoundManager.isMusicEnabled = it
                if (it) SoundManager.startBackgroundMusic() else SoundManager.stopBackgroundMusic()
            }
        }
        viewModelScope.launch {
            preferences.hapticEnabled.collect {
                hapticManager.isHapticsEnabled = it
            }
        }

        if (preferences.musicEnabled.value) {
            SoundManager.startBackgroundMusic()
        }

        // Load saved level or level 1
        loadLevel(preferences.currentLevel.value)
    }

    fun loadLevel(levelNumber: Int) {
        viewModelScope.launch {
            _isDailyMode.value = false
            preferences.setCurrentLevel(levelNumber)
            val levelData = LevelGenerator.getLevel(levelNumber)
            initializeGameState(levelData, preferences.gameMode.value)
        }
    }

    fun loadDailyPuzzle() {
        viewModelScope.launch {
            _isDailyMode.value = true
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val dateKey = dateFormat.format(Date()).toIntOrNull() ?: 20261001
            val dailyLevel = LevelGenerator.getDailyLevel(dateKey)
            initializeGameState(dailyLevel, GameMode.NORMAL)
        }
    }

    /**
     * Calculates the level time limit based on difficulty, strictly capped at max 1 minute (60s).
     * Easy / Early levels: 60 seconds (1 minute max)
     * Medium levels: 50 seconds
     * Hard levels: 45 seconds
     * Expert levels: 40 seconds
     */
    fun getTimeLimitForLevel(levelData: LevelData): Int {
        val limit = when (levelData.difficulty.lowercase(Locale.US)) {
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
        }
        return limit.coerceIn(30, 60) // Always max 1 minute (60s)
    }

    var revivesUsedInCurrentLevel = 0
        private set

    private fun initializeGameState(levelData: LevelData, mode: GameMode) {
        revivesUsedInCurrentLevel = 0
        timerJob?.cancel()
        moodResetJob?.cancel()
        _mascotMood.value = MascotMood.HAPPY
        val maxLives = 3
        val timeLimit = getTimeLimitForLevel(levelData)
        _gameState.value = GameState(
            levelData = levelData,
            activeArrows = levelData.arrows,
            history = emptyList(),
            movesTaken = 0,
            mistakes = 0,
            lives = maxLives,
            maxLives = maxLives,
            hintsRemaining = preferences.hintsCount.value,
            hintedArrowId = null,
            blockedArrowId = null,
            escapingArrowId = null,
            status = GameStatus.PLAYING,
            gameMode = mode,
            timeRemainingSeconds = timeLimit,
            totalTimeSeconds = timeLimit,
            timeElapsedSeconds = 0
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val curr = _gameState.value ?: break
                if (curr.status != GameStatus.PLAYING) break

                val newRemaining = curr.timeRemainingSeconds - 1
                val newElapsed = curr.timeElapsedSeconds + 1

                if (newRemaining <= 0) {
                    // Maximum 1 minute / difficulty time limit passed: automatic player loss!
                    SoundManager.playBlocked()
                    hapticManager.blocked()
                    moodResetJob?.cancel()
                    _mascotMood.value = MascotMood.CRYING_SAD

                    _gameState.value = curr.copy(
                        timeRemainingSeconds = 0,
                        timeElapsedSeconds = newElapsed,
                        status = GameStatus.GAME_OVER
                    )
                    break
                } else {
                    _gameState.value = curr.copy(
                        timeRemainingSeconds = newRemaining,
                        timeElapsedSeconds = newElapsed
                    )
                }
            }
        }
    }

    fun canRevive(): Boolean {
        return revivesUsedInCurrentLevel < AdMobConfig.MAX_REVIVES_PER_LEVEL
    }

    fun revivePlayer(): Boolean {
        val curr = _gameState.value ?: return false
        if (!canRevive()) return false

        revivesUsedInCurrentLevel++
        moodResetJob?.cancel()
        _mascotMood.value = MascotMood.CHEERING
        viewModelScope.launch {
            delay(1500)
            if (_mascotMood.value == MascotMood.CHEERING) {
                _mascotMood.value = MascotMood.HAPPY
            }
        }
        SoundManager.playVictory()
        hapticManager.victory()
        val extraTime = if (curr.timeRemainingSeconds <= 10) 35 else (curr.timeRemainingSeconds + 30)
        _gameState.value = curr.copy(
            lives = curr.maxLives,
            timeRemainingSeconds = extraTime,
            status = GameStatus.PLAYING
        )
        startTimer()
        return true
    }

    fun giveExtraMovesAndTimer(extraTimeSeconds: Int = 30) {
        val curr = _gameState.value ?: return
        moodResetJob?.cancel()
        _mascotMood.value = MascotMood.CHEERING
        viewModelScope.launch {
            delay(1500)
            if (_mascotMood.value == MascotMood.CHEERING) {
                _mascotMood.value = MascotMood.HAPPY
            }
        }
        SoundManager.playCoinReward()
        hapticManager.tap()
        val extraTime = (curr.timeRemainingSeconds + extraTimeSeconds).coerceAtLeast(30)
        _gameState.value = curr.copy(
            timeRemainingSeconds = extraTime,
            status = GameStatus.PLAYING
        )
        startTimer()
    }

    fun giveExtraLife() {
        revivePlayer()
    }

    fun doubleCurrentLevelReward(): com.example.model.LevelReward? {
        val curr = _gameState.value ?: return null
        val currentReward = curr.reward ?: return null
        val doubledTotal = currentReward.totalCoins * 2
        val bonusAdded = currentReward.totalCoins
        preferences.addCoins(bonusAdded) // Add the 2x portion
        SoundManager.playCoinReward()
        hapticManager.victory()
        val updatedReward = currentReward.copy(
            totalCoins = doubledTotal
        )
        _gameState.value = curr.copy(reward = updatedReward)
        return updatedReward
    }

    fun openMysteryChest(isAd: Boolean): MysteryChestReward? {
        val reward = preferences.openMysteryChest(isAd)
        if (reward != null) {
            SoundManager.playVictory()
            hapticManager.victory()
            _mascotMood.value = MascotMood.CELEBRATING
            viewModelScope.launch {
                delay(2000)
                if (_mascotMood.value == MascotMood.CELEBRATING) {
                    _mascotMood.value = MascotMood.HAPPY
                }
            }
            if (reward.extraMoves > 0 && _gameState.value?.status == GameStatus.PLAYING) {
                giveExtraMovesAndTimer(30)
            }
        }
        return reward
    }

    fun buyHintWithCoins(): Boolean {
        val success = preferences.buyHint(cost = 50)
        if (success) {
            SoundManager.playHint()
            hapticManager.tap()
        }
        return success
    }

    fun buyExtraMovesWithCoins(): Boolean {
        val success = preferences.buyExtraMoves(cost = 75)
        if (success) {
            giveExtraMovesAndTimer(30)
        }
        return success
    }

    fun buyReviveWithCoins(): Boolean {
        if (!canRevive()) return false
        val success = preferences.buyRevive(cost = 100)
        if (success) {
            revivePlayer()
        }
        return success
    }

    fun watchRewardedAd(
        activity: Activity,
        rewardType: RewardedAdRewardType,
        onSuccess: (RewardedAdRewardType) -> Unit,
        onUnavailable: (String) -> Unit = {}
    ) {
        AdMobManager.showRewardedAd(
            activity = activity,
            rewardType = rewardType,
            onRewardEarned = { earnedType ->
                when (earnedType) {
                    RewardedAdRewardType.FREE_HINT -> {
                        preferences.addHints(1)
                        SoundManager.playHint()
                        hapticManager.tap()
                    }
                    RewardedAdRewardType.EXTRA_MOVES -> {
                        giveExtraMovesAndTimer(30)
                    }
                    RewardedAdRewardType.REVIVE -> {
                        revivePlayer()
                    }
                    RewardedAdRewardType.DOUBLE_LEVEL_COINS -> {
                        doubleCurrentLevelReward()
                    }
                    RewardedAdRewardType.MYSTERY_CHEST -> {
                        // Caller initiates chest opening animation
                    }
                    RewardedAdRewardType.DAILY_REWARD_DOUBLE -> {
                        // Caller claims with multiplier
                    }
                    RewardedAdRewardType.REWARD_CENTER_COINS -> {
                        preferences.addCoins(50)
                        SoundManager.playCoinReward()
                        hapticManager.victory()
                    }
                    RewardedAdRewardType.REWARD_CENTER_HINT -> {
                        preferences.addHints(1)
                        SoundManager.playHint()
                        hapticManager.tap()
                    }
                    RewardedAdRewardType.REWARD_CENTER_BOOST -> {
                        preferences.addCoins(25)
                        preferences.addHints(1)
                        SoundManager.playCoinReward()
                        hapticManager.victory()
                    }
                }
                onSuccess(earnedType)
            },
            onAdDismissed = { /* dismissed */ },
            onAdUnavailable = onUnavailable
        )
    }

    fun onArrowTapped(arrow: ArrowPiece) {
        val current = _gameState.value ?: return
        if (current.status != GameStatus.PLAYING) return
        if (current.escapingArrowId != null) return // Ignore taps while an arrow is escaping

        val isBlocked = arrow.isBlockedBy(current.activeArrows, current.levelData.cols, current.levelData.rows)

        if (isBlocked) {
            // Blocked move
            handleBlockedArrow(arrow)
        } else {
            // Valid escape move
            handleEscapingArrow(arrow)
        }
    }

    private fun handleBlockedArrow(arrow: ArrowPiece) {
        val current = _gameState.value ?: return

        SoundManager.playBlocked()
        hapticManager.blocked()

        val newMistakes = current.mistakes + 1
        val newLives = (current.lives - 1).coerceAtLeast(0)
        val isGameOver = newLives <= 0

        // Emotional reaction: Mascot weeps cute tears when player fails or collides!
        moodResetJob?.cancel()
        _mascotMood.value = MascotMood.CRYING_SAD
        if (!isGameOver) {
            moodResetJob = viewModelScope.launch {
                delay(2200)
                if (_mascotMood.value == MascotMood.CRYING_SAD) {
                    _mascotMood.value = MascotMood.HAPPY
                }
            }
        }

        _gameState.value = current.copy(
            blockedArrowId = arrow.id,
            mistakes = newMistakes,
            lives = newLives,
            comboStreak = 0,
            status = if (isGameOver) GameStatus.GAME_OVER else GameStatus.PLAYING
        )

        // Clear blocked animation after 420ms
        viewModelScope.launch {
            delay(420)
            val stateNow = _gameState.value ?: return@launch
            if (stateNow.blockedArrowId == arrow.id) {
                _gameState.value = stateNow.copy(blockedArrowId = null)
            }
        }
    }

    private fun handleEscapingArrow(arrow: ArrowPiece) {
        val current = _gameState.value ?: return

        val newCombo = current.comboStreak + 1
        SoundManager.playEscape(newCombo)
        hapticManager.tap()

        // Emotional reaction: Mascot cheers excitedly with sparkles & hops!
        moodResetJob?.cancel()
        _mascotMood.value = MascotMood.CHEERING
        moodResetJob = viewModelScope.launch {
            delay(1400)
            if (_mascotMood.value == MascotMood.CHEERING) {
                _mascotMood.value = MascotMood.HAPPY
            }
        }

        // Clear hinted highlight if this was the hinted arrow
        val clearHint = if (current.hintedArrowId == arrow.id) null else current.hintedArrowId

        // Mark as escaping to trigger visual glide animation
        _gameState.value = current.copy(
            escapingArrowId = arrow.id,
            hintedArrowId = clearHint,
            blockedArrowId = null,
            comboStreak = newCombo,
            movesTaken = current.movesTaken + 1
        )

        viewModelScope.launch {
            // Wait for exit slide animation (450ms)
            val animDuration = if (preferences.animationsEnabled.value) 450L else 50L
            delay(animDuration)

            val stateNow = _gameState.value ?: return@launch
            val updatedActive = stateNow.activeArrows.filter { it.id != arrow.id }
            val updatedHistory = stateNow.history + arrow

            if (updatedActive.isEmpty()) {
                // LEVEL COMPLETED! Stop timer!
                timerJob?.cancel()
                moodResetJob?.cancel()
                _mascotMood.value = MascotMood.CELEBRATING
                val stars = stateNow.levelData.calculateStars(stateNow.movesTaken, stateNow.mistakes)
                SoundManager.playVictory()
                hapticManager.victory()

                // Level completion coin reward according to star rating:
                // 1 Star: 10 coins base
                // 2 Stars: 20 coins base
                // 3 Stars: 30 coins base
                val starCoins = when (stars) {
                    3 -> 30
                    2 -> 20
                    else -> 10
                }
                val baseCoins = starCoins
                val parBonus = if (stateNow.movesTaken <= stateNow.levelData.parMoves) 10 else 0
                val streak = preferences.incrementStreak()
                val streakBonus = (streak * 2).coerceAtMost(20)
                val dailyBonus = if (_isDailyMode.value) 50 else 0
                val dailyStreakDays = preferences.dailyStreak.value
                val dailyStreakBonus = (dailyStreakDays * 5).coerceAtMost(35)
                val totalCoins = baseCoins + parBonus + streakBonus + dailyBonus + dailyStreakBonus
                preferences.addCoins(totalCoins)

                val bonusHint = if (_isDailyMode.value) {
                    preferences.addHint(2)
                    true
                } else if (stateNow.levelData.levelNumber % 3 == 0 || streak % 5 == 0) {
                    preferences.addHint(1)
                    true
                } else {
                    false
                }

                val reward = com.example.model.LevelReward(
                    totalCoins = totalCoins,
                    baseCoins = baseCoins,
                    starBonus = starCoins,
                    parBonus = parBonus,
                    streakBonus = streakBonus,
                    currentStreak = streak,
                    dailyStreakBonus = dailyStreakBonus,
                    dailyStreakDays = dailyStreakDays,
                    earnedBonusHint = bonusHint
                )

                if (_isDailyMode.value) {
                    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
                    val dateKey = dateFormat.format(Date())
                    preferences.setDailyCompleted(dateKey)
                } else {
                    // Save to Room DB
                    progressDao.saveProgress(
                        LevelProgressEntity(
                            levelNumber = stateNow.levelData.levelNumber,
                            isCompleted = true,
                            stars = stars,
                            bestMoves = stateNow.movesTaken
                        )
                    )
                    // Unlock next level
                    preferences.unlockUpToLevel(stateNow.levelData.levelNumber + 1)
                }

                _gameState.value = stateNow.copy(
                    activeArrows = emptyList(),
                    history = updatedHistory,
                    escapingArrowId = null,
                    status = GameStatus.VICTORY,
                    earnedStars = stars,
                    reward = reward
                )
            } else {
                // Check if remaining state has become stuck
                val isStuck = PuzzleSolver.isStuck(
                    updatedActive,
                    stateNow.levelData.cols,
                    stateNow.levelData.rows
                )

                if (isStuck) {
                    moodResetJob?.cancel()
                    _mascotMood.value = MascotMood.CRYING_SAD
                }

                _gameState.value = stateNow.copy(
                    activeArrows = updatedActive,
                    history = updatedHistory,
                    escapingArrowId = null,
                    status = if (isStuck) GameStatus.STUCK else GameStatus.PLAYING
                )
            }
        }
    }

    fun useHint() {
        val current = _gameState.value ?: return
        if (!current.canUseHint) return

        if (!preferences.consumeHint()) return

        val hintArrow = PuzzleSolver.findHint(
            current.activeArrows,
            current.levelData.cols,
            current.levelData.rows
        )

        if (hintArrow != null) {
            SoundManager.playHint()
            hapticManager.tap()
            _mascotMood.value = MascotMood.THINKING
            viewModelScope.launch {
                delay(1600)
                if (_mascotMood.value == MascotMood.THINKING) {
                    _mascotMood.value = MascotMood.HAPPY
                }
            }
            _gameState.value = current.copy(
                hintsRemaining = preferences.hintsCount.value,
                hintedArrowId = hintArrow.id
            )
        }
    }

    fun undo() {
        val current = _gameState.value ?: return
        if (current.history.isEmpty()) return

        SoundManager.playTap()
        hapticManager.tap()

        val lastArrow = current.history.last()
        val updatedHistory = current.history.dropLast(1)
        val updatedActive = current.activeArrows + lastArrow

        _gameState.value = current.copy(
            activeArrows = updatedActive,
            history = updatedHistory,
            movesTaken = (current.movesTaken - 1).coerceAtLeast(0),
            hintedArrowId = null,
            blockedArrowId = null,
            escapingArrowId = null,
            status = GameStatus.PLAYING
        )
    }

    fun restartLevel() {
        val current = _gameState.value ?: return
        SoundManager.playTap()
        initializeGameState(current.levelData, preferences.gameMode.value)
    }

    fun nextLevel() {
        val current = _gameState.value ?: return
        SoundManager.playTap()
        val nextLevelNumber = (current.levelData.levelNumber + 1).coerceAtMost(500)
        loadLevel(nextLevelNumber)
    }

    fun setGameMode(mode: GameMode) {
        preferences.setGameMode(mode)
        restartLevel()
    }

    fun resetAllData() {
        viewModelScope.launch {
            progressDao.clearAll()
            preferences.resetAllProgress()
            loadLevel(1)
        }
    }
}
