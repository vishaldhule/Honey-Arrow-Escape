package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.components.CartoonMascot
import com.example.ui.components.CartoonMascotType
import com.example.ui.components.MascotMood
import com.example.ui.theme.GameFontFamily
import com.example.viewmodel.GameViewModel

@Composable
fun CharacterSelectScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept back button to return to HomeScreen cleanly
    BackHandler { onBack() }

    val coins by viewModel.preferences.coins.collectAsState()
    val unlockedMascots by viewModel.preferences.unlockedMascots.collectAsState()
    val equippedMascot by viewModel.selectedMascot.collectAsState()
    val theme by viewModel.preferences.theme.collectAsState()

    // Preview state: player can tap any character to preview their emotions & theme
    var previewMascot by remember { mutableStateOf(equippedMascot) }
    var previewMood by remember { mutableStateOf(MascotMood.HAPPY) }
    var selectedCategory by remember { mutableStateOf("All") }

    // Floating bobbing animation for preview hero
    val heroBob = remember { Animatable(0f) }
    LaunchedEffect(previewMascot) {
        heroBob.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val categories = listOf("All", "Kids & Heroes", "Cute Animals", "Elemental & Magic")

    val filteredMascots = remember(selectedCategory) {
        if (selectedCategory == "All") {
            CartoonMascotType.entries
        } else {
            CartoonMascotType.entries.filter { it.category == selectedCategory }
        }
    }

    // Dynamic background matching the active previewed theme
    val screenGradient = Brush.verticalGradient(
        colors = listOf(
            theme.backgroundColor,
            theme.surfaceColor,
            theme.boardBackground.copy(alpha = 0.85f),
            theme.accentColor.copy(alpha = 0.35f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(screenGradient)
            .statusBarsPadding()
            .testTag("character_select_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // =========================================================================
            // TOP HEADER BAR: BACK BUTTON, TITLE, COIN BALANCE
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = {
                        SoundManager.playTap()
                        viewModel.hapticManager.tap()
                        onBack()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f))
                        .shadow(4.dp, CircleShape)
                        .testTag("characters_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = Color(0xFF1E293B)
                    )
                }

                // Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HERO ROSTER",
                        style = TextStyle(
                            fontFamily = GameFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.textPrimaryColor
                        )
                    )
                    Text(
                        text = "Switch Character & Signature Theme",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.textSecondaryColor
                    )
                }

                // Gold Coins Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                        .border(1.5.dp, Color(0xFFFEF3C7), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🪙", fontSize = 14.sp)
                        Text(
                            text = "$coins",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // =========================================================================
            // FEATURED HERO SHOWCASE CARD (Interactive emotions + apply theme button)
            // =========================================================================
            val isPreviewUnlocked = previewMascot.unlockCost == 0 ||
                    previewMascot.id.equals("bee", ignoreCase = true) ||
                    unlockedMascots.contains(previewMascot.id.lowercase())
            val isPreviewEquipped = previewMascot == equippedMascot
            val canAfford = coins >= previewMascot.unlockCost

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .border(2.5.dp, previewMascot.primaryColor, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Character Name & Emoji
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(previewMascot.iconEmoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = previewMascot.displayName,
                                    style = TextStyle(
                                        fontFamily = GameFontFamily,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                                Text(
                                    text = previewMascot.subtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Theme Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(previewMascot.secondaryColor)
                                .border(1.dp, previewMascot.primaryColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🎨 ${previewMascot.associatedTheme.title}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = previewMascot.primaryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Character Mascot Drawing in Hero Bob
                    Box(
                        modifier = Modifier
                            .size(118.dp)
                            .offset(y = (heroBob.value * (-6)).dp)
                            .clickable {
                                SoundManager.playTap()
                                previewMood = when (previewMood) {
                                    MascotMood.HAPPY -> MascotMood.CHEERING
                                    MascotMood.CHEERING -> MascotMood.CELEBRATING
                                    MascotMood.CELEBRATING -> MascotMood.CRYING_SAD
                                    MascotMood.CRYING_SAD -> MascotMood.THINKING
                                    else -> MascotMood.HAPPY
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CartoonMascot(
                            type = previewMascot,
                            mood = previewMood,
                            size = 112.dp
                        )
                    }

                    // Mascot Greeting Bubble
                    Text(
                        text = if (previewMood == MascotMood.CRYING_SAD) "\"${previewMascot.bubbleSad}\"" else "\"${previewMascot.bubbleGreeting}\"",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Emotion Testing Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            MascotMood.HAPPY to "😊 Happy",
                            MascotMood.CHEERING to "🎉 Cheer",
                            MascotMood.CELEBRATING to "🥳 Celebrate",
                            MascotMood.CRYING_SAD to "😭 Sad",
                            MascotMood.THINKING to "🤔 Think"
                        ).forEach { (mood, label) ->
                            val isMoodSelected = previewMood == mood
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isMoodSelected) previewMascot.primaryColor else Color(0xFFF1F5F9))
                                    .clickable {
                                        SoundManager.playTap()
                                        previewMood = mood
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isMoodSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isMoodSelected) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Button (Equip / Apply Theme / Unlock 1,000 Coins)
                    if (isPreviewEquipped) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF10B981))
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓ EQUIPPED & ACTIVE THEME",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    } else if (isPreviewUnlocked) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(listOf(previewMascot.primaryColor, previewMascot.secondaryColor)))
                                .border(1.5.dp, Color.White, RoundedCornerShape(16.dp))
                                .clickable {
                                    SoundManager.playTap()
                                    viewModel.hapticManager.tap()
                                    viewModel.setSelectedMascot(previewMascot)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "APPLY CHARACTER & SWITCH THEME 🎨",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    } else {
                        // Locked: requires 1,000 coins
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (canAfford)
                                        Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                                    else
                                        Brush.horizontalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                                )
                                .clickable(enabled = canAfford) {
                                    viewModel.unlockMascot(previewMascot)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (canAfford) "UNLOCK FOR 1,000 🪙 (CLICK TO BUY)" else "LOCKED (NEED 1,000 🪙 • Earn ${1000 - coins} more)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =========================================================================
            // CATEGORY FILTER CHIPS (All, Kids & Heroes, Cute Animals, Elemental & Magic)
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCatSelected) theme.accentColor else Color.White.copy(alpha = 0.85f))
                            .border(
                                width = if (isCatSelected) 2.dp else 1.dp,
                                color = if (isCatSelected) Color.White else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                SoundManager.playTap()
                                selectedCategory = cat
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isCatSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isCatSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // =========================================================================
            // 2-COLUMN GRID OF CHARACTERS
            // =========================================================================
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredMascots) { mascot ->
                    val isSelected = mascot == previewMascot
                    val isEquipped = mascot == equippedMascot
                    val isUnlocked = mascot.unlockCost == 0 ||
                            mascot.id.equals("bee", ignoreCase = true) ||
                            unlockedMascots.contains(mascot.id.lowercase())
                    val canAffordThis = coins >= mascot.unlockCost

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) mascot.secondaryColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.90f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.5.dp else if (isEquipped) 2.dp else 1.dp,
                                color = if (isSelected) mascot.primaryColor else if (isEquipped) Color(0xFF10B981) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                SoundManager.playTap()
                                previewMascot = mascot
                            }
                            .testTag("character_card_${mascot.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CartoonMascot(
                                    type = mascot,
                                    mood = if (isEquipped) MascotMood.CHEERING else if (!isUnlocked) MascotMood.THINKING else MascotMood.HAPPY,
                                    size = 64.dp
                                )
                                if (!isUnlocked) {
                                    // Lock Badge
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(Color(0xCC0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = Color(0xFFFBBF24),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else if (isEquipped) {
                                    // Active Star Badge
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${mascot.iconEmoji} ${mascot.displayName}",
                                style = TextStyle(
                                    fontFamily = GameFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            )

                            Text(
                                text = mascot.subtitle,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Theme Label
                            Text(
                                text = "🎨 ${mascot.associatedTheme.title}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = mascot.primaryColor
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Action button
                            if (isEquipped) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓ ACTIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            } else if (isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(mascot.primaryColor)
                                        .clickable {
                                            viewModel.setSelectedMascot(mascot)
                                            previewMascot = mascot
                                        }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "APPLY THEME",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (canAffordThis)
                                                Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                                            else
                                                Brush.horizontalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                                        )
                                        .clickable(enabled = canAffordThis) {
                                            viewModel.unlockMascot(mascot)
                                            previewMascot = mascot
                                        }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (canAffordThis) "UNLOCK 1,000 🪙" else "1,000 🪙 (-${mascot.unlockCost - coins})",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
