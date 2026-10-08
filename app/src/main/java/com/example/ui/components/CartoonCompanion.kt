package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.window.Dialog
import com.example.model.GameTheme
import com.example.ui.theme.GameFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Mascot emotional states reacting dynamically to user gameplay
 */
enum class MascotMood {
    HAPPY,
    CHEERING,
    THINKING,
    CELEBRATING,
    CRYING_SAD,
    SURPRISED
}

/**
 * Cartoon character companions available to the player
 * BEE is the FREE default starter character.
 * All other characters require 1,000 coins to unlock!
 * Applying a character switches the main game theme to their signature style.
 */
enum class CartoonMascotType(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val category: String,
    val iconEmoji: String,
    val bubbleGreeting: String,
    val bubbleSad: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val associatedTheme: GameTheme,
    val unlockCost: Int
) {
    BEE(
        id = "bee",
        displayName = "Buzzy",
        subtitle = "Honey Bee (Starter)",
        category = "Cute Animals",
        iconEmoji = "🐝",
        bubbleGreeting = "Buzzz! Sweet puzzles ahead! 🍯",
        bubbleSad = "Buzzz... don't give up! 💧",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFEF3C7),
        associatedTheme = GameTheme.CLASSIC,
        unlockCost = 0 // Free starter character!
    ),
    HUMAN(
        id = "human",
        displayName = "Toby",
        subtitle = "School Hero",
        category = "Kids & Heroes",
        iconEmoji = "👦",
        bubbleGreeting = "You got this, champ! ⭐",
        bubbleSad = "Aww man, don't give up! 😭",
        primaryColor = Color(0xFFFF6B81),
        secondaryColor = Color(0xFFFFC0CB),
        associatedTheme = GameTheme.SUNSET,
        unlockCost = 1000
    ),
    GIRL_PINK(
        id = "girl_pink",
        displayName = "Lily",
        subtitle = "Blossom Girl",
        category = "Kids & Heroes",
        iconEmoji = "👧",
        bubbleGreeting = "Yay! Let's solve together! 🌸",
        bubbleSad = "Sniff... we'll do better! 🥺",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFFFDF2F8),
        associatedTheme = GameTheme.CANDY_PINK,
        unlockCost = 1000
    ),
    KNIGHT(
        id = "knight",
        displayName = "Sir Leo",
        subtitle = "Brave Knight",
        category = "Kids & Heroes",
        iconEmoji = "🗡️",
        bubbleGreeting = "For honor! Clear the path! 🛡️",
        bubbleSad = "My shield was dented! 💧",
        primaryColor = Color(0xFF3B82F6),
        secondaryColor = Color(0xFFDBEAFE),
        associatedTheme = GameTheme.CARAMEL,
        unlockCost = 1000
    ),
    WITCH(
        id = "witch",
        displayName = "Luna",
        subtitle = "Magic Witch",
        category = "Kids & Heroes",
        iconEmoji = "🧙‍♀️",
        bubbleGreeting = "Hocus Pocus! Magic moves! 🔮",
        bubbleSad = "My wand lost its sparkle! 💧",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFEDE9FE),
        associatedTheme = GameTheme.MIDNIGHT,
        unlockCost = 1000
    ),
    ROBOT(
        id = "robot",
        displayName = "Sparky",
        subtitle = "Cyber Bot",
        category = "Kids & Heroes",
        iconEmoji = "🤖",
        bubbleGreeting = "Beep Boop! Path computed! ⚡",
        bubbleSad = "Error: Short circuit detected! 💧",
        primaryColor = Color(0xFF06B6D4),
        secondaryColor = Color(0xFFCFFAFE),
        associatedTheme = GameTheme.OCEAN,
        unlockCost = 1000
    ),
    EXPLORER(
        id = "explorer",
        displayName = "Finn",
        subtitle = "Safari Scout",
        category = "Kids & Heroes",
        iconEmoji = "🧭",
        bubbleGreeting = "Adventure awaits! Let's trek! 🗺️",
        bubbleSad = "Lost the compass trail! 💧",
        primaryColor = Color(0xFF84CC16),
        secondaryColor = Color(0xFFECFCCB),
        associatedTheme = GameTheme.FOREST,
        unlockCost = 1000
    ),
    FANTASY_BUNNY(
        id = "bunny",
        displayName = "Pip",
        subtitle = "Adventurer Bunny",
        category = "Cute Animals",
        iconEmoji = "🐰",
        bubbleGreeting = "Hop hop! Sparkle magic! 🌟",
        bubbleSad = "Sniff... we'll try again! 🥺",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFFFBCFE8),
        associatedTheme = GameTheme.CANDY_PINK,
        unlockCost = 1000
    ),
    CAT(
        id = "cat",
        displayName = "Milo",
        subtitle = "Bandana Kitty",
        category = "Cute Animals",
        iconEmoji = "🐱",
        bubbleGreeting = "Purr! Sharp moves today! ✨",
        bubbleSad = "Meoww... so close! 😿",
        primaryColor = Color(0xFFF97316),
        secondaryColor = Color(0xFFFFEDD5),
        associatedTheme = GameTheme.MIDNIGHT,
        unlockCost = 1000
    ),
    DOG(
        id = "dog",
        displayName = "Barnaby",
        subtitle = "Bandana Pup",
        category = "Cute Animals",
        iconEmoji = "🐶",
        bubbleGreeting = "Woof woof! Clear the path! 🦴",
        bubbleSad = "Whimper... I believe in you! 🥺",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFDE68A),
        associatedTheme = GameTheme.CARAMEL,
        unlockCost = 1000
    ),
    PANDA(
        id = "panda",
        displayName = "Bao Bao",
        subtitle = "Overalls Panda",
        category = "Cute Animals",
        iconEmoji = "🐼",
        bubbleGreeting = "Crunch crunch! Bamboo energy! 🎋",
        bubbleSad = "Droopy ears... try again! 🥺",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFFD1FAE5),
        associatedTheme = GameTheme.FOREST,
        unlockCost = 1000
    ),
    MONKEY(
        id = "monkey",
        displayName = "Kiki",
        subtitle = "Cap Monkey",
        category = "Cute Animals",
        iconEmoji = "🐵",
        bubbleGreeting = "Ooh ooh aah! Swing to victory! 🍌",
        bubbleSad = "Dropped my banana! 😭",
        primaryColor = Color(0xFFEA580C),
        secondaryColor = Color(0xFFFFEDD5),
        associatedTheme = GameTheme.SUNSET,
        unlockCost = 1000
    ),
    DRAGON(
        id = "dragon",
        displayName = "Draco",
        subtitle = "Baby Fire Dragon",
        category = "Cute Animals",
        iconEmoji = "🐲",
        bubbleGreeting = "Roar! Blazing puzzle wings! 翼",
        bubbleSad = "My smoke puffed out! 💧",
        primaryColor = Color(0xFFEF4444),
        secondaryColor = Color(0xFFFEE2E2),
        associatedTheme = GameTheme.SUNSET,
        unlockCost = 1000
    ),
    FOX(
        id = "fox",
        displayName = "Foxy",
        subtitle = "Wizard Fox",
        category = "Cute Animals",
        iconEmoji = "🦊",
        bubbleGreeting = "Abracadabra! Clear the maze! ✨",
        bubbleSad = "My magic wand fizzled! 💧",
        primaryColor = Color(0xFFEA580C),
        secondaryColor = Color(0xFFFFEDD5),
        associatedTheme = GameTheme.SUNSET,
        unlockCost = 1000
    ),
    ELEPHANT(
        id = "elephant",
        displayName = "Ellie",
        subtitle = "Baby Elephant",
        category = "Cute Animals",
        iconEmoji = "🐘",
        bubbleGreeting = "Pawoo! Let's solve this! 💖",
        bubbleSad = "Oh no, my ears are sad! 💧",
        primaryColor = Color(0xFF38BDF8),
        secondaryColor = Color(0xFFBAE6FD),
        associatedTheme = GameTheme.OCEAN,
        unlockCost = 1000
    ),
    MAGICAL_TREE(
        id = "tree",
        displayName = "Twiggy",
        subtitle = "Living Treant",
        category = "Elemental & Magic",
        iconEmoji = "🌳",
        bubbleGreeting = "Rustle! Nature guides you! 🍃",
        bubbleSad = "My sap is weeping! 💧",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFFA7F3D0),
        associatedTheme = GameTheme.FOREST,
        unlockCost = 1000
    ),
    WATER_NYMPH(
        id = "water_nymph",
        displayName = "Marina",
        subtitle = "Water Fairy",
        category = "Elemental & Magic",
        iconEmoji = "💧",
        bubbleGreeting = "Splash! Flow with the stream! 🌊",
        bubbleSad = "My fountain ran dry! 💧",
        primaryColor = Color(0xFF0284C7),
        secondaryColor = Color(0xFFE0F2FE),
        associatedTheme = GameTheme.OCEAN,
        unlockCost = 1000
    ),
    FIRE_SPRITE(
        id = "fire_sprite",
        displayName = "Blaze",
        subtitle = "Flame Sprite",
        category = "Elemental & Magic",
        iconEmoji = "🔥",
        bubbleGreeting = "Ignite! Burning hot combos! 🔥",
        bubbleSad = "Extinguished! Brrr! 💧",
        primaryColor = Color(0xFFF97316),
        secondaryColor = Color(0xFFFEF3C7),
        associatedTheme = GameTheme.SUNSET,
        unlockCost = 1000
    ),
    NATURE_FAIRY(
        id = "nature_fairy",
        displayName = "Flora",
        subtitle = "Nature Nymph",
        category = "Elemental & Magic",
        iconEmoji = "🌿",
        bubbleGreeting = "Bloom! Forest magic blooms! 🌸",
        bubbleSad = "My flower petals wilted! 💧",
        primaryColor = Color(0xFF059669),
        secondaryColor = Color(0xFFD1FAE5),
        associatedTheme = GameTheme.FOREST,
        unlockCost = 1000
    ),
    CLOUD_SPRITE(
        id = "cloud_sprite",
        displayName = "Nimbus",
        subtitle = "Cloud Spirit",
        category = "Elemental & Magic",
        iconEmoji = "☁️",
        bubbleGreeting = "Puff puff! Floating on clouds! 🌈",
        bubbleSad = "Thunderstorm tears! 🌧️",
        primaryColor = Color(0xFF60A5FA),
        secondaryColor = Color(0xFFEFF6FF),
        associatedTheme = GameTheme.OCEAN,
        unlockCost = 1000
    ),
    STONE_GOLEM(
        id = "stone_golem",
        displayName = "Rocky",
        subtitle = "Earth Guardian",
        category = "Elemental & Magic",
        iconEmoji = "🗿",
        bubbleGreeting = "Rumble! Solid as rock! 🪨",
        bubbleSad = "Cracking under pressure! 💧",
        primaryColor = Color(0xFF64748B),
        secondaryColor = Color(0xFFF1F5F9),
        associatedTheme = GameTheme.MIDNIGHT,
        unlockCost = 1000
    );

    companion object {
        fun fromId(id: String): CartoonMascotType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: BEE
        }
    }
}

