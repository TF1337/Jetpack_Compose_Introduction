package com.example.myapplication0.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// FASE 1: UI STYLING - TYPOGRAFIE
//
// TECHNISCHE CONTEXT: Typography System
// Hier configureren we de Material Design 3 typografie-schaal.
//
// VRAAG: Waarom "sp" (Scale-independent Pixels) voor tekst?
// ANTWOORD: "sp" houdt rekening met de gebruikersinstellingen voor lettergrootte (Accessibility).
// Als een slechtziende gebruiker de tekst groter zet in Android instellingen, schaalt "sp" mee.
// "dp" (Density-independent Pixels) doet dat NIET en wordt gebruikt voor layout afmetingen (padding, knopgrootte).

val Typography = Typography(
    // Default stijl voor normale tekst (Body Large)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /*
    Andere stijlen (Title, Label, Headline) kunnen hier worden overschreven
    om een consistente huisstijl af te dwingen in de hele app.
    */
)
