package com.example.myapplication0.ui

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset


import coil.compose.AsyncImage

import com.example.myapplication0.R
import com.example.myapplication0.data.Photo
import com.example.myapplication0.viewmodel.MainViewModel
import com.example.myapplication0.viewmodel.PhotoUiState

// De sealed interface die beschrijft in welke toestand de UI zich kan bevinden
// (Loading, Success, Error).

// FASE 1: UI IMPLEMENTATIE & COMPONENT ARCHITECTUUR
//
// TECHNISCHE CONTEXT: Composables & State Hoisting
// Compose werkt declaratief: je beschrijft WAT je wilt tonen,
// Compose bepaalt HOE en WANNEER het opnieuw wordt getekend.
// In dit bestand staan:
// - "Screens" → vullen het hele scherm
// - "Components" → herbruikbare bouwstenen binnen een screen

// COMPONENT: Lesson1Header
// PATTERN: State Hoisting (Stateless Component)
// Deze component bevat GEEN eigen state.
// De waarde (`counter`) komt van buitenaf.
// De actie (`onIncrement`) stuurt een event terug naar de ouder.
@Composable
fun Lesson1Header(
    counter: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier // Modifier laat de aanroeper bepalen hoe dit component zich gedraagt in de layout.
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // padding in dp voor consistente layout op alle schermdichtheden
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Text is een Material3-Composable.
            // Deze hertekent automatisch bij elke wijziging van `counter`.
            Text(text = "Les 1 State: $counter clicks")

            // Button is een klikbare Material3 knop.
            Button(onClick = onIncrement) {
                Text("Click Me")
            }
        }

        // HorizontalDivider tekent een scheidingslijn in de UI.
        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationSegmentedButtons(
    currentSelection: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Home", "Les 1", "Les 2", "Les 3", "Les 4")
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(end = 8.dp)
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onNavigate(label) },
                selected = currentSelection == label
            ) {
                Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
    }
}

