package com.example.ads

/**
 * Types of rewards that players can voluntarily watch an ad for.
 */
enum class RewardedAdRewardType(
    val title: String,
    val description: String,
    val iconEmoji: String
) {
    FREE_HINT("Free Hint", "1 Free Puzzle Hint", "💡"),
    EXTRA_MOVES("Extra Moves", "+5 Moves & +30s Timer", "⚡"),
    REVIVE("Full Revive", "Restore 3 Lives & Continue", "❤️"),
    DOUBLE_LEVEL_COINS("2X Level Coins", "Double your victory coin bounty", "🪙"),
    MYSTERY_CHEST("Mystery Chest", "Unlocks a Honey Mystery Chest", "🎁"),
    DAILY_REWARD_DOUBLE("2X Daily Reward", "Doubles today's streak reward", "🔥"),
    REWARD_CENTER_COINS("+50 Honey Coins", "Instant 50 bonus coins", "🪙"),
    REWARD_CENTER_HINT("+1 Hint Booster", "Instant 1 free hint", "💡"),
    REWARD_CENTER_BOOST("+1 Time Booster", "Adds +30s time buffer", "⏰")
}
