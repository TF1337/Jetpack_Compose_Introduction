package com.example.myapplication0.navigation
// PACKAGE
// Dit bestand hoort bij de navigatie-laag van de app.
// Alles in deze package is verantwoordelijk voor:
// - routes
// - schermovergangen
// - argumenten tussen schermen

import androidx.compose.runtime.Composable
// `@Composable` maakt van een functie een bouwsteen voor de UI.

import androidx.lifecycle.viewmodel.compose.viewModel
// Hulpfunctie om een ViewModel op te halen binnen een Composable.

import androidx.navigation.NavType
// Wordt gebruikt om het type van route-argumenten te definiëren (bijv. Int, String).

import androidx.navigation.compose.NavHost
// Container waarin alle schermen (routes) worden getekend.

import androidx.navigation.compose.composable
// Koppelt een route (String) aan een Composable (scherm).

import androidx.navigation.compose.rememberNavController
// Maakt en bewaart een NavController tijdens recompositions.

import androidx.navigation.navArgument
// Wordt gebruikt om argumenten (zoals id’s) te definiëren bij routes.

import com.example.myapplication0.ui.DetailScreen
// Het detailscherm van de app.

import com.example.myapplication0.ui.ListScreen
import com.example.myapplication0.ui.AnswersScreen
import com.example.myapplication0.ui.Lesson1Screen
import com.example.myapplication0.ui.Lesson2Screen
import com.example.myapplication0.ui.Lesson3Screen
import com.example.myapplication0.ui.Lesson4Screen
// Het lijstscherm van de app.

import com.example.myapplication0.viewmodel.MainViewModel
// De centrale ViewModel die door meerdere schermen wordt gedeeld.

// FASE 4: CLIENT-SIDE ROUTING
//
// TECHNISCHE CONTEXT: Navigation Component
// De Navigation Component regelt:
// - welk scherm zichtbaar is
// - welke route actief is
// - wat de terugknop doet
//
// Er is één Activity en meerdere virtuele schermen (Composables).

