package com.example.myapplication0.ui
// PACKAGE
// Dit bestand hoort bij de UI-laag van de app.
// Alles in deze package bevat uitsluitend schermen en visuele componenten.

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
// `clickable` maakt een UI-element aanklikbaar en koppelt er een actie aan.

import androidx.compose.foundation.layout.*
// Bevat layout-componenten zoals Column, Row, Box, Spacer en padding/fill-modifiers.

import androidx.compose.foundation.lazy.LazyColumn
// `LazyColumn` is een verticale, scrollbare lijst die alleen zichtbare items rendert.

import androidx.compose.foundation.lazy.items
// `items` wordt gebruikt om een lijst data te koppelen aan een LazyColumn.

import androidx.compose.animation.Crossfade
// Crossfade zorgt voor een zachte overgang tussen twee UI-staten (hier: achtergronden)

import androidx.compose.material3.*
// Material 3 UI-componenten zoals Text, Button, ListItem, Scaffold, etc.
import androidx.compose.animation.core.animateDpAsState

import androidx.compose.runtime.*
// Bevat Compose state-mechanismen zoals @Composable, remember en mutableStateOf.
import androidx.compose.runtime.saveable.rememberSaveable
// rememberSaveable bewaart state door heropbouw en configuratiewijzigingen heen.

import androidx.compose.ui.Alignment
// Wordt gebruikt om UI-elementen uit te lijnen (bijv. Center, Start, End).

import androidx.compose.ui.Modifier
// `Modifier` wordt gebruikt om gedrag en layout toe te voegen aan Composables.

import androidx.compose.ui.layout.ContentScale
// Bepaalt hoe een afbeelding wordt geschaald binnen zijn container.

// Vorm met afgeronde hoeken voor Cards en containers.
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// Basiskleurtype voor backgrounds/borders.

import androidx.compose.ui.unit.dp
// dp = density-independent pixels → standaard maatvoering voor layout.

import coil.compose.AsyncImage
// AsyncImage is een Coil-Composable voor het laden van afbeeldingen uit een URL.
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

import com.example.myapplication0.data.Photo
// Data class die één foto-object beschrijft.

import com.example.myapplication0.viewmodel.MainViewModel
// De ViewModel die de data en UI-state beheert.

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
// (De demonstratie-implementatie is tijdelijk verwijderd om de UI op te schonen,
//  maar de toelichting blijft aanwezig als blueprint voor Les 1.)