// SCREEN: ListScreen
// Dit is een volledig scherm (pagina) in de app.
// Het scherm ontvangt:
// - een ViewModel (voor data en state)
// - een callback voor navigatie bij klik op een item.
@Composable
fun ListScreen(
    viewModel: MainViewModel,
    onItemClick: (Int) -> Unit,
    onAnswersClick: () -> Unit, // Les 4: navigatie naar antwoorden-overzicht (state hoisted)
    onLesson1Click: () -> Unit, // Nieuwe route voor Les 1 scherm
    onLesson2Click: () -> Unit, // Nieuwe route voor Les 2 scherm
    onLesson3Click: () -> Unit, // Nieuwe route voor Les 3 scherm
    onLesson4Click: () -> Unit  // Nieuwe route voor Les 4 scherm
) {
    // STATE OBSERVATION
    // `uiState` is een Compose-observable waarde in de ViewModel.
    // Bij elke wijziging wordt dit scherm automatisch opnieuw opgebouwd.
    val state = viewModel.uiState
    // Presence (SSOT in ViewModel) — alleen ListScreen leest dit.
    val presence = viewModel.presenceState

    // LOCAL UI STATE
    // `rememberSaveable` zorgt ervoor dat de waarde ook na rotatie blijft bestaan (Les 1).
    // Deze counter hoort alleen bij dit scherm en niet in de ViewModel.
    var counter by rememberSaveable { mutableIntStateOf(0) }

    // UI-toelichtingen ("spraakbubbles") per knop. Klik toggelt zichtbaar/onzichtbaar.
    // Minimalistische menu-toggle + uitleg in het midden van het scherm
    var menuExpanded by rememberSaveable { mutableStateOf(false) } // standaard DICHT (per jouw wens)

    // Welke uitleg moet in het midden (content) getoond worden? null = niets
    var selectedInfo by rememberSaveable { mutableStateOf<String?>(null) }

    // Klein hulpfunctietje om dezelfde knop 2x te kunnen klikken → uitleg aan/uit
    fun toggleInfo(key: String) {
        selectedInfo = if (selectedInfo == key) null else key
    }

    // Event-bridge van header → bottomBar voor reset van de prompt (les 1: lokale state)
    // Verwijderd want prompt is nu SSOT in ViewModel.

    // Les 1–3: zichtbaarheid van het antwoord is nu SSOT in de ViewModel
    // (voorheen lokale UI-state die verloren ging bij navigatie)

    // Scaffold = enige root (verplicht). Achtergrondkleur via containerColor.
    Scaffold(
        containerColor = Color(0xFF0E0B0A),
        topBar = {
            // Vaste header met TopAppBar-principe: titel fungeert als "menu" (anchor voor dropdown)
            Surface(
                color = Color.Transparent,
                modifier = Modifier.statusBarsPadding()
            ) {
                // Compacte hoogte voor een appbar-gevoel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // TITEL als navigatie/menuknop (anchor voor DropdownMenu)
                    NavigationSegmentedButtons(
                        currentSelection = "Home",
                        onNavigate = { option ->
                            when (option) {
                                "Home" -> { /* Already here */ }
                                "Les 1" -> onLesson1Click()
                                "Les 2" -> onLesson2Click()
                                "Les 3" -> onLesson3Click()
                                "Les 4" -> onLesson4Click()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // ACTIONS (drie puntjes) — nu ook een DropdownMenu, zoals gevraagd
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var overflowOpen by rememberSaveable { mutableStateOf(false) }

                        Box {
                            IconButton(onClick = { overflowOpen = true }) {
                                // Gebruik Unicode 'Vertical Ellipsis' om geen extra icon-dependency te vereisen
                                Text("⋮", style = MaterialTheme.typography.titleLarge)
                            }

                            DropdownMenu(
                                expanded = overflowOpen,
                                onDismissRequest = { overflowOpen = false }
                            ) {
                                // Verberg/Toon antwoord
                                DropdownMenuItem(
                                    text = { Text(if (viewModel.isReplyHidden) "Toon antwoord" else "Verberg antwoord") },
                                    onClick = {
                                        overflowOpen = false
                                        viewModel.toggleReplyVisibility()
                                    }
                                )

                                // Geschiedenis (navigatie)
                                DropdownMenuItem(
                                    text = { Text("Geschiedenis") },
                                    onClick = {
                                        overflowOpen = false
                                        // Direct navigeren zonder uitleg op het startscherm (conform Les 4 verzoek)
                                        onAnswersClick()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Chatbot vast onderin: reply boven de prompt; prompt staat tegen de rand van de bottomBar
            Surface(color = Color.Transparent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Laat alleen de bottom-bar reageren op IME + systeem navigatie insets
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Toon de laatste reply (of foutmelding) — boven het invoerveld
                    // Gebruik AnimatedVisibility voor fade-in/out (conform verzoek gebruiker)
                    val showReply = !viewModel.isReplyHidden && (viewModel.chatReply != null || viewModel.isThinking)
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            if (viewModel.isThinking) {
                                ThinkingIndicator()
                            } else {
                                viewModel.chatReply?.let { reply ->
                                    Text(text = reply, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }

                    // Prompt + verzendknop onderaan
                    // Prompt is nu SSOT in ViewModel
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viewModel.chatPrompt,
                            onValueChange = { viewModel.updatePrompt(it) },
                            label = { Text("Vraag aan EzChatbot") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                errorContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { viewModel.sendPrompt("http://10.0.2.2:8080", viewModel.chatPrompt) }) {
                            Text("Ask")
                        }
                    }
                }
            }
        }
        ) { innerPadding ->
            // CONTENT-lagen gesplitst: achtergrond zonder padding, voorgrond met innerPadding.
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // VOORGROND B: scherpe voorgrondafbeelding container (neumorfisch), als sibling naast de achtergrond
                Box(
                    modifier = Modifier
                        .offset(y = (-42).dp)
                        .matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {

                    val painter = painterResource(id = R.drawable.magic_lab_bg)
                    val density = LocalDensity.current

                    val configuration = LocalConfiguration.current
                    val imageWidthDp = configuration.screenWidthDp.dp          // zo breed als het scherm
                    val imageHeightDp = (configuration.screenWidthDp * 4f / 3f).dp  // ongeveer 3:4 verhouding (hoger dan breed)

                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageWidthDp, imageHeightDp)
                                .graphicsLayer {
                                    scaleX = 1.00f
                                    scaleY = 1.00f
                                }
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // === NEW CHARACTER LAYER (Animated) ===
                        val infiniteTransition = rememberInfiniteTransition(label = "characterAnimation")

                        // 1. Breathing Animation (Scale)
                        val breathScale by infiniteTransition.animateFloat(
                            initialValue = 1.00f,
                            targetValue = 1.02f, // Subtle breathing
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "breathing"
                        )

                        // 2. Twitching Animation (Rotation)
                        val twitchRotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = keyframes {
                                    durationMillis = 5000
                                    0f at 0
                                    0f at 4000      // Still for 4s
                                    0f at 4100      // Twitch right
                                    0f at 4200     // Twitch left
                                    0f at 4300      // Center
                                },
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "twitching"
                        )

                        // The Character Cutout (drawn ON TOP of the background)
                        Image(
                            painter = painterResource(id = R.drawable.magic_lab_bgcc),
                            contentDescription = "Animated Character",
                            modifier = Modifier
                                .size(imageWidthDp, imageHeightDp) // Matches background size exactly
                                .graphicsLayer {
                                    scaleX = breathScale
                                    scaleY = breathScale
                                    rotationZ = twitchRotation
                                }
                                .clip(RoundedCornerShape(24.dp)), // Matches background shape
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                // AMBIENT PRESENCE OVERLAY (Idle)
                // Plaatsing: boven beide images, onder de UI‑Column
                // LANTERN IDLE FIRE PULSE (organic, non-linear)
                val idleAlpha by rememberInfiniteTransition(label = "idleLantern")
                    .animateFloat(
                        initialValue = 0.12f,
                        targetValue = 0.52f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 11600,
                                easing = FastOutSlowInEasing   // ✅ fire-like breathing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "idleLanternAlpha"
                    )


                // === LANTERN LIGHT CONE (UPGRADED) ===
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = idleAlpha }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD8A8), // warm lantern core
                                    Color(0x88FFB35C), // soft glow falloff
                                    Color.Transparent
                                ),
                                center = Offset(310f, 580f), // ceiling-origin feel
                                radius = 80f               // cone spread instead of hotspot
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = idleAlpha }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD8A8), // warm lantern core
                                    Color(0x88FFB35C), // soft glow falloff
                                    Color.Transparent
                                ),
                                center = Offset(910f, 580f), // ceiling-origin feel
                                radius = 80f               // cone spread instead of hotspot
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = idleAlpha * 0.35f }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF2A00), // warm lantern core
                                    Color(0xFFFF2A00), // soft glow falloff
                                    Color.Transparent
                                ),
                                center = Offset(-60f, 850f), // ceiling-origin feel
                                radius = 310f               // cone spread instead of hotspot
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = idleAlpha * 0.35f }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF2A00), // warm lantern core
                                    Color(0xFFFF2A00), // soft glow falloff
                                    Color.Transparent
                                ),
                                center = Offset(1275f, 950f), // ceiling-origin feel
                                radius = 440f               // cone spread instead of hotspot
                            )
                        )
                )
                if (presence == com.example.myapplication0.viewmodel.PresenceState.Idle) {

                    val candleBase by rememberInfiniteTransition(label = "candleBase")
                        .animateFloat(
                            initialValue = 0.15f,
                            targetValue = 0.55f,
                            animationSpec = infiniteRepeatable(
                                animation = keyframes {
                                    durationMillis = 2200
                                    0.24f at 0
                                    0.48f at 300     // 🔥 fast flare
                                    0.72f at 600
                                    0.48f at 900       // slow decay
                                    0.24f at 1200    // secondary rise
                                    0.48f at 1500
                                    0.72f at 1800
                                    0.48f at 2200
                                },
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "candleBase"
                        )

                    // ✅ Micro chaotic shimmer (tiny high-frequency noise)
                    val candleShimmer by rememberInfiniteTransition(label = "candleShimmer")
                        .animateFloat(
                            initialValue = 0.98f,
                            targetValue = 1.12f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 600,
                                    easing = LinearEasing
                                ),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "candleShimmer"
                        )

                    // ✅ Vertical heat convection
                    val candleDrift by rememberInfiniteTransition(label = "candleDrift")
                        .animateFloat(
                            initialValue = 0f,
                            targetValue = -4.5f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 120,
                                    easing = LinearOutSlowInEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "candleDrift"
                        )

                    val finalAlpha = candleBase * candleShimmer

                    // ===============================
                    // 🔥 VIAL 1 — CANDLE FLAME
                    // ===============================
                    Box(
                        modifier = Modifier
                            .offset(x = 45.dp, y = 550.dp)
                            .graphicsLayer {
                                alpha = finalAlpha * 0.85f
                                translationY = candleDrift
                                scaleX =  0.16f   // uneven shape
                                scaleY =  0.56f
                            }
                            .size(width = 22.dp, height = 34.dp)
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF1A8),  // white hot core
                                        Color(0xFFFFB35C),  // flame yellow
                                        Color(0xFFFF6A00),  // deep orange
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // ===============================
                    // 🔥 VIAL 2 — CANDLE FLAME
                    // ===============================
                    Box(
                        modifier = Modifier
                            .offset(x = 350.dp, y = 535.dp)
                            .graphicsLayer {
                                alpha = finalAlpha * 0.85f   // variation per candle
                                translationY = candleDrift * 0.85f
                                scaleX = 0.16f
                                scaleY = 0.96f
                            }
                            .size(width = 26.dp, height = 30.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF1A8),
                                        Color(0xFFFFB35C),
                                        Color(0xFFFF6A00),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    // ===============================
                    // 🔥 VIAL 3 — CANDLE FLAME
                    // ===============================
                    Box(
                        modifier = Modifier
                            .offset(x = 19.dp, y = 549.dp)
                            .graphicsLayer {
                                alpha = finalAlpha * 0.55f
                                translationY = candleDrift
                                scaleX =  0.16f   // uneven shape
                                scaleY =  0.46f
                            }
                            .size(width = 22.dp, height = 14.dp)
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF1A8),  // white hot core
                                        Color(0xFFFFB35C),  // flame yellow
                                        Color(0xFFFF6A00),  // deep orange
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    // IDLE VIAL LIQUID GLOW (contained, no radial spill)
                    if (presence == com.example.myapplication0.viewmodel.PresenceState.Idle) {

                        val vialPulse by rememberInfiniteTransition(label = "vialPulse")
                            .animateFloat(
                                initialValue = 0.25f,
                                targetValue = 1.32f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 3200,
                                        easing = FastOutSlowInEasing   // ✅ organic liquid feel
                                    ),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "vialPulseAlpha"
                            )

                        // === VIAL 1 ===
                        Box(
                            modifier = Modifier
                                .offset(x = 35.dp, y = 545.dp)
                                .size(width = 40.dp, height = 65.dp)
                                .graphicsLayer {
                                    alpha = vialPulse * 0.30f
                                }
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x99FFD27D),   // bright liquid core
                                            Color(0xCCFF6A00),   // hot orange middle
                                            Color(0x88B11212),   // deep red bottom
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // === VIAL 2 ===
                        Box(
                            modifier = Modifier
                                .offset(x = 327.dp, y = 507.dp)
                                .size(width = 66.dp, height = 60.dp)
                                .graphicsLayer { alpha = vialPulse * 0.30f }
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x99FFD27D),
                                            Color(0xCCFF6A00),
                                            Color(0x88B11212),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        // === VIAL 3 ===
                        Box(
                            modifier = Modifier
                                .offset(x = 22.dp, y = 544.dp)
                                .size(width = 18.dp, height = 12.dp)
                                .graphicsLayer {
                                    alpha = vialPulse * 0.55f
                                }
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x99FFD27D),   // bright liquid core
                                            Color(0xCCFF6A00),   // hot orange middle
                                            Color(0x88B11212),   // deep red bottom
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        // === VIAL 4 ===
                        Box(
                            modifier = Modifier
                                .offset(x = -48.dp, y = 507.dp)
                                .size(width = 66.dp, height = 60.dp)
                                .graphicsLayer { alpha = vialPulse * 0.55f }
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x99FFD27D),
                                            Color(0xCCFF6A00),
                                            Color(0x88B11212),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                }

                // VOORGROND: inhoud met innerPadding zodat alleen UI meebeweegt met insets
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
            // Centrale uitlegkaart op basis van geselecteerde knop
            when (selectedInfo) {
                "counter" -> {
                    Card(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = "Click Counter → Les 1: State & recomposition — elke klik verhoogt de teller en triggert een recomposition.",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                "reset" -> {
                    Card(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = "Reset Invoer → Les 1: Local state & rememberSaveable — we resetten alleen de lokale invoer (prompt).",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                else -> { /* geen uitleg tonen */ }
            }

            // Lijst vult de resterende ruimte
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val s = state) {
                    is PhotoUiState.Loading ->
                        CircularProgressIndicator(Modifier.align(Alignment.Center))

                    is PhotoUiState.Error -> Text(
                        text = s.message,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )

                    is PhotoUiState.Success -> {
                        PhotoList(
                            photos = s.photos,
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }
    }
}
}

// COMPONENT: PhotoList
// Dit component toont een lijst van foto's.
// LazyColumn rendert alleen de zichtbare items → efficiënt voor lange lijsten.
@Composable
fun PhotoList(
    photos: List<Photo>,
    onItemClick: (Int) -> Unit
) {
    LazyColumn {
        items(
            items = photos,

            // Stable keys zorgen ervoor dat Compose goed kan bepalen
            // welk item gewijzigd is bij updates.
            key = { it.id }
        ) { photo ->
            PhotoItem(photo, onItemClick)
        }
    }
}

// COMPONENT: PhotoItem
// Eén enkel item in de lijst.
@Composable
fun PhotoItem(photo: Photo, onClick: (Int) -> Unit) {
    ListItem(
        headlineContent = {
            // Titeltekst met een maximum van 1 regel
            Text(photo.title, maxLines = 1)
        },

        leadingContent = {
            // Afbeelding wordt vanaf internet geladen met Coil
            AsyncImage(
                model = photo.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        },

        // clickable koppelt een klikactie aan het hele item
        modifier = Modifier.clickable { onClick(photo.id) }
    )
}

// SCREEN: DetailScreen
// Toont de details van één geselecteerde foto.
@Composable
fun DetailScreen(
    photoId: Int,            // ID afkomstig uit de navigatie
    viewModel: MainViewModel,
    onBack: () -> Unit       // Callback om terug te navigeren
) {
    val state = viewModel.uiState

    // We zoeken de geselecteerde foto op in de huidige succes-state
    val photo = (state as? PhotoUiState.Success)
        ?.photos
        ?.find { it.id == photoId }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Terug-knop die via navigatie teruggaat
        Button(onClick = onBack) {
            Text("Terug")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (photo != null) {

            Text(
                text = "Detail (ID: $photoId)",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = photo.url,
                contentDescription = photo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = photo.title,
                style = MaterialTheme.typography.bodyLarge
            )

        } else {
            // Wordt getoond als de data nog niet geladen is
            Text("Foto niet gevonden of data nog niet geladen.")
        }
    }
}

// SCREEN: AnswersScreen
// Toont alle eerder gegenereerde antwoorden met uniek ID en het idee (prompt).
@Composable
fun AnswersScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val answers = viewModel.answers

    // Gebruik een Scaffold om systeem-insets (status- en navigatiebalk) netjes af te handelen.
    // We voegen een TopAppBar en BottomAppBar toe met een zichtbare kleur, puur ter illustratie.
    // De "Terug"-knop blijft bewust in de content, niet in de header/footer.
    Scaffold(
        topBar = {
            // Geen experimentele API: eenvoudige Surface als visuele TopBar
            Surface(color = Color.Transparent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {}
            }
        },
        bottomBar = {
            // Geen experimentele API: eenvoudige Surface als visuele BottomBar
            Surface(color = Color.Transparent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {}
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // zorgt dat content onder top/bottombar en buiten systeem-insets valt
                .padding(16.dp)
        ) {

            // Terug-knop naar vorig scherm (blijft in de content)
            Button(onClick = onBack) { Text("Terug") }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Antwoord geschiedenis",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (answers.isEmpty()) {
                Text("Nog geen antwoorden opgeslagen.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(answers, key = { it.id }) { a ->
                        Card(shape = MaterialTheme.shapes.medium) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "ID: ${a.id}")
                                Spacer(Modifier.height(4.dp))
                                Text(text = "Idee: ${a.idea}")
                                Spacer(Modifier.height(4.dp))
                                Text(text = a.text)
                            }
                        }
                    }
                }
            }
        }
    }
}

