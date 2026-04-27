package com.memori.app.ui.screens.trail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PuzzleScreen(onPuzzleCompleted: (Int) -> Unit) {
    var timeElapsed by remember { mutableStateOf(0) }
    var puzzleFinished by remember { mutableStateOf(false) }

    LaunchedEffect(puzzleFinished) {
        while (!puzzleFinished) {
            delay(1000)
            timeElapsed++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9)) // Verde muito claro
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quebra-Cabeça do KKKK", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cronômetro
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = "Tempo: ${timeElapsed}s",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Área do Puzzle (Simulada)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("Área do Jogo Drag & Drop (Puzzle)\nArraste as peças aqui...", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                puzzleFinished = true
                onPuzzleCompleted(timeElapsed)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Simular: Concluir Puzzle", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
