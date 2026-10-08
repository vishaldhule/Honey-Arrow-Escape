package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
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
import com.example.audio.SoundManager
import com.example.ui.theme.GameFontFamily

@Composable
fun NeedHintDialog(
    coins: Int,
    mascotType: CartoonMascotType = CartoonMascotType.BEE,
    onWatchAd: () -> Unit,
    onBuyWithCoins: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(20.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33000000))
                .clip(RoundedCornerShape(26.dp))
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFB703), Color(0xFFFB8500), Color(0xFFD46000))
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .testTag("need_hint_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cheerful thinking mascot
                CartoonMascot(
                    type = mascotType,
                    mood = MascotMood.THINKING,
                    size = 80.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "💡 NEED A HINT?",
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Watch a short ad to receive 1 FREE HINT, or use your Honey Coins!",
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Option 1: WATCH AD FOR 1 FREE HINT
                CandyButton(
                    onClick = onWatchAd,
                    gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309)),
                    borderColor = Color(0xFFFDE68A),
                    shadowColor = Color(0xFF78350F),
                    cornerRadius = 20.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("hint_watch_ad_button")
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
                            text = "WATCH & GET HINT",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 14.5.sp,
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

                // Option 2: BUY WITH 50 COINS
                val canAffordCoins = coins >= 50
                CandyButton(
                    onClick = {
                        if (canAffordCoins) {
                            onBuyWithCoins()
                        }
                    },
                    gradient = if (canAffordCoins) {
                        listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857))
                    } else {
                        listOf(Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF475569))
                    },
                    borderColor = if (canAffordCoins) Color(0xFF6EE7B7) else Color(0xFFCBD5E1),
                    shadowColor = Color(0xFF065F46),
                    cornerRadius = 20.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("hint_buy_coins_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 15.sp)
                        Text(
                            text = if (canAffordCoins) "BUY FOR 50 COINS (HAVE: $coins)" else "NEED 50 COINS (HAVE: $coins)",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Option 3: MAYBE LATER
                CandyButton(
                    onClick = {
                        SoundManager.playTap()
                        onDismiss()
                    },
                    gradient = listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF334155)),
                    borderColor = Color(0xFFCBD5E1),
                    shadowColor = Color(0xFF1E293B),
                    cornerRadius = 20.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("hint_maybe_later_button")
                ) {
                    Text(
                        text = "MAYBE LATER",
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
    }
}
