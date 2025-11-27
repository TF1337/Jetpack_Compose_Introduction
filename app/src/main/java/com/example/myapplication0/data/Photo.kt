package com.example.myapplication0.data

import kotlinx.serialization.Serializable

// FASE 2: DATA MODELLERING
//
// TECHNISCHE CONTEXT: Data Transfer Object (DTO)
// Dit is de blauwdruk van de data die we van de API ontvangen.
// In Clean Architecture fungeert dit vaak als de "Entity" of het "Remote Model".

// ANNOTATION: @Serializable
// Trigger voor de Kotlinx Serialization compiler plugin.
// Dit genereert tijdens compile-time automatisch de `Serializer` implementatie
// die nodig is voor JSON deserialisatie (JSON -> Kotlin Object).
@Serializable
data class Photo(
    // IMMUTABILITY
    // We gebruiken `val` (read-only) properties. Dit garandeert dat data-objecten
    // onveranderlijk zijn (Immutable), wat essentieel is voor thread-safety en voorspelbare state.
    
    val id: Int,            // Unieke identifier (Primary Key in DB termen)
    val title: String,      // Payload data
    val url: String,        // Remote resource URL (High-res)
    val thumbnailUrl: String // Remote resource URL (Low-res optimization)
)
// Generated methods:
// Omdat dit een `data class` is, genereert Kotlin automatisch:
// - equals() / hashCode() (voor vergelijking in lijsten/DiffUtil)
// - toString() (voor logging/debugging)
// - copy() (voor het maken van gemuteerde kopieën van onveranderlijke objecten)
