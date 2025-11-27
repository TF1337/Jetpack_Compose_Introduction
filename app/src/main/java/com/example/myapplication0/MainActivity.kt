package com.example.myapplication0

// PACKAGE
// Een `package` groepeert bestanden logisch binnen een project.
// Dit voorkomt naamconflicten en maakt de projectstructuur overzichtelijk.
// Elke Kotlin-file hoort precies bij één package.

import android.os.Bundle
// Import van de Android-klasse `Bundle`.
// Deze wordt gebruikt om tijdelijke data op te slaan bij lifecycle-events
// (bijv. bij rotatie van het scherm).

import androidx.activity.ComponentActivity
// `ComponentActivity` is een basis Activity-klasse van AndroidX.
// Deze bevat lifecycle-ondersteuning en is geschikt als basis voor Compose.

import androidx.activity.compose.setContent
// `setContent` is de brug tussen de traditionele Android Activity
// en de declaratieve Jetpack Compose UI.
// Dit vervangt het oude `setContentView(R.layout...)`.

import androidx.activity.enableEdgeToEdge
// Deze functie zorgt ervoor dat de UI onder de systeem-balken mag tekenen
// (statusbar en navigatiebalk). Dit hoort bij moderne Android UI-gedrag.

import com.example.myapplication0.navigation.AppNavigation
import com.example.myapplication0.ui.theme.MyApplication0Theme

// FASE 1: UI BASIS & ANDROID FUNDAMENTALS
//
// TECHNISCHE CONTEXT: Entry Point & Overerving
// `MainActivity` is het startpunt van de Android-app.
// Het Android-systeem start altijd met een Activity die in het Manifest staat gedefinieerd.
//
// Door `:` te gebruiken erven we van `ComponentActivity`.
// Dit betekent dat `MainActivity` automatisch standaard Android-gedrag krijgt
// zoals lifecycle-afhandeling (onCreate, onStart, onStop, etc.).
class MainActivity : ComponentActivity() {

    // LIFECYCLE METHODE: onCreate
    // `onCreate` is een lifecycle-functie die altijd wordt aangeroepen
    // wanneer de Activity voor het eerst wordt aangemaakt.
    //
    // `override` is een Kotlin-keyword waarmee je expliciet aangeeft
    // dat je een bestaande functie uit de superklasse (`ComponentActivity`)
    // overschrijft met je eigen implementatie.
    override fun onCreate(savedInstanceState: Bundle?) {

        // `super.onCreate(...)` roept eerst de oorspronkelijke implementatie aan.
        // Dit is verplicht zodat Android de Activity correct kan initialiseren.
        super.onCreate(savedInstanceState)

        // Activeert edge-to-edge rendering:
        // De inhoud mag tot onder de statusbalk en navigatiebalk tekenen.
        // Dit is puur UI-gedrag en heeft geen invloed op logica of data.
        enableEdgeToEdge()

        // COMPOSE KOPPELING AAN DE ACTIVITY
        // Alles binnen `setContent {}` behoort tot de Jetpack Compose UI.
        // Vanaf dit punt wordt de UI declaratief opgebouwd met Composables.
        // Dit vervangt volledig het oude XML-layoutsysteem.
        setContent {

            // THEMA WRAPPER
            // `MyApplication0Theme` stelt kleuren, typografie en stijlen in
            // voor alle onderliggende Composables.
            // Hierdoor is de hele app visueel consistent.
            MyApplication0Theme {

                // ROOT VAN DE COMPOSABLE-BOOM
                // `AppNavigation` vormt het centrale startpunt van de UI.
                // Hier wordt bepaald welk scherm zichtbaar is (lijst of detail).
                // De Activity zelf bevat bewust GEEN schermlogica.
                AppNavigation()
            }
        }
    }
}