/**
 * Unified Composable rendering any chosen mascot in any emotion
 */
@Composable
fun CartoonMascot(
    type: CartoonMascotType,
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(clickModifier)
            .testTag("mascot_${type.id}_${mood.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            CartoonMascotType.BEE -> CartoonHoneyBeeMascot(mood = mood, size = size)
            CartoonMascotType.HUMAN -> CartoonChibiHuman(mood = mood, size = size)
            CartoonMascotType.GIRL_PINK -> CartoonPinkGirlMascot(mood = mood, size = size)
            CartoonMascotType.KNIGHT -> CartoonKnightMascot(mood = mood, size = size)
            CartoonMascotType.WITCH -> CartoonWitchMascot(mood = mood, size = size)
            CartoonMascotType.ROBOT -> CartoonRobotMascot(mood = mood, size = size)
            CartoonMascotType.EXPLORER -> CartoonSafariExplorerMascot(mood = mood, size = size)
            CartoonMascotType.FANTASY_BUNNY -> CartoonStarBunnyMascot(mood = mood, size = size)
            CartoonMascotType.CAT -> CartoonKittyMascot(mood = mood, size = size)
            CartoonMascotType.DOG -> CartoonPuppyMascot(mood = mood, size = size)
            CartoonMascotType.PANDA -> CartoonPandaMascot(mood = mood, size = size)
            CartoonMascotType.MONKEY -> CartoonMonkeyMascot(mood = mood, size = size)
            CartoonMascotType.DRAGON -> CartoonBabyDragonMascot(mood = mood, size = size)
            CartoonMascotType.FOX -> CartoonWizardFoxMascot(mood = mood, size = size)
            CartoonMascotType.ELEPHANT -> CartoonBabyElephant(mood = mood, size = size)
            CartoonMascotType.MAGICAL_TREE -> CartoonFantasyTreeMascot(mood = mood, size = size)
            CartoonMascotType.WATER_NYMPH -> CartoonWaterNymphMascot(mood = mood, size = size)
            CartoonMascotType.FIRE_SPRITE -> CartoonFireSpriteMascot(mood = mood, size = size)
            CartoonMascotType.NATURE_FAIRY -> CartoonNatureFairyMascot(mood = mood, size = size)
            CartoonMascotType.CLOUD_SPRITE -> CartoonCloudSpriteMascot(mood = mood, size = size)
            CartoonMascotType.STONE_GOLEM -> CartoonStoneGolemMascot(mood = mood, size = size)
        }
    }
}

