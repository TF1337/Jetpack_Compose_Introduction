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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import kotlin.math.hypot

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

                // Global radial gradient background (compose-only)
                // Colors from your example: #2BE4DC -> #243484 with stops [0f, 0.95f]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            // Use diagonal/2 so the gradient comfortably covers the screen
                            val radius = hypot(size.width, size.height) / 2f
                            drawRect(
                                brush = Brush.radialGradient(
                                    0f to Color(0xFF2BE4DC),
                                    0.95f to Color(0xFF243484),
                                    center = center,
                                    radius = radius
                                )
                            )
                        }
                ) {
                    // ROOT VAN DE COMPOSABLE-BOOM
                    // `AppNavigation` vormt het centrale startpunt van de UI.
                    // De Activity zelf bevat bewust GEEN schermlogica.
                    AppNavigation()
                }
            }
        }
    }
}
