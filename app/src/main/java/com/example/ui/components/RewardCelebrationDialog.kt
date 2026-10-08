package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.GameFontFamily

/**
 * Player Happiness System:
 * Delightful, satisfying celebration dialog presented after any rewarded ad or reward event.
 */
@Composable
fun RewardCelebrationDialog(
    title: String,
    subtitle: String,
    emoji: String,
    mascotType: CartoonMascotType = CartoonMascotType.BEE,
    onDismiss: () -> Unit
) {
    val scale = remember { Animatable(0.2f) }
    val mascotHop = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        SoundManager.playCoinReward()
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        mascotHop.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .scale(scale.value)
                .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = Color(0x33000000))
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFD700), Color(0xFFF59E0B), Color(0xFFFF9800))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .testTag("reward_celebration_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cheerful pill header
                CandyPillBadge(
                    gradient = listOf(Color(0xFFFF8F00), Color(0xFFFF6F00), Color(0xFFE65100)),
                    borderColor = Color(0xFFFFE082),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "REWARD UNLOCKED!",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 16.sp,
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
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cheering mascot
                CartoonMascot(
                    type = mascotType,
                    mood = MascotMood.CHEERING,
                    size = 92.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Big Emoji & Title
                Text(
                    text = emoji,
                    fontSize = 42.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
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
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = GameFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Sweet Candy Claim Button
                CandyButton(
                    onClick = {
                        SoundManager.playTap()
                        onDismiss()
                    },
                    gradient = listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857)),
                    borderColor = Color(0xFF6EE7B7),
                    shadowColor = Color(0xFF065F46),
                    cornerRadius = 22.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("reward_celebration_claim_button")
                ) {
                    Text(
                        text = "AWESOME! 🍯",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 17.sp,
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
        }
    }
}
