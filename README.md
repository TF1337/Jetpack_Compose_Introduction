# Android Basics Leer-App

Dit project is ontwikkeld als een interactief leermiddel voor de cursus "Android Basics with Compose". De codebasis is voorzien van uitgebreide commentaren (notaties) die precies aangeven welke lesstof en fase van de ontwikkeling in elk bestand wordt toegepast.

## 1. Project Opdracht (Prompt)

De opdracht voor dit project was om een Android applicatie te bouwen die dient als kapstok voor de stof uit Units 1 t/m 5 van de Google Android cursus. Het doel is niet alleen een werkende app, maar een referentiekader waarin duidelijk is **waarom** code op een bepaalde plek staat en **welk leerdoel** ermee wordt behaald.

De app bevat functionaliteiten zoals UI-opbouw, State Hoisting, LazyColumn lijsten, MVVM-architectuur, Navigatie en Netwerkverzoeken (API & Images).

## 2. Het Stappenplan & Huiswerk

Dit project is gebouwd op basis van het onderstaande stappenplan, aangeleverd door de student/gebruiker.

### A — Stappenplan om alle lesstof chronologisch te leren en te oefenen

**(Jij → volgt deze stappen in deze volgorde om alle leerstof te begrijpen.)**

**Stap 1 — Basis Jetpack Compose (Les 1 / Unit 1)**
*   Lees Unit 1 van Android Basics with Compose.
*   Installeer Android Studio + Emulator.
*   Maak de eerste eenvoudige Compose-app (Pathway 3).
*   Begrijp:
    *   Composable functies
    *   Stateless vs stateful composables
    *   remember {} + mutableStateOf
    *   Modifiers en volgorde van modifiers
*   Bestudeer het BMI-Calculator voorbeeld.
*   Ervaar State Hoisting met simpele voorbeelden.

**Stap 2 — Lists & State Herhaling (Les 2 / Unit 2)**
*   Lees Unit 2 van Android Basics.
*   Bestudeer LazyColumn en maak er 1 of 2 kleine opdrachten mee.
*   Doorloop de Tip Calculator (oefent State Hoisting).
*   Bestudeer het BasicStateCodelab.
*   Begrijp:
    *   Lijsten in Compose
    *   Keys
    *   State opnieuw: state vs hoisting
*   Oefen met meerdere items in LazyColumn.

**Stap 3 — Architectuur met ViewModel + Navigation (Les 3 / Unit 3)**
*   Lees Unit 3.
*   Begrijp het principe van MVVM.
*   Maak een ViewModel met simpele state.
*   Bekijk Navigation 3 documentatie.
*   Implementeer navigatie tussen 2 schermen (bijv. Home → Detail).
*   Doorloop Navigation Codelab.

**Stap 4 — Networking met Ktor Client + Coil (Les 4 / Unit 4)**
*   Lees Unit 4.
*   Begrijp Ktor Client basis (GET requests, model mapping).
*   Bestudeer fsaSimpleRick voorbeeld (Ktor Client in praktijk).
*   Begrijp Coil (images laden).
*   Bekijk Retrofit-demo en begrijp de verschillen.
*   Bouw een simpele API-call in jouw eigen project.

**Stap 5 — Verdere verdieping (Units 4–5)**
*   Rond Unit 4 af om architectuur verder te versterken.
*   Lees Unit 5 als vervolgonderdeel.
*   Verbind UI, ViewModel, Repository, API en Navigatie in één kleine app.

---

### ✅ B — Stappenplan om een Android app te bouwen volgens alle lesonderdelen

**(App bouwen van nul → gebaseerd op jouw volledige samenvatting.)**

**Fase 1 — UI Basis (Les 1)**
*   Maak een composables-bestand.
*   Bouw een simpele layout met Column + Text + Button.
*   Voeg state toe met remember { mutableStateOf(...) }.
*   Hoist de state:
    *   Verplaats state naar parent
    *   Gebruik onValueChanged lambdas.
*   Pas Material components toe.
*   Gebruik correcte DP/SP waarden (veelvoud 4).

**Fase 2 — Lijsten en Interactie (Les 2)**
*   Maak een dataklasse voor lijst-items.
*   Creëer LazyColumn die de lijst renderert.
*   Maak een item composable (stateless).
*   Gebruik state hoisting om item-interacties op te vangen.
*   Voeg eenvoudige UI-logica toe (bijv. “open detail” in volgende fase).

**Fase 3 — MVVM + ViewModel (Les 3)**
*   Maak een ViewModel.
*   Zet lijst- of schermstate in ViewModel (StateFlow of mutableStateOf).
*   UI observeert state.
*   Verplaats businesslogica naar ViewModel.
*   Zorg dat UI volledig stateless wordt.

**Fase 4 — Navigatie (Les 3)**
*   Voeg Navigation 3 dependency toe.
*   Maak een NavGraph met 2–3 routes.
*   Maak per route een eigen scherm composable.
*   Pas route parameters toe (bijv. id).
*   Navigatie gebeurt vanuit UI via NavController.

**Fase 5 — Networking (Les 4)**
*   Maak een Repository-laag (hier vereenvoudigd in DataModule).
*   Configureer Ktor Client.
*   Voeg een GET-request toe.
*   Munte data om naar dataklasse.
*   Vraag data op in ViewModel.
*   UI observeert async state (loading, success, error).
*   Toon images via Coil.

**Fase 6 — Eindstructuur**
Project bevat dan:
*   `ui/`
*   `ui/components/`
*   `ui/screens/`
*   `navigation/`
*   `data/api/`
*   `data/repository/`
*   `viewmodel/`

Je hebt dan één complete Compose-app volgens alle lessen.

## 3. Uitleg van de Code & Notaties

In de codebestanden zijn comments toegevoegd die verwijzen naar bovenstaande stappen en fases. Dit helpt je te navigeren door de theorie in de praktijk.

*   **`MainActivity.kt`**
    *   *Fase 1*: Het startpunt van de app.
*   **`data/Photo.kt`**
    *   *Fase 2 & 5*: Het datamodel (het "paspoort" van de data).
*   **`data/DataModule.kt`**
    *   *Fase 5*: De netwerklaag (Ktor Client) voor communicatie met de API.
*   **`viewmodel/MainViewModel.kt`**
    *   *Fase 3*: De ViewModel (het "brein") dat de UI State beheert.
*   **`navigation/AppNavigation.kt`**
    *   *Fase 4*: De NavHost en NavController configuratie.
*   **`ui/Screens.kt`**
    *   *Fase 1*: Basis UI componenten (`Lesson1Header`).
    *   *Fase 2 & 3*: De lijst weergave (`ListScreen`, `PhotoList`).
    *   *Fase 4 & 5*: Het detail scherm (`DetailScreen`) met parameters en afbeeldingen.
