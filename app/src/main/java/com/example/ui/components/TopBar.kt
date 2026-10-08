package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameTheme
import com.example.ui.theme.GameFontFamily
import java.util.Locale

@Composable
fun TopBar(
    levelNumber: Int,
    remainingArrows: Int,
    totalArrows: Int,
    currentLives: Int,
    maxLives: Int,
    difficulty: String,
    timeRemainingSeconds: Int = 60,
    timeElapsedSeconds: Int = 0,
    theme: GameTheme,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displaySeconds = timeRemainingSeconds.coerceAtLeast(0)
    val minutes = displaySeconds / 60
    val seconds = displaySeconds % 60
    val timerText = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    val isHurry = displaySeconds in 1..10

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // =========================================================================
        // ROW 1: CANDY CRUSH STYLE HEADER (Back Button | BIG Pop Level Title | Settings)
        // =========================================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: 3D Candy Back Button
            CandyCircleButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                size = 46.dp,
                iconSize = 24.dp,
                gradient = listOf(Color(0xFFFF3385), Color(0xFFD81B60), Color(0xFF880E4F)),
                borderColor = Color(0xFFFF99C8),
                testTag = "top_bar_back_button"
            )

            // Center: BIG POP CANDY PILL BADGE FOR LEVEL TITLE (Toon/Game Font)
            CandyPillBadge(
                modifier = Modifier.testTag("top_bar_level_title"),
                gradient = listOf(Color(0xFFFF2A85), Color(0xFFD81B60), Color(0xFF9C1349)),
                borderColor = Color(0xFFFFB3D9),
                cornerRadius = 24.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (levelNumber > 0) "LEVEL $levelNumber" else "DAILY PUZZLE",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0x66000000),
                                offset = Offset(1.5f, 2.5f),
                                blurRadius = 4f
                            )
                        )
                    )
                    Text(
                        text = "⭐",
                        fontSize = 18.sp
                    )
                }
            }

            // Right: 3D Candy Settings Button
            CandyCircleButton(
                onClick = onSettingsClick,
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                size = 46.dp,
                iconSize = 24.dp,
                gradient = listOf(Color(0xFFFF3385), Color(0xFFD81B60), Color(0xFF880E4F)),
                borderColor = Color(0xFFFF99C8),
                testTag = "top_bar_settings_button"
            )
        }

        // =========================================================================
        // ROW 2: JUICY STATS (Paths Pill | 3D Jelly Hearts | Timer Pill)
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Cyan Candy Pill for Remaining Paths ("12 PATHS")
            CandyPillBadge(
                modifier = Modifier.testTag("path_count_chip"),
                gradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF075985)),
                borderColor = Color(0xFF7DD3FC),
                cornerRadius = 16.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color(0xFFBAE6FD),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$remainingArrows PATHS",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0x44000000),
                                offset = Offset(1f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )
                }
            }

            // Center: 3D Candy Jelly Hearts with Floating Animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("hearts_row")
            ) {
                for (i in 1..maxLives) {
                    CandyJellyHeart(
                        isAlive = (i <= currentLives),
                        index = i,
                        size = 32.dp
                    )
                }
            }

            // Right: Amber / Red Candy Pill for Countdown Timer ("⏱ 00:54")
            CandyPillBadge(
                modifier = Modifier.testTag("timer_chip"),
                gradient = if (isHurry) {
                    listOf(Color(0xFFEF4444), Color(0xFFDC2626), Color(0xFF991B1B))
                } else {
                    listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309))
                },
                borderColor = if (isHurry) Color(0xFFFCA5A5) else Color(0xFFFDE68A),
                cornerRadius = 16.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = if (isHurry) Color(0xFFFFE4E6) else Color(0xFFFEF3C7),
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = timerText,
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0x44000000),
                                offset = Offset(1f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )
                }
            }
        }
    }
}