// =========================================================================
// 1. CARTOON BABY ELEPHANT ("Ellie")
// =========================================================================
@Composable
fun CartoonBabyElephant(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val bobAnim = remember { Animatable(0f) }
    val earFlutter = remember { Animatable(1f) }
    val trunkWave = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(mood) {
        val speed = when (mood) {
            MascotMood.CELEBRATING -> 360
            MascotMood.CHEERING -> 450
            MascotMood.CRYING_SAD -> 1300
            else -> 1000
        }
        bobAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(speed, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        earFlutter.animateTo(
            targetValue = 1.09f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        trunkWave.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Teardrop falling physics loop when sad
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(750, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            tearAnim.snapTo(0f)
        }
    }

    // Periodic organic blink
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(2800)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val yOffset = when (mood) {
        MascotMood.CELEBRATING -> (-bobAnim.value * 12f).dp
        MascotMood.CHEERING -> (-bobAnim.value * 8f).dp
        MascotMood.CRYING_SAD -> (bobAnim.value * 3f).dp
        else -> (-bobAnim.value * 4f).dp
    }
    val bounceScale = if (mood == MascotMood.CELEBRATING) 1f + bobAnim.value * 0.08f else 1f

    Box(
        modifier = modifier
            .size(size)
            .offset(y = yOffset)
            .scale(bounceScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.52f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.38f, h * 0.88f),
                size = Size(w * 0.76f, h * 0.10f)
            )

            // Floppy Ears (Lowered droopy when sad)
            val isSad = mood == MascotMood.CRYING_SAD
            val earScale = if (isSad) 0.95f else earFlutter.value
            val earW = w * 0.38f * earScale
            val earH = h * (if (isSad) 0.48f else 0.44f)
            val earDrop = if (isSad) h * 0.08f else 0f

            // Left Ear
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFF38BDF8)),
                    center = Offset(cx - w * 0.32f, cy - h * 0.18f + earDrop),
                    radius = earW
                ),
                topLeft = Offset(cx - w * 0.52f, cy - h * 0.36f + earDrop),
                size = Size(earW, earH)
            )
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFB6C1), Color(0xFFFFA4B6), Color(0xFFFF8DA1)),
                    center = Offset(cx - w * 0.32f, cy - h * 0.16f + earDrop),
                    radius = earW * 0.65f
                ),
                topLeft = Offset(cx - w * 0.44f, cy - h * 0.28f + earDrop),
                size = Size(earW * 0.65f, earH * 0.68f)
            )

            // Right Ear
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFF38BDF8)),
                    center = Offset(cx + w * 0.32f, cy - h * 0.18f + earDrop),
                    radius = earW
                ),
                topLeft = Offset(cx + w * 0.14f, cy - h * 0.36f + earDrop),
                size = Size(earW, earH)
            )
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFB6C1), Color(0xFFFFA4B6), Color(0xFFFF8DA1)),
                    center = Offset(cx + w * 0.32f, cy - h * 0.16f + earDrop),
                    radius = earW * 0.65f
                ),
                topLeft = Offset(cx + w * 0.20f, cy - h * 0.28f + earDrop),
                size = Size(earW * 0.65f, earH * 0.68f)
            )

            // Chubby Body
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFF0284C7))),
                topLeft = Offset(cx - w * 0.30f, cy + h * 0.05f),
                size = Size(w * 0.60f, h * 0.38f)
            )

            // Paws
            val footR = w * 0.12f
            drawCircle(Brush.verticalGradient(listOf(Color(0xFF7DD3FC), Color(0xFF0284C7))), radius = footR, center = Offset(cx - w * 0.24f, cy + h * 0.35f))
            drawCircle(Color(0xFFFFC0CB), radius = footR * 0.55f, center = Offset(cx - w * 0.24f, cy + h * 0.35f))
            drawCircle(Brush.verticalGradient(listOf(Color(0xFF7DD3FC), Color(0xFF0284C7))), radius = footR, center = Offset(cx + w * 0.24f, cy + h * 0.35f))
            drawCircle(Color(0xFFFFC0CB), radius = footR * 0.55f, center = Offset(cx + w * 0.24f, cy + h * 0.35f))

            // Head
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFF38BDF8)),
                    center = Offset(cx - w * 0.05f, cy - h * 0.12f),
                    radius = w * 0.34f
                ),
                radius = w * 0.32f,
                center = Offset(cx, cy - h * 0.08f)
            )

            // Hair Tuft
            val hairPath = Path().apply {
                moveTo(cx - 6f, cy - h * 0.38f)
                quadraticTo(cx - 10f, cy - h * 0.46f, cx - 2f, cy - h * 0.47f)
                quadraticTo(cx + 4f, cy - h * 0.45f, cx + 8f, cy - h * 0.37f)
                close()
            }
            drawPath(hairPath, Color(0xFF0284C7))

            // Eyes
            val eyeSpacing = w * 0.15f
            val eyeCenterY = cy - h * 0.10f
            val eyeR = w * 0.075f

            if (isSad) {
                // Sad tearful anime eyes (Watery glistening eyes)
                drawOval(Color.White, topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR), size = Size(eyeR * 2, eyeR * 2))
                drawOval(Color.White, topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR), size = Size(eyeR * 2, eyeR * 2))
                // Watery dark pupils
                drawCircle(Color(0xFF0C4A6E), radius = eyeR * 0.75f, center = Offset(cx - eyeSpacing, eyeCenterY + 1f))
                drawCircle(Color(0xFF0C4A6E), radius = eyeR * 0.75f, center = Offset(cx + eyeSpacing, eyeCenterY + 1f))
                // Water reflection sparkles
                drawCircle(Color.White, radius = 4f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 4f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))

                // Streaming animated cute blue teardrops!
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else {
                // Cheerful blinking anime eyes
                val blink = blinkAnim.value
                drawOval(Color.White, topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color.White, topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0C4A6E))), topLeft = Offset(cx - eyeSpacing - eyeR * 0.7f, eyeCenterY - eyeR * 0.7f * blink), size = Size(eyeR * 1.4f, eyeR * 1.4f * blink))
                drawOval(Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0C4A6E))), topLeft = Offset(cx + eyeSpacing - eyeR * 0.7f, eyeCenterY - eyeR * 0.7f * blink), size = Size(eyeR * 1.4f, eyeR * 1.4f * blink))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 3f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 3f))
                }
            }

            // Blushing Cheeks
            val blushColor = if (isSad) Color(0xFFFF94A6).copy(alpha = 0.65f) else Color(0xFFFF8DA1).copy(alpha = 0.55f)
            drawCircle(blushColor, radius = w * 0.055f, center = Offset(cx - eyeSpacing - eyeR * 1.2f, eyeCenterY + eyeR * 0.9f))
            drawCircle(blushColor, radius = w * 0.055f, center = Offset(cx + eyeSpacing + eyeR * 1.2f, eyeCenterY + eyeR * 0.9f))

            // Elephant Trunk
            val trunkWiggle = (trunkWave.value - 0.5f) * (if (isSad) 2f else 8f)
            val trunkPath = Path().apply {
                moveTo(cx - w * 0.04f, cy - h * 0.02f)
                if (isSad) {
                    // Drooping sad trunk
                    quadraticTo(cx - w * 0.03f, cy + h * 0.12f, cx, cy + h * 0.18f)
                    quadraticTo(cx + w * 0.04f, cy + h * 0.16f, cx + w * 0.02f, cy - h * 0.02f)
                } else {
                    // Upcurled happy waving trunk
                    quadraticTo(cx - w * 0.05f + trunkWiggle, cy + h * 0.10f, cx + w * 0.02f + trunkWiggle, cy + h * 0.14f)
                    quadraticTo(cx + w * 0.10f + trunkWiggle, cy + h * 0.12f, cx + w * 0.08f + trunkWiggle, cy + h * 0.06f)
                    quadraticTo(cx + w * 0.04f + trunkWiggle, cy + h * 0.08f, cx + w * 0.02f, cy - h * 0.02f)
                }
                close()
            }
            drawPath(trunkPath, Brush.verticalGradient(listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFF0284C7))))

            // Mouth
            if (isSad) {
                // Quivering sad frown
                val sadMouth = Path().apply {
                    moveTo(cx - 8f, cy + h * 0.14f)
                    quadraticTo(cx, cy + h * 0.10f, cx + 8f, cy + h * 0.14f)
                }
                drawPath(sadMouth, Color(0xFF0369A1), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            } else {
                // Cheerful smile
                val smilePath = Path().apply {
                    moveTo(cx - 10f, cy + h * 0.08f)
                    quadraticTo(cx, cy + h * 0.12f, cx + 10f, cy + h * 0.08f)
                }
                drawPath(smilePath, color = Color(0xFF0369A1), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            }

            // Party Hat & Confetti for Celebrations
            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 2. CHIBI CARTOON HUMAN HERO ("Toby")
// =========================================================================
@Composable
fun CartoonChibiHuman(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val bobAnim = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val armWave = remember { Animatable(0f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(mood) {
        val dur = if (mood == MascotMood.CELEBRATING) 350 else if (mood == MascotMood.CHEERING) 420 else 950
        bobAnim.animateTo(1f, infiniteRepeatable(tween(dur, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        armWave.animateTo(1f, infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(1f, infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Restart))
        } else {
            tearAnim.snapTo(0f)
        }
    }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(3100)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val yOffset = when (mood) {
        MascotMood.CELEBRATING -> (-bobAnim.value * 14f).dp
        MascotMood.CHEERING -> (-bobAnim.value * 9f).dp
        MascotMood.CRYING_SAD -> (bobAnim.value * 3f).dp
        else -> (-bobAnim.value * 4f).dp
    }

    Box(
        modifier = modifier
            .size(size)
            .offset(y = yOffset)
            .scale(if (mood == MascotMood.CELEBRATING) 1f + bobAnim.value * 0.08f else 1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.54f
            val isSad = mood == MascotMood.CRYING_SAD
            val isCheering = mood == MascotMood.CHEERING || mood == MascotMood.CELEBRATING

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.32f, h * 0.88f),
                size = Size(w * 0.64f, h * 0.10f)
            )

            // Cute Chibi Red/Coral Hoodie Body
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFFF6B81), Color(0xFFEE5253))),
                topLeft = Offset(cx - w * 0.25f, cy + h * 0.05f),
                size = Size(w * 0.50f, h * 0.32f)
            )
            // Little sneakers
            drawCircle(Color(0xFF2E86DE), radius = w * 0.09f, center = Offset(cx - w * 0.14f, cy + h * 0.34f))
            drawCircle(Color(0xFF2E86DE), radius = w * 0.09f, center = Offset(cx + w * 0.14f, cy + h * 0.34f))
            drawCircle(Color.White, radius = w * 0.045f, center = Offset(cx - w * 0.14f, cy + h * 0.36f))
            drawCircle(Color.White, radius = w * 0.045f, center = Offset(cx + w * 0.14f, cy + h * 0.36f))

            // Little Cartoon Arms / Hands
            if (isCheering) {
                // Hands raised in celebration!
                val armBob = armWave.value * 8f
                drawCircle(Color(0xFFFFD2BB), radius = 10f, center = Offset(cx - w * 0.32f, cy - h * 0.08f - armBob))
                drawCircle(Color(0xFFFFD2BB), radius = 10f, center = Offset(cx + w * 0.32f, cy - h * 0.08f - armBob))
            } else if (isSad) {
                // Hands rubbing teary eyes
                drawCircle(Color(0xFFFFD2BB), radius = 9f, center = Offset(cx - w * 0.16f, cy - h * 0.06f))
                drawCircle(Color(0xFFFFD2BB), radius = 9f, center = Offset(cx + w * 0.16f, cy - h * 0.06f))
            } else {
                // Rest hands
                drawCircle(Color(0xFFFFD2BB), radius = 9f, center = Offset(cx - w * 0.26f, cy + h * 0.14f))
                drawCircle(Color(0xFFFFD2BB), radius = 9f, center = Offset(cx + w * 0.26f, cy + h * 0.14f))
            }

            // Head (Chibi round peach face)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF0E6), Color(0xFFFFDFC8), Color(0xFFFFCCAA)),
                    center = Offset(cx, cy - h * 0.14f),
                    radius = w * 0.34f
                ),
                radius = w * 0.30f,
                center = Offset(cx, cy - h * 0.10f)
            )

            // Spiky/Fluffy Stylized Cartoon Hair (Behind Cap)
            val hairColor = Color(0xFF5D4037)
            drawCircle(hairColor, radius = w * 0.33f, center = Offset(cx, cy - h * 0.15f))

            // Cap / Hat (Candy Red / Yellow Peak)
            val capPath = Path().apply {
                moveTo(cx - w * 0.31f, cy - h * 0.18f)
                quadraticTo(cx, cy - h * 0.44f, cx + w * 0.31f, cy - h * 0.18f)
                close()
            }
            drawPath(capPath, Brush.verticalGradient(listOf(Color(0xFFFF9F43), Color(0xFFEE5253))))
            // Cap visor
            val visor = Path().apply {
                moveTo(cx - w * 0.30f, cy - h * 0.16f)
                quadraticTo(cx, cy - h * 0.22f, cx + w * 0.34f, cy - h * 0.13f)
                quadraticTo(cx + w * 0.32f, cy - h * 0.10f, cx - w * 0.28f, cy - h * 0.13f)
                close()
            }
            drawPath(visor, Color(0xFFFECA57))

            // Big Sparkling Anime Eyes
            val eyeSpacing = w * 0.13f
            val eyeCenterY = cy - h * 0.08f
            val eyeR = w * 0.075f

            if (isSad) {
                // Sad crying eyes with giant watery tears
                drawCircle(Color(0xFF222F3E), radius = eyeR, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF222F3E), radius = eyeR, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color.White, radius = 4f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 4f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                // Streaming waterfall cute tears
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else if (isCheering) {
                // Happy curved eye arcs (^_^)
                val eyeArcLeft = Path().apply {
                    moveTo(cx - eyeSpacing - eyeR, eyeCenterY + 2f)
                    quadraticTo(cx - eyeSpacing, eyeCenterY - eyeR * 1.2f, cx - eyeSpacing + eyeR, eyeCenterY + 2f)
                }
                val eyeArcRight = Path().apply {
                    moveTo(cx + eyeSpacing - eyeR, eyeCenterY + 2f)
                    quadraticTo(cx + eyeSpacing, eyeCenterY - eyeR * 1.2f, cx + eyeSpacing + eyeR, eyeCenterY + 2f)
                }
                drawPath(eyeArcLeft, Color(0xFF222F3E), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                drawPath(eyeArcRight, Color(0xFF222F3E), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            } else {
                // Normal bright anime eyes with blink
                val blink = blinkAnim.value
                drawOval(Color.White, topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color.White, topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawCircle(Color(0xFF10AC84), radius = eyeR * 0.65f, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF10AC84), radius = eyeR * 0.65f, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF222F3E), radius = eyeR * 0.45f, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF222F3E), radius = eyeR * 0.45f, center = Offset(cx + eyeSpacing, eyeCenterY))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 3f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 3f))
                }
            }

            // Rosy Blushing Cheeks
            drawCircle(Color(0xFFFF6B81).copy(alpha = 0.55f), radius = w * 0.05f, center = Offset(cx - eyeSpacing - eyeR, eyeCenterY + eyeR * 1.1f))
            drawCircle(Color(0xFFFF6B81).copy(alpha = 0.55f), radius = w * 0.05f, center = Offset(cx + eyeSpacing + eyeR, eyeCenterY + eyeR * 1.1f))

            // Mouth
            if (isSad) {
                val pout = Path().apply {
                    moveTo(cx - 7f, cy + h * 0.06f)
                    quadraticTo(cx, cy + h * 0.02f, cx + 7f, cy + h * 0.06f)
                }
                drawPath(pout, Color(0xFFC0392B), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            } else if (isCheering) {
                // Big joyful open mouth
                val openMouth = Path().apply {
                    moveTo(cx - 8f, cy + h * 0.03f)
                    quadraticTo(cx, cy + h * 0.10f, cx + 8f, cy + h * 0.03f)
                    close()
                }
                drawPath(openMouth, Color(0xFFC0392B))
                drawCircle(Color(0xFFFF7675), radius = 4f, center = Offset(cx, cy + h * 0.06f))
            } else {
                val smile = Path().apply {
                    moveTo(cx - 7f, cy + h * 0.03f)
                    quadraticTo(cx, cy + h * 0.07f, cx + 7f, cy + h * 0.03f)
                }
                drawPath(smile, Color(0xFFC0392B), style = Stroke(width = 2.2f, cap = StrokeCap.Round))
            }

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 3. TUXEDO KITTY ("Milo")
// =========================================================================
@Composable
fun CartoonKittyMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val tailWag = remember { Animatable(0f) }
    val pawBounce = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        tailWag.animateTo(1f, infiniteRepeatable(tween(650, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        pawBounce.animateTo(1f, infiniteRepeatable(tween(850, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(1f, infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Restart))
        } else {
            tearAnim.snapTo(0f)
        }
    }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(3200)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val isSad = mood == MascotMood.CRYING_SAD

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.52f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.35f, h * 0.88f),
                size = Size(w * 0.70f, h * 0.10f)
            )

            // Animated Tail
            val tailAngle = (tailWag.value - 0.5f) * (if (isSad) 8f else 18f)
            val tailPath = Path().apply {
                moveTo(cx + w * 0.20f, cy + h * 0.25f)
                cubicTo(
                    cx + w * 0.40f + tailAngle, cy + h * 0.20f,
                    cx + w * 0.45f + tailAngle, cy - h * 0.05f,
                    cx + w * 0.38f + tailAngle, cy - h * 0.18f
                )
            }
            drawPath(tailPath, color = Color(0xFF1E293B), style = Stroke(width = 10f, cap = StrokeCap.Round))
            drawCircle(Color.White, radius = 7f, center = Offset(cx + w * 0.38f + tailAngle, cy - h * 0.18f))

            // Body (Dark slate fur with white chest fluff)
            drawOval(Color(0xFF1E293B), topLeft = Offset(cx - w * 0.26f, cy + h * 0.02f), size = Size(w * 0.52f, h * 0.38f))
            drawOval(Color(0xFFFFF1F2), topLeft = Offset(cx - w * 0.15f, cy + h * 0.08f), size = Size(w * 0.30f, h * 0.26f))

            // Ears (Airplane/drooping when sad)
            val earDrop = if (isSad) 14f else 0f
            val earPathLeft = Path().apply {
                moveTo(cx - w * 0.30f, cy - h * 0.15f + earDrop)
                lineTo(cx - w * 0.34f, cy - h * 0.42f + earDrop * 1.5f)
                lineTo(cx - w * 0.08f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(earPathLeft, Color(0xFF1E293B))
            val innerLeft = Path().apply {
                moveTo(cx - w * 0.28f, cy - h * 0.18f + earDrop)
                lineTo(cx - w * 0.31f, cy - h * 0.38f + earDrop * 1.5f)
                lineTo(cx - w * 0.12f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(innerLeft, Color(0xFFFF8DA1))

            val earPathRight = Path().apply {
                moveTo(cx + w * 0.30f, cy - h * 0.15f + earDrop)
                lineTo(cx + w * 0.34f, cy - h * 0.42f + earDrop * 1.5f)
                lineTo(cx + w * 0.08f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(earPathRight, Color(0xFF1E293B))
            val innerRight = Path().apply {
                moveTo(cx + w * 0.28f, cy - h * 0.18f + earDrop)
                lineTo(cx + w * 0.31f, cy - h * 0.38f + earDrop * 1.5f)
                lineTo(cx + w * 0.12f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(innerRight, Color(0xFFFF8DA1))

            // Head
            drawCircle(Color(0xFF1E293B), radius = w * 0.30f, center = Offset(cx, cy - h * 0.08f))

            // White Face Mask
            val maskPath = Path().apply {
                moveTo(cx - w * 0.12f, cy - h * 0.28f)
                lineTo(cx, cy - h * 0.14f)
                lineTo(cx + w * 0.12f, cy - h * 0.28f)
                lineTo(cx + w * 0.26f, cy - h * 0.04f)
                quadraticTo(cx, cy + h * 0.18f, cx - w * 0.26f, cy - h * 0.04f)
                close()
            }
            drawPath(maskPath, Color(0xFFFFF8F8))

            // Eyes
            val eyeSpacing = w * 0.14f
            val eyeCenterY = cy - h * 0.10f
            val eyeR = w * 0.08f

            if (isSad) {
                // Big crying tearful eyes
                drawCircle(Color(0xFF451A03), radius = eyeR, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF451A03), radius = eyeR, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFFFBBF24), radius = eyeR * 0.65f, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFFFBBF24), radius = eyeR * 0.65f, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color.White, radius = 4f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 4f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else {
                val blink = blinkAnim.value
                drawOval(Color(0xFF451A03), topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color(0xFF451A03), topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawCircle(Color(0xFFFBBF24), radius = eyeR * 0.65f, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFFFBBF24), radius = eyeR * 0.65f, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF1E293B), radius = eyeR * 0.45f, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF1E293B), radius = eyeR * 0.45f, center = Offset(cx + eyeSpacing, eyeCenterY))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 3f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 3f))
                }
            }

            // Nose
            val nose = Path().apply {
                moveTo(cx - 5f, cy + 2f)
                lineTo(cx + 5f, cy + 2f)
                lineTo(cx, cy + 8f)
                close()
            }
            drawPath(nose, Color(0xFFFF5277))

            // Whiskers
            val whiskerDrop = if (isSad) 5f else 0f
            drawLine(Color(0xFF64748B), start = Offset(cx - 18f, cy + 6f), end = Offset(cx - 44f, cy + 2f + whiskerDrop), strokeWidth = 1.5f)
            drawLine(Color(0xFF64748B), start = Offset(cx - 18f, cy + 10f), end = Offset(cx - 44f, cy + 12f + whiskerDrop), strokeWidth = 1.5f)
            drawLine(Color(0xFF64748B), start = Offset(cx + 18f, cy + 6f), end = Offset(cx + 44f, cy + 2f + whiskerDrop), strokeWidth = 1.5f)
            drawLine(Color(0xFF64748B), start = Offset(cx + 18f, cy + 10f), end = Offset(cx + 44f, cy + 12f + whiskerDrop), strokeWidth = 1.5f)

            // Front Paws
            val pawOffset = pawBounce.value * 4f
            drawCircle(Color.White, radius = 12f, center = Offset(cx - w * 0.15f, cy + h * 0.32f - pawOffset))
            drawCircle(Color.White, radius = 12f, center = Offset(cx + w * 0.15f, cy + h * 0.32f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 4. GOLDEN PUPPY ("Barnaby")
// =========================================================================
@Composable
fun CartoonPuppyMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val tailWag = remember { Animatable(0f) }
    val earFlap = remember { Animatable(0f) }
    val tongueAnim = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        tailWag.animateTo(1f, infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        earFlap.animateTo(1f, infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        tongueAnim.animateTo(1f, infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(1f, infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Restart))
        } else {
            tearAnim.snapTo(0f)
        }
    }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(2900)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val isSad = mood == MascotMood.CRYING_SAD

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.52f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.35f, h * 0.88f),
                size = Size(w * 0.70f, h * 0.10f)
            )

            // Wagging tail
            val tailAngle = (tailWag.value - 0.5f) * (if (isSad) 8f else 22f)
            drawLine(
                color = Color(0xFFD97706),
                start = Offset(cx + w * 0.18f, cy + h * 0.22f),
                end = Offset(cx + w * 0.38f + tailAngle, cy + h * 0.05f),
                strokeWidth = 9f,
                cap = StrokeCap.Round
            )

            // Puppy Body (Golden fur)
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706))),
                topLeft = Offset(cx - w * 0.28f, cy + h * 0.04f),
                size = Size(w * 0.56f, h * 0.36f)
            )
            // Cream chest patch
            drawOval(Color(0xFFFEF3C7), topLeft = Offset(cx - w * 0.14f, cy + h * 0.09f), size = Size(w * 0.28f, h * 0.25f))

            // Big Floppy Velvet Ears
            val earDrop = if (isSad) 18f else 0f
            val earWave = earFlap.value * 6f

            // Left Ear
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF92400E))),
                topLeft = Offset(cx - w * 0.44f, cy - h * 0.22f + earDrop + earWave),
                size = Size(w * 0.24f, h * 0.42f)
            )
            // Right Ear
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF92400E))),
                topLeft = Offset(cx + w * 0.20f, cy - h * 0.22f + earDrop - earWave),
                size = Size(w * 0.24f, h * 0.42f)
            )

            // Head
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFDE68A), Color(0xFFFBBF24), Color(0xFFD97706)),
                    center = Offset(cx, cy - h * 0.12f),
                    radius = w * 0.32f
                ),
                radius = w * 0.31f,
                center = Offset(cx, cy - h * 0.08f)
            )

            // Puppy Muzzle
            drawOval(Color(0xFFFEF3C7), topLeft = Offset(cx - w * 0.18f, cy - h * 0.06f), size = Size(w * 0.36f, h * 0.24f))

            // Eyes
            val eyeSpacing = w * 0.14f
            val eyeCenterY = cy - h * 0.11f
            val eyeR = w * 0.08f

            if (isSad) {
                // Puppy-dog sad tearful eyes
                drawCircle(Color(0xFF451A03), radius = eyeR, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF451A03), radius = eyeR, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color.White, radius = 4f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 4f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else {
                val blink = blinkAnim.value
                drawOval(Color(0xFF451A03), topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color(0xFF451A03), topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 3f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 3f))
                }
            }

            // Black button nose
            drawOval(Color(0xFF1E293B), topLeft = Offset(cx - 8f, cy - h * 0.05f), size = Size(16f, 11f))
            drawCircle(Color.White, radius = 1.5f, center = Offset(cx - 2f, cy - h * 0.045f))

            // Mouth & Tongue
            if (isSad) {
                val pout = Path().apply {
                    moveTo(cx - 8f, cy + h * 0.08f)
                    quadraticTo(cx, cy + h * 0.04f, cx + 8f, cy + h * 0.08f)
                }
                drawPath(pout, Color(0xFF78350F), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            } else {
                // Happy panting tongue
                val tongueH = 8f + tongueAnim.value * 4f
                drawOval(Color(0xFFFB7185), topLeft = Offset(cx - 6f, cy + h * 0.05f), size = Size(12f, tongueH))
                val smile = Path().apply {
                    moveTo(cx - 10f, cy + h * 0.04f)
                    quadraticTo(cx, cy + h * 0.08f, cx + 10f, cy + h * 0.04f)
                }
                drawPath(smile, Color(0xFF78350F), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            }

            // Paws
            drawCircle(Color(0xFFFEF3C7), radius = 12f, center = Offset(cx - w * 0.16f, cy + h * 0.33f))
            drawCircle(Color(0xFFFEF3C7), radius = 12f, center = Offset(cx + w * 0.16f, cy + h * 0.33f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f, w, h)
            }
        }
    }
}

