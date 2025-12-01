package com.example.myapplication0.viewmodel
// PACKAGE
// Dit bestand hoort bij de ViewModel-laag van de app.
// Alles in deze package bevat de logica die de UI aanstuurt,
// maar zelf GEEN UI tekent.

import androidx.compose.runtime.getValue
// Leest de waarde van een Compose-state property via Kotlin-delegatie (`by`).

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
// Maakt een waarneembare (observable) waarde voor Jetpack Compose.

import androidx.compose.runtime.setValue
// Maakt het mogelijk om een nieuwe waarde toe te wijzen aan een delegated state (`by`).

import androidx.lifecycle.ViewModel
// Basis ViewModel-klasse van Android.
// Deze blijft bestaan bij schermrotaties (configuratieveranderingen).

import androidx.lifecycle.viewModelScope
// Een CoroutineScope die automatisch stopt wanneer de ViewModel wordt opgeruimd.

import com.example.myapplication0.data.Photo
// Data class die één foto-object voorstelt.

import com.example.myapplication0.data.fetchPhotos
// Functie die via Ktor foto's ophaalt van het internet.
import com.example.myapplication0.data.askBackend
// Functie die een prompt naar het backend stuurt en een antwoord ontvangt.

import kotlinx.coroutines.launch
// `launch` start een coroutine (achtergrondtaak).

// Feature flag om de "andere API" (foto's) tijdelijk uit te schakelen.
// Zet op `true` om opnieuw te laden; op `false` om te skippen en een lege lijst te tonen.
private const val PHOTOS_ENABLED = false

// FASE 3: MVVM ARCHITECTUUR & STATE MANAGEMENT
//
// TECHNISCHE CONTEXT: ViewModel Pattern
// De ViewModel is de "tussenlaag" tussen:
// - de UI (Screens.kt)
// - en de data (fetchPhotos)
//
// De ViewModel:
// ✔ bewaart de UI-state
// ✔ start netwerkacties
// ✔ bevat GEEN UI-code (zoals Text, Button, etc.)

// UI STATE MODEL
// Met een `sealed interface` geven we een vaste set van mogelijke toestanden.
// Dit dwingt af dat de UI altijd ALLE situaties afhandelt.
sealed interface PhotoUiState {

    // Wordt gebruikt zolang de data wordt opgehaald
    object Loading : PhotoUiState

    // Wordt gebruikt zodra de data succesvol is binnengekomen
    data class Success(val photos: List<Photo>) : PhotoUiState

    // Wordt gebruikt als er iets misgaat bij het ophalen van de data
    data class Error(val message: String) : PhotoUiState
}

// VIEWMODEL KLASSE
// Deze klasse bevat de toestand (state) en logica van de app.
class MainViewModel : ViewModel() {

    // UI STATE
    // `mutableStateOf` maakt van `uiState` een observable waarde.
    // Elke wijziging hier zorgt ervoor dat de UI automatisch opnieuw wordt opgebouwd.
    //
    // `by` is Kotlin delegatie-syntax:
    // hiermee hoef je geen `.value` te schrijven.

    // TOEKOMST (bij eigen API + Repository-laag):
    // Deze uiState zal later gevoed worden door een StateFlow uit de repository.
    // Nu gebruiken we bewust mutableStateOf voor directe Compose-observatie.
    // -------------------------------------------------------------------------
    // VOORBEELD IMPLEMENTATIE MET STATEFLOW (voor later):
    //
    // 1. Zorg voor de juiste imports:
    // import kotlinx.coroutines.flow.MutableStateFlow
    // import kotlinx.coroutines.flow.StateFlow
    // import kotlinx.coroutines.flow.asStateFlow
    //
    // 2. Vervang de huidige 'var uiState' declaratie door deze twee regels:
    // private val _uiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    // val uiState: StateFlow<PhotoUiState> = _uiState.asStateFlow()
    //
    // 3. Pas in de functie 'loadPhotos()' de toewijzingen aan:
    // In plaats van: uiState = PhotoUiState.Success(...)
    // Gebruik je:    _uiState.value = PhotoUiState.Success(...)
    // -------------------------------------------------------------------------
    var uiState: PhotoUiState by mutableStateOf(PhotoUiState.Loading)
        private set
        // `private set` betekent:
        // - buiten deze ViewModel mag de state alleen gelezen worden
        // - alleen de ViewModel zelf mag de state aanpassen
        // Dit is essentieel voor "Single Source of Truth".

    // INIT BLOK
    // Deze code wordt automatisch uitgevoerd zodra de ViewModel wordt aangemaakt.
    init {
        if (PHOTOS_ENABLED) {
            loadPhotos()
        } else {
            // Direct een lege lijst aanbieden om netwerk + rendering kosten te vermijden
            uiState = PhotoUiState.Success(emptyList())
        }
    }

    // -------------------------------------------------------------
    // CHAT: SIMPELE STATE + ACTIE (MINIMALE INTEGRATIE)
    // -------------------------------------------------------------
    // We tonen alleen de tekstuele reply. `audioPath` wordt genegeerd.
    var chatReply: String? by mutableStateOf(null)
        private set

    // Antwoord-geschiedenis (Les 1–4: Single Source of Truth in ViewModel)
    // We slaan elk gegenereerd antwoord op met een uniek ID en het bijbehorende idee (de prompt).
    data class Answer(
        val id: Int,
        val idea: String,   // Het ingevoerde idee/prompt van de gebruiker
        val text: String,   // Het antwoord van de backend
        val timestamp: Long // Voor sortering/weergave (optioneel)
    )

    var answers: List<Answer> by mutableStateOf(emptyList())
        private set