// SCREEN: ListScreen
// Dit is een volledig scherm (pagina) in de app.
// Het scherm ontvangt:
// - een ViewModel (voor data en state)
// - een callback voor navigatie bij klik op een item.
@Composable
fun ListScreen(
    viewModel: MainViewModel,
    onItemClick: (Int) -> Unit,
    onOpenAnswers: () -> Unit
) {
    // STATE OBSERVATION
    val state = viewModel.uiState

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            // Invoerbalk onderaan zodat de lijst vrij kan scrollen.
            ChatInputBar(
                onSend = { prompt ->
                    // Event (Les 3): klik → verstuur prompt (logica ongewijzigd)
                    viewModel.sendPrompt("http://10.0.2.2:8080", prompt)
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Achtergrondlaag
            val ctx = LocalContext.current
            val defaultBgResId = remember {
                ctx.resources.getIdentifier("npc_bg_default", "drawable", ctx.packageName)
            }.let { if (it == 0) null else it }

            CrossfadeBackground(resId = defaultBgResId, url = null, scrimAlpha = 0.40f)

            // CONTEXTKNOPPEN (Les 1–3 demo) + REPLYPANEEL
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
                var showExplanation by rememberSaveable { mutableStateOf<String?>(null) }
                var hideReply by rememberSaveable { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = {
                        ChatInputActions.resetInput()
                        showExplanation = "Les 1 – State: we resetten lokale UI‑state (TextField) met remember/rememberSaveable."
                    }) { Text("Reset invoer") }

                    OutlinedButton(onClick = {
                        hideReply = !hideReply
                        showExplanation = if (hideReply) {
                            "Les 1–3 – Events → UI: we tonen het antwoord opnieuw door lokale state terug te zetten."
                        } else {
                            "Les 1–3 – Events → UI: we verbergen het antwoord door lokale state te wijzigen."
                        }
                    }) { Text(if (hideReply) "Toon antwoord" else "Verberg antwoord") }

                    OutlinedButton(onClick = onOpenAnswers) { Text("Antwoorden") }
                }

                showExplanation?.let { msg ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                viewModel.chatReply?.takeIf { !hideReply }?.let { reply ->
                    Spacer(Modifier.height(12.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) { Text(text = reply) }
                    }
                }
                LaunchedEffect(viewModel.chatReply) { hideReply = false }

                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                when (val s = state) {
                    is PhotoUiState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is PhotoUiState.Error -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = s.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    is PhotoUiState.Success -> PhotoList(photos = s.photos, onItemClick = onItemClick)
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

// -----------------------------------------------------------------------------
// UI-HULPFUNCTIES (ALLEEN PRESENTATIE) — binnen scope Les 1–4
// -----------------------------------------------------------------------------

// Achtergrond weergeven met scrim; ondersteunt ofwel een resource ofwel een URL.
@Composable
private fun BackgroundWithScrim(
    resId: Int? = null,
    url: String? = null,
    scrimAlpha: Float = 0.35f
) {
    Box(Modifier.fillMaxSize()) {
        when {
            resId != null -> {
                // Resource-afbeelding tekenen uit res/drawable
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            url != null -> {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> { /* geen achtergrond */ }
        }

        // Scrim: semi-transparante laag voor leesbaarheid van de voorgrond
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = scrimAlpha))
        )
    }
}

// Zachte overgang tussen achtergronden.
@Composable
private fun CrossfadeBackground(
    resId: Int? = null,
    url: String? = null,
    scrimAlpha: Float = 0.35f
) {
    // Gebruik een Pair als key; we gebruiken de gedeconstrueerde waarden in de lambda
    // zodat de compiler-warning over een ongebruikte parameter verdwijnt.
    val key = resId to url
    Crossfade(targetState = key, label = "bgCrossfade") { (res, u) ->
        BackgroundWithScrim(resId = res, url = u, scrimAlpha = scrimAlpha)
    }
}

// -----------------------------------------------------------------------------
// SCREEN: AnswersScreen — overzicht van bewaarde chatbot-antwoorden
// Demonstreert:
// - Les 1: state hoisting → ViewModel bewaart de data (ids + teksten)
// - Les 2: lijstpresentatie met LazyColumn en stabiele keys
// - Les 3: events (terugknop) → UI-actie
// - Les 4: navigatie naar een apart scherm
// -----------------------------------------------------------------------------
@Composable
fun AnswersScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val answers = viewModel.answers

    Scaffold(
        topBar = {
            // Stable, non-experimental header (no TopAppBar API needed)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onBack) { Text("Terug") }
                    Text(
                        text = "Antwoorden",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(
                items = answers,
                key = { it.id }
            ) { ans ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "#${ans.id}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(text = ans.text)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Context: Les 1 (state hoisting in ViewModel) • Les 2 (list keys) • Les 3 (events→UI) • Les 4 (navigatie)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// Topbar die visueel past bij de achtergrond (transparant met lichte scrim)
@Composable
private fun AppTopBar() {
    // Stable replacement for TopAppBar to avoid ExperimentalMaterial3Api
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.15f))
            .padding(vertical = 10.dp)
    ) {
        // Intentionally minimal (no title text)
        Spacer(Modifier.height(0.dp))
    }
}

// Simpele invoerbalk onderaan; input‑state lokaal en resetbaar via ChatInputActions
private object ChatInputActions {
    // Shared callback om input te resetten zonder ViewModel te veranderen
    var resetInput: (() -> Unit)? = null
    fun resetInput() { resetInput?.invoke() }
}

@Composable
private fun ChatInputBar(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by rememberSaveable { mutableStateOf("") }

    // Registreer reset‑actie voor de demoknop
    DisposableEffect(Unit) {
        val cb: () -> Unit = { prompt = "" }
        ChatInputActions.resetInput = cb
        onDispose { if (ChatInputActions.resetInput === cb) ChatInputActions.resetInput = null }
    }

    // Standaard Material 3 invoerbalk (geen middeleeuwse styling)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        tonalElevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Vraag aan EzChatBot") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onSend(prompt) }) {
                Text("Ask EzChatBot")
            }
        }
    }
}

// Chatcontainer met subtiele middeleeuwse "kaart"-vibe (alleen styling)
@Composable
private fun MedievalChatCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(Modifier.padding(12.dp), content = content)
    }
}