// =========================================================================
// 5. ENCHANTED FANTASY TREE ("Twiggy")
// =========================================================================
@Composable
fun CartoonFantasyTreeMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val swayAnim = remember { Animatable(0f) }
    val leafRustle = remember { Animatable(0f) }
    val fireflyOrbit = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        swayAnim.animateTo(1f, infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        leafRustle.animateTo(1f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        fireflyOrbit.animateTo(360f, infiniteRepeatable(tween(3500, easing = LinearEasing), RepeatMode.Restart))
    }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(1f, infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Restart))
        } else {
            tearAnim.snapTo(0f)
        }
    }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(3500)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val isSad = mood == MascotMood.CRYING_SAD
    val isCheering = mood == MascotMood.CHEERING || mood == MascotMood.CELEBRATING

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.54f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.38f, h * 0.88f),
                size = Size(w * 0.76f, h * 0.10f)
            )

            // Lush Cartoon Foliage Canopy (Behind & Above Trunk)
            val leafSway = (leafRustle.value - 0.5f) * 6f
            drawCircle(Color(0xFF059669), radius = w * 0.28f, center = Offset(cx - w * 0.20f + leafSway, cy - h * 0.32f))
            drawCircle(Color(0xFF10B981), radius = w * 0.32f, center = Offset(cx + w * 0.18f - leafSway, cy - h * 0.34f))
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFF6EE7B7), Color(0xFF10B981), Color(0xFF047857))),
                radius = w * 0.34f,
                center = Offset(cx, cy - h * 0.38f)
            )

            // Fantasy Apples / Blossoms on leaves
            drawCircle(Color(0xFFFF4757), radius = 5f, center = Offset(cx - w * 0.22f, cy - h * 0.40f))
            drawCircle(Color(0xFFFFD200), radius = 5f, center = Offset(cx + w * 0.20f, cy - h * 0.38f))
            drawCircle(Color(0xFFFF4757), radius = 5f, center = Offset(cx + w * 0.02f, cy - h * 0.48f))

            // Living Tree Trunk Body (Soft wooden bark)
            val trunkPath = Path().apply {
                moveTo(cx - w * 0.22f, cy - h * 0.15f)
                lineTo(cx + w * 0.22f, cy - h * 0.15f)
                quadraticTo(cx + w * 0.28f, cy + h * 0.28f, cx + w * 0.32f, cy + h * 0.34f)
                lineTo(cx - w * 0.32f, cy + h * 0.34f)
                quadraticTo(cx - w * 0.28f, cy + h * 0.28f, cx - w * 0.22f, cy - h * 0.15f)
                close()
            }
            drawPath(
                trunkPath,
                Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF78350F), Color(0xFF451A03)))
            )

            // Leafy Branch Arms
            val armDrop = if (isSad) 18f else 0f
            val branchWave = if (isCheering) (swayAnim.value * 12f) else 0f

            // Left Branch Arm
            val leftBranch = Path().apply {
                moveTo(cx - w * 0.22f, cy + 4f)
                quadraticTo(cx - w * 0.38f, cy - h * 0.05f + armDrop - branchWave, cx - w * 0.44f, cy - h * 0.12f + armDrop - branchWave * 2f)
            }
            drawPath(leftBranch, Color(0xFF78350F), style = Stroke(width = 7f, cap = StrokeCap.Round))
            drawCircle(Color(0xFF10B981), radius = 8f, center = Offset(cx - w * 0.44f, cy - h * 0.12f + armDrop - branchWave * 2f))

            // Right Branch Arm
            val rightBranch = Path().apply {
                moveTo(cx + w * 0.22f, cy + 4f)
                quadraticTo(cx + w * 0.38f, cy - h * 0.05f + armDrop - branchWave, cx + w * 0.44f, cy - h * 0.12f + armDrop - branchWave * 2f)
            }
            drawPath(rightBranch, Color(0xFF78350F), style = Stroke(width = 7f, cap = StrokeCap.Round))
            drawCircle(Color(0xFF10B981), radius = 8f, center = Offset(cx + w * 0.44f, cy - h * 0.12f + armDrop - branchWave * 2f))

            // Cute Living Tree Face in Bark
            val eyeSpacing = w * 0.12f
            val eyeCenterY = cy + 2f
            val eyeR = w * 0.07f

            if (isSad) {
                // Sad crying sap tears
                drawCircle(Color(0xFF0F172A), radius = eyeR, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF0F172A), radius = eyeR, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else {
                val blink = blinkAnim.value
                drawOval(Color(0xFF0F172A), topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color(0xFF0F172A), topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                    drawCircle(Color.White, radius = 3f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                }
            }

            // Wooden Smile / Pout
            if (isSad) {
                val sadMouth = Path().apply {
                    moveTo(cx - 7f, cy + h * 0.16f)
                    quadraticTo(cx, cy + h * 0.12f, cx + 7f, cy + h * 0.16f)
                }
                drawPath(sadMouth, Color(0xFFFEF3C7), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            } else {
                val smile = Path().apply {
                    moveTo(cx - 8f, cy + h * 0.12f)
                    quadraticTo(cx, cy + h * 0.18f, cx + 8f, cy + h * 0.12f)
                }
                drawPath(smile, Color(0xFFFEF3C7), style = Stroke(width = 2.8f, cap = StrokeCap.Round))
            }

            // Magic Glowing Firefly
            val angleRad = Math.toRadians(fireflyOrbit.value.toDouble())
            val fireflyX = cx + (w * 0.38f * cos(angleRad)).toFloat()
            val fireflyY = cy - h * 0.20f + (h * 0.18f * sin(angleRad)).toFloat()
            drawCircle(Color(0x66FEF08A), radius = 7f, center = Offset(fireflyX, fireflyY))
            drawCircle(Color(0xFFFEF08A), radius = 3.5f, center = Offset(fireflyX, fireflyY))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.50f, w, h)
            }
        }
    }
}

