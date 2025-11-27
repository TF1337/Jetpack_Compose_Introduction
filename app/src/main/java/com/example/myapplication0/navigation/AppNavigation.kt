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
                }
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
    }
}
