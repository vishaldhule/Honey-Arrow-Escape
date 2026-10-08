package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.DailyStreakDialog
import com.example.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyPuzzleScreen(
    viewModel: GameViewModel,
    onStartDaily: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val theme by viewModel.preferences.theme.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()
    val isStreakClaimedToday by viewModel.isStreakClaimedToday.collectAsState()
    val selectedMascot by viewModel.selectedMascot.collectAsState()
    var showStreakDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    val displayFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    val now = Date()
    val dateKey = dateFormat.format(now)
    val dateDisplay = displayFormat.format(now)

    val isCompleted = viewModel.preferences.isDailyCompleted(dateKey)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("daily_puzzle_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .testTag("daily_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = theme.accentColor
                )
            }

            Text(
                text = "Daily Puzzle",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimaryColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(theme.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (isCompleted) Color(0xFF10B981) else theme.accentColor,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Text(
                    text = dateDisplay,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimaryColor
                )

                Text(
                    text = if (isCompleted)
                        "You've completed today's challenge! Come back tomorrow for a new puzzle."
                    else
                        "A unique mystery board generated specially for today. All players receive the same layout!",
                    fontSize = 14.sp,
                    color = theme.textSecondaryColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Reward showcase
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (theme.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🪙", fontSize = 18.sp)
                        Text(
                            text = "+50 Coins",
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimaryColor,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+3 Stars",
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimaryColor,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+2 Hints",
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimaryColor,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Streak Retention Banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isStreakClaimedToday) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isStreakClaimedToday) Color(0xFF10B981) else Color(0xFFF59E0B),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { showStreakDialog = true }
                        .testTag("daily_screen_streak_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔥", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "$dailyStreak-Day Login Streak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF78350F)
                                )
                                Text(
                                    text = if (isStreakClaimedToday) "Today's reward claimed! ✓" else "Tap to claim today's coin bonus!",
                                    fontSize = 11.sp,
                                    color = if (isStreakClaimedToday) Color(0xFF047857) else Color(0xFFB45309)
                                )
                            }
                        }

                        Text(
                            text = "CALENDAR →",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.loadDailyPuzzle()
                        onStartDaily()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("daily_play_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = if (isCompleted) "REPLAY TODAY'S PUZZLE" else "PLAY DAILY PUZZLE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (showStreakDialog) {
            DailyStreakDialog(
                dailyStreak = dailyStreak,
                isClaimedToday = isStreakClaimedToday,
                mascotType = selectedMascot,
                theme = theme,
                onClaimReward = {
                    viewModel.claimDailyStreak()
                },
                onDismiss = { showStreakDialog = false }
            )
        }
    }
}
