package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.RewardedAdRewardType
import com.example.audio.SoundManager
import com.example.ui.components.CartoonMascot
import com.example.ui.components.CartoonMascotType
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.MascotCompanionPickerDialog
import com.example.ui.components.MascotMood
import com.example.ui.components.MysteryChestDialog
import com.example.ui.components.PixarHoneyBeeMascot
import com.example.ui.components.RewardCelebrationDialog
import com.example.ui.theme.GameFontFamily
import com.example.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onPlay: () -> Unit,
    onNavigateLevels: () -> Unit,
    onNavigateDaily: () -> Unit,
    onNavigateAchievements: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateCharacters: () -> Unit = {},
    onNavigateRewards: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val currentLevel by viewModel.preferences.currentLevel.collectAsState()
    val totalStars by viewModel.totalStarsFlow.collectAsState(initial = 0)
    val coins by viewModel.preferences.coins.collectAsState()
    val winStreak by viewModel.preferences.winStreak.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()
    val isStreakClaimedToday by viewModel.isStreakClaimedToday.collectAsState()
    val selectedMascot by viewModel.selectedMascot.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val unlockedMascots by viewModel.preferences.unlockedMascots.collectAsState()
    val theme by viewModel.preferences.theme.collectAsState()
    var showCompanionPicker by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var showMysteryChest by remember { mutableStateOf(false) }
    var celebrationDialogData by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Auto-prompt daily streak reward when player visits home screen and it is ready to claim
    var hasAutoShownStreak by remember { mutableStateOf(false) }
    LaunchedEffect(isStreakClaimedToday) {
        if (!isStreakClaimedToday && !hasAutoShownStreak) {
            hasAutoShownStreak = true
            showStreakDialog = true
        }
    }

    // Floating Mascot Bobbing Animation
    val mascotBob = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        mascotBob.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Mascot Wings Flutter
    val wingFlutter = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        wingFlutter.animateTo(
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(140, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Play Button Juicy Bouncing Pulse
    val playPulse = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        playPulse.animateTo(
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(850, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Dynamic Theme Gradient Background adapting to active companion!
    val themeGradient = Brush.verticalGradient(
        colors = listOf(
            theme.backgroundColor,
            theme.surfaceColor,
            theme.boardBackground.copy(alpha = 0.90f),
            theme.accentColor.copy(alpha = 0.40f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeGradient)
    ) {
        // Floating Honeycomb background pattern & dripping honey canopy
        HoneycombBackgroundCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Stats Bar (Honey Coins, Stars, Streak, Settings)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Honey Coins Capsule
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.90f))
                        .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🪙", fontSize = 16.sp)
                    Text(
                        text = "$coins",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF78350F)
                    )
                }

                // Stars Capsule
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.90f))
                        .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "$totalStars",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF78350F)
                    )
                }

                // DAILY STREAK FLAME CAPSULE (Opens Daily Streak Dialog!)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (!isStreakClaimedToday)
                                Brush.horizontalGradient(listOf(Color(0xFFFF5722), Color(0xFFF59E0B)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFFEA580C), Color(0xFFC2410C)))
                        )
                        .border(
                            width = if (!isStreakClaimedToday) 1.5.dp else 1.dp,
                            color = if (!isStreakClaimedToday) Color(0xFFFEF08A) else Color(0xFFFFCCBC),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            SoundManager.playTap()
                            viewModel.hapticManager.tap()
                            showStreakDialog = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("top_daily_streak_badge"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔥", fontSize = 14.sp)
                    Text(
                        text = if (dailyStreak > 0) "$dailyStreak d" else "Streak",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    if (!isStreakClaimedToday) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF08A))
                        )
                    }
                }

                // TOP SHORTCUT BUTTON: Character Switcher Screen
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(selectedMascot.primaryColor, selectedMascot.secondaryColor)
                            )
                        )
                        .border(1.5.dp, Color.White, RoundedCornerShape(20.dp))
                        .clickable {
                            SoundManager.playTap()
                            viewModel.hapticManager.tap()
                            onNavigateCharacters()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("top_characters_shortcut"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(selectedMascot.iconEmoji, fontSize = 15.sp)
                        Text(
                            text = "HEROES",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }

                // Settings Button
                IconButton(
                    onClick = onNavigateSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.90f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Title with Honey Drips
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Cute Honey Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFD97706), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🍯 SWEET HONEY PUZZLE 🐝",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF92400E),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "HONEY",
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD97706),
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = "ARROW ESCAPE",
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F),
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.offset(y = (-4).dp)
                )
            }

            // Daily Streak Claim Reminder Banner (High retention visibility!)
            if (!isStreakClaimedToday) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                        .clickable {
                            SoundManager.playTap()
                            viewModel.hapticManager.tap()
                            showStreakDialog = true
                        }
                        .testTag("daily_streak_reminder_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🎁", fontSize = 26.sp)
                            Column {
                                Text(
                                    text = "DAILY REWARD READY!",
                                    style = TextStyle(
                                        fontFamily = GameFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309)
                                    )
                                )
                                Text(
                                    text = "Day ${(dailyStreak % 7) + 1} coins waiting • Tap to claim!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "CLAIM 🪙",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // CARTOON MASCOTS & ADORABLE COMPANION HERO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (mascotBob.value * (-8)).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Speech bubble greeting from active companion
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(1.5.dp, selectedMascot.secondaryColor, RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "${selectedMascot.iconEmoji} \"${selectedMascot.bubbleGreeting}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sole Main Character Display according to player's selection
                Box(
                    modifier = Modifier
                        .size(154.dp)
                        .clickable {
                            viewModel.onMascotInteract()
                            onNavigateCharacters()
                        }
                        .testTag("home_main_character"),
                    contentAlignment = Alignment.Center
                ) {
                    // Soft circular aura behind mascot matching their signature colors
                    Box(
                        modifier = Modifier
                            .size(142.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        selectedMascot.secondaryColor.copy(alpha = 0.90f),
                                        selectedMascot.primaryColor.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    if (selectedMascot == CartoonMascotType.BEE) {
                        PixarHoneyBeeMascot(
                            mood = mascotMood,
                            size = 146.dp,
                            onClick = {
                                viewModel.onMascotInteract()
                                onNavigateCharacters()
                            }
                        )
                    } else {
                        CartoonMascot(
                            type = selectedMascot,
                            mood = mascotMood,
                            size = 136.dp,
                            onClick = {
                                viewModel.onMascotInteract()
                                onNavigateCharacters()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Companion Quick Switcher Chips with Lock Status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    CartoonMascotType.entries.forEach { mascot ->
                        val isSelected = mascot == selectedMascot
                        val isUnlocked = mascot.unlockCost == 0 ||
                                mascot.id.equals("bee", ignoreCase = true) ||
                                unlockedMascots.contains(mascot.id.lowercase())

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) mascot.primaryColor
                                    else if (!isUnlocked) Color(0xFFF1F5F9).copy(alpha = 0.9f)
                                    else Color.White.copy(alpha = 0.85f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.White else if (!isUnlocked) Color(0xFFCBD5E1) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    if (isUnlocked) {
                                        viewModel.setSelectedMascot(mascot)
                                    } else {
                                        SoundManager.playTap()
                                        onNavigateCharacters()
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = mascot.iconEmoji, fontSize = 12.sp)
                                Text(
                                    text = mascot.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (!isUnlocked) Color(0xFF94A3B8) else Color(0xFF475569)
                                )
                                if (!isUnlocked) {
                                    Text(text = "🔒", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Companion Shop & Switcher Page Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                        .clickable {
                            SoundManager.playTap()
                            viewModel.hapticManager.tap()
                            onNavigateCharacters()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("companion_shop_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🎭", fontSize = 14.sp)
                        Text(
                            text = "SWITCH CHARACTER & SHOP (1,000 🪙)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MAIN PLAY BUTTON: Golden Honey Glow, Juicy Scale Bounce
            Box(
                modifier = Modifier
                    .scale(playPulse.value)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0xFFD97706))
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFF59E0B), Color(0xFFFBBF24), Color(0xFFD97706))
                        )
                    )
                    .border(2.dp, Color(0xFFFFFBEB), RoundedCornerShape(26.dp))
                    .clickable {
                        SoundManager.playLevelSelect()
                        onPlay()
                    }
                    .padding(vertical = 18.dp)
                    .testTag("home_play_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🍯", fontSize = 24.sp)
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "PLAY LEVEL $currentLevel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tap arrows to clear the honey hive!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E).copy(alpha = 0.9f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color(0xFF78350F),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Secondary Action Cards: Daily Challenge, Levels, Trophy Hive
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Daily Challenge Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(105.dp)
                        .clickable { onNavigateDaily() }
                        .testTag("home_daily_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📅", fontSize = 26.sp)
                        Text(
                            text = "Daily Hive",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Heart Shape ❤️",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = Color(0xFFD97706)
                        )
                    }
                }

                // Level Select Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(105.dp)
                        .clickable { onNavigateLevels() }
                        .testTag("home_levels_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🗺️", fontSize = 26.sp)
                        Text(
                            text = "Level Map",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "1000 Shapes",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = Color(0xFFD97706)
                        )
                    }
                }

                // Trophy / Hive Achievements Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(105.dp)
                        .clickable { onNavigateAchievements() }
                        .testTag("home_achievements_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆", fontSize = 26.sp)
                        Text(
                            text = "Trophy Hive",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Badges & Stars",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // HONEY ADMOB REWARD SYSTEM SHORTCUTS (🎁 Reward Center, 🎁 Mystery Chest)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // REWARD CENTER CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(92.dp)
                        .clickable { onNavigateRewards() }
                        .testTag("home_reward_center_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎁", fontSize = 22.sp)
                        Text(
                            text = "Reward Center",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "Free Hints & Coins",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.5.sp,
                            color = Color(0xFF059669)
                        )
                    }
                }

                // MYSTERY CHEST CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(92.dp)
                        .clickable { showMysteryChest = true }
                        .testTag("home_mystery_chest_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✨", fontSize = 22.sp)
                        Text(
                            text = "Mystery Chest",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Open Free Loot",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.5.sp,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showCompanionPicker) {
            MascotCompanionPickerDialog(
                currentMascot = selectedMascot,
                coins = coins,
                unlockedMascots = unlockedMascots,
                onMascotSelected = { newMascot ->
                    viewModel.setSelectedMascot(newMascot)
                },
                onMascotUnlocked = { mascotToUnlock ->
                    viewModel.unlockMascot(mascotToUnlock)
                },
                onDismiss = { showCompanionPicker = false }
            )
        }

        if (showStreakDialog) {
            DailyStreakDialog(
                dailyStreak = dailyStreak,
                isClaimedToday = isStreakClaimedToday,
                mascotType = selectedMascot,
                theme = theme,
                onClaimReward = {
                    val result = viewModel.claimDailyStreak(doubleReward = false)
                    celebrationDialogData = "DAILY REWARD!" to "+${result?.coinsEarned ?: 50} Honey Coins added to your stash!"
                    result
                },
                onClaimWithDouble = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.DAILY_REWARD_DOUBLE,
                            onSuccess = {
                                val result = viewModel.claimDailyStreak(doubleReward = true)
                                celebrationDialogData = "2X DAILY REWARD!" to "+${result?.coinsEarned ?: 100} Double Honey Coins poured into your stash!"
                            },
                            onUnavailable = { msg ->
                                celebrationDialogData = "NOTICE" to msg
                            }
                        )
                    }
                    null
                },
                onDismiss = { showStreakDialog = false }
            )
        }

        if (showMysteryChest) {
            MysteryChestDialog(
                viewModel = viewModel,
                mascotType = selectedMascot,
                onDismiss = { showMysteryChest = false }
            )
        }

        if (celebrationDialogData != null) {
            RewardCelebrationDialog(
                title = celebrationDialogData?.first ?: "REWARD UNLOCKED!",
                subtitle = celebrationDialogData?.second ?: "Enjoy your sweet reward!",
                emoji = "🍯",
                mascotType = selectedMascot,
                onDismiss = { celebrationDialogData = null }
            )
        }
    }
}