// =========================================================================
// 6. COSMIC STAR BUNNY ("Pip")
// =========================================================================
@Composable
fun CartoonStarBunnyMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val earHop = remember { Animatable(0f) }
    val noseTwitch = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        earHop.animateTo(1f, infiniteRepeatable(tween(750, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(Unit) {
        noseTwitch.animateTo(1f, infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(1f, infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Restart))
        } else {
            tearAnim.snapTo(0f)
        }
    }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(2700)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val isSad = mood == MascotMood.CRYING_SAD

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.56f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.32f, h * 0.88f),
                size = Size(w * 0.64f, h * 0.10f)
            )

            // Long Fluffy Bunny Ears
            val earDrop = if (isSad) 24f else 0f
            val earBounce = earHop.value * 8f

            // Left Long Ear
            val leftEar = Path().apply {
                moveTo(cx - w * 0.22f, cy - h * 0.22f + earDrop)
                quadraticTo(cx - w * 0.30f, cy - h * 0.62f + earDrop - earBounce, cx - w * 0.16f, cy - h * 0.68f + earDrop - earBounce)
                quadraticTo(cx - w * 0.08f, cy - h * 0.52f + earDrop, cx - w * 0.08f, cy - h * 0.24f + earDrop)
                close()
            }
            drawPath(leftEar, Color(0xFFFFF0F5))
            // Left inner pink
            val innerLeftEar = Path().apply {
                moveTo(cx - w * 0.20f, cy - h * 0.26f + earDrop)
                quadraticTo(cx - w * 0.26f, cy - h * 0.56f + earDrop - earBounce, cx - w * 0.16f, cy - h * 0.62f + earDrop - earBounce)
                quadraticTo(cx - w * 0.10f, cy - h * 0.48f + earDrop, cx - w * 0.10f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(innerLeftEar, Color(0xFFFFB6C1))

            // Right Long Ear
            val rightEar = Path().apply {
                moveTo(cx + w * 0.08f, cy - h * 0.24f + earDrop)
                quadraticTo(cx + w * 0.08f, cy - h * 0.52f + earDrop, cx + w * 0.16f, cy - h * 0.68f + earDrop - earBounce)
                quadraticTo(cx + w * 0.30f, cy - h * 0.62f + earDrop - earBounce, cx + w * 0.22f, cy - h * 0.22f + earDrop)
                close()
            }
            drawPath(rightEar, Color(0xFFFFF0F5))
            val innerRightEar = Path().apply {
                moveTo(cx + w * 0.10f, cy - h * 0.26f + earDrop)
                quadraticTo(cx + w * 0.10f, cy - h * 0.48f + earDrop, cx + w * 0.16f, cy - h * 0.62f + earDrop - earBounce)
                quadraticTo(cx + w * 0.26f, cy - h * 0.56f + earDrop - earBounce, cx + w * 0.20f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(innerRightEar, Color(0xFFFFB6C1))

            // Fluffy Marshmallow Bunny Body
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFFFF5F7), Color(0xFFFED7E2))),
                topLeft = Offset(cx - w * 0.26f, cy + h * 0.02f),
                size = Size(w * 0.52f, h * 0.34f)
            )

            // Round Bunny Head
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFFF0F5), Color(0xFFFCE7F3))),
                radius = w * 0.30f,
                center = Offset(cx, cy - h * 0.10f)
            )

            // Eyes
            val eyeSpacing = w * 0.13f
            val eyeCenterY = cy - h * 0.12f
            val eyeR = w * 0.075f

            if (isSad) {
                drawCircle(Color(0xFFBE185D), radius = eyeR, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFFBE185D), radius = eyeR, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else {
                val blink = blinkAnim.value
                drawOval(Color(0xFF831843), topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color(0xFF831843), topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 3f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 3f))
                }
            }

            // Star Cheeks
            drawCircle(Color(0xFFF472B6).copy(alpha = 0.6f), radius = w * 0.05f, center = Offset(cx - eyeSpacing - eyeR, eyeCenterY + eyeR * 1.1f))
            drawCircle(Color(0xFFF472B6).copy(alpha = 0.6f), radius = w * 0.05f, center = Offset(cx + eyeSpacing + eyeR, eyeCenterY + eyeR * 1.1f))

            // Pink Nose & Mouth
            val noseY = cy - h * 0.05f + noseTwitch.value * 2f
            drawCircle(Color(0xFFEC4899), radius = 4f, center = Offset(cx, noseY))
            val bunnyMouth = Path().apply {
                moveTo(cx - 5f, noseY + 6f)
                quadraticTo(cx, noseY + 9f, cx + 5f, noseY + 6f)
            }
            drawPath(bunnyMouth, Color(0xFFDB2777), style = Stroke(width = 1.8f, cap = StrokeCap.Round))

            // Paws
            drawCircle(Color(0xFFFFF0F5), radius = 10f, center = Offset(cx - w * 0.14f, cy + h * 0.18f))
            drawCircle(Color(0xFFFFF0F5), radius = 10f, center = Offset(cx + w * 0.14f, cy + h * 0.18f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.45f, w, h)
            }
        }
    }
}

