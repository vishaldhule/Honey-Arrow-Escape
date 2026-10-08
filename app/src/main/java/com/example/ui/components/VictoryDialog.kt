package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundManager
import com.example.model.GameTheme
import com.example.model.LevelReward
import com.example.model.PuzzleShape
import com.example.ui.theme.GameFontFamily
import java.util.Locale

@Composable
fun VictoryDialog(
    levelNumber: Int,
    stars: Int,
    movesTaken: Int,
    parMoves: Int,
    timeElapsedSeconds: Int = 42,
    mistakes: Int = 0,
    shape: PuzzleShape = PuzzleShape.HEART,
    reward: LevelReward? = null,
    theme: GameTheme,
    mascotType: CartoonMascotType = CartoonMascotType.BEE,
    isDoubleRewardClaimed: Boolean = false,
    onWatchDoubleReward: (() -> Unit)? = null,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    val bannerScale = remember { Animatable(0f) }
    val starScale1 = remember { Animatable(0f) }
    val starScale2 = remember { Animatable(0f) }
    val starScale3 = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        SoundManager.playVictory()
        bannerScale.animateTo(1.15f, tween(200, easing = FastOutSlowInEasing))
        bannerScale.animateTo(1.0f, tween(100))

        starScale1.animateTo(1.25f, tween(180, easing = FastOutSlowInEasing))
        starScale1.animateTo(1.0f, tween(80))

        if (stars >= 2) {
            starScale2.animateTo(1.25f, tween(180, easing = FastOutSlowInEasing))
            starScale2.animateTo(1.0f, tween(80))
        }

        if (stars >= 3) {
            starScale3.animateTo(1.25f, tween(180, easing = FastOutSlowInEasing))
            starScale3.animateTo(1.0f, tween(80))
        }
    }

    val minutes = timeElapsedSeconds / 60
    val seconds = timeElapsedSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Dialog(onDismissRequest = { /* Non-dismissible without choice */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x33000000))
                .clip(RoundedCornerShape(28.dp))
                .testTag("victory_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // =========================================================================
                // CANDY CRUSH "SWEET! LEVEL COMPLETE" HEADER
                // =========================================================================
                CandyPillBadge(
                    modifier = Modifier.scale(bannerScale.value),
                    gradient = listOf(Color(0xFFFF2A85), Color(0xFFD81B60), Color(0xFF880E4F)),
                    borderColor = Color(0xFFFFB3D9),
                    cornerRadius = 24.dp
                ) {
                    Text(
                        text = "SWEET! LEVEL COMPLETE",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0x66000000),
                                offset = Offset(1.5f, 2.5f),
                                blurRadius = 4f
                            )
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bouncy 3 Golden Stars Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 1",
                        tint = if (stars >= 1) Color(0xFFFFC107) else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .scale(starScale1.value)
                            .size(42.dp)
                    )
                    // Star 2 (Center - slightly larger)
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 2",
                        tint = if (stars >= 2) Color(0xFFFFC107) else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .scale(if (stars >= 2) starScale2.value else 1f)
                            .size(52.dp)
                    )
                    // Star 3
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 3",
                        tint = if (stars >= 3) Color(0xFFFFC107) else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .scale(if (stars >= 3) starScale3.value else 1f)
                            .size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Adorable Cartoon Companion in Celebration Mood
                CartoonMascot(
                    type = mascotType,
                    mood = MascotMood.CELEBRATING,
                    size = 100.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Level Subtitle
                Text(
                    text = "Level $levelNumber Solved!",
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Target Shape
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Shape",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${shape.badge} ${shape.displayName}",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 30.dp)
                                .background(Color(0xFFCBD5E1))
                        )

                        // Time
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Time",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = timeFormatted,
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 30.dp)
                                .background(Color(0xFFCBD5E1))
                        )

                        // Mistakes
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Mistakes",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "$mistakes",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (mistakes == 0) Color(0xFF059669) else Color(0xFFEF4444)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =========================================================================
                // LEVEL REWARD COINS BREAKDOWN (⭐ Star Coins, Par Bonus, Streak Bonus)
                // =========================================================================
                val totalRewardCoins = reward?.totalCoins ?: (stars * 10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                            )
                        )
                        .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🪙", fontSize = 20.sp)
                            Text(
                                text = "+$totalRewardCoins COINS EARNED!",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF92400E)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        val starBonusText = if (reward != null) "⭐ $stars Stars: +${reward.baseCoins}" else "⭐ $stars Stars: +${stars * 10}"
                        val parText = if (reward != null && reward.parBonus > 0) " • 🎯 Par: +${reward.parBonus}" else ""
                        val streakText = if (reward != null && reward.streakBonus > 0) " • 🔥 Streak: +${reward.streakBonus}" else ""
                        val dailyStreakText = if (reward != null && reward.dailyStreakBonus > 0) " • 📅 Day ${reward.dailyStreakDays}: +${reward.dailyStreakBonus}" else ""
                        Text(
                            text = "$starBonusText$parText$streakText$dailyStreakText",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Save 1,000 🪙 to unlock new cartoon companions & themes in the shop!",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                // =========================================================================
                // 2X REWARD OPPORTUNITY (WATCH AD TO DOUBLE REWARD)
                // =========================================================================
                if (!isDoubleRewardClaimed && onWatchDoubleReward != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CandyButton(
                        onClick = {
                            SoundManager.playTap()
                            onWatchDoubleReward()
                        },
                        gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                        borderColor = Color(0xFFFDE68A),
                        shadowColor = Color(0xFF78350F),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("victory_double_reward_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "✨", fontSize = 16.sp)
                            Text(
                                text = "WATCH AD → DOUBLE (+${totalRewardCoins * 2} 🪙)",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    shadow = Shadow(
                                        color = Color(0x66000000),
                                        offset = Offset(1.5f, 2f),
                                        blurRadius = 3f
                                    )
                                )
                            )
                        }
                    }
                } else if (isDoubleRewardClaimed) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🎉 2X REWARD CLAIMED! Sweet Honey Bonus! 🍯",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =========================================================================
                // BIG 3D CANDY "NEXT LEVEL →" BUTTON
                // =========================================================================
                CandyButton(
                    onClick = {
                        SoundManager.playLevelSelect()
                        onNextLevel()
                    },
                    gradient = listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857)),
                    borderColor = Color(0xFF6EE7B7),
                    shadowColor = Color(0xFF065F46),
                    cornerRadius = 24.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("victory_next_level_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "NEXT LEVEL",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color(0x66000000),
                                    offset = Offset(1.5f, 2f),
                                    blurRadius = 3f
                                )
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Level",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary actions: Replay and Home
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CandyButton(
                        onClick = onReplay,
                        gradient = listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF334155)),
                        borderColor = Color(0xFF94A3B8),
                        shadowColor = Color(0xFF1E293B),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Replay",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Replay",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    CandyButton(
                        onClick = onHome,
                        gradient = listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF334155)),
                        borderColor = Color(0xFF94A3B8),
                        shadowColor = Color(0xFF1E293B),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Home",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
