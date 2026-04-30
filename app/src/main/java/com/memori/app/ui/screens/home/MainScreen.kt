package com.memori.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.memori.app.ui.screens.profile.AchievementsScreen
import com.memori.app.ui.screens.profile.ProfileScreen
import com.memori.app.ui.theme.RedPrimary

@Composable
fun MainScreen(onNavigateToTrailStart: () -> Unit, onLogout: () -> Unit) {
    val bottomNavController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    
    // Menu ajustado para coincidir com a imagem
    val items = listOf("Explorar", "Perfil", "Conquistas")
    val icons = listOf(Icons.Default.CompassCalibration, Icons.Default.Person, Icons.Default.Image)
    val routes = listOf("map", "profile", "achievements")

    Scaffold(
        bottomBar = {
            // Menu "Cilíndrico" (Cápsula) flutuante conforme a foto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEachIndexed { index, item ->
                            val isSelected = selectedItem == index
                            
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .let { 
                                        if (index == 1 && isSelected) {
                                            // Destaque do perfil no meio
                                            it.border(2.dp, RedPrimary, CircleShape).padding(4.dp)
                                        } else it
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = {
                                    selectedItem = index
                                    bottomNavController.navigate(routes[index]) {
                                        popUpTo(bottomNavController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }) {
                                    if (index == 1) {
                                        // Ícone de perfil amarelado como na foto
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFFFFD54F) else Color.LightGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("😑", fontSize = 20.sp)
                                        }
                                    } else {
                                        Icon(
                                            icons[index],
                                            contentDescription = item,
                                            tint = if (isSelected) RedPrimary else RedPrimary.copy(alpha = 0.4f),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = bottomNavController, startDestination = "map") {
                composable("map") {
                    HomeMapScreen(
                        onNavigateToTrailStart = onNavigateToTrailStart,
                        onNavigateToProfile = {
                            selectedItem = 1
                            bottomNavController.navigate("profile") {
                                popUpTo(bottomNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable("profile") {
                    ProfileScreen(onLogout = onLogout)
                }
                composable("achievements") {
                    AchievementsScreen()
                }
            }
        }
    }
}