// =========================================================================
// 7. CARTOON HONEY BEE ("Buzzy" - Free Starter Mascot!)
// =========================================================================
@Composable
fun CartoonHoneyBeeMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    PixarHoneyBeeMascot(
        modifier = modifier,
        mood = mood,
        size = size
    )
}

// =========================================================================
// 8. CARTOON WIZARD FOX ("Foxy")
// =========================================================================
@Composable
fun CartoonWizardFoxMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 100.dp
) {
    val tailWag = remember { Animatable(0f) }
    val blinkAnim = remember { Animatable(1f) }
    val tearAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        tailWag.animateTo(1f, infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(1f, infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Restart))
        } else {
            tearAnim.snapTo(0f)
        }
    }
    LaunchedEffect(mood) {
        while (mood != MascotMood.CRYING_SAD) {
            delay(3300)
            blinkAnim.animateTo(0.1f, tween(60, easing = LinearEasing))
            blinkAnim.animateTo(1f, tween(80, easing = FastOutSlowInEasing))
        }
    }

    val isSad = mood == MascotMood.CRYING_SAD

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.54f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(listOf(Color(0x28000000), Color.Transparent)),
                topLeft = Offset(cx - w * 0.35f, h * 0.88f),
                size = Size(w * 0.70f, h * 0.10f)
            )

            // Fluffy bushy fox tail
            val tailAngle = (tailWag.value - 0.5f) * (if (isSad) 8f else 20f)
            val tailPath = Path().apply {
                moveTo(cx + w * 0.18f, cy + h * 0.22f)
                cubicTo(
                    cx + w * 0.42f + tailAngle, cy + h * 0.22f,
                    cx + w * 0.48f + tailAngle, cy - h * 0.05f,
                    cx + w * 0.36f + tailAngle, cy - h * 0.20f
                )
            }
            drawPath(tailPath, Color(0xFFEA580C), style = Stroke(width = 12f, cap = StrokeCap.Round))
            drawCircle(Color.White, radius = 8f, center = Offset(cx + w * 0.36f + tailAngle, cy - h * 0.20f))

            // Body
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C))),
                topLeft = Offset(cx - w * 0.26f, cy + h * 0.02f),
                size = Size(w * 0.52f, h * 0.36f)
            )
            // White chest fluff
            drawOval(Color(0xFFFFF7ED), topLeft = Offset(cx - w * 0.14f, cy + h * 0.08f), size = Size(w * 0.28f, h * 0.26f))

            // Pointy Ears
            val earDrop = if (isSad) 14f else 0f
            val leftEar = Path().apply {
                moveTo(cx - w * 0.28f, cy - h * 0.18f + earDrop)
                lineTo(cx - w * 0.34f, cy - h * 0.44f + earDrop)
                lineTo(cx - w * 0.08f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(leftEar, Color(0xFFEA580C))
            val leftEarInner = Path().apply {
                moveTo(cx - w * 0.26f, cy - h * 0.20f + earDrop)
                lineTo(cx - w * 0.30f, cy - h * 0.38f + earDrop)
                lineTo(cx - w * 0.12f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(leftEarInner, Color(0xFFFFEDD5))

            val rightEar = Path().apply {
                moveTo(cx + w * 0.28f, cy - h * 0.18f + earDrop)
                lineTo(cx + w * 0.34f, cy - h * 0.44f + earDrop)
                lineTo(cx + w * 0.08f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(rightEar, Color(0xFFEA580C))
            val rightEarInner = Path().apply {
                moveTo(cx + w * 0.26f, cy - h * 0.20f + earDrop)
                lineTo(cx + w * 0.30f, cy - h * 0.38f + earDrop)
                lineTo(cx + w * 0.12f, cy - h * 0.26f + earDrop)
                close()
            }
            drawPath(rightEarInner, Color(0xFFFFEDD5))

            // Head
            drawCircle(Color(0xFFEA580C), radius = w * 0.30f, center = Offset(cx, cy - h * 0.08f))

            // White Fox Cheek Patches
            val cheekLeft = Path().apply {
                moveTo(cx - w * 0.28f, cy - h * 0.04f)
                quadraticTo(cx - w * 0.15f, cy + h * 0.14f, cx, cy + h * 0.06f)
                lineTo(cx - w * 0.08f, cy - h * 0.14f)
                close()
            }
            drawPath(cheekLeft, Color(0xFFFFF7ED))
            val cheekRight = Path().apply {
                moveTo(cx + w * 0.28f, cy - h * 0.04f)
                quadraticTo(cx + w * 0.15f, cy + h * 0.14f, cx, cy + h * 0.06f)
                lineTo(cx + w * 0.08f, cy - h * 0.14f)
                close()
            }
            drawPath(cheekRight, Color(0xFFFFF7ED))

            // Eyes
            val eyeSpacing = w * 0.13f
            val eyeCenterY = cy - h * 0.10f
            val eyeR = w * 0.075f

            if (isSad) {
                drawCircle(Color(0xFF431407), radius = eyeR, center = Offset(cx - eyeSpacing, eyeCenterY))
                drawCircle(Color(0xFF431407), radius = eyeR, center = Offset(cx + eyeSpacing, eyeCenterY))
                drawCircle(Color.White, radius = 3.5f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                drawCircle(Color.White, radius = 3.5f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                drawAnimatedCryingTears(cx - eyeSpacing, eyeCenterY + eyeR * 0.8f, tearAnim.value, h)
                drawAnimatedCryingTears(cx + eyeSpacing, eyeCenterY + eyeR * 0.8f, (tearAnim.value + 0.5f) % 1f, h)
            } else {
                val blink = blinkAnim.value
                drawOval(Color(0xFF431407), topLeft = Offset(cx - eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                drawOval(Color(0xFF431407), topLeft = Offset(cx + eyeSpacing - eyeR, eyeCenterY - eyeR * blink), size = Size(eyeR * 2, eyeR * 2 * blink))
                if (blink > 0.4f) {
                    drawCircle(Color.White, radius = 3f, center = Offset(cx - eyeSpacing - 2f, eyeCenterY - 2f))
                    drawCircle(Color.White, radius = 3f, center = Offset(cx + eyeSpacing - 2f, eyeCenterY - 2f))
                }
            }

            // Black Nose & Mouth
            drawCircle(Color(0xFF1E293B), radius = 4f, center = Offset(cx, cy + 4f))
            if (isSad) {
                val pout = Path().apply {
                    moveTo(cx - 5f, cy + 12f)
                    quadraticTo(cx, cy + 8f, cx + 5f, cy + 12f)
                }
                drawPath(pout, Color(0xFF7C2D12), style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                val smile = Path().apply {
                    moveTo(cx - 5f, cy + 8f)
                    quadraticTo(cx, cy + 12f, cx + 5f, cy + 8f)
                }
                drawPath(smile, Color(0xFF7C2D12), style = Stroke(width = 2f, cap = StrokeCap.Round))
            }

            // Paws
            drawCircle(Color(0xFFFFF7ED), radius = 10f, center = Offset(cx - w * 0.14f, cy + h * 0.32f))
            drawCircle(Color(0xFFFFF7ED), radius = 10f, center = Offset(cx + w * 0.14f, cy + h * 0.32f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.40f, w, h)
            }
        }
    }
}

// =========================================================================
// 9. CARTOON BAMBOO PANDA ("Bao Bao")
// =========================================================================
@Composable
fun CartoonPandaMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val earWiggle = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CHEERING || mood == MascotMood.CELEBRATING) {
            earWiggle.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(280, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            earWiggle.snapTo(0f)
        }
    }

    val tearAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            tearAnim.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height

            // 1. Black Panda Ears
            val earR = w * 0.17f
            val earWiggleOffset = earWiggle.value * 4f
            // Left Ear
            drawCircle(
                color = Color(0xFF1E293B),
                radius = earR,
                center = Offset(cx - w * 0.28f, cy - h * 0.26f + earWiggleOffset)
            )
            drawCircle(
                color = Color(0xFF334155),
                radius = earR * 0.55f,
                center = Offset(cx - w * 0.28f, cy - h * 0.26f + earWiggleOffset)
            )
            // Right Ear
            drawCircle(
                color = Color(0xFF1E293B),
                radius = earR,
                center = Offset(cx + w * 0.28f, cy - h * 0.26f - earWiggleOffset)
            )
            drawCircle(
                color = Color(0xFF334155),
                radius = earR * 0.55f,
                center = Offset(cx + w * 0.28f, cy - h * 0.26f - earWiggleOffset)
            )

            // 2. Panda Chubby Face
            drawCircle(
                color = Color(0xFFF8FAFC),
                radius = w * 0.38f,
                center = Offset(cx, cy + 2f)
            )
            // Soft border
            drawCircle(
                color = Color(0xFFE2E8F0),
                radius = w * 0.38f,
                center = Offset(cx, cy + 2f),
                style = Stroke(width = 2.5f)
            )

            // 3. Iconic Black Eye Patches (Angled Ovals)
            val patchW = w * 0.16f
            val patchH = h * 0.20f
            // Left patch
            drawOval(
                color = Color(0xFF1E293B),
                topLeft = Offset(cx - w * 0.27f, cy - h * 0.14f),
                size = Size(patchW, patchH)
            )
            // Right patch
            drawOval(
                color = Color(0xFF1E293B),
                topLeft = Offset(cx + w * 0.11f, cy - h * 0.14f),
                size = Size(patchW, patchH)
            )

            // 4. Expressive Eyes
            when (mood) {
                MascotMood.CRYING_SAD -> {
                    // Closed sad weeping arcs inside patches
                    val sadL = Path().apply {
                        moveTo(cx - w * 0.22f, cy - 2f)
                        quadraticTo(cx - w * 0.19f, cy - 8f, cx - w * 0.16f, cy - 2f)
                    }
                    val sadR = Path().apply {
                        moveTo(cx + w * 0.16f, cy - 2f)
                        quadraticTo(cx + w * 0.19f, cy - 8f, cx + w * 0.22f, cy - 2f)
                    }
                    drawPath(sadL, Color.White, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    drawPath(sadR, Color.White, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    // Big Crying Tears streaming down
                    drawAnimatedCryingTears(cx - w * 0.19f, cy + 4f, tearAnim.value, h)
                    drawAnimatedCryingTears(cx + w * 0.19f, cy + 4f, tearAnim.value, h)
                }
                MascotMood.CHEERING, MascotMood.CELEBRATING -> {
                    // Joyful rainbow sparkle eyes
                    drawCircle(Color.White, radius = 5.5f, center = Offset(cx - w * 0.19f, cy - 4f))
                    drawCircle(Color.White, radius = 5.5f, center = Offset(cx + w * 0.19f, cy - 4f))
                    drawCircle(Color(0xFF38BDF8), radius = 2.5f, center = Offset(cx - w * 0.19f, cy - 4f))
                    drawCircle(Color(0xFF38BDF8), radius = 2.5f, center = Offset(cx + w * 0.19f, cy - 4f))
                }
                else -> {
                    // Cute shining round eyes
                    drawCircle(Color.White, radius = 5f, center = Offset(cx - w * 0.19f, cy - 4f))
                    drawCircle(Color.White, radius = 5f, center = Offset(cx + w * 0.19f, cy - 4f))
                    drawCircle(Color(0xFF0F172A), radius = 3.5f, center = Offset(cx - w * 0.18f, cy - 4f))
                    drawCircle(Color(0xFF0F172A), radius = 3.5f, center = Offset(cx + w * 0.20f, cy - 4f))
                    drawCircle(Color.White, radius = 1.5f, center = Offset(cx - w * 0.19f, cy - 5f))
                    drawCircle(Color.White, radius = 1.5f, center = Offset(cx + w * 0.19f, cy - 5f))
                }
            }

            // 5. Pink Cheeks
            drawCircle(Color(0xFFFFB6C1).copy(alpha = 0.6f), radius = 7f, center = Offset(cx - w * 0.26f, cy + 10f))
            drawCircle(Color(0xFFFFB6C1).copy(alpha = 0.6f), radius = 7f, center = Offset(cx + w * 0.26f, cy + 10f))

            // 6. Cute Triangle Button Nose
            val nosePath = Path().apply {
                moveTo(cx - 5f, cy + 5f)
                lineTo(cx + 5f, cy + 5f)
                lineTo(cx, cy + 10f)
                close()
            }
            drawPath(nosePath, Color(0xFF1E293B))

            // 7. Smile & Bamboo Leaf
            if (mood == MascotMood.CRYING_SAD) {
                val sadMouth = Path().apply {
                    moveTo(cx - 6f, cy + 18f)
                    quadraticTo(cx, cy + 13f, cx + 6f, cy + 18f)
                }
                drawPath(sadMouth, Color(0xFF1E293B), style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                val smilePath = Path().apply {
                    moveTo(cx - 7f, cy + 13f)
                    quadraticTo(cx, cy + 18f, cx + 7f, cy + 13f)
                }
                drawPath(smilePath, Color(0xFF1E293B), style = Stroke(width = 2f, cap = StrokeCap.Round))

                // Green Bamboo Stalk & Leaves sticking out from mouth
                val bambooPath = Path().apply {
                    moveTo(cx + 4f, cy + 15f)
                    quadraticTo(cx + 18f, cy + 12f, cx + 24f, cy + 4f)
                }
                drawPath(bambooPath, Color(0xFF10B981), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                // Bamboo leaf
                val leafPath = Path().apply {
                    moveTo(cx + 24f, cy + 4f)
                    quadraticTo(cx + 30f, cy + 2f, cx + 32f, cy - 3f)
                    quadraticTo(cx + 26f, cy, cx + 24f, cy + 4f)
                    close()
                }
                drawPath(leafPath, Color(0xFF34D399))
            }

            // 8. Party Hat for Celebration
            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.38f, w, h)
            }
        }
    }
}

// =========================================================================
// 10. CARTOON ROYAL LION CUB ("Leo")
// =========================================================================
@Composable
fun CartoonLionMascot(
    mood: MascotMood,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val maneBounce = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CHEERING || mood == MascotMood.CELEBRATING) {
            maneBounce.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(320, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            maneBounce.snapTo(0f)
        }
    }

    val tearAnim = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.CRYING_SAD) {
            tearAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            tearAnim.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val w = this.size.width
            val h = this.size.height
            val bounceY = maneBounce.value * 3f

            // 1. Fluffy Golden Brown Lion Mane (Scalloped Petals)
            val maneColor = Color(0xFFD97706)
            val maneRadius = w * 0.44f
            val petalCount = 12
            for (i in 0 until petalCount) {
                val angle = (i * 360f / petalCount) * (Math.PI / 180f)
                val px = cx + cos(angle).toFloat() * (maneRadius * 0.72f)
                val py = (cy - bounceY) + sin(angle).toFloat() * (maneRadius * 0.72f)
                drawCircle(
                    color = maneColor,
                    radius = w * 0.16f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = w * 0.12f,
                    center = Offset(px, py)
                )
            }

            // 2. Lion Ears
            val earR = w * 0.13f
            drawCircle(Color(0xFFF59E0B), radius = earR, center = Offset(cx - w * 0.26f, cy - h * 0.22f - bounceY))
            drawCircle(Color(0xFFFDE68A), radius = earR * 0.55f, center = Offset(cx - w * 0.26f, cy - h * 0.22f - bounceY))
            drawCircle(Color(0xFFF59E0B), radius = earR, center = Offset(cx + w * 0.26f, cy - h * 0.22f - bounceY))
            drawCircle(Color(0xFFFDE68A), radius = earR * 0.55f, center = Offset(cx + w * 0.26f, cy - h * 0.22f - bounceY))

            // 3. Cute Golden Cub Face
            drawCircle(
                color = Color(0xFFFDE68A),
                radius = w * 0.32f,
                center = Offset(cx, cy - bounceY)
            )

            // 4. White Muzzle / Cheeks
            drawCircle(Color(0xFFFFFBEB), radius = w * 0.13f, center = Offset(cx - w * 0.08f, cy + 8f - bounceY))
            drawCircle(Color(0xFFFFFBEB), radius = w * 0.13f, center = Offset(cx + w * 0.08f, cy + 8f - bounceY))

            // 5. Cute Button Nose
            val nose = Path().apply {
                moveTo(cx - 5f, cy + 2f - bounceY)
                lineTo(cx + 5f, cy + 2f - bounceY)
                lineTo(cx, cy + 8f - bounceY)
                close()
            }
            drawPath(nose, Color(0xFFB45309))

            // 6. Whiskers
            drawLine(Color(0xFF92400E), start = Offset(cx - 14f, cy + 8f - bounceY), end = Offset(cx - 28f, cy + 5f - bounceY), strokeWidth = 1.5f)
            drawLine(Color(0xFF92400E), start = Offset(cx - 14f, cy + 11f - bounceY), end = Offset(cx - 28f, cy + 12f - bounceY), strokeWidth = 1.5f)
            drawLine(Color(0xFF92400E), start = Offset(cx + 14f, cy + 8f - bounceY), end = Offset(cx + 28f, cy + 5f - bounceY), strokeWidth = 1.5f)
            drawLine(Color(0xFF92400E), start = Offset(cx + 14f, cy + 11f - bounceY), end = Offset(cx + 28f, cy + 12f - bounceY), strokeWidth = 1.5f)

            // 7. Expressive Eyes
            when (mood) {
                MascotMood.CRYING_SAD -> {
                    val sadL = Path().apply {
                        moveTo(cx - w * 0.17f, cy - 4f - bounceY)
                        quadraticTo(cx - w * 0.13f, cy - 10f - bounceY, cx - w * 0.09f, cy - 4f - bounceY)
                    }
                    val sadR = Path().apply {
                        moveTo(cx + w * 0.09f, cy - 4f - bounceY)
                        quadraticTo(cx + w * 0.13f, cy - 10f - bounceY, cx + w * 0.17f, cy - 4f - bounceY)
                    }
                    drawPath(sadL, Color(0xFF78350F), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    drawPath(sadR, Color(0xFF78350F), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    drawAnimatedCryingTears(cx - w * 0.13f, cy + 4f - bounceY, tearAnim.value, h)
                    drawAnimatedCryingTears(cx + w * 0.13f, cy + 4f - bounceY, tearAnim.value, h)
                }
                MascotMood.CHEERING, MascotMood.CELEBRATING -> {
                    drawCircle(Color(0xFF78350F), radius = 5.5f, center = Offset(cx - w * 0.13f, cy - 6f - bounceY))
                    drawCircle(Color(0xFF78350F), radius = 5.5f, center = Offset(cx + w * 0.13f, cy - 6f - bounceY))
                    drawCircle(Color.White, radius = 2.5f, center = Offset(cx - w * 0.14f, cy - 7f - bounceY))
                    drawCircle(Color.White, radius = 2.5f, center = Offset(cx + w * 0.12f, cy - 7f - bounceY))
                }
                else -> {
                    drawCircle(Color(0xFF78350F), radius = 5f, center = Offset(cx - w * 0.13f, cy - 6f - bounceY))
                    drawCircle(Color(0xFF78350F), radius = 5f, center = Offset(cx + w * 0.13f, cy - 6f - bounceY))
                    drawCircle(Color.White, radius = 2f, center = Offset(cx - w * 0.14f, cy - 7f - bounceY))
                    drawCircle(Color.White, radius = 2f, center = Offset(cx + w * 0.12f, cy - 7f - bounceY))
                }
            }

            // 8. Mouth
            if (mood == MascotMood.CRYING_SAD) {
                val sadMouth = Path().apply {
                    moveTo(cx - 5f, cy + 16f - bounceY)
                    quadraticTo(cx, cy + 12f - bounceY, cx + 5f, cy + 16f - bounceY)
                }
                drawPath(sadMouth, Color(0xFF92400E), style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                val happyMouth = Path().apply {
                    moveTo(cx - 6f, cy + 12f - bounceY)
                    quadraticTo(cx, cy + 18f - bounceY, cx + 6f, cy + 12f - bounceY)
                }
                drawPath(happyMouth, Color(0xFF92400E), style = Stroke(width = 2f, cap = StrokeCap.Round))
            }

            // 9. Royal Crown for Cub
            val crownW = w * 0.22f
            val crownH = h * 0.14f
            val crownTopY = cy - h * 0.34f - bounceY
            val crownPath = Path().apply {
                moveTo(cx - crownW / 2f, crownTopY + crownH)
                lineTo(cx - crownW / 2f, crownTopY)
                lineTo(cx - crownW / 4f, crownTopY + crownH * 0.5f)
                lineTo(cx, crownTopY)
                lineTo(cx + crownW / 4f, crownTopY + crownH * 0.5f)
                lineTo(cx + crownW / 2f, crownTopY)
                lineTo(cx + crownW / 2f, crownTopY + crownH)
                close()
            }
            drawPath(crownPath, Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFF59E0B))))
            drawCircle(Color(0xFFEF4444), radius = 2.5f, center = Offset(cx, crownTopY + crownH * 0.6f))

            if (mood == MascotMood.CELEBRATING) {
                drawCelebrationPartyHat(cx, cy - h * 0.36f - bounceY, w, h)
            }
        }
    }
}

// =========================================================================
// HELPER DRAWING FUNCTIONS
// =========================================================================

/**
 * Animated cute streaming blue teardrops with splash
 */
internal fun DrawScope.drawAnimatedCryingTears(originX: Float, originY: Float, progress: Float, totalHeight: Float) {
    val dropY = originY + progress * (totalHeight * 0.25f)
    val dropScale = (1f - progress * 0.3f)
    val alpha = (1f - progress * 0.2f).coerceIn(0f, 1f)

    // Teardrop shape (Pear/oval with tapered top)
    val tearColor = Color(0xFF38BDF8).copy(alpha = alpha)
    val tearPath = Path().apply {
        moveTo(originX, dropY - 8f * dropScale)
        quadraticTo(originX - 4f * dropScale, dropY, originX, dropY + 6f * dropScale)
        quadraticTo(originX + 4f * dropScale, dropY, originX, dropY - 8f * dropScale)
        close()
    }
    drawPath(tearPath, tearColor)
    // Little glint on teardrop
    drawCircle(Color.White.copy(alpha = alpha), radius = 1.5f * dropScale, center = Offset(originX - 1.5f, dropY))

    // Water splash at bottom of path
    if (progress > 0.85f) {
        val splashAlpha = ((1f - progress) / 0.15f).coerceIn(0f, 1f)
        drawCircle(Color(0xFF7DD3FC).copy(alpha = splashAlpha), radius = 2.5f, center = Offset(originX - 6f, dropY + 4f))
        drawCircle(Color(0xFF7DD3FC).copy(alpha = splashAlpha), radius = 2.5f, center = Offset(originX + 6f, dropY + 4f))
    }
}

/**
 * Festive 3D party hat with pom-pom and confetti for celebration
 */
internal fun DrawScope.drawCelebrationPartyHat(cx: Float, topY: Float, w: Float, h: Float) {
    val hatPath = Path().apply {
        moveTo(cx - 14f, topY)
        lineTo(cx + 8f, topY - h * 0.20f)
        lineTo(cx + 18f, topY + 2f)
        close()
    }
    drawPath(hatPath, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF5722))))
    // Pom-pom
    drawCircle(Color(0xFFFF4081), radius = 6f, center = Offset(cx + 8f, topY - h * 0.20f))
    // Hat stripes
    drawLine(Color.White, start = Offset(cx - 4f, topY - 10f), end = Offset(cx + 14f, topY - 8f), strokeWidth = 2f)

    // Star & Confetti sparkles
    drawCircle(Color(0xFFFFD700), radius = 4f, center = Offset(cx - w * 0.38f, topY + 8f))
    drawCircle(Color(0xFFFF4081), radius = 4f, center = Offset(cx + w * 0.38f, topY + 6f))
    drawCircle(Color(0xFF00E676), radius = 3.5f, center = Offset(cx - w * 0.30f, topY + h * 0.30f))
    drawCircle(Color(0xFF00B0FF), radius = 4f, center = Offset(cx + w * 0.32f, topY + h * 0.28f))
}