/**
 * Draws animated cartoon honey bee mascot with flapping wings, cute face, and honey wand!
 */
@Composable
private fun CartoonBeeMascot(wingScale: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = size.minDimension * 0.32f

        // 1. Translucent animated wings
        val wingW = radius * 0.75f
        val wingH = radius * 1.1f * wingScale

        // Left Wing
        drawOval(
            color = Color(0xAAECFEFF),
            topLeft = Offset(cx - wingW * 1.3f, cy - wingH * 0.85f),
            size = androidx.compose.ui.geometry.Size(wingW, wingH)
        )
        drawOval(
            color = Color(0xFF06B6D4),
            topLeft = Offset(cx - wingW * 1.3f, cy - wingH * 0.85f),
            size = androidx.compose.ui.geometry.Size(wingW, wingH),
            style = Stroke(width = 2.5f)
        )

        // Right Wing
        drawOval(
            color = Color(0xAAECFEFF),
            topLeft = Offset(cx + wingW * 0.3f, cy - wingH * 0.85f),
            size = androidx.compose.ui.geometry.Size(wingW, wingH)
        )
        drawOval(
            color = Color(0xFF06B6D4),
            topLeft = Offset(cx + wingW * 0.3f, cy - wingH * 0.85f),
            size = androidx.compose.ui.geometry.Size(wingW, wingH),
            style = Stroke(width = 2.5f)
        )

        // 2. Chubby Golden Honey Bee Body
        drawOval(
            color = Color(0xFFFBBF24), // Golden Yellow
            topLeft = Offset(cx - radius * 0.95f, cy - radius * 0.75f),
            size = androidx.compose.ui.geometry.Size(radius * 1.9f, radius * 1.5f)
        )

        // 3. Cute Black Stripes
        val stripeWidth = radius * 0.28f
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(cx - stripeWidth * 0.7f, cy - radius * 0.75f),
            size = androidx.compose.ui.geometry.Size(stripeWidth, radius * 1.5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(cx + stripeWidth * 0.7f, cy - radius * 0.70f),
            size = androidx.compose.ui.geometry.Size(stripeWidth * 0.75f, radius * 1.4f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )

        // 4. Little Cute Stinger
        val stingerPath = Path().apply {
            moveTo(cx + radius * 0.95f, cy)
            lineTo(cx + radius * 1.25f, cy)
            lineTo(cx + radius * 0.90f, cy + radius * 0.15f)
            close()
        }
        drawPath(stingerPath, Color(0xFF1E293B))

        // 5. Big Cute Anime Cartoon Eyes
        val eyeLeftX = cx - radius * 0.55f
        val eyeRightX = cx - radius * 0.15f
        val eyeY = cy - radius * 0.18f
        val eyeR = radius * 0.24f

        // White eye base
        drawCircle(Color.White, radius = eyeR, center = Offset(eyeLeftX, eyeY))
        drawCircle(Color.White, radius = eyeR, center = Offset(eyeRightX, eyeY))

        // Big dark pupils
        drawCircle(Color(0xFF1E293B), radius = eyeR * 0.65f, center = Offset(eyeLeftX + 2f, eyeY))
        drawCircle(Color(0xFF1E293B), radius = eyeR * 0.65f, center = Offset(eyeRightX + 2f, eyeY))

        // Sparkle reflections
        drawCircle(Color.White, radius = eyeR * 0.25f, center = Offset(eyeLeftX + 4f, eyeY - 4f))
        drawCircle(Color.White, radius = eyeR * 0.25f, center = Offset(eyeRightX + 4f, eyeY - 4f))

        // 6. Rosy Cheeks
        drawCircle(Color(0xFFFF8080).copy(alpha = 0.6f), radius = eyeR * 0.45f, center = Offset(eyeLeftX - 4f, eyeY + eyeR * 0.9f))
        drawCircle(Color(0xFFFF8080).copy(alpha = 0.6f), radius = eyeR * 0.45f, center = Offset(eyeRightX + 8f, eyeY + eyeR * 0.9f))

        // 7. Happy Smile
        val mouthPath = Path().apply {
            moveTo(cx - radius * 0.40f, cy + radius * 0.18f)
            quadraticTo(
                cx - radius * 0.25f, cy + radius * 0.38f,
                cx - radius * 0.10f, cy + radius * 0.18f
            )
        }
        drawPath(mouthPath, Color(0xFF78350F), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

        // 8. Cute Antennae with Golden Honey Drop Tips
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(cx - radius * 0.55f, cy - radius * 0.65f),
            end = Offset(cx - radius * 0.75f, cy - radius * 1.10f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        drawCircle(Color(0xFFF59E0B), radius = 6f, center = Offset(cx - radius * 0.75f, cy - radius * 1.10f))

        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(cx - radius * 0.25f, cy - radius * 0.70f),
            end = Offset(cx - radius * 0.35f, cy - radius * 1.15f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        drawCircle(Color(0xFFF59E0B), radius = 6f, center = Offset(cx - radius * 0.35f, cy - radius * 1.15f))
    }
}

/**
 * Draws floating golden honeycomb hexagons and honey drips.
 */
@Composable
private fun HoneycombBackgroundCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Honeycomb floating hexagons
        val hexColor = Color(0xFFF59E0B).copy(alpha = 0.12f)
        val hexSize = 34f

        for (row in 0..7) {
            for (col in 0..6) {
                val offsetX = if (row % 2 == 1) hexSize * 1.5f else 0f
                val hx = col * hexSize * 3f + offsetX - 20f
                val hy = row * hexSize * 2.2f + 40f
                drawHexagon(center = Offset(hx, hy), radius = hexSize * 0.85f, color = hexColor)
            }
        }

        // Dripping honey wavy canopy at the top
        val dripPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, 55f)
            quadraticTo(w * 0.88f, 95f, w * 0.78f, 60f)
            quadraticTo(w * 0.68f, 35f, w * 0.58f, 75f)
            quadraticTo(w * 0.48f, 105f, w * 0.38f, 65f)
            quadraticTo(w * 0.28f, 35f, w * 0.18f, 85f)
            quadraticTo(w * 0.08f, 110f, 0f, 50f)
            close()
        }
        drawPath(
            path = dripPath,
            color = Color(0xFFF59E0B).copy(alpha = 0.25f)
        )
        val dripPathInner = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, 40f)
            quadraticTo(w * 0.75f, 70f, w * 0.55f, 45f)
            quadraticTo(w * 0.35f, 80f, w * 0.15f, 50f)
            quadraticTo(w * 0.05f, 70f, 0f, 35f)
            close()
        }
        drawPath(
            path = dripPathInner,
            color = Color(0xFFFBBF24).copy(alpha = 0.40f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexagon(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    for (i in 0..5) {
        val angle = Math.toRadians((60.0 * i - 30.0))
        val x = center.x + radius * kotlin.math.cos(angle).toFloat()
        val y = center.y + radius * kotlin.math.sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = 2.0f))
}