@Composable
fun AppNavigation() {

    // NAVCONTROLLER
    // De NavController beheert de navigatiegeschiedenis (back stack).
    // `rememberNavController` zorgt ervoor dat deze niet opnieuw wordt aangemaakt
    // bij elke recomposition.
    val navController = rememberNavController()

    // VIEWMODEL BINNEN NAVIGATIE
    // We maken hier één instantie van de MainViewModel aan.
    // Deze wordt gedeeld door:
    // - ListScreen
    // - DetailScreen
    //
    // Hierdoor blijft de data behouden tijdens navigatie.
    val viewModel: MainViewModel = viewModel()

    // NAVHOST
    // De NavHost is de container waarin het actieve scherm wordt getoond.
    // `startDestination` bepaalt welk scherm als eerste wordt geladen.
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {

        // ROUTE: LIJSTSCHERM
        // Dit is het startscherm van de app.
        composable("list") {

            ListScreen(
                viewModel = viewModel,

                // CALLBACK VOOR NAVIGATIE
                // Wanneer een item wordt aangeklikt,
                // navigeren we naar het detailscherm met het id als parameter.
                onItemClick = { photoId ->

                    // We bouwen hier dynamisch de route op:
                    // "detail/5"
                    navController.navigate("detail/$photoId")
                },
                onAnswersClick = { navController.navigate("answers") },
                onLesson1Click = { navController.navigate("lesson1") },
                onLesson2Click = { navController.navigate("lesson2") },
                onLesson3Click = { navController.navigate("lesson3") },
                onLesson4Click = { navController.navigate("lesson4") }
            )
        }

        // ROUTE: DETAILSCHERM MET ARGUMENT
        // De `{photoId}` is een dynamische placeholder in de route.
        composable(
            route = "detail/{photoId}",

            // TYPE SAFETY
            // We geven expliciet aan dat `photoId` een Int is.
            arguments = listOf(
                navArgument("photoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            // ARGUMENT UITLEZEN
            // We halen de meegestuurde `photoId` op uit de route.
            val photoId =
                backStackEntry.arguments?.getInt("photoId") ?: 0

            DetailScreen(
                photoId = photoId,
                viewModel = viewModel,

                // TERUG NAVIGEREN
                // Bij klikken op "Terug" verwijderen we het huidige scherm
                // van de back stack.
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ROUTE: ANTWOORDEN OVERZICHT
        composable("answers") {
            AnswersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ROUTE: LES 1 SCHERM
        composable("lesson1") {
            Lesson1Screen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAnswersClick = { navController.navigate("answers") },
                onLesson1Click = { }, // Doe niets als we al op les 1 zijn (of herlaad)
                onLesson2Click = { navController.navigate("lesson2") },
                onLesson3Click = { navController.navigate("lesson3") },
                onLesson4Click = { navController.navigate("lesson4") }
            )
        }

        // ROUTE: LES 2 SCHERM
        composable("lesson2") {
            Lesson2Screen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAnswersClick = { navController.navigate("answers") },
                onLesson1Click = { navController.navigate("lesson1") },
                onLesson3Click = { navController.navigate("lesson3") },
                onLesson4Click = { navController.navigate("lesson4") }
            )
        }

        // ROUTE: LES 3 SCHERM
        composable("lesson3") {
            Lesson3Screen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAnswersClick = { navController.navigate("answers") },
                onLesson1Click = { navController.navigate("lesson1") },
                onLesson2Click = { navController.navigate("lesson2") },
                onLesson4Click = { navController.navigate("lesson4") }
            )
        }

        // ROUTE: LES 4 SCHERM
        composable("lesson4") {
            Lesson4Screen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAnswersClick = { navController.navigate("answers") },
                onLesson1Click = { navController.navigate("lesson1") },
                onLesson2Click = { navController.navigate("lesson2") },
                onLesson3Click = { navController.navigate("lesson3") },
                onLesson4Click = { }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// TOEKOMSTOPTIE: MIGRATIE NAAR JETPACK NAVIGATION 3 (NAV-3)
// -----------------------------------------------------------------------------
//
// Navigation 3 is de nieuwe generatie navigatie voor Jetpack Compose.
// Het vervangt de traditionele Navigation Compose (“Navigation 2”).
//
// Waarom zou je Nav-3 later willen gebruiken?
// • Navigatie wordt volledig *declaratief* (zoals de rest van Compose)
// • Je beheert ZELF de back stack → veel voorspelbaarder gedrag
// • Perfect voor list–detail, split-view, tablets, foldables
// • Routes worden echte Kotlin-objecten i.p.v. Strings
// • Beter te testen en minder boilerplate
//
// -----------------------------------------------------------------------------
// 1) WELKE DEPENDENCIES JE ZOU MOETEN TOEVOEGEN (LATER)
// -----------------------------------------------------------------------------
// (Niet nu! Alleen wanneer je écht Nav-3 wil gebruiken.)
// 
// In libs.versions.toml of build.gradle.kts:
//
// implementation("androidx.navigation3:navigation-runtime:<latest>")
// implementation("androidx.navigation3:navigation-ui:<latest>")
//
// -----------------------------------------------------------------------------
// 2) HOE JE ROUTES DEFINIEERT MET NAV-3 (GEEN STRINGS MEER)
// -----------------------------------------------------------------------------
//
// In plaats van:
//    composable("detail/{photoId}")
//
// gebruik je in Nav-3 Kotlin objecten:
//
// sealed interface AppRoute : NavKey {
//     data object List : AppRoute
//     data class Detail(val photoId: Int) : AppRoute
// }
//
// Dit dwingt type-veiligheid af.
// Je hebt nooit meer foutieve route-strings.
//
// -----------------------------------------------------------------------------
// 3) HOE JE EEN BACK STACK MAAKT (GEEN NAVCONTROLLER MEER)
// -----------------------------------------------------------------------------
//
// In plaats van:
//    val navController = rememberNavController()
//
// gebruik je:
//
//    val backStack = rememberNavBackStack<AppRoute>(start = AppRoute.List)
//
// De backstack *is jouw Compose-state*.
// Dit betekent:
// • Je kunt hem testen
// • Je kunt hem debuggen
// • Hij is volledig voorspelbaar
//
// -----------------------------------------------------------------------------
// 4) HOE JE NAVIGATIE UITVOERT (GEEN navigate("detail/...") MEER)
// -----------------------------------------------------------------------------
//
// In plaats van:
//    navController.navigate("detail/")
//
// gebruik je:
//
//    backStack.push(AppRoute.Detail(photoId))
//
// Voor terug:
//
//    backStack.pop()
//
// Navigation wordt dus:
// - push: vooruit navigeren
// - pop: terug navigeren
//
// -----------------------------------------------------------------------------
// 5) HOE JE SCHERMEN TONEN (VERVANGT NavHost)
// -----------------------------------------------------------------------------
//
// In plaats van:
//
// NavHost(navController) { ... }
//
// gebruik je:
//
// NavDisplay(
//     backStack = backStack,
//     entryProvider = { entry ->
//         when (val route = entry.key) {
//             is AppRoute.List -> ListScreen(...)
//             is AppRoute.Detail -> DetailScreen(photoId = route.photoId)
//         }
//     }
// )
//
// Hiermee beschreven we declaratief welk scherm actief is.
//
// -----------------------------------------------------------------------------
// 6) CONCREET: WAT MOET JE IN DIT PROJECT AANPASSEN ALS JE LATER MIGREERT
// -----------------------------------------------------------------------------
// • Verwijder NavController + NavHost + composable-routes
// • Maak een sealed AppRoute-structuur aan
// • Maak een rememberNavBackStack<AppRoute>
// • NavDisplay gebruiken i.p.v. NavHost
// • Navigatie aanpassen naar push/pop
//
// -----------------------------------------------------------------------------
// SAMENVATTING
// -----------------------------------------------------------------------------
// Je huidige Navigation Compose (“Navigation 2”) is perfect voor deze les.
// Later kun je overstappen naar Navigation 3 voor:
// - betere testbaarheid
// - type-veiliger navigeren
// - complexere user flows
//
// Dit commentblok dient als migratiegids zodat je later eenvoudig kunt upgraden.
// -----------------------------------------------------------------------------
