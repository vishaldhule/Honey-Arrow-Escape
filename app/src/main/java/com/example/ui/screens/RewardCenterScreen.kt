package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.RewardedAdRewardType
import com.example.audio.SoundManager
import com.example.ui.components.CandyButton
import com.example.ui.components.CandyCircleButton
import com.example.ui.components.CandyPillBadge
import com.example.ui.components.CartoonMascot
import com.example.ui.components.MysteryChestDialog
import com.example.ui.components.RewardCelebrationDialog
import com.example.ui.theme.GameFontFamily
import com.example.viewmodel.GameViewModel

@Composable
fun RewardCenterScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val activity = context as? Activity

    val coins by viewModel.preferences.coins.collectAsState()
    val hintsCount by viewModel.preferences.hintsCount.collectAsState()
    val theme by viewModel.preferences.theme.collectAsState()
    val selectedMascot by viewModel.selectedMascot.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()

    var showMysteryChest by remember { mutableStateOf(false) }
    var celebrationInfo by remember { mutableStateOf<Pair<String, String>?>(null) }
    var statusNotice by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Top Header: Back Button + Title + Stash
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CandyCircleButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    size = 46.dp,
                    gradient = listOf(Color(0xFFFF3385), Color(0xFFD81B60), Color(0xFF880E4F)),
                    borderColor = Color(0xFFFF99C8),
                    testTag = "reward_center_back_button"
                )

                CandyPillBadge(
                    gradient = listOf(Color(0xFFFFB300), Color(0xFFF59E0B), Color(0xFFD97706)),
                    borderColor = Color(0xFFFDE68A),
                    cornerRadius = 24.dp
                ) {
                    Text(
                        text = "🎁 REWARD CENTER",
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
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Balance Stash
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("🪙", fontSize = 16.sp)
                    Text(
                        text = "$coins",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cheerful Mascot Greeting Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .border(1.5.dp, Color(0xFFFDE68A), RoundedCornerShape(22.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CartoonMascot(
                    type = selectedMascot,
                    mood = mascotMood,
                    size = 64.dp
                )
                Column {
                    Text(
                        text = "FREE SWEET TREATS!",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    )
                    Text(
                        text = "Watch short ads to grab free hints, moves, and coins, or spend your hard-earned coins in the pantry!",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF78350F),
                        lineHeight = 15.sp
                    )
                }
            }

            if (statusNotice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusNotice ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE11D48)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // REWARD CARD 1: 💡 FREE HINT
            // =========================================================================
            RewardItemCard(
                title = "FREE HINT",
                subtitle = "Stuck on a tricky arrow? Get 1 puzzle solution step.",
                iconEmoji = "💡",
                currentAmount = "$hintsCount in stash",
                onWatchAd = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.FREE_HINT,
                            onSuccess = {
                                celebrationInfo = "1 FREE HINT" to "Ready to guide you through the next maze!"
                            },
                            onUnavailable = { msg -> statusNotice = msg }
                        )
                    }
                },
                onBuyCoins = {
                    val bought = viewModel.buyHintWithCoins()
                    if (bought) {
                        celebrationInfo = "1 FREE HINT" to "Added 1 hint to your puzzle kit!"
                    } else {
                        statusNotice = "Need 50 Coins to buy a hint!"
                    }
                },
                coinCost = 50,
                watchButtonLabel = "FREE (WATCH AD)"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // REWARD CARD 2: 🪙 50 HONEY COINS
            // =========================================================================
            RewardItemCard(
                title = "+50 HONEY COINS",
                subtitle = "Sweet gold coins to save up for cute companions & power-ups.",
                iconEmoji = "🪙",
                currentAmount = "$coins coins",
                onWatchAd = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.REWARD_CENTER_COINS,
                            onSuccess = {
                                celebrationInfo = "+50 HONEY COINS" to "Poured into your honey coin jar!"
                            },
                            onUnavailable = { msg -> statusNotice = msg }
                        )
                    }
                },
                onBuyCoins = null,
                coinCost = null,
                watchButtonLabel = "GET +50 🪙 (WATCH AD)"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // REWARD CARD 3: 🎁 MYSTERY CHEST
            // =========================================================================
            RewardItemCard(
                title = "MYSTERY CHEST",
                subtitle = "Crack open a lucky treasure! Contains coins, hints, or super boosters.",
                iconEmoji = "🎁",
                currentAmount = "Free every 10 min",
                onWatchAd = {
                    showMysteryChest = true
                },
                onBuyCoins = {
                    showMysteryChest = true
                },
                coinCost = 150,
                watchButtonLabel = "OPEN CHEST"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // REWARD CARD 4: ⚡ MOVE & TIMER BOOSTER
            // =========================================================================
            RewardItemCard(
                title = "MOVE & TIME BOOSTER",
                subtitle = "+5 Extra Moves and +30s timer extension for difficult boards.",
                iconEmoji = "⚡",
                currentAmount = "Active when stuck",
                onWatchAd = {
                    if (activity != null) {
                        viewModel.watchRewardedAd(
                            activity = activity,
                            rewardType = RewardedAdRewardType.REWARD_CENTER_BOOST,
                            onSuccess = {
                                celebrationInfo = "BOOSTER ACTIVATED" to "Bonus coins and hints credited!"
                            },
                            onUnavailable = { msg -> statusNotice = msg }
                        )
                    }
                },
                onBuyCoins = {
                    val bought = viewModel.buyExtraMovesWithCoins()
                    if (bought) {
                        celebrationInfo = "+5 MOVES & 30s" to "Boost applied!"
                    } else {
                        statusNotice = "Need 75 Coins!"
                    }
                },
                coinCost = 75,
                watchButtonLabel = "FREE (WATCH AD)"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Mystery Chest Popup
        if (showMysteryChest) {
            MysteryChestDialog(
                viewModel = viewModel,
                mascotType = selectedMascot,
                onDismiss = { showMysteryChest = false }
            )
        }

        // Celebration Popup
        if (celebrationInfo != null) {
            RewardCelebrationDialog(
                title = celebrationInfo?.first ?: "REWARD EARNED!",
                subtitle = celebrationInfo?.second ?: "Enjoy your reward!",
                emoji = "🍯",
                mascotType = selectedMascot,
                onDismiss = { celebrationInfo = null }
            )
        }

        // Ad Status / Notification Dialog
        if (statusNotice != null) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { statusNotice = null }) {
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "HONEY NOTICE",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F)
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = statusNotice ?: "",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CandyButton(
                            onClick = { statusNotice = null },
                            gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                            borderColor = Color(0xFFFEF3C7),
                            shadowColor = Color(0xFF78350F),
                            cornerRadius = 18.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("reward_status_notice_ok_button")
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

@Composable
private fun RewardItemCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    currentAmount: String,
    onWatchAd: () -> Unit,
    onBuyCoins: (() -> Unit)?,
    coinCost: Int?,
    watchButtonLabel: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconEmoji, fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        )
                        Text(
                            text = currentAmount,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Watch Ad Button
                CandyButton(
                    onClick = onWatchAd,
                    gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                    borderColor = Color(0xFFFDE68A),
                    shadowColor = Color(0xFF78350F),
                    cornerRadius = 16.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = watchButtonLabel,
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }

                // Buy with Coins Button (if available)
                if (onBuyCoins != null && coinCost != null) {
                    CandyButton(
                        onClick = onBuyCoins,
                        gradient = listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF334155)),
                        borderColor = Color(0xFFCBD5E1),
                        shadowColor = Color(0xFF1E293B),
                        cornerRadius = 16.dp,
                        modifier = Modifier
                            .weight(0.8f)
                            .height(44.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🪙", fontSize = 13.sp)
                            Text(
                                text = "$coinCost COINS",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 11.5.sp,
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
