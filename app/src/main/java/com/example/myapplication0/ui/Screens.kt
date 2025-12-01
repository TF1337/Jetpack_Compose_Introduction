package com.example.myapplication0.ui
// PACKAGE
// Dit bestand hoort bij de UI-laag van de app.
// Alles in deze package bevat uitsluitend schermen en visuele componenten.

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// `clickable` maakt een UI-element aanklikbaar en koppelt er een actie aan.

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
// Animatie-API's voor het in- en uitfaden van UI-elementen.

import androidx.compose.foundation.layout.*
// Bevat layout-componenten zoals Column, Row, Box, Spacer en padding/fill-modifiers.

import androidx.compose.foundation.lazy.LazyColumn
// `LazyColumn` is een verticale, scrollbare lijst die alleen zichtbare items rendert.

import androidx.compose.foundation.lazy.items
// `items` wordt gebruikt om een lijst data te koppelen aan een LazyColumn.

import androidx.compose.material3.*
// Material 3 UI-componenten zoals Text, Button, ListItem, Scaffold, etc.

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
// Bevat Compose state-mechanismen zoals @Composable, remember en mutableStateOf.

import androidx.compose.ui.Alignment
// Wordt gebruikt om UI-elementen uit te lijnen (bijv. Center, Start, End).

import androidx.compose.ui.Modifier
// `Modifier` wordt gebruikt om gedrag en layout toe te voegen aan Composables.

import androidx.compose.ui.layout.ContentScale
// Bepaalt hoe een afbeelding wordt geschaald binnen zijn container.

import androidx.compose.ui.unit.dp
// dp = density-independent pixels → standaard maatvoering voor layout.

import coil.compose.AsyncImage
// AsyncImage is een Coil-Composable voor het laden van afbeeldingen uit een URL.

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
    onLesson4Click: () -> Unit  // Nieuwe route voor Les 4 scherm
) {
    // STATE OBSERVATION
    // `uiState` is een Compose-observable waarde in de ViewModel.
    // Bij elke wijziging wordt dit scherm automatisch opnieuw opgebouwd.
    val state = viewModel.uiState

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

    // Scaffold is een standaard Material-layout met vaste plekken voor topBar, content en bottomBar.
    // We plaatsen nu de knoppen (met uitleg) in de topBar en de chatbot in de bottomBar.
    Scaffold(
        topBar = {
            // Vaste header met TopAppBar-principe: titel fungeert als "menu" (anchor voor dropdown)
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
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
                    var menuOpen by rememberSaveable { mutableStateOf(false) }

                    Box {
                        Text(
                            text = "Menu", // Titel fungeert als menu-trigger (voorheen "EzChat")
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.clickable { menuOpen = true }
                        )

                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            // Menu-items zonder functionaliteit (alle acties staan in het overflow-menu)
                            DropdownMenuItem(
                                text = { Text("Les 1") },
                                onClick = {
                                    menuOpen = false
                                    // Navigeer naar het nieuwe Les 1 scherm
                                    onLesson1Click()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Les 2") },
                                onClick = { menuOpen = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Les 3") },
                                onClick = { menuOpen = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Les 4") },
                                onClick = {
                                    menuOpen = false
                                    onLesson4Click()
                                }
                            )
                            // Let op: echte acties blijven in het overflow-menu (⋮), conform afspraak.
                        }
                    }

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
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
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
                    val showReply = !viewModel.isReplyHidden && viewModel.chatReply != null
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        viewModel.chatReply?.let { reply ->
                            OutlinedCard(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Text(text = reply, modifier = Modifier.padding(12.dp))
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
        // CONTENT: uitleg in het midden + fotolijst. Header/footer blijven vast staan.
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
            Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {}
            }
        },
        bottomBar = {
            // Geen experimentele API: eenvoudige Surface als visuele BottomBar
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
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
                color = MaterialTheme.colorScheme.primaryContainer,
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
                    var menuOpen by rememberSaveable { mutableStateOf(false) }
                    Box {
                        Text(
                            text = "Menu",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.clickable { menuOpen = true }
                        )
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            DropdownMenuItem(text = { Text("Les 1") }, onClick = { menuOpen = false })
                            DropdownMenuItem(text = { Text("Les 2") }, onClick = { menuOpen = false })
                            DropdownMenuItem(text = { Text("Les 3") }, onClick = { menuOpen = false })
                            DropdownMenuItem(text = { Text("Les 4") }, onClick = {
                                menuOpen = false
                                onLesson4Click()
                            })
                        }
                    }

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
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
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
                    val showReply = !viewModel.isReplyHidden && viewModel.chatReply != null
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        viewModel.chatReply?.let { reply ->
                            OutlinedCard(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Text(text = reply, modifier = Modifier.padding(12.dp))
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
                        else -> Text(
                            text = "Kies een actie (Click Counter of Kleur wisselen) om de uitleg te tonen."
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

// SCREEN: Lesson4Screen
// Doel: een pagina voor Les 4 (Navigatie) met structuur voor Geschiedenis.
@Composable
fun Lesson4Screen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAnswersClick: () -> Unit,
    onLesson1Click: () -> Unit
) {
    // Lokaal state-beheer voor uitleg
    var selectedInfo by rememberSaveable { mutableStateOf<String?>(null) }

    // Lokale state voor chatbot-input (resetTick verwijderd want prompt is nu SSOT in ViewModel)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
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
                    var menuOpen by rememberSaveable { mutableStateOf(false) }
                    Box {
                        Text(
                            text = "Menu",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.clickable { menuOpen = true }
                        )
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            DropdownMenuItem(text = { Text("Les 1") }, onClick = {
                                menuOpen = false
                                onLesson1Click()
                            })
                            DropdownMenuItem(text = { Text("Les 2") }, onClick = { menuOpen = false })
                            DropdownMenuItem(text = { Text("Les 3") }, onClick = { menuOpen = false })
                            DropdownMenuItem(text = { Text("Les 4") }, onClick = { menuOpen = false })
                        }
                    }

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
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Les 1–4: Fade in/out animatie (AnimatedVisibility)
                    val showReply = !viewModel.isReplyHidden && viewModel.chatReply != null
                    AnimatedVisibility(
                        visible = showReply,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        viewModel.chatReply?.let { reply ->
                            OutlinedCard(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Text(text = reply, modifier = Modifier.padding(12.dp))
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
            } else {
                Text("Klik op Geschiedenis om de navigatie te starten en uitleg te zien.")
            }
        }
    }
}
