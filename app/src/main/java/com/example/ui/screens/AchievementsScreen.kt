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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.viewmodel.GameViewModel

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int
) {
    val isUnlocked: Boolean get() = current >= target
    val progress: Float get() = (current.toFloat() / target).coerceIn(0f, 1f)
}

@Composable
fun AchievementsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val theme by viewModel.preferences.theme.collectAsState()
    val completedCount by viewModel.completedCountFlow.collectAsState(initial = 0)
    val totalStars by viewModel.totalStarsFlow.collectAsState(initial = 0)
    val starsEarned = totalStars ?: 0

    val achievements = listOf(
        AchievementItem("first", "First Honey Escape", "Complete your first puzzle", 1, completedCount),
        AchievementItem("ten", "Honey Novice", "Clear 10 puzzle levels in Honey Arrow Escape", 10, completedCount),
        AchievementItem("fifty", "Pathfinder", "Clear 50 puzzle levels", 50, completedCount),
        AchievementItem("hundred", "Century Solver", "Clear 100 puzzle levels", 100, completedCount),
        AchievementItem("half", "Halfway Hero", "Clear 250 puzzle levels", 250, completedCount),
        AchievementItem("master", "Honey Arrow Grandmaster", "Clear all 500 levels in Honey Arrow Escape!", 500, completedCount),
        AchievementItem("stars50", "Star Collector", "Earn 50 golden stars", 50, starsEarned),
        AchievementItem("stars300", "Star Constellation", "Earn 300 golden stars", 300, starsEarned),
        AchievementItem("stars1500", "Flawless Galaxy", "Earn 1500 golden stars", 1500, starsEarned)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .statusBarsPadding()
            .testTag("achievements_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .testTag("achievements_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = theme.accentColor
                )
            }

            Text(
                text = "Trophies & Milestones",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimaryColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (item.isUnlocked) Color(0xFFFBBF24).copy(alpha = 0.2f)
                                    else if (theme.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (item.isUnlocked) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimaryColor
                                )

                                if (item.isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = item.description,
                                fontSize = 13.sp,
                                color = theme.textSecondaryColor,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { item.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (item.isUnlocked) Color(0xFF10B981) else theme.accentColor,
                                trackColor = if (theme.isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                            )

                            Text(
                                text = "${item.current} / ${item.target}",
                                fontSize = 11.sp,
                                color = theme.textSecondaryColor,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
