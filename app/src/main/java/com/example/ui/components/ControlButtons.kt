package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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

@Composable
fun ControlButtons(
    hintsCount: Int,
    canUseHint: Boolean,
    isUndoAvailable: Boolean,
    theme: GameTheme,
    mascotType: CartoonMascotType = CartoonMascotType.BEE,
    mascotMood: MascotMood = MascotMood.HAPPY,
    onHintClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRestartClick: () -> Unit,
    onMascotClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Subtle pulsating glow on hint button when ready to assist
    val hintPulse = remember { Animatable(1f) }
    LaunchedEffect(canUseHint) {
        if (canUseHint) {
            hintPulse.animateTo(
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            hintPulse.snapTo(1f)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // =========================================================================
        // JUICY 3D CANDY HINT BUTTON ("💡 HINT (3)")
        // =========================================================================
        CandyButton(
            onClick = onHintClick,
            enabled = canUseHint,
            gradient = if (canUseHint) {
                listOf(Color(0xFFFFB703), Color(0xFFFB8500), Color(0xFFD46000))
            } else {
                listOf(Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF475569))
            },
            borderColor = if (canUseHint) Color(0xFFFFE082) else Color(0xFFCBD5E1),
            shadowColor = if (canUseHint) Color(0xFFE65100) else Color(0xFF334155),
            cornerRadius = 26.dp,
            modifier = Modifier
                .scale(hintPulse.value)
                .height(52.dp)
                .width(136.dp)
                .testTag("hint_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Hint",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "HINT ($hintsCount)",
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
            }
        }

        // =========================================================================
        // ANIMATED CARTOON COMPANION IN GAME BAR WITH REACTIVE EMOTIONS!
        // =========================================================================
        CartoonMascot(
            type = mascotType,
            mood = mascotMood,
            size = 64.dp,
            onClick = onMascotClick
        )

        // =========================================================================
        // JUICY 3D CANDY RESET BUTTON ("🔄 RESET")
        // =========================================================================
        CandyButton(
            onClick = onRestartClick,
            gradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9), Color(0xFF4C1D95)),
            borderColor = Color(0xFFC4B5FD),
            shadowColor = Color(0xFF3B0764),
            cornerRadius = 26.dp,
            modifier = Modifier
                .height(52.dp)
                .width(120.dp)
                .testTag("restart_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Board",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "RESET",
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
            }
        }
    }
}