// =========================================================================
// COMPANION SELECTION & UNLOCK SHOP DIALOG
// =========================================================================
@Composable
fun MascotCompanionPickerDialog(
    currentMascot: CartoonMascotType,
    coins: Int = 1000,
    unlockedMascots: Set<String> = setOf("bee"),
    onMascotSelected: (CartoonMascotType) -> Unit,
    onMascotUnlocked: (CartoonMascotType) -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .shadow(20.dp, RoundedCornerShape(28.dp))
                .border(3.dp, Brush.linearGradient(listOf(Color(0xFFFBBF24), Color(0xFFFFB6C1), Color(0xFFBAE6FD))), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Coins Balance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COMPANION SHOP",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                        )
                        Text(
                            text = "Apply character to switch main game theme!",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Gold Coin Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                            .border(1.5.dp, Color(0xFFFEF3C7), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "🪙 $coins",
                            style = TextStyle(
                                fontFamily = GameFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mascot List
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 440.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    CartoonMascotType.entries.chunked(2).forEach { pair ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pair.forEach { mascot ->
                                val isSelected = mascot == currentMascot
                                val isUnlocked = mascot.unlockCost == 0 ||
                                        mascot.id.equals("bee", ignoreCase = true) ||
                                        unlockedMascots.contains(mascot.id.lowercase())
                                val canAfford = coins >= mascot.unlockCost

                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) mascot.secondaryColor.copy(alpha = 0.5f) else Color(0xFFF8FAFC)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) mascot.primaryColor else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                        .clickable {
                                            if (isUnlocked) {
                                                onMascotSelected(mascot)
                                                onDismiss()
                                            } else if (canAfford) {
                                                onMascotUnlocked(mascot)
                                            }
                                        }
                                        .testTag("companion_card_${mascot.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            CartoonMascot(
                                                type = mascot,
                                                mood = if (isSelected) MascotMood.CHEERING else if (!isUnlocked) MascotMood.THINKING else MascotMood.HAPPY,
                                                size = 56.dp
                                            )
                                            if (!isUnlocked) {
                                                // Lock Badge Overlay
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
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "${mascot.iconEmoji} ${mascot.displayName}",
                                            style = TextStyle(
                                                fontFamily = GameFontFamily,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                        )

                                        // Theme Badge
                                        Text(
                                            text = "🎨 ${mascot.associatedTheme.title}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = mascot.primaryColor
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Status Action Button
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0xFF10B981))
                                                    .padding(vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "✓ EQUIPPED",
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
                                                        onMascotSelected(mascot)
                                                        onDismiss()
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
                                            // Locked button
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (canAfford)
                                                            Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                                                        else
                                                            Brush.horizontalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                                                    )
                                                    .clickable(enabled = canAfford) {
                                                        onMascotUnlocked(mascot)
                                                    }
                                                    .padding(vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (canAfford) "UNLOCK 1,000 🪙" else "1,000 🪙 (-${mascot.unlockCost - coins})",
                                                    fontSize = 9.5.sp,
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

                Spacer(modifier = Modifier.height(10.dp))

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable { onDismiss() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CLOSE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
