package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.SoundManager
import com.example.data.DailyStreakClaimResult
import com.example.data.DailyStreakReward
import com.example.model.GameTheme
import com.example.ui.theme.GameFontFamily

@Composable
fun DailyStreakDialog(
    dailyStreak: Int,
    isClaimedToday: Boolean,
    mascotType: CartoonMascotType,
    theme: GameTheme,
    onClaimReward: () -> DailyStreakClaimResult?,
    onClaimWithDouble: (() -> DailyStreakClaimResult?)? = null,
    onDismiss: () -> Unit
) {
    // Pulse animation for claim button and active reward card
    val pulseScale = remember { Animatable(1f) }
    LaunchedEffect(isClaimedToday) {
        if (!isClaimedToday) {
            pulseScale.animateTo(
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(650, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    val flameBob = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        flameBob.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    var claimedJustNow by remember { mutableStateOf<DailyStreakClaimResult?>(null) }
    val claimedState = isClaimedToday || claimedJustNow != null

    // Determine current cycle day (1 to 7)
    val effectiveStreak = if (claimedState) dailyStreak else (dailyStreak + 1)
    val currentCycleDay = if (effectiveStreak <= 0) 1 else ((effectiveStreak - 1) % 7) + 1

    val rewardsList = listOf(
        DailyStreakReward(1, 50, 0, "Day 1"),
        DailyStreakReward(2, 75, 0, "Day 2"),
        DailyStreakReward(3, 100, 1, "Day 3"),
        DailyStreakReward(4, 150, 0, "Day 4"),
        DailyStreakReward(5, 200, 1, "Day 5"),
        DailyStreakReward(6, 300, 0, "Day 6"),
        DailyStreakReward(7, 500, 2, "Day 7 👑", isGrandReward = true)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(2.5.dp, Color(0xFFF59E0B), RoundedCornerShape(28.dp))
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .padding(20.dp)
                .testTag("daily_streak_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flame Streak Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF5722), Color(0xFFF59E0B))
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 15.sp,
                            modifier = Modifier.offset(y = (flameBob.value * (-2)).dp)
                        )
                        Text(
                            text = "$dailyStreak DAY STREAK",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title & Subtitle
                Text(
                    text = "DAILY REWARD STREAK",
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F)
                    )
                )

                Text(
                    text = "Play consecutive days to unlock massive coin bonuses!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF92400E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Active Companion cheering
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CartoonMascot(
                        type = mascotType,
                        mood = if (claimedState) MascotMood.CELEBRATING else MascotMood.CHEERING,
                        size = 56.dp
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (claimedState)
                                "Awesome dedication! Keep your streak burning tomorrow! 🔥"
                            else
                                "Tap below to claim your Day $currentCycleDay coins! Save for 1,000 🪙 heroes! 🍯",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7-DAY REWARD GRID TRACK
                // Row 1: Days 1 to 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rewardsList.subList(0, 4).forEach { reward ->
                        StreakDayCard(
                            reward = reward,
                            currentCycleDay = currentCycleDay,
                            isClaimedToday = claimedState,
                            pulseScale = if (!claimedState && reward.day == currentCycleDay) pulseScale.value else 1f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Days 5 to 7 (Day 7 takes 2x weight for Grand Milestone!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rewardsList.subList(4, 6).forEach { reward ->
                        StreakDayCard(
                            reward = reward,
                            currentCycleDay = currentCycleDay,
                            isClaimedToday = claimedState,
                            pulseScale = if (!claimedState && reward.day == currentCycleDay) pulseScale.value else 1f,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Day 7 Grand Reward Card
                    val day7 = rewardsList[6]
                    StreakDayCard(
                        reward = day7,
                        currentCycleDay = currentCycleDay,
                        isClaimedToday = claimedState,
                        pulseScale = if (!claimedState && day7.day == currentCycleDay) pulseScale.value else 1f,
                        modifier = Modifier.weight(1.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CLAIM BUTTON / STATUS BANNER
                if (!claimedState) {
                    val activeReward = rewardsList.firstOrNull { it.day == currentCycleDay } ?: rewardsList[0]

                    if (onClaimWithDouble != null) {
                        // Option 1: WATCH AD → 2X DOUBLE TODAY'S REWARD
                        CandyButton(
                            onClick = {
                                val res = onClaimWithDouble()
                                if (res != null) {
                                    claimedJustNow = res
                                }
                            },
                            gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                            borderColor = Color(0xFFFEF3C7),
                            shadowColor = Color(0xFF78350F),
                            cornerRadius = 22.dp,
                            modifier = Modifier
                                .scale(pulseScale.value)
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("claim_double_daily_streak_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("✨", fontSize = 18.sp)
                                Text(
                                    text = "WATCH AD → DOUBLE (+${activeReward.coins * 2} 🪙)",
                                    style = TextStyle(
                                        fontFamily = GameFontFamily,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Option 2: CLAIM NORMAL REWARD
                        CandyButton(
                            onClick = {
                                val res = onClaimReward()
                                if (res != null) {
                                    claimedJustNow = res
                                }
                            },
                            gradient = listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857)),
                            borderColor = Color(0xFF6EE7B7),
                            shadowColor = Color(0xFF065F46),
                            cornerRadius = 20.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("claim_daily_streak_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🎁", fontSize = 16.sp)
                                Text(
                                    text = "CLAIM NORMAL (+${activeReward.coins} 🪙)",
                                    style = TextStyle(
                                        fontFamily = GameFontFamily,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        // Standard Claim
                        CandyButton(
                            onClick = {
                                val res = onClaimReward()
                                if (res != null) {
                                    claimedJustNow = res
                                }
                            },
                            gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                            borderColor = Color(0xFFFEF3C7),
                            shadowColor = Color(0xFF78350F),
                            cornerRadius = 22.dp,
                            modifier = Modifier
                                .scale(pulseScale.value)
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("claim_daily_streak_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🎁", fontSize = 18.sp)
                                Text(
                                    text = "CLAIM DAY $currentCycleDay (+${activeReward.coins} 🪙)",
                                    style = TextStyle(
                                        fontFamily = GameFontFamily,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Already Claimed Today Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFD1FAE5))
                            .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(20.dp))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF047857),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "TODAY'S BONUS CLAIMED! COME BACK TOMORROW",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // In-Game Extra Bonus Note
                Text(
                    text = "🔥 Bonus: Your daily streak also gives up to +35 extra coins on EVERY level you win!",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StreakDayCard(
    reward: DailyStreakReward,
    currentCycleDay: Int,
    isClaimedToday: Boolean,
    pulseScale: Float,
    modifier: Modifier = Modifier
) {
    val isPast = if (isClaimedToday) reward.day <= currentCycleDay else reward.day < currentCycleDay
    val isCurrent = reward.day == currentCycleDay
    val isFuture = if (isClaimedToday) reward.day > currentCycleDay else reward.day > currentCycleDay

    val cardBackground = when {
        isPast -> Color(0xFFECFDF5) // Soft Mint Green
        isCurrent && !isClaimedToday -> Color(0xFFFEF3C7) // Golden Glow
        isCurrent && isClaimedToday -> Color(0xFFD1FAE5)
        reward.isGrandReward -> Color(0xFFFFFBEB)
        else -> Color(0xFFF8FAFC)
    }

    val borderColor = when {
        isPast -> Color(0xFF10B981)
        isCurrent && !isClaimedToday -> Color(0xFFF59E0B)
        isCurrent && isClaimedToday -> Color(0xFF10B981)
        reward.isGrandReward -> Color(0xFFFBBF24)
        else -> Color(0xFFE2E8F0)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        modifier = modifier
            .scale(pulseScale)
            .shadow(if (isCurrent && !isClaimedToday) 6.dp else 2.dp, RoundedCornerShape(16.dp))
            .border(if (isCurrent) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Day Label
            Text(
                text = reward.title,
                fontSize = if (reward.isGrandReward) 11.sp else 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isCurrent) Color(0xFFB45309) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Icon or Coin
            if (isPast) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Claimed",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            } else if (reward.isGrandReward) {
                Text("👑", fontSize = 18.sp)
            } else {
                Text("🪙", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Coin Amount
            Text(
                text = "+${reward.coins}",
                fontSize = if (reward.isGrandReward) 13.sp else 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isPast) Color(0xFF047857) else Color(0xFF0F172A)
            )

            // Hint Bonus
            if (reward.hints > 0) {
                Text(
                    text = "+${reward.hints} 💡",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
            }

            // Status Badge
            Spacer(modifier = Modifier.height(2.dp))
            if (isCurrent && !isClaimedToday) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF59E0B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "READY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            } else if (isFuture) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
