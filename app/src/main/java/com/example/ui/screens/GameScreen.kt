package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobManager
import com.example.ads.RewardedAdRewardType
import com.example.model.GameStatus
import com.example.ui.components.ControlButtons
import com.example.ui.components.GameBoardCanvas
import com.example.ui.components.MascotCompanionPickerDialog
import com.example.ui.components.NeedHintDialog
import com.example.ui.components.PlayfulConfettiAndStarBurst
import com.example.ui.components.RewardCelebrationDialog
import com.example.ui.components.StuckDialog
import com.example.ui.components.TopBar
import com.example.ui.components.VictoryDialog
import com.example.ui.theme.GameFontFamily
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val gameState by viewModel.gameState.collectAsState()
    val theme by viewModel.preferences.theme.collectAsState()
    val hintsCount by viewModel.preferences.hintsCount.collectAsState()
    val isDaily by viewModel.isDailyMode.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val selectedMascot by viewModel.selectedMascot.collectAsState()
    val coins by viewModel.preferences.coins.collectAsState()
    val unlockedMascots by viewModel.preferences.unlockedMascots.collectAsState()

    var showCompanionPicker by remember { mutableStateOf(false) }
    var showNeedHintDialog by remember { mutableStateOf(false) }
    var isDoubleRewardClaimed by remember { mutableStateOf(false) }
    var celebrationDialogData by remember { mutableStateOf<Pair<String, String>?>(null) }
    var adStatusNotice by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity

    val state = gameState ?: return

    var showVictoryDialog by remember { mutableStateOf(false) }
    var isVictoryCelebrating by remember { mutableStateOf(false) }

    // Screen Shake Animatable offsets
    val shakeX = remember { Animatable(0f) }
    val shakeY = remember { Animatable(0f) }

    // Celebratory overlay scale and radiant board glow
    val bannerScale = remember { Animatable(0f) }
    val victoryGlowAlpha = remember { Animatable(0f) }

    LaunchedEffect(state.status) {
        if (state.status == GameStatus.VICTORY) {
            isVictoryCelebrating = true

            // 1. Tactile Screen Shake: decays rapidly like a juicy arcade impact
            launch {
                val shakeKeyframes = listOf(0f, -14f, 14f, -11f, 11f, -8f, 8f, -4f, 4f, -2f, 2f, 0f)
                for (offset in shakeKeyframes) {
                    shakeX.snapTo(offset)
                    shakeY.snapTo(-offset * 0.55f)
                    delay(28)
                }
                shakeX.snapTo(0f)
                shakeY.snapTo(0f)
            }

            // 2. Radiant Golden Aura Bloom on the board
            launch {
                victoryGlowAlpha.animateTo(
                    targetValue = 0.90f,
                    animationSpec = tween(260, easing = FastOutSlowInEasing)
                )
                victoryGlowAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(450, easing = LinearOutSlowInEasing)
                )
            }

            // 3. Celebratory "GRID CLEARED!" Banner Pop with juicy spring bounce
            launch {
                bannerScale.snapTo(0.2f)
                bannerScale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
                bannerScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            // 4. Savor celebration moment before presenting the full victory modal
            delay(1150)
            showVictoryDialog = true
        } else {
            isVictoryCelebrating = false
            showVictoryDialog = false
            shakeX.snapTo(0f)
            shakeY.snapTo(0f)
            bannerScale.snapTo(0f)
            victoryGlowAlpha.snapTo(0f)
        }
    }

    // Dynamic theme background adapting to active companion!
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        theme.backgroundColor,
                        theme.surfaceColor,
                        theme.backgroundColor
                    )
                )
            )
            .statusBarsPadding()
            .testTag("game_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Big Pop Candy Crush Top HUD (Back, Big Level Pill, Settings, Paths Left, 3D Jelly Hearts, Timer)
            TopBar(
                levelNumber = if (isDaily) 0 else state.levelData.levelNumber,
                remainingArrows = state.remainingArrowsCount,
                totalArrows = state.totalArrowsCount,
                currentLives = state.lives,
                maxLives = state.maxLives,
                difficulty = state.levelData.difficulty,
                timeRemainingSeconds = state.timeRemainingSeconds,
                timeElapsedSeconds = state.timeElapsedSeconds,
                theme = theme,
                onBackClick = onNavigateBack,
                onSettingsClick = onNavigateSettings
            )

            // Main Puzzle Board framed in a glossy, rounded card (matching Image 1 & 2)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .offset(x = shakeX.value.dp, y = shakeY.value.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = if (isVictoryCelebrating) 16.dp else 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = if (isVictoryCelebrating) Color(0xFFFFD700) else Color(0x22FF2A85)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = if (victoryGlowAlpha.value > 0.05f) 4.dp else 2.5.dp,
                            brush = if (victoryGlowAlpha.value > 0.05f) {
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFD700).copy(alpha = victoryGlowAlpha.value),
                                        Color(0xFFFF9800).copy(alpha = victoryGlowAlpha.value),
                                        Color(0xFFFF4081).copy(alpha = victoryGlowAlpha.value)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFB3D9),
                                        Color(0xFFE2E8F0),
                                        Color(0xFFBAE6FD)
                                    )
                                )
                            },
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    GameBoardCanvas(
                        gameState = state,
                        theme = theme,
                        onArrowTapped = { arrow ->
                            viewModel.onArrowTapped(arrow)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }

            // Big 3D Candy Bottom Controls (💡 HINT (3), 🔄 RESET + Dynamic Mascot)
            ControlButtons(
                hintsCount = hintsCount,
                canUseHint = state.status == GameStatus.PLAYING && state.hintedArrowId == null,
                isUndoAvailable = state.isUndoAvailable,
                theme = theme,
                mascotType = selectedMascot,
                mascotMood = mascotMood,
                onHintClick = {
                    if (hintsCount > 0) {
                        viewModel.useHint()
                    } else {
                        showNeedHintDialog = true
                    }
                },
                onUndoClick = { viewModel.undo() },
                onRestartClick = { viewModel.restartLevel() },
                onMascotClick = {
                    viewModel.onMascotInteract()
                    showCompanionPicker = true
                }
            )
        }

        // Candy Crush Victory Celebration Modal
        if (showVictoryDialog && state.status == GameStatus.VICTORY) {
            VictoryDialog(
                levelNumber = if (isDaily) 0 else state.levelData.levelNumber,
                stars = state.earnedStars,
                movesTaken = state.movesTaken,
                parMoves = state.levelData.parMoves,
                timeElapsedSeconds = state.timeElapsedSeconds,
                mistakes = state.mistakes,
                shape = state.levelData.shape,
                reward = state.reward,
                theme = theme,
                mascotType = selectedMascot,
                isDoubleRewardClaimed = isDoubleRewardClaimed,
                onWatchDoubleReward = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.DOUBLE_LEVEL_COINS,
                            onSuccess = {
                                isDoubleRewardClaimed = true
                                celebrationDialogData = "2X REWARD DOUBLED!" to "Double honey coins poured into your stash!"
                            },
                            onUnavailable = { msg -> adStatusNotice = msg }
                        )
                    }
                },
                onNextLevel = {
                    isDoubleRewardClaimed = false
                    if (activity != null) {
                        AdMobManager.recordLevelClearedAndShowInterstitialIfAllowed(activity) {
                            if (isDaily) onNavigateBack() else viewModel.nextLevel()
                        }
                    } else {
                        if (isDaily) onNavigateBack() else viewModel.nextLevel()
                    }
                },
                onReplay = {
                    isDoubleRewardClaimed = false
                    viewModel.restartLevel()
                },
                onHome = {
                    isDoubleRewardClaimed = false
                    onNavigateBack()
                }
            )
        }

        // Out of Lives Dialog (with crying sad cartoon mascot!)
        if (state.status == GameStatus.STUCK || state.status == GameStatus.GAME_OVER) {
            val isTimeout = state.timeRemainingSeconds <= 0
            StuckDialog(
                isGameOver = state.status == GameStatus.GAME_OVER,
                isTimeout = isTimeout,
                canRevive = viewModel.canRevive(),
                revivesUsed = viewModel.revivesUsedInCurrentLevel,
                maxRevives = com.example.ads.AdMobConfig.MAX_REVIVES_PER_LEVEL,
                theme = theme,
                mascotType = selectedMascot,
                onRestart = { viewModel.restartLevel() },
                onWatchAdForRevive = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.REVIVE,
                            onSuccess = {
                                celebrationDialogData = "REVIVED!" to "3 Lives & +30 seconds restored! Go for it!"
                            },
                            onUnavailable = { msg -> adStatusNotice = msg }
                        )
                    }
                },
                onBuyReviveWithCoins = {
                    val bought = viewModel.buyReviveWithCoins()
                    if (bought) {
                        celebrationDialogData = "REVIVED!" to "3 Lives & +30 seconds restored!"
                    } else {
                        adStatusNotice = "Need 100 Coins to Revive!"
                    }
                }
            )
        }

        // Celebratory "GRID CLEARED!" Banner Pop Overlay (Triggered when grid is fully cleared!)
        if (isVictoryCelebrating && bannerScale.value > 0.05f && !showVictoryDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
                    modifier = Modifier
                        .scale(bannerScale.value)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFF9800), Color(0xFFFF4081))
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .shadow(20.dp, RoundedCornerShape(26.dp))
                        .testTag("grid_cleared_celebration_banner")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 26.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🎉 GRID CLEARED! 🌟",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309)
                            )
                        )
                        Text(
                            text = "ALL ARROWS ESCAPED IN TIME! ⏱️",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⭐ ${state.earnedStars} Stars", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFFD97706))
                            Text("•", color = Color(0xFF94A3B8))
                            Text("🎯 ${state.movesTaken} Moves", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF475569))
                            Text("•", color = Color(0xFF94A3B8))
                            Text("⏱️ ${state.timeRemainingSeconds}s Left", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF10B981))
                        }
                    }
                }
            }
        }

        // Full-Screen Celebratory Confetti & Radiant Star-Burst Particle Effect!
        if (isVictoryCelebrating) {
            PlayfulConfettiAndStarBurst(
                trigger = isVictoryCelebrating,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Mascot Companion Chooser
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

        // Need A Hint Modal (Watch Ad or use 50 Coins)
        if (showNeedHintDialog) {
            NeedHintDialog(
                coins = coins,
                mascotType = selectedMascot,
                onWatchAd = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.FREE_HINT,
                            onSuccess = {
                                showNeedHintDialog = false
                                viewModel.useHint()
                                celebrationDialogData = "1 FREE HINT" to "Guiding arrow highlighted on your grid!"
                            },
                            onUnavailable = { msg -> adStatusNotice = msg }
                        )
                    }
                },
                onBuyWithCoins = {
                    val bought = viewModel.buyHintWithCoins()
                    if (bought) {
                        showNeedHintDialog = false
                        viewModel.useHint()
                        celebrationDialogData = "1 HINT UNLOCKED" to "Guiding arrow highlighted on your grid!"
                    } else {
                        adStatusNotice = "Need 50 Coins to buy a hint!"
                    }
                },
                onDismiss = { showNeedHintDialog = false }
            )
        }

        // Player Happiness Celebration Modal
        if (celebrationDialogData != null) {
            RewardCelebrationDialog(
                title = celebrationDialogData?.first ?: "REWARD UNLOCKED!",
                subtitle = celebrationDialogData?.second ?: "Enjoy your sweet treat!",
                emoji = "🍯",
                mascotType = selectedMascot,
                onDismiss = { celebrationDialogData = null }
            )
        }

        // Ad Status / Notification Dialog
        if (adStatusNotice != null) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { adStatusNotice = null }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍯", fontSize = 42.sp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "HONEY NOTICE",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F)
                            )
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = adStatusNotice ?: "",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                        com.example.ui.components.CandyButton(
                            onClick = { adStatusNotice = null },
                            gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                            borderColor = Color(0xFFFEF3C7),
                            shadowColor = Color(0xFF78350F),
                            cornerRadius = 18.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("ad_status_notice_ok_button")
                        ) {
                            Text(
                                text = "OK",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
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
