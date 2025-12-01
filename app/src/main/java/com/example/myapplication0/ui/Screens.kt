package com.example.myapplication0.ui
// PACKAGE
// Dit bestand hoort bij de UI-laag van de app.
// Alles in deze package bevat uitsluitend schermen en visuele componenten.

import androidx.compose.foundation.clickable
// `clickable` maakt een UI-element aanklikbaar en koppelt er een actie aan.

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
    onAnswersClick: () -> Unit // Les 4: navigatie naar antwoorden-overzicht (state hoisted)
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
    var showCounterInfo by rememberSaveable { mutableStateOf(false) }
    var showResetInfo by rememberSaveable { mutableStateOf(false) }
    var showToggleReplyInfo by rememberSaveable { mutableStateOf(false) }
    var showAnswersInfo by rememberSaveable { mutableStateOf(false) }

    // Les 1–3: eenvoudige UI-event → UI-presentatie (verberg/toon antwoord)
    // We houden het bij lokale UI-state; de ViewModel blijft onaangetast.
    var hideReply by rememberSaveable { mutableStateOf(false) }

    // Scaffold is een standaard Material-layout met vaste plekken
    // voor content, snackbar, etc. TopBar is volledig verwijderd (eis 1).
    Scaffold { padding ->
        // Box wordt hier gebruikt als container voor de inhoud
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Eenvoudige chat-sectie + didactische knoppen bovenaan het scherm
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1) Click Counter (Les 1)
                Button(
                    onClick = {
                        counter++                // State & recomposition (Les 1)
                        showCounterInfo = !showCounterInfo // Toon/verberg uitleg
                    }
                ) { Text("Click Counter: $counter") }

                if (showCounterInfo) {
                    // Spraakbubble met uitleg
                    Card(shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = "Les 1: State & recomposition — elke klik verhoogt de teller en triggert een recomposition.",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 2) Reset invoer (maakt alleen de lokale prompt leeg)
                Button(
                    onClick = {
                        // Wordt hieronder op de daadwerkelijke prompt toegepast
                        showResetInfo = !showResetInfo
                    }
                ) { Text("Reset invoer") }

                if (showResetInfo) {
                    Card(shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = "Les 1: Local state & rememberSaveable — we resetten alleen de lokale invoer.",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 3) Verberg/Toon antwoord (UI-event → UI-presentatie)
                Button(
                    onClick = {
                        hideReply = !hideReply
                        showToggleReplyInfo = !showToggleReplyInfo
                    }
                ) { Text(if (hideReply) "Toon antwoord" else "Verberg antwoord") }

                if (showToggleReplyInfo) {
                    Card(shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = "Les 1–3: Events → UI — een klik toggelt lokale UI-state die bepaalt of het antwoord zichtbaar is.",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 4) Antwoorden
                // Navigatie naar een apart scherm waar je alle opgeslagen antwoorden ziet (met ID & idee)
                Button(onClick = onAnswersClick) { Text("Antwoorden") }
                // Didactische toelichting blijft beschikbaar via toggle, maar we navigeren direct met de knop.
                if (showAnswersInfo) {
                    Card(shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = "Les 4: Navigatie — 'Antwoorden' opent een nieuw scherm met alle eerder opgeslagen antwoorden (met ID & idee).",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                var prompt by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Vraag aan EzChatbot") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                // Reset invoer actie (koppelen aan de knop hierboven)
                LaunchedEffect(showResetInfo) {
                    // Als de gebruiker op "Reset invoer" klikt, maken we de prompt leeg.
                    // We koppelen dit via LaunchedEffect om de logica in dezelfde scope te houden
                    // zonder extra state-variabelen aan te maken.
                    if (showResetInfo) {
                        prompt = ""
                    }
                }

                Button(onClick = { viewModel.sendPrompt("http://10.0.2.2:8080", prompt) }) {
                    Text("Ask EzChatBot")
                }
                // Toon de laatste reply (of foutmelding)
                viewModel.chatReply?.let { reply ->
                    Spacer(Modifier.height(8.dp))
                    if (!hideReply) {
                        Text(text = reply)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // when is een Kotlin controle-structuur.
            // Hiermee bepalen we welk UI-scherm we tonen op basis van de state.
            when (val s = state) {

                // Tijdens laden tonen we een draaipictogram
                is PhotoUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                // Bij een fout tonen we de foutmelding in de foutkleur
                is PhotoUiState.Error -> Text(
                    text = s.message,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )

                // Bij succes tonen we de lijst met foto's
                is PhotoUiState.Success -> {
                    // Voeg extra top-padding toe zodat de lijst niet onder de didactische sectie valt
                    // Let op: deze waarde is puur presentational en kan afhankelijk van de zichtbare
                    // spraakbubbles variëren. Voor de les houden we het eenvoudig en statisch.
                    Column(modifier = Modifier.fillMaxSize().padding(top = 240.dp)) {
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Terug-knop naar vorig scherm
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
