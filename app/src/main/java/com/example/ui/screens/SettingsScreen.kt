package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.GameTheme
import com.example.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val prefs = viewModel.preferences
    val soundEnabled by prefs.soundEnabled.collectAsState()
    val musicEnabled by prefs.musicEnabled.collectAsState()
    val hapticEnabled by prefs.hapticEnabled.collectAsState()
    val animationsEnabled by prefs.animationsEnabled.collectAsState()
    val currentTheme by prefs.theme.collectAsState()
    val currentMode by prefs.gameMode.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(currentTheme.backgroundColor)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
            .testTag("settings_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = currentTheme.accentColor
                )
            }

            Text(
                text = "Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.textPrimaryColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Section: Audio & Haptics
        SettingsCard(
            title = "Audio & Feedback",
            icon = Icons.Default.VolumeUp,
            theme = currentTheme
        ) {
            SettingsToggleRow(
                label = "Sound Effects",
                checked = soundEnabled,
                onCheckedChange = { prefs.setSoundEnabled(it) },
                theme = currentTheme
            )

            SettingsToggleRow(
                label = "Music (Ambient)",
                checked = musicEnabled,
                onCheckedChange = { prefs.setMusicEnabled(it) },
                theme = currentTheme
            )

            SettingsToggleRow(
                label = "Haptic Vibration",
                checked = hapticEnabled,
                onCheckedChange = { prefs.setHapticEnabled(it) },
                theme = currentTheme
            )

            SettingsToggleRow(
                label = "Smooth Animations",
                checked = animationsEnabled,
                onCheckedChange = { prefs.setAnimationsEnabled(it) },
                theme = currentTheme
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Game Mode
        SettingsCard(
            title = "Game Mode",
            icon = Icons.Default.Tune,
            theme = currentTheme
        ) {
            GameMode.entries.forEach { mode ->
                val isSelected = currentMode == mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) currentTheme.accentColor.copy(alpha = 0.12f)
                            else Color.Transparent
                        )
                        .clickable { viewModel.setGameMode(mode) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mode.label,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = currentTheme.textPrimaryColor
                        )
                        Text(
                            text = mode.description,
                            fontSize = 12.sp,
                            color = currentTheme.textSecondaryColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) currentTheme.accentColor else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                            .background(if (isSelected) currentTheme.accentColor else Color.Transparent)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Visual Themes
        SettingsCard(
            title = "Visual Themes",
            icon = Icons.Default.Palette,
            theme = currentTheme
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameTheme.entries.forEach { themeOption ->
                    val isSelected = currentTheme == themeOption
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = themeOption.surfaceColor),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.5.dp, currentTheme.accentColor) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { prefs.setTheme(themeOption) }
                            .testTag("theme_card_${themeOption.name}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(themeOption.accentColor)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = themeOption.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = themeOption.textPrimaryColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: About Honey Arrow Escape
        SettingsCard(
            title = "About Honey Arrow Escape",
            icon = Icons.Default.Info,
            theme = currentTheme
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Honey Arrow Escape 🍯",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = currentTheme.textPrimaryColor
                )
                Text(
                    text = "Version 1.0.0 • 1,000 Sweet Puzzle Shapes & Companions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.accentColor
                )
                Text(
                    text = "Guide your sweet honey arrows to escape intricate mazes, unlock adorable companions, and keep your daily streak alive!",
                    fontSize = 11.5.sp,
                    color = currentTheme.textSecondaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 4.dp)
                        .testTag("settings_privacy_policy_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📜", fontSize = 14.sp)
                    Text(
                        text = "View Privacy Policy",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.accentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Data Button
        TextButton(
            onClick = { showResetDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("reset_progress_button")
        ) {
            Text(
                text = "Reset All Game Progress",
                color = Color(0xFFEF4444),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?") },
            text = { Text("This will reset all unlocked levels, stars, and records back to level 1. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetAllData()
                    }
                ) {
                    Text("RESET", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = "Privacy Policy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Honey Arrow Escape is committed to protecting your privacy. We do not collect, transmit, or share any personal information.",
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )
                    Text(
                        text = "• All level progress, unlocked cartoon companions, and coin balances remain strictly on your local device.",
                        fontSize = 12.5.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "• No location, contacts, camera, or file storage permissions are requested.",
                        fontSize = 12.5.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "• You can clear all saved data anytime via 'Reset All Game Progress'.",
                        fontSize = 12.5.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "Support Contact:\nvdhule328@gmail.com",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFB45309)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("CLOSE", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    theme: GameTheme,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimaryColor
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    theme: GameTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = theme.textPrimaryColor
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = theme.accentColor,
                uncheckedThumbColor = Color(0xFFCBD5E1),
                uncheckedTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}