// SCREEN: Lesson1Screen
// Doel: een simpele pagina voor Les 1 met een lokale Click Counter knop
// die hetzelfde gedrag demonstreert als de eerdere overflow-actie:
// - lokale rememberSaveable counter
// - bij klik counter++
// - optioneel een korte uitlegkaart die je aan/uit kunt toggelen door op de knop te klikken
@Composable
fun Lesson1Screen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAnswersClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLesson1Click: () -> Unit,
    onLesson2Click: () -> Unit,
    onLesson3Click: () -> Unit,
    onLesson4Click: () -> Unit
) {
    // Les 1: Click Counter is nu ge-hoist naar de ViewModel (SSOT)
    // zodat de waarde behouden blijft bij navigatie.
    // Eén gedeeld uitlegveld: welke uitleg is actief? "counter" | "color" | null
    var selectedInfo by rememberSaveable { mutableStateOf<String?>(null) }
    // Les 1: lokale appearance-state voor kleurwissel (Blauw ↔ Rood)
    // Gehoist naar ViewModel (SSOT) zodat de kleur en kliktelling behouden blijven.
    // Gebruik viewModel.lesson1IsBlue en viewModel.toggleLesson1Color().

    // Lokale UI‑state voor de chatbot in deze screen (zoals in ListScreen)
    // (resetTick verwijderd want prompt is nu SSOT in ViewModel)

    Scaffold(
        topBar = {
            // Neem de topbar mee (zelfde principe als ListScreen): titel als menu‑anchor + overflow
            Surface(
                color = Color.Transparent,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Titelmenu (alleen visueel/plaatsvervangend hier)
                    NavigationSegmentedButtons(
                        currentSelection = "Les 1",
                        onNavigate = { option ->
                            when (option) {
                                "Home" -> onHomeClick()
                                "Les 1" -> onLesson1Click()
                                "Les 2" -> onLesson2Click()
                                "Les 3" -> onLesson3Click()
                                "Les 4" -> onLesson4Click()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Overflow menu met lokale acties (geen navigatie hier, conform minimale wijziging)
                    var overflowOpen by rememberSaveable { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { overflowOpen = true }) {
                            Text("⋮", style = MaterialTheme.typography.titleLarge)
                        }
                        DropdownMenu(
                            expanded = overflowOpen,
                            onDismissRequest = { overflowOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (viewModel.isReplyHidden) "Toon antwoord" else "Verberg antwoord") },
                                onClick = {
                                    overflowOpen = false
                                    viewModel.toggleReplyVisibility()
                                }
                            )
                            // Geschiedenis (navigatie naar antwoorden-overzicht)
                            DropdownMenuItem(
                                text = { Text("Geschiedenis") },
                                onClick = {
                                    overflowOpen = false
                                    onAnswersClick()
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Dezelfde chatbot‑bottomBar als in ListScreen zodat je kunt blijven chatten
            Surface(color = Color.Transparent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Laatste reply boven de prompt
                    // Les 1–4: Fade in/out animatie (AnimatedVisibility)
                    val showReply = !viewModel.isReplyHidden && (viewModel.chatReply != null || viewModel.isThinking)
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            if (viewModel.isThinking) {
                                ThinkingIndicator()
                            } else {
                                viewModel.chatReply?.let { reply ->
                                    Text(text = reply, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }

                    // Prompt + verzendknop onderaan
                    // Prompt is nu SSOT in ViewModel
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viewModel.chatPrompt,
                            onValueChange = { viewModel.updatePrompt(it) },
                            label = { Text("Vraag aan EzChatbot") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { viewModel.sendPrompt("http://10.0.2.2:8080", viewModel.chatPrompt) }) {
                            Text("Ask")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // Inhoud van Les 1 blijft identiek, maar nu binnen de Scaffold en met back‑knop
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Terugknop (conform pattern)
            Button(onClick = onBack) { Text("Terug") }

            Text(text = "Les 1 — State & recomposition", style = MaterialTheme.typography.headlineSmall)

            // Actieknoppen voor Les 1 (onder elkaar of naast elkaar; we houden het compact)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 1) Click Counter
                Button(
                    onClick = {
                        viewModel.incrementLesson1Counter()
                        selectedInfo = "counter" // gedeelde uitleg updaten
                    }
                ) {
                    Text("Click Counter (${viewModel.lesson1Counter})")
                }

                // 2) Kleur wisselen (Blauw ↔ Rood)
                Button(
                    onClick = {
                        viewModel.toggleLesson1Color()
                        selectedInfo = "color" // gedeelde uitleg updaten
                    }
                ) {
                    Text("Kleur wisselen (${viewModel.lesson1ColorClicks})")
                }

                // 3) Reset invoer — verplaatst vanuit overflow naar Les 1
                Button(
                    onClick = {
                        viewModel.resetPrompt()
                        selectedInfo = "reset"
                    }
                ) {
                    Text("Reset invoer")
                }

                // 4) Verberg/Toon antwoord
                Button(
                    onClick = {
                        viewModel.toggleReplyVisibility()
                        selectedInfo = "toggle"
                    }
                ) {
                    Text(if (viewModel.isReplyHidden) "Toon antwoord" else "Verberg antwoord")
                }

                // 5) Single Source of Thruth (uitlegknop)
                Button(
                    onClick = {
                        selectedInfo = "ssot"
                    }
                ) {
                    Text("Single Source of Thruth")
                }
            }

            // Eén gedeeld uitlegveld op een vaste plek (voorkomt layout‑verschuivingen)
            if (selectedInfo != null) {
                Card(shape = MaterialTheme.shapes.medium) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (selectedInfo) {
                            "counter" -> Text(
                                text = "Click Counter → Les 1: lokale state (rememberSaveable) en recomposition: elke klik verandert de Int‑state en hertekent de UI."
                            )
                            "color" -> Text(
                                text = "Kleur wisselen → Les 1: appearance via state in de ViewModel (SSOT). Een Boolean bepaalt de kleur (Blauw ↔ Rood) en triggert recomposition."
                            )
                            "reset" -> Text(
                                text = "Reset Invoer → Les 1: Local state & rememberSaveable — we resetten alleen de lokale invoer (prompt) op dit scherm."
                            )
                            "toggle" -> Text(
                                text = "Verberg/Toon antwoord → Les 1–3: events → UI en SSOT. De zichtbaarheid wordt geregeld in de ViewModel."
                            )
                            "ssot" -> Text(
                                text = "Single Source of Truth → Les 1–4: we bewaren de Click Counter in de ViewModel. Zo blijft de waarde bestaan bij navigatie/rotatie, en is de ViewModel de bron waar de UI naar kijkt."
                            )
                        }

                        // Klein demovlakje dat de huidige kleur laat zien (Blauw = primaryContainer, Rood = errorContainer)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .padding(top = 4.dp)
                                .let { base ->
                                    // Gebruik een Box binnen een Card: we geven de achtergrondkleur met een Surface voor Material-kleuren
                                    base
                                }
                        ) {
                            Surface(color = if (viewModel.lesson1IsBlue) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer) {
                                Spacer(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }
}

// SCREEN: Lesson4Screen
// Doel: een pagina voor Les 4 (Navigatie) met structuur voor Geschiedenis.
@Composable
fun Lesson4Screen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAnswersClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLesson1Click: () -> Unit,
    onLesson2Click: () -> Unit,
    onLesson3Click: () -> Unit,
    onLesson4Click: () -> Unit
) {
    // Lokaal state-beheer voor uitleg
    var selectedInfo by rememberSaveable { mutableStateOf<String?>(null) }

    // Lokale state voor chatbot-input (resetTick verwijderd want prompt is nu SSOT in ViewModel)

    Scaffold(
        topBar = {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Titelmenu
                    NavigationSegmentedButtons(
                        currentSelection = "Les 4",
                        onNavigate = { option ->
                            when (option) {
                                "Home" -> onHomeClick()
                                "Les 1" -> onLesson1Click()
                                "Les 2" -> onLesson2Click()
                                "Les 3" -> onLesson3Click()
                                "Les 4" -> onLesson4Click()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Overflow menu
                    var overflowOpen by rememberSaveable { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { overflowOpen = true }) {
                            Text("⋮", style = MaterialTheme.typography.titleLarge)
                        }
                        DropdownMenu(
                            expanded = overflowOpen,
                            onDismissRequest = { overflowOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (viewModel.isReplyHidden) "Toon antwoord" else "Verberg antwoord") },
                                onClick = {
                                    overflowOpen = false
                                    viewModel.toggleReplyVisibility()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Geschiedenis") },
                                onClick = {
                                    overflowOpen = false
                                    // Navigeer direct, maar zet uitleg aan voor als we terugkomen
                                    selectedInfo = "history"
                                    onAnswersClick()
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(color = Color.Transparent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Les 1–4: Fade in/out animatie (AnimatedVisibility)
                    val showReply = !viewModel.isReplyHidden && (viewModel.chatReply != null || viewModel.isThinking)
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            if (viewModel.isThinking) {
                                ThinkingIndicator()
                            } else {
                                viewModel.chatReply?.let { reply ->
                                    Text(text = reply, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }
                    
                    // Prompt is nu SSOT in ViewModel
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viewModel.chatPrompt,
                            onValueChange = { viewModel.updatePrompt(it) },
                            label = { Text("Vraag aan EzChatbot") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { viewModel.sendPrompt("http://10.0.2.2:8080", viewModel.chatPrompt) }) {
                            Text("Ask")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onBack) { Text("Terug") }

            Text(text = "Les 4 — Navigatie", style = MaterialTheme.typography.headlineSmall)

            // Knop "Geschiedenis"
            Button(
                onClick = {
                    selectedInfo = "history"
                    // Navigatie gebeurt nu alleen via het overflow menu (Les 4 eis)
                }
            ) {
                Text("Geschiedenis")
            }

            // Uitlegkaart
            if (selectedInfo == "history") {
                Card(shape = MaterialTheme.shapes.medium) {
                    Text(
                        text = "Geschiedenis → Les 4: navigatie — opent een nieuw scherm met alle eerder opgeslagen antwoorden. De navigatie wordt geregeld via de NavController en routes.",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// SCREEN: Lesson2Screen
// Doel: een pagina voor Les 2.
@Composable
fun Lesson2Screen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAnswersClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLesson1Click: () -> Unit,
    onLesson3Click: () -> Unit,
    onLesson4Click: () -> Unit
) {
    // Lokaal state-beheer voor uitleg
    var selectedInfo by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Titelmenu
                    NavigationSegmentedButtons(
                        currentSelection = "Les 2",
                        onNavigate = { option ->
                            when (option) {
                                "Home" -> onHomeClick()
                                "Les 1" -> onLesson1Click()
                                "Les 2" -> { /* Already here */ }
                                "Les 3" -> onLesson3Click()
                                "Les 4" -> onLesson4Click()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Overflow menu
                    var overflowOpen by rememberSaveable { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { overflowOpen = true }) {
                            Text("⋮", style = MaterialTheme.typography.titleLarge)
                        }
                        DropdownMenu(
                            expanded = overflowOpen,
                            onDismissRequest = { overflowOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (viewModel.isReplyHidden) "Toon antwoord" else "Verberg antwoord") },
                                onClick = {
                                    overflowOpen = false
                                    viewModel.toggleReplyVisibility()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Geschiedenis") },
                                onClick = {
                                    overflowOpen = false
                                    selectedInfo = "history"
                                    onAnswersClick()
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(color = Color.Transparent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Les 1–4: Fade in/out animatie (AnimatedVisibility)
                    val showReply = !viewModel.isReplyHidden && (viewModel.chatReply != null || viewModel.isThinking)
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            if (viewModel.isThinking) {
                                ThinkingIndicator()
                            } else {
                                viewModel.chatReply?.let { reply ->
                                    Text(text = reply, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viewModel.chatPrompt,
                            onValueChange = { viewModel.updatePrompt(it) },
                            label = { Text("Vraag aan EzChatbot") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { viewModel.sendPrompt("http://10.0.2.2:8080", viewModel.chatPrompt) }) {
                            Text("Ask")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onBack) { Text("Terug") }
            Text(text = "Les 2 — Layouts", style = MaterialTheme.typography.headlineSmall)

            // Knoppen voor Les 2 (Layouts)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.setLesson2LayoutChoice("Column")
                        selectedInfo = "column"
                    }
                ) {
                    // SSOT: We tonen welke modus actief is vanuit de ViewModel
                    Text("Column (Verticaal)")
                }

                Button(
                    onClick = {
                        viewModel.setLesson2LayoutChoice("Row")
                        selectedInfo = "row"
                    }
                ) {
                    Text("Row (Horizontaal)")
                }
            }

            // Gedeelde uitlegkaart (alleen tonen als er een keuze is gemaakt)
            if (selectedInfo != null) {
                Card(shape = MaterialTheme.shapes.medium) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        when (selectedInfo) {
                            "column" -> Text("Column → Les 2: Layouts — plaatst elementen verticaal onder elkaar (y-as). Gebruik dit voor standaard schermen.")
                            "row" -> Text("Row → Les 2: Layouts — plaatst elementen horizontaal naast elkaar (x-as). Gebruik dit voor regels met iconen of knoppen.")
                        }

                        // Visuele demonstratie (balkje)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(top = 8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (viewModel.lesson2Layout == "Column") {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {}
                                            Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {}
                                            Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {}
                                        }
                                    } else {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {}
                                            Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {}
                                            Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// SCREEN: Lesson3Screen
// Doel: een pagina voor Les 3.
@Composable
fun Lesson3Screen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAnswersClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLesson1Click: () -> Unit,
    onLesson2Click: () -> Unit,
    onLesson4Click: () -> Unit
) {
    var selectedInfo by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NavigationSegmentedButtons(
                        currentSelection = "Les 3",
                        onNavigate = { option ->
                            when (option) {
                                "Home" -> onHomeClick()
                                "Les 1" -> onLesson1Click()
                                "Les 2" -> onLesson2Click()
                                "Les 3" -> { /* Already here */ }
                                "Les 4" -> onLesson4Click()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    var overflowOpen by rememberSaveable { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { overflowOpen = true }) {
                            Text("⋮", style = MaterialTheme.typography.titleLarge)
                        }
                        DropdownMenu(
                            expanded = overflowOpen,
                            onDismissRequest = { overflowOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (viewModel.isReplyHidden) "Toon antwoord" else "Verberg antwoord") },
                                onClick = {
                                    overflowOpen = false
                                    viewModel.toggleReplyVisibility()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Geschiedenis") },
                                onClick = {
                                    overflowOpen = false
                                    selectedInfo = "history"
                                    onAnswersClick()
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(color = Color.Transparent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Les 1–4: Fade in/out animatie (AnimatedVisibility)
                    val showReply = !viewModel.isReplyHidden && (viewModel.chatReply != null || viewModel.isThinking)
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            if (viewModel.isThinking) {
                                ThinkingIndicator()
                            } else {
                                viewModel.chatReply?.let { reply ->
                                    Text(text = reply, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viewModel.chatPrompt,
                            onValueChange = { viewModel.updatePrompt(it) },
                            label = { Text("Vraag aan EzChatbot") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { viewModel.sendPrompt("http://10.0.2.2:8080", viewModel.chatPrompt) }) {
                            Text("Ask")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onBack) { Text("Terug") }
            Text(text = "Les 3 — Lists", style = MaterialTheme.typography.headlineSmall)

            // Knoppen voor Les 3 (Lists)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.setLesson3ListTypeChoice("LazyColumn")
                        selectedInfo = "lazy"
                    }
                ) {
                    Text("LazyColumn")
                }

                Button(
                    onClick = {
                        viewModel.setLesson3ListTypeChoice("Scroll")
                        selectedInfo = "scroll"
                    }
                ) {
                    Text("Scroll Modifiers")
                }
            }

            // Gedeelde uitlegkaart (alleen tonen als er een keuze is gemaakt)
            if (selectedInfo != null) {
                Card(shape = MaterialTheme.shapes.medium) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        when (selectedInfo) {
                            "lazy" -> Text("LazyColumn → Les 3: Lists — rendert alleen items die op het scherm zichtbaar zijn. Essentieel voor lange lijsten (performance).")
                            "scroll" -> Text("Scroll Modifiers → Les 3: Lists — maakt een vast blok scrollbaar (bijv. met verticalScroll). Simpel, maar laadt alle content direct.")
                        }

                        // Visuele demonstratie (balkje)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp) // Iets hoger voor lijst-demo
                                .padding(top = 8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (viewModel.lesson3ListType == "LazyColumn") {
                                    // LazyColumn demo: toont items in een echte lazy list
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize().padding(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(20) { index ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().height(20.dp),
                                                color = MaterialTheme.colorScheme.tertiary,
                                                shape = MaterialTheme.shapes.extraSmall
                                            ) {
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                    Text(
                                                        text = "Lazy Item ${index + 1}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onTertiary,
                                                        modifier = Modifier.padding(start = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Scroll demo: toont items in een Column met verticalScroll
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(20) { index ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().height(20.dp),
                                                color = MaterialTheme.colorScheme.secondary,
                                                shape = MaterialTheme.shapes.extraSmall
                                            ) {
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                    Text(
                                                        text = "Scroll Item ${index + 1}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSecondary,
                                                        modifier = Modifier.padding(start = 4.dp)
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
            }
        }
    }
}

// COMPONENT: ThinkingIndicator (Les 1-4 Animation)
@Composable
fun ThinkingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Thinking")
        Spacer(modifier = Modifier.width(4.dp))

        val infiniteTransition = rememberInfiniteTransition(label = "dots")

        val dots = listOf(0, 1, 2)

        dots.forEach { index ->
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(300, easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 100)
                ),
                label = "dot$index"
            )

            Text(
                text = ".",
                modifier = Modifier.offset(y = offset.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
