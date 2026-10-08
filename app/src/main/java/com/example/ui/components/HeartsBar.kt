package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun HeartsBar(
    currentLives: Int,
    maxLives: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("hearts_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxLives) {
            val isAlive = i <= currentLives
            val scale by animateFloatAsState(
                targetValue = if (isAlive) 1.0f else 0.75f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                label = "heart_scale_$i"
            )
            val heartColor by animateColorAsState(
                targetValue = if (isAlive) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                label = "heart_color_$i"
            )

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = if (isAlive) "Active life" else "Lost life",
                tint = heartColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
                    .testTag("heart_icon_$i")
            )
        }
    }
}
