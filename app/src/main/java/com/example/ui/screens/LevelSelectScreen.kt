package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.model.PuzzleShape
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

data class LevelSection(
    val title: String,
    val subtitle: String,
    val startLevel: Int,
    val endLevel: Int
)

val LEVEL_SECTIONS = listOf(
    LevelSection("BEGINNER", "Levels 1–100", 1, 100),
    LevelSection("CHALLENGE", "Levels 101–250", 101, 250),
    LevelSection("ADVANCED", "Levels 251–500", 251, 500),
    LevelSection("EXPERT", "Levels 501–750", 501, 750),
    LevelSection("MYTHIC 1000", "Levels 751–1000", 751, 1000)
)

@Composable
fun LevelSelectScreen(
    viewModel: GameViewModel,
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val theme by viewModel.preferences.theme.collectAsState()
    val highestUnlocked by viewModel.preferences.highestUnlocked.collectAsState()
    val allProgress by viewModel.allProgressFlow.collectAsState(initial = emptyList())

    val progressMap = remember(allProgress) {
        allProgress.associateBy { it.levelNumber }
    }

    val coroutineScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpLevelText by remember { mutableStateOf("") }
    var jumpError by remember { mutableStateOf(false) }

    fun getItemIndexForLevel(levelNum: Int): Int {
        var currentIndex = 0
        for (section in LEVEL_SECTIONS) {
            if (levelNum < section.startLevel) break
            if (levelNum in section.startLevel..section.endLevel) {
                return currentIndex + 1 + (levelNum - section.startLevel)
            }
            currentIndex += 1 + (section.endLevel - section.startLevel + 1)
        }
        return 0
    }

    fun getHeaderIndexForSection(sectionIdx: Int): Int {
        var currentIndex = 0
        for (i in 0 until sectionIdx) {
            val section = LEVEL_SECTIONS[i]
            currentIndex += 1 + (section.endLevel - section.startLevel + 1)
        }
        return currentIndex
    }

    LaunchedEffect(highestUnlocked) {
        val targetIndex = getItemIndexForLevel(highestUnlocked)
        val scrollTarget = (targetIndex - 4).coerceAtLeast(0)
        gridState.scrollToItem(scrollTarget)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .statusBarsPadding()
            .testTag("level_select_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    SoundManager.playTap()
                    onBack()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (theme.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .testTag("level_select_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = theme.accentColor
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = "1000 LEVELS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = theme.textPrimaryColor,
                    modifier = Modifier.testTag("level_select_title")
                )
                Text(
                    text = "Shapes, curves & knots",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = theme.textSecondaryColor,
                    modifier = Modifier.testTag("level_select_subtitle")
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick Jump to any of the 1000 levels
            Button(
                onClick = {
                    jumpLevelText = ""
                    jumpError = false
                    showJumpDialog = true
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                modifier = Modifier.testTag("jump_level_button")
            ) {
                Text(
                    text = "# Jump",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Quick Jump Section Chips
        ScrollableTabRow(
            selectedTabIndex = 0,
            edgePadding = 16.dp,
            containerColor = theme.backgroundColor,
            indicator = {},
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            LEVEL_SECTIONS.forEachIndexed { index, section ->
                val isTierActive = highestUnlocked in section.startLevel..section.endLevel
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 2.dp, bottom = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isTierActive) theme.accentColor.copy(alpha = if (theme.isDark) 0.25f else 0.12f)
                            else if (theme.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        )
                        .clickable {
                            SoundManager.playTap()
                            coroutineScope.launch {
                                val target = getHeaderIndexForSection(index)
                                gridState.animateScrollToItem(target)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${section.title} (${section.startLevel}-${section.endLevel})",
                        fontSize = 12.sp,
                        fontWeight = if (isTierActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isTierActive) theme.accentColor else theme.textSecondaryColor
                    )
                }
            }
        }

        // Continuous Smooth-Scrolling 4-Column Grid for all 1000 Levels
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("level_grid")
        ) {
            LEVEL_SECTIONS.forEach { section ->
                item(
                    key = "header_${section.title}",
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = section.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = theme.textPrimaryColor
                            )
                            Text(
                                text = section.subtitle,
                                fontSize = 11.sp,
                                color = theme.textSecondaryColor
                            )
                        }

                        val completedCount = (section.startLevel..section.endLevel).count {
                            progressMap[it]?.isCompleted == true
                        }
                        val totalInSec = section.endLevel - section.startLevel + 1
                        Text(
                            text = "$completedCount / $totalInSec",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.accentColor
                        )
                    }
                }

                val levels = (section.startLevel..section.endLevel).toList()
                items(
                    count = levels.size,
                    key = { idx -> "level_${levels[idx]}" }
                ) { idx ->
                    val levelNum = levels[idx]
                    val isUnlocked = levelNum <= highestUnlocked
                    val isCurrent = levelNum == highestUnlocked
                    val progress = progressMap[levelNum]
                    val isCompleted = progress?.isCompleted == true
                    val stars = progress?.stars ?: 0

                    LevelBoxCard(
                        levelNumber = levelNum,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        isCurrent = isCurrent,
                        stars = stars,
                        themeColor = theme.accentColor,
                        surfaceColor = theme.surfaceColor,
                        textPrimary = theme.textPrimaryColor,
                        textSecondary = theme.textSecondaryColor,
                        isDark = theme.isDark,
                        onClick = {
                            if (isUnlocked) {
                                SoundManager.playTap()
                                onSelectLevel(levelNum)
                            }
                        }
                    )
                }
            }
        }
    }

    // Jump to level dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text("Jump to Level (1–1000)", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Enter any level number up to 1000:", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = jumpLevelText,
                        onValueChange = {
                            jumpLevelText = it.filter { char -> char.isDigit() }.take(4)
                            jumpError = false
                        },
                        label = { Text("Level (1 - 1000)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = jumpError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (jumpError) {
                        Text("Please enter a valid level between 1 and 1000.", color = Color.Red, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = jumpLevelText.toIntOrNull()
                        if (num != null && num in 1..1000) {
                            showJumpDialog = false
                            viewModel.preferences.unlockUpToLevel(num)
                            onSelectLevel(num)
                        } else {
                            jumpError = true
                        }
                    }
                ) {
                    Text("Play Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LevelBoxCard(
    levelNumber: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isCurrent: Boolean,
    stars: Int,
    themeColor: Color,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(isCurrent) {
        if (isCurrent) {
            pulseAnim.animateTo(
                targetValue = 1.03f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseAnim.snapTo(1f)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && isUnlocked) 0.94f else 1.0f,
        animationSpec = tween(120),
        label = "press_scale_$levelNumber"
    )

    val finalScale = if (isCurrent) pulseAnim.value * pressScale else pressScale
    val shape = remember(levelNumber) { PuzzleShape.forLevel(levelNumber) }

    val backgroundColor = when {
        isCurrent -> themeColor.copy(alpha = if (isDark) 0.22f else 0.12f)
        isCompleted -> if (isDark) Color(0xFF0F253C) else Color(0xFFF0F9FF)
        isUnlocked -> surfaceColor
        else -> if (isDark) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFF1F5F9)
    }

    val border = when {
        isCurrent -> BorderStroke(2.5.dp, themeColor)
        isCompleted -> BorderStroke(1.dp, themeColor.copy(alpha = 0.35f))
        isUnlocked -> BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
        else -> null
    }

    val elevation = when {
        isCurrent -> 8.dp
        isCompleted -> 3.dp
        isUnlocked -> 2.dp
        else -> 0.dp
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = Modifier
            .aspectRatio(1f)
            .scale(finalScale)
            .shadow(
                elevation = if (isCurrent) 6.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isCurrent) themeColor else Color.Transparent,
                spotColor = if (isCurrent) themeColor else Color.Transparent
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isUnlocked
            ) { onClick() }
            .testTag("level_card_$levelNumber")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (shape != PuzzleShape.RECTANGLE) {
                                Text(shape.badge, fontSize = 9.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = themeColor,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }

                        Text(
                            text = "$levelNumber",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (s in 1..3) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (s <= stars) Color(0xFFFBBF24) else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }

                isCurrent -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            if (shape != PuzzleShape.RECTANGLE) {
                                Text(shape.badge, fontSize = 10.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Current level",
                                    tint = themeColor,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Text(
                                text = "PLAY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = themeColor
                            )
                        }

                        Text(
                            text = "$levelNumber",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = themeColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                isUnlocked -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (shape != PuzzleShape.RECTANGLE) {
                            Text(shape.badge, fontSize = 10.sp)
                        }
                        Text(
                            text = "$levelNumber",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked level",
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$levelNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
