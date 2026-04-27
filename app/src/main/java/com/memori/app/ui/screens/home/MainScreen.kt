package com.memori.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    onNavigateToTrailStart: () -> Unit
) {
    val bottomNavController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Mapa", "Favoritos", "Perfil")
    val icons = listOf(Icons.Filled.LocationOn, Icons.Filled.Favorite, Icons.Filled.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            val route = when (index) {
                                0 -> "map"
                                1 -> "favorites"
                                else -> "profile"
                            }
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(navController = bottomNavController, startDestination = "map") {
                composable("map") {
                    HomeMapScreen(onNavigateToTrailStart)
                }
                composable("favorites") {
                    Text("Favoritos")
                }
                composable("profile") {
                    Text("Perfil Screen")
                }
            }
        }
    }
}
