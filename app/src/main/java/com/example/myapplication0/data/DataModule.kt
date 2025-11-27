package com.example.myapplication0.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// FASE 5: NETWORKING LAAG
//
// TECHNISCHE CONTEXT: Ktor Client Configuration
// We configureren hier een singleton HTTP client instance.
// Ktor is een multiplatform asynchrone framework, gebouwd op coroutines.

// EXTRA (Advanced - Niet voor tentamen): COMPONENT: HttpClient & CIO Engine
// Engine: CIO (Coroutine-based I/O). Dit is een volledig Kotlin-gebaseerde engine
// die gebruik maakt van non-blocking I/O operations. Zeer efficiënt voor concurrent requests.
val client = HttpClient(CIO) {

    // EXTRA (Advanced - Niet voor tentamen): PLUGIN: ContentNegotiation
    // Verzorgt de automatische transformatie van HTTP payloads (bytes) naar Kotlin objecten.
    // Gebaseerd op de `Content-Type` header (application/json).
    install(ContentNegotiation) {
        json(Json {
            // CONFIGURATIE: Robustness
            // `ignoreUnknownKeys = true`: Voorkomt crashes als de API velden terugstuurt
            // die niet in ons `Photo` datamodel gedefinieerd zijn. (Forward compatibility).
            ignoreUnknownKeys = true
        })
    }
}

// FUNCTION: fetchPhotos
// KEYWORD: suspend
// Een `suspend` functie geeft aan dat de uitvoering gepauzeerd ("suspended") kan worden.
// Dit maakt het mogelijk om langdurige netwerkoperaties uit te voeren zonder de aanroepende thread
// (vaak de Main Thread) te blokkeren. De onderliggende CIO engine handelt de sockets af.
suspend fun fetchPhotos(): List<Photo> {
    // API ENDPOINT
    // INSTRUCTIE: Vervang dit voor productie met je eigen backend URL.
    // Localhost Android Emulator alias: "http://10.0.2.2:8080/api/photos"
    val url = "https://jsonplaceholder.typicode.com/photos"

    // REQUEST EXECUTION
    // 1. `client.get(url)`: Voert het HTTP GET request uit.
    // 2. `.body()`: Deserialiseert de JSON response body naar `List<Photo>` 
    //    gebruikmakend van de geregistreerde ContentNegotiation plugin.
    return client.get(url).body()
}
