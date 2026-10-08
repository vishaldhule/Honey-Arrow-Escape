package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ads.AdMobManager
import com.example.audio.SoundManager
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.CharacterSelectScreen
import com.example.ui.screens.DailyPuzzleScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.RewardCenterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    HOME,
    GAME,
    LEVELS,
    DAILY,
    SETTINGS,
    ACHIEVEMENTS,
    CHARACTERS,
    REWARDS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google AdMob SDK on startup
        AdMobManager.initialize(this)

        setContent {
            val viewModel: GameViewModel = viewModel()
            val theme by viewModel.preferences.theme.collectAsState()

            MyApplicationTheme(darkTheme = theme.isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = theme.backgroundColor
                ) {
                    ArrowEscapeApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ArrowEscapeApp(viewModel: GameViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val theme by viewModel.preferences.theme.collectAsState()

    if (currentScreen == AppScreen.GAME) {
        GameScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = AppScreen.LEVELS },
            onNavigateSettings = { currentScreen = AppScreen.SETTINGS }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    currentScreen = currentScreen,
                    theme = theme,
                    onTabSelected = { screen ->
                        SoundManager.playTap()
                        viewModel.hapticManager.tap()
                        currentScreen = screen
                    }
                )
            },
            containerColor = theme.backgroundColor
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = androidx.compose.animation.core.tween(180)) togetherWith
                            fadeOut(animationSpec = androidx.compose.animation.core.tween(120))
                },
                modifier = Modifier.padding(innerPadding),
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onPlay = {
                                val currentLevel = viewModel.preferences.currentLevel.value
                                viewModel.loadLevel(currentLevel)
                                currentScreen = AppScreen.GAME
                            },
                            onNavigateLevels = { currentScreen = AppScreen.LEVELS },
                            onNavigateDaily = { currentScreen = AppScreen.DAILY },
                            onNavigateAchievements = { currentScreen = AppScreen.ACHIEVEMENTS },
                            onNavigateSettings = { currentScreen = AppScreen.SETTINGS },
                            onNavigateCharacters = { currentScreen = AppScreen.CHARACTERS },
                            onNavigateRewards = { currentScreen = AppScreen.REWARDS }
                        )
                    }

                    AppScreen.REWARDS -> {
                        RewardCenterScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.CHARACTERS -> {
                        CharacterSelectScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.LEVELS -> {
                        LevelSelectScreen(
                            viewModel = viewModel,
                            onSelectLevel = { levelNumber ->
                                viewModel.loadLevel(levelNumber)
                                currentScreen = AppScreen.GAME
                            },
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.DAILY -> {
                        DailyPuzzleScreen(
                            viewModel = viewModel,
                            onStartDaily = { currentScreen = AppScreen.GAME },
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.ACHIEVEMENTS -> {
                        AchievementsScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.GAME -> {
                        // Handled above outside the scaffold
                    }
                }
            }
        }
    }
}
