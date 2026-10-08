package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.RewardedAdRewardType
import com.example.audio.SoundManager
import com.example.data.MysteryChestReward
import com.example.ui.theme.GameFontFamily
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Honey-Themed Mystery Chest Dialog.
 *
 * Players can voluntarily watch a short ad to open the free chest, or use 150 coins.
 * Features chest opening bounce physics and transparent reward reveal.
 */
@Composable
fun MysteryChestDialog(
    viewModel: GameViewModel,
    mascotType: CartoonMascotType = CartoonMascotType.BEE,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    val coins = viewModel.preferences.coins.value
    val canOpenFree = viewModel.preferences.canOpenFreeMysteryChest()
    val cooldownSeconds = viewModel.preferences.getChestCooldownRemainingSeconds()

    var isOpening by remember { mutableStateOf(false) }
    var earnedReward by remember { mutableStateOf<MysteryChestReward?>(null) }
    var statusNotice by remember { mutableStateOf<String?>(null) }

    // Wiggle / bounce animation for the chest
    val chestShake = remember { Animatable(0f) }
    val chestScale = remember { Animatable(1f) }

    LaunchedEffect(isOpening) {
        if (isOpening) {
            // Exciting anticipation shake
            repeat(6) {
                chestShake.animateTo(12f, tween(50, easing = LinearEasing))
                chestShake.animateTo(-12f, tween(50, easing = LinearEasing))
            }
            chestShake.animateTo(0f, tween(40))
            chestScale.animateTo(1.35f, tween(180, easing = FastOutSlowInEasing))
            chestScale.animateTo(1.0f, spring())
        } else {
            chestScale.animateTo(
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = Color(0x33000000))
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFB300), Color(0xFFF59E0B), Color(0xFFD97706))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .testTag("mystery_chest_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CandyPillBadge(
                        gradient = listOf(Color(0xFFFF8F00), Color(0xFFE65100), Color(0xFFBF360C)),
                        borderColor = Color(0xFFFFE082),
                        cornerRadius = 18.dp
                    ) {
                        Text(
                            text = "🎁 MYSTERY CHEST",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

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
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Honey Chest Icon
                Box(
                    modifier = Modifier
                        .scale(chestScale.value)
                        .padding(horizontal = chestShake.value.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (earnedReward != null) "✨🎁✨" else "🎁",
                        fontSize = if (earnedReward != null) 60.sp else 68.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (earnedReward != null) {
                    // Reward Reveal View
                    Text(
                        text = earnedReward?.title ?: "REWARD UNLOCKED!",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Added to your Honey stash! Enjoy your puzzle perks!",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CandyButton(
                        onClick = {
                            SoundManager.playTap()
                            onDismiss()
                        },
                        gradient = listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857)),
                        borderColor = Color(0xFF6EE7B7),
                        shadowColor = Color(0xFF065F46),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("mystery_chest_collect_button")
                    ) {
                        Text(
                            text = "CLAIM REWARD 🍯",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                } else {
                    // Pre-opening view with options
                    Text(
                        text = "SWEET HONEY TREASURE",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Contains free Coins, Hints, Move Boosters, or Super Honey Pots!",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF78350F),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Option 1: WATCH AD TO OPEN FREE
                    CandyButton(
                        onClick = {
                            if (activity != null) {
                                viewModel.watchRewardedAd(
                                    activity = activity,
                                    rewardType = RewardedAdRewardType.MYSTERY_CHEST,
                                    onSuccess = {
                                        scope.launch {
                                            isOpening = true
                                            delay(700)
                                            earnedReward = viewModel.openMysteryChest(isAd = true)
                                            isOpening = false
                                        }
                                    },
                                    onUnavailable = { msg ->
                                        statusNotice = msg
                                    }
                                )
                            }
                        },
                        gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                        borderColor = Color(0xFFFDE68A),
                        shadowColor = Color(0xFF78350F),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("mystery_chest_watch_ad_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Watch Ad",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "WATCH AD → OPEN FREE",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 15.sp,
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: OPEN FOR 150 COINS
                    val hasEnoughCoins = coins >= 150
                    CandyButton(
                        onClick = {
                            if (hasEnoughCoins) {
                                scope.launch {
                                    isOpening = true
                                    delay(500)
                                    earnedReward = viewModel.openMysteryChest(isAd = false)
                                    isOpening = false
                                }
                            } else {
                                statusNotice = "Need 150 Coins! Solve puzzles or watch an ad to earn more."
                            }
                        },
                        gradient = if (hasEnoughCoins) {
                            listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF334155))
                        } else {
                            listOf(Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF475569))
                        },
                        borderColor = if (hasEnoughCoins) Color(0xFFCBD5E1) else Color(0xFF94A3B8),
                        shadowColor = Color(0xFF1E293B),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("mystery_chest_coin_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🪙", fontSize = 16.sp)
                            Text(
                                text = "OPEN FOR 150 COINS",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                if (statusNotice != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusNotice ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE11D48),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