    private var nextAnswerId: Int = 1

    // VOORBEELD AANROEP:
    // viewModel.sendPrompt("http://10.0.2.2:8080", prompt)
    fun sendPrompt(baseUrl: String, prompt: String) {
        viewModelScope.launch {
            try {
                // Auto-open the reply area so the user sees "Thinking..."
                isReplyHidden = false
                isThinking = true
                chatReply = null
                val resp = askBackend(baseUrl, prompt)
                chatReply = resp.reply

                // Sla het antwoord op in de ViewModel-geschiedenis
                val answer = Answer(
                    id = nextAnswerId++,
                    idea = prompt,
                    text = resp.reply,
                    timestamp = System.currentTimeMillis()
                )
                answers = answers + answer
            } catch (e: Exception) {
                chatReply = "Fout: ${e.message}"
            } finally {
                isThinking = false
            }
        }
    }

    // -------------------------------------------------------------
    // LES 1–3: SSOT voor zichtbaarheid van het chatbot-antwoord
    // -------------------------------------------------------------
    // Voorheen was hideReply lokale UI-state per scherm. Daardoor ging de
    // gekozen zichtbaarheid verloren bij navigatie. Als Single Source of Truth
    // in de ViewModel blijft de keuze behouden tussen schermen.
    var isReplyHidden: Boolean by mutableStateOf(false)
        private set

    // LOADING STATE (Thinking...)
    // Zorgt dat de UI weet dat de chatbot bezig is.
    var isThinking: Boolean by mutableStateOf(false)
        private set

    fun toggleReplyVisibility() {
        isReplyHidden = !isReplyHidden
    }

    fun showReply() {
        isReplyHidden = false
    }

    fun hideReply() {
        isReplyHidden = true
    }

    // -------------------------------------------------------------
    // LES 1 & 4: SSOT voor de chat prompt (Persistentie)
    // -------------------------------------------------------------
    // De gebruiker wil dat de ingevoerde tekst behouden blijft bij navigatie
    // tussen schermen (Les 1, Les 4, Main). Daarom verplaatsen we de prompt
    // van lokale state (rememberSaveable) naar de ViewModel.
    var chatPrompt: String by mutableStateOf("")
        // Setter mag public zijn voor Two-Way binding in TextFields,
        // maar we gebruiken functies voor consistentie als je dat liever hebt.
        // Voor textfields is een var met public set vaak handigst in Compose,
        // maar om het patroon te volgen:
        private set

    fun updatePrompt(newText: String) {
        chatPrompt = newText
    }

    fun resetPrompt() {
        chatPrompt = ""
    }

    // -------------------------------------------------------------
    // LES 1: STATE HOISTING VOOR LESSON1 CLICK COUNTER (SSOT)
    // -------------------------------------------------------------
    // We bewaren de teller in de ViewModel zodat deze blijft bestaan
    // bij navigatie en rotatie. Dit is de Single Source of Truth.
    var lesson1Counter: Int by mutableIntStateOf(0)
        private set

    fun incrementLesson1Counter() {
        lesson1Counter++
    }

    // -------------------------------------------------------------
    // LES 1: STATE HOISTING VOOR LESSON1 KLEUR WISSELEN (SSOT)
    // -------------------------------------------------------------
    // We bewaren ook de kleur (Blauw ↔ Rood) en het aantal wissels in de ViewModel
    // zodat deze waarden behouden blijven bij navigatie en rotatie.
    var lesson1IsBlue: Boolean by mutableStateOf(true)
        private set

    var lesson1ColorClicks: Int by mutableIntStateOf(0)
        private set

    fun toggleLesson1Color() {
        lesson1IsBlue = !lesson1IsBlue
        lesson1ColorClicks++
    }

    // -------------------------------------------------------------
    // LES 2: LAYOUT SELECTIE (SSOT)
    // -------------------------------------------------------------
    // Bewaar de gekozen layout-modus (Column/Row) in de ViewModel.
    var lesson2Layout: String by mutableStateOf("Column")
        private set

    fun setLesson2LayoutChoice(layout: String) {
        lesson2Layout = layout
    }

    // -------------------------------------------------------------
    // LES 3: LIST TYPE SELECTIE (SSOT)
    // -------------------------------------------------------------
    // Bewaar de gekozen lijst-modus (Lazy/Scroll) in de ViewModel.
    var lesson3ListType: String by mutableStateOf("LazyColumn")
        private set

    fun setLesson3ListTypeChoice(type: String) {
        lesson3ListType = type
    }

    // FUNCTIE: loadPhotos
    // Deze functie start het ophalen van data van het internet.
    fun loadPhotos() {

        // viewModelScope.launch start een coroutine:
        // - de code draait niet op de main thread
        // - de UI blijft daardoor vloeiend
        // - de coroutine wordt automatisch gestopt wanneer de ViewModel verdwijnt
        viewModelScope.launch {

            // Zet de UI eerst expliciet in de Loading-status
            uiState = PhotoUiState.Loading

            try {
                // `fetchPhotos` is een suspend-functie.
                // De uitvoering wordt hier tijdelijk gepauzeerd,
                // zonder dat de app vastloopt.
                val photos = fetchPhotos()

                // We beperken de lijst tot 20 items
                // zodat de lijst overzichtelijk blijft voor de demo.
                uiState = PhotoUiState.Success(photos.take(20))

            } catch (e: Exception) {
                // Als er iets fout gaat (bijv. geen internet),
                // zetten we de UI in de Error-status.
                uiState = PhotoUiState.Error("Fout: ${e.message}")
            }
        }
    }
}
