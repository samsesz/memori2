package com.memori.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(onNavigateToTrailStart: () -> Unit) {
    val bottomNavController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    
    // Perfil no meio conforme solicitado
    val items = listOf("Mapa", "Perfil", "Favoritos")
    val icons = listOf(Icons.Filled.LocationOn, Icons.Filled.Person, Icons.Filled.Favorite)
    val routes = listOf("map", "profile", "favorites")

    Scaffold(
        bottomBar = {
            // Menu "Cilíndrico" (Cápsula) flutuante
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(35.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                    shadowElevation = 12.dp,
                    tonalElevation = 4.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(icons[index], contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    bottomNavController.navigate(routes[index]) {
                                        popUpTo(bottomNavController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // O padding é ignorado aqui para que o mapa preencha a tela toda por baixo do menu
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = bottomNavController, startDestination = "map") {
                composable("map") {
                    HomeMapScreen(onNavigateToTrailStart)
                }
                composable("profile") {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tela de Perfil")
                    }
                }
                composable("favorites") {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Favoritos")
                    }
                }
            }
        }
    }
}
