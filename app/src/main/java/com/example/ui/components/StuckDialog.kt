package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.model.GameTheme
import com.example.ui.theme.GameFontFamily

@Composable
fun StuckDialog(
    isGameOver: Boolean,
    isTimeout: Boolean = false,
    canRevive: Boolean = true,
    revivesUsed: Int = 0,
    maxRevives: Int = 2,
    theme: GameTheme,
    mascotType: CartoonMascotType = CartoonMascotType.BEE,
    onRestart: () -> Unit,
    onWatchAdForRevive: () -> Unit,
    onBuyReviveWithCoins: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = { /* Non-dismissible without choosing action */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x33000000))
                .clip(RoundedCornerShape(26.dp))
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFF6B8B), Color(0xFFE11D48), Color(0xFF9F1239))
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .testTag("out_of_lives_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crying mascot
                CartoonMascot(
                    type = mascotType,
                    mood = MascotMood.CRYING_SAD,
                    size = 88.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = when {
                        isTimeout -> "⏰ TIME RAN OUT!"
                        isGameOver -> "❤️ DON'T GIVE UP!"
                        else -> "⚡ OUT OF MOVES!"
                    },
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isTimeout)
                        "The level timer hit zero! Get a revive to continue clearing the board."
                    else
                        mascotType.bubbleSad,
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE11D48),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (canRevive) {
                    Text(
                        text = "Revives available: ${maxRevives - revivesUsed}/$maxRevives",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                } else {
                    Text(
                        text = "Maximum revives reached for this level run.",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action 1: WATCH AD TO REVIVE
                if (canRevive) {
                    CandyButton(
                        onClick = onWatchAdForRevive,
                        gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                        borderColor = Color(0xFFFDE68A),
                        shadowColor = Color(0xFF78350F),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("revive_watch_ad_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Watch Ad to Revive",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "WATCH AD → REVIVE (+3 LIVES)",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 14.sp,
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

                    // Action 2: BUY REVIVE WITH 100 COINS
                    if (onBuyReviveWithCoins != null) {
                        CandyButton(
                            onClick = onBuyReviveWithCoins,
                            gradient = listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857)),
                            borderColor = Color(0xFF6EE7B7),
                            shadowColor = Color(0xFF065F46),
                            cornerRadius = 20.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("revive_coins_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🪙", fontSize = 16.sp)
                                Text(
                                    text = "REVIVE FOR 100 COINS",
                                    style = TextStyle(
                                        fontFamily = GameFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Action 3: TRY AGAIN (Restart level)
                CandyButton(
                    onClick = onRestart,
                    gradient = listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF334155)),
                    borderColor = Color(0xFFCBD5E1),
                    shadowColor = Color(0xFF1E293B),
                    cornerRadius = 20.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("try_again_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Level",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "TRY AGAIN",
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
