package com.memori.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.memori.app.ui.screens.auth.SplashScreen
import com.memori.app.ui.screens.auth.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.HomeMap.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToCadastro = {
                    // navController.navigate(Screen.Cadastro.route)
                }
            )
        }
        composable(Screen.HomeMap.route) {
            com.memori.app.ui.screens.home.MainScreen(
                onNavigateToTrailStart = {
                    navController.navigate(Screen.Splash.route + "_trail")
                }
            )
        }
        composable(Screen.Splash.route + "_trail") {
            com.memori.app.ui.screens.trail.TrailStartScreen(
                onStartAR = {
                    navController.navigate(Screen.ARScanner.route)
                }
            )
        }
        composable(Screen.ARScanner.route) {
            com.memori.app.ui.screens.trail.ARScannerScreen(
                onFinishedAR = {
                    navController.navigate(Screen.Puzzle.route)
                }
            )
        }
        composable(Screen.Puzzle.route) {
            com.memori.app.ui.screens.trail.PuzzleScreen(
                onPuzzleCompleted = { time ->
                    navController.navigate("final_story/$time")
                }
            )
        }
        composable("final_story/{time}") { backStackEntry ->
            val time = backStackEntry.arguments?.getString("time")?.toIntOrNull() ?: 0
            com.memori.app.ui.screens.trail.FinalStoryScreen(
                timeElapsed = time,
                onBackToHome = {
                    navController.navigate(Screen.HomeMap.route) {
                        popUpTo(Screen.HomeMap.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