// -----------------------------------------------------------------------------
// Stap 2: MedievalButton (Bronze Inset) — alleen styling, geen gedragswijziging
// -----------------------------------------------------------------------------
// UI‑component met een donker bronzen basis, gouden rand en subtiele
// hoogte/press‑animatie. Past binnen Les 1–3 (events → UI) en vervangt
// uitsluitend de visuele laag van bestaande knoppen.
@Composable
private fun MedievalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MedievalThemeValues.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(targetValue = if (pressed) 8.dp else 2.dp, label = "btnElev")

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = colors.darkBronze,
        border = BorderStroke(2.dp, colors.warmGold),
        shadowElevation = elevation,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = colors.parchment,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// MedievalChatBubble — V2 Parchment Card (alleen styling)
// -----------------------------------------------------------------------------
@Composable
private fun MedievalChatBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    val colors = MedievalThemeValues.colors
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, colors.warmGold.copy(alpha = 0.9f)),
        colors = CardDefaults.cardColors(
            containerColor = colors.parchment.copy(alpha = 0.94f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(text = text)
        }
    }
}

// -----------------------------------------------------------------------------
// GoldOrnateDivider — dunne goudkleurige scheidingslijn met lichte glans
// -----------------------------------------------------------------------------
@Composable
private fun GoldOrnateDivider(modifier: Modifier = Modifier) {
    val colors = MedievalThemeValues.colors
    val brush = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            colors.warmGold.copy(alpha = 0.85f),
            colors.warmGold,
            colors.warmGold.copy(alpha = 0.85f),
            Color.Transparent
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(brush = brush, shape = RoundedCornerShape(1.dp))
    )
}

// -----------------------------------------------------------------------------
// Stap 1: Lokale MedievalTheme (alleen beschikbaar maken, geen gedrag/visuals)
// -----------------------------------------------------------------------------
// Deze minimale themalaag gebruikt CompositionLocals om een palet en typografie
// beschikbaar te stellen aan onderliggende composables. In deze stap wijzigen we
// GEEN bestaande MaterialTheme-kleuren en passen we de waarden nergens toe, zodat
// er nul visuele/gedragswijzigingen zijn. In latere stappen kunnen nieuwe
// componenten (zoals MedievalChatInput/MedievalButton) deze waarden gebruiken.

// KLEURENPALET (middeleeuwse tinten)
data class MedievalColors(
    val inkBlack: Color,
    val deepBrown: Color,
    val parchment: Color,
    val warmGold: Color,
    val darkBronze: Color,
    val torchOrange: Color
)

// TYPOGRAFIE (optioneel, blijft hier neutraal zodat er geen zichtbare verandering is)
data class MedievalTypography(
    val body: TextStyle,
    val title: TextStyle
)

// CompositionLocals met veilige defaults
private val LocalMedievalColors = compositionLocalOf {
    MedievalColors(
        inkBlack = Color(0xFF0E0E10),
        deepBrown = Color(0xFF3A2B1E),
        parchment = Color(0xFFF1E5C6),
        warmGold = Color(0xFFC9A227),
        darkBronze = Color(0xFF5B4636),
        torchOrange = Color(0xFFE07A2D)
    )
}

private val LocalMedievalTypography = compositionLocalOf {
    MedievalTypography(
        body = TextStyle.Default.copy(fontFamily = FontFamily.Serif),
        title = TextStyle.Default.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold)
    )
}

@Composable
fun MedievalTheme(
    colors: MedievalColors = LocalMedievalColors.current,
    typography: MedievalTypography = LocalMedievalTypography.current,
    content: @Composable () -> Unit
) {
    // Alleen providers; geen MaterialTheme override. Hiermee zijn de waarden
    // beschikbaar voor toekomstige componenten zonder huidige UI te beïnvloeden.
    CompositionLocalProvider(
        LocalMedievalColors provides colors,
        LocalMedievalTypography provides typography
    ) {
        content()
    }
}

// Helpers om later eenvoudig bij de themewaarden te kunnen
object MedievalThemeValues {
    val colors: MedievalColors @Composable get() = LocalMedievalColors.current
    val typography: MedievalTypography @Composable get() = LocalMedievalTypography.current
}
