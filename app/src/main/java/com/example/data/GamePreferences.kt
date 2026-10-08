package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameMode
import com.example.model.GameTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyStreakReward(
    val day: Int,
    val coins: Int,
    val hints: Int = 0,
    val title: String,
    val isGrandReward: Boolean = false
)

data class DailyStreakClaimResult(
    val day: Int,
    val coinsEarned: Int,
    val hintsEarned: Int,
    val totalStreak: Int,
    val isGrandReward: Boolean
)

data class MysteryChestReward(
    val coins: Int = 0,
    val hints: Int = 0,
    val extraMoves: Int = 0,
    val title: String,
    val emoji: String
)

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("arrow_escape_prefs", Context.MODE_PRIVATE)

    fun checkDailyHintReset() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val lastDate = prefs.getString(KEY_HINTS_DATE, "")
        if (lastDate != today) {
            prefs.edit().putString(KEY_HINTS_DATE, today).putInt(KEY_HINTS, 3).apply()
            _hintsCount.value = 3
        }
    }

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _musicEnabled = MutableStateFlow(prefs.getBoolean(KEY_MUSIC, true))
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC, true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _animationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_ANIMATIONS, true))
    val animationsEnabled: StateFlow<Boolean> = _animationsEnabled.asStateFlow()

    private val _theme = MutableStateFlow(
        try {
            GameTheme.valueOf(prefs.getString(KEY_THEME, GameTheme.CLASSIC.name) ?: GameTheme.CLASSIC.name)
        } catch (_: Exception) {
            GameTheme.CLASSIC
        }
    )
    val theme: StateFlow<GameTheme> = _theme.asStateFlow()

    private val _gameMode = MutableStateFlow(
        try {
            GameMode.valueOf(prefs.getString(KEY_MODE, GameMode.NORMAL.name) ?: GameMode.NORMAL.name)
        } catch (_: Exception) {
            GameMode.NORMAL
        }
    )
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _currentLevel = MutableStateFlow(prefs.getInt(KEY_CURRENT_LEVEL, 1))
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _highestUnlocked = MutableStateFlow(prefs.getInt(KEY_HIGHEST_UNLOCKED, 1))
    val highestUnlocked: StateFlow<Int> = _highestUnlocked.asStateFlow()

    private val _hintsCount = MutableStateFlow(prefs.getInt(KEY_HINTS, 3))
    val hintsCount: StateFlow<Int> = _hintsCount.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt(KEY_COINS, 100))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _winStreak = MutableStateFlow(prefs.getInt(KEY_WIN_STREAK, 0))
    val winStreak: StateFlow<Int> = _winStreak.asStateFlow()

    private val _dailyStreak = MutableStateFlow(prefs.getInt(KEY_DAILY_STREAK, 0))
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _isStreakClaimedToday = MutableStateFlow(false)
    val isStreakClaimedToday: StateFlow<Boolean> = _isStreakClaimedToday.asStateFlow()

    private val _selectedMascot = MutableStateFlow(
        prefs.getString(KEY_MASCOT, "bee") ?: "bee"
    )
    val selectedMascot: StateFlow<String> = _selectedMascot.asStateFlow()

    private val _unlockedMascots = MutableStateFlow(
        prefs.getStringSet(KEY_UNLOCKED_MASCOTS, setOf("bee"))?.toSet() ?: setOf("bee")
    )
    val unlockedMascots: StateFlow<Set<String>> = _unlockedMascots.asStateFlow()

    init {
        checkDailyHintReset()
        checkDailyStreakStatus()
    }

    fun isMascotUnlocked(mascotId: String): Boolean {
        if (mascotId.equals("bee", ignoreCase = true)) return true
        return _unlockedMascots.value.contains(mascotId.lowercase())
    }

    fun unlockMascot(mascotId: String, cost: Int = 1000): Boolean {
        val id = mascotId.lowercase()
        if (isMascotUnlocked(id)) return true
        if (_coins.value < cost) return false

        val newCoins = _coins.value - cost
        val newUnlocked = _unlockedMascots.value + id
        prefs.edit()
            .putInt(KEY_COINS, newCoins)
            .putStringSet(KEY_UNLOCKED_MASCOTS, newUnlocked)
            .apply()
        _coins.value = newCoins
        _unlockedMascots.value = newUnlocked
        return true
    }

    fun setSelectedMascot(mascotId: String) {
        prefs.edit().putString(KEY_MASCOT, mascotId).apply()
        _selectedMascot.value = mascotId
    }

    fun addCoins(amount: Int) {
        val updated = (_coins.value + amount).coerceAtLeast(0)
        prefs.edit().putInt(KEY_COINS, updated).apply()
        _coins.value = updated
    }

    fun incrementStreak(): Int {
        val next = _winStreak.value + 1
        prefs.edit().putInt(KEY_WIN_STREAK, next).apply()
        _winStreak.value = next
        return next
    }

    fun resetStreak() {
        prefs.edit().putInt(KEY_WIN_STREAK, 0).apply()
        _winStreak.value = 0
    }

    fun addHint(amount: Int = 1) {
        addHints(amount)
    }

    private fun getDaysBetween(fromDateStr: String, toDateStr: String): Int {
        if (fromDateStr.isEmpty() || toDateStr.isEmpty()) return -1
        return try {
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
            val d1 = sdf.parse(fromDateStr) ?: return -1
            val d2 = sdf.parse(toDateStr) ?: return -1
            val c1 = Calendar.getInstance().apply {
                time = d1
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val c2 = Calendar.getInstance().apply {
                time = d2
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffMs = c2.timeInMillis - c1.timeInMillis
            (diffMs / (24L * 60 * 60 * 1000)).toInt()
        } catch (_: Exception) {
            -1
        }
    }

    fun checkDailyStreakStatus() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val lastClaim = prefs.getString(KEY_LAST_STREAK_CLAIM_DATE, "") ?: ""
        val diff = getDaysBetween(lastClaim, today)

        if (diff == 0) {
            _isStreakClaimedToday.value = true
        } else {
            _isStreakClaimedToday.value = false
            if (diff > 1) {
                // Streak was broken after missing consecutive day
                prefs.edit().putInt(KEY_DAILY_STREAK, 0).apply()
                _dailyStreak.value = 0
            }
        }
    }

    fun getDailyStreakReward(day: Int): DailyStreakReward {
        val dayCycle = if (day <= 0) 1 else ((day - 1) % 7) + 1
        return when (dayCycle) {
            1 -> DailyStreakReward(1, 50, 0, "Welcome Back! 🍯")
            2 -> DailyStreakReward(2, 75, 0, "Double Energy! ⚡")
            3 -> DailyStreakReward(3, 100, 1, "Puzzle Spark! 💡")
            4 -> DailyStreakReward(4, 150, 0, "Unstoppable! 🚀")
            5 -> DailyStreakReward(5, 200, 1, "Champion Focus! 🎯")
            6 -> DailyStreakReward(6, 300, 0, "Grand Dedication! ⭐")
            7 -> DailyStreakReward(7, 500, 2, "7-Day Legend! 👑", isGrandReward = true)
            else -> DailyStreakReward(dayCycle, 150, 1, "Daily Master! 🔥")
        }
    }

    fun getNextStreakDay(): Int {
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val lastClaim = prefs.getString(KEY_LAST_STREAK_CLAIM_DATE, "") ?: ""
        val diff = getDaysBetween(lastClaim, today)

        return if (diff == 0 && _isStreakClaimedToday.value) {
            val curr = _dailyStreak.value
            if (curr <= 0) 1 else ((curr - 1) % 7) + 1
        } else if (diff == 1) {
            val next = _dailyStreak.value + 1
            ((next - 1) % 7) + 1
        } else {
            1
        }
    }

    fun claimDailyStreakReward(multiplier: Int = 1): DailyStreakClaimResult? {
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val lastClaim = prefs.getString(KEY_LAST_STREAK_CLAIM_DATE, "") ?: ""
        val diff = getDaysBetween(lastClaim, today)

        if (diff == 0 && _isStreakClaimedToday.value) {
            return null
        }

        val currentStreak = _dailyStreak.value
        val nextStreak = if (diff == 1) currentStreak + 1 else 1
        val reward = getDailyStreakReward(nextStreak)

        val coinsToAdd = reward.coins * multiplier.coerceIn(1, 2)
        val hintsToAdd = reward.hints * multiplier.coerceIn(1, 2)

        val newCoins = _coins.value + coinsToAdd
        val newHints = _hintsCount.value + hintsToAdd

        prefs.edit()
            .putInt(KEY_DAILY_STREAK, nextStreak)
            .putString(KEY_LAST_STREAK_CLAIM_DATE, today)
            .putInt(KEY_COINS, newCoins)
            .putInt(KEY_HINTS, newHints)
            .apply()

        _dailyStreak.value = nextStreak
        _coins.value = newCoins
        _hintsCount.value = newHints
        _isStreakClaimedToday.value = true

        return DailyStreakClaimResult(
            day = ((nextStreak - 1) % 7) + 1,
            coinsEarned = coinsToAdd,
            hintsEarned = hintsToAdd,
            totalStreak = nextStreak,
            isGrandReward = reward.isGrandReward
        )
    }

    private val _lastChestOpenTime = MutableStateFlow(prefs.getLong(KEY_LAST_CHEST_TIME, 0L))
    val lastChestOpenTime: StateFlow<Long> = _lastChestOpenTime.asStateFlow()

    fun getChestCooldownRemainingSeconds(): Long {
        val now = System.currentTimeMillis()
        val elapsed = now - _lastChestOpenTime.value
        val cooldownMs = 10 * 60 * 1000L // 10 minutes between free ad mystery chests
        val remaining = (cooldownMs - elapsed) / 1000L
        return remaining.coerceAtLeast(0L)
    }

    fun canOpenFreeMysteryChest(): Boolean {
        return getChestCooldownRemainingSeconds() <= 0L
    }

    fun openMysteryChest(isAd: Boolean): MysteryChestReward? {
        if (!isAd) {
            val cost = 150
            if (_coins.value < cost) return null
            addCoins(-cost)
        }

        val now = System.currentTimeMillis()
        prefs.edit().putLong(KEY_LAST_CHEST_TIME, now).apply()
        _lastChestOpenTime.value = now

        // Balanced weighted RNG rewards:
        // 30% -> 50 Coins
        // 25% -> 100 Coins
        // 20% -> 1 Hint
        // 15% -> +5 Moves / +30s Booster
        // 5%  -> 150 Coins
        // 5%  -> Super Honey Treasure (150 Coins + 1 Hint)
        val roll = (1..100).random()
        val reward = when {
            roll <= 30 -> {
                addCoins(50)
                MysteryChestReward(coins = 50, title = "+50 Honey Coins!", emoji = "🪙")
            }
            roll <= 55 -> {
                addCoins(100)
                MysteryChestReward(coins = 100, title = "+100 Honey Coins!", emoji = "🪙")
            }
            roll <= 75 -> {
                addHints(1)
                MysteryChestReward(hints = 1, title = "1 Free Hint!", emoji = "💡")
            }
            roll <= 90 -> {
                MysteryChestReward(extraMoves = 5, title = "+5 Extra Moves & 30s!", emoji = "⚡")
            }
            roll <= 95 -> {
                addCoins(150)
                MysteryChestReward(coins = 150, title = "Jackpot: +150 Coins!", emoji = "💰")
            }
            else -> {
                addCoins(150)
                addHints(1)
                MysteryChestReward(coins = 150, hints = 1, title = "Super Honey Treasure!", emoji = "👑")
            }
        }
        return reward
    }

    fun buyHint(cost: Int = 50): Boolean {
        if (_coins.value >= cost) {
            addCoins(-cost)
            addHints(1)
            return true
        }
        return false
    }

    fun buyExtraMoves(cost: Int = 75): Boolean {
        if (_coins.value >= cost) {
            addCoins(-cost)
            return true
        }
        return false
    }

    fun buyRevive(cost: Int = 100): Boolean {
        if (_coins.value >= cost) {
            addCoins(-cost)
            return true
        }
        return false
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC, enabled).apply()
        _musicEnabled.value = enabled
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _hapticEnabled.value = enabled
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _animationsEnabled.value = enabled
    }

    fun setTheme(theme: GameTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _theme.value = theme
    }

    fun setGameMode(mode: GameMode) {
        prefs.edit().putString(KEY_MODE, mode.name).apply()
        _gameMode.value = mode
    }

    fun setCurrentLevel(level: Int) {
        prefs.edit().putInt(KEY_CURRENT_LEVEL, level).apply()
        _currentLevel.value = level
    }

    fun unlockUpToLevel(level: Int) {
        if (level > _highestUnlocked.value) {
            val next = level.coerceAtMost(1000)
            prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, next).apply()
            _highestUnlocked.value = next
        }
    }

    fun setHintsCount(count: Int) {
        val safeCount = count.coerceAtLeast(0)
        prefs.edit().putInt(KEY_HINTS, safeCount).apply()
        _hintsCount.value = safeCount
    }

    fun addHints(amount: Int) {
        setHintsCount(_hintsCount.value + amount)
    }

    fun consumeHint(): Boolean {
        if (_hintsCount.value > 0) {
            setHintsCount(_hintsCount.value - 1)
            return true
        }
        return false
    }

    fun isDailyCompleted(dateKey: String): Boolean {
        return prefs.getBoolean("daily_completed_$dateKey", false)
    }

    fun setDailyCompleted(dateKey: String) {
        prefs.edit().putBoolean("daily_completed_$dateKey", true).apply()
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        _currentLevel.value = 1
        _highestUnlocked.value = 1
        _hintsCount.value = 3
        _soundEnabled.value = true
        _hapticEnabled.value = true
        _animationsEnabled.value = true
        _theme.value = GameTheme.CLASSIC
        _gameMode.value = GameMode.NORMAL
    }

    companion object {
        private const val KEY_SOUND = "pref_sound"
        private const val KEY_MUSIC = "pref_music"
        private const val KEY_HAPTIC = "pref_haptic"
        private const val KEY_ANIMATIONS = "pref_animations"
        private const val KEY_THEME = "pref_theme"
        private const val KEY_MODE = "pref_mode"
        private const val KEY_CURRENT_LEVEL = "pref_current_level"
        private const val KEY_HIGHEST_UNLOCKED = "pref_highest_unlocked"
        private const val KEY_HINTS = "pref_hints"
        private const val KEY_HINTS_DATE = "pref_hints_date"
        private const val KEY_COINS = "pref_coins"
        private const val KEY_WIN_STREAK = "pref_win_streak"
        private const val KEY_DAILY_STREAK = "pref_daily_streak_days"
        private const val KEY_LAST_STREAK_CLAIM_DATE = "pref_last_streak_claim_date"
        private const val KEY_MASCOT = "pref_mascot"
        private const val KEY_UNLOCKED_MASCOTS = "pref_unlocked_mascots"
        private const val KEY_LAST_CHEST_TIME = "pref_last_chest_time"
    }
}
