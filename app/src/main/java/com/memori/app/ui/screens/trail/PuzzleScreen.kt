package com.memori.app.ui.screens.trail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R
import kotlinx.coroutines.delay
import java.util.Collections

@Composable
fun PuzzleScreen(onPuzzleCompleted: (Int) -> Unit, onExit: () -> Unit) {
    var timeElapsed by remember { mutableIntStateOf(0) }
    var puzzleFinished by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    val tiles = remember {
        mutableStateListOf<Int>().apply {
            addAll(0..8)
            shuffleSolvable(this)
        }
    }

    LaunchedEffect(puzzleFinished) {
        while (!puzzleFinished) {
            delay(1000)
            timeElapsed++
        }
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage.isNotEmpty()) {
            delay(2000)
            feedbackMessage = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF9F9F9))) {
        // Botão Sair
        IconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Sair", tint = Color.Gray)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reconstrua o KKKK",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TIMER VOLTOU
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "⏱️ ${timeElapsed}s",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Grade do Puzzle - Clique para mover habilitado para estabilidade
            Surface(
                modifier = Modifier
                    .size(312.dp)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(2.dp, Color.White)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..2) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                val tileValue = tiles[index]
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (!puzzleFinished) {
                                                tryMove(index, tiles)
                                            }
                                        }
                                ) {
                                    PuzzlePiece(tileValue)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (feedbackMessage.isNotEmpty()) {
                Text(feedbackMessage, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botões de Ação
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { 
                        puzzleFinished = false
                        shuffleSolvable(tiles) 
                    },
                    modifier = Modifier.height(48.dp).weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Embaralhar", fontSize = 12.sp, maxLines = 1)
                }

                Button(
                    onClick = {
                        val isCorrect = tiles.toList() == (0..8).toList()
                        if (isCorrect) {
                            puzzleFinished = true
                            showCompletionDialog = true
                        } else {
                            feedbackMessage = "Ainda não está correto!"
                        }
                    },
                    modifier = Modifier.height(48.dp).weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !puzzleFinished
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Finalizar", fontSize = 12.sp, maxLines = 1)
                }
            }
        }

        if (showCompletionDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("🎉 Parabéns!") },
                text = { Text("Você reconstruiu o KKKK em ${timeElapsed} segundos!") },
                confirmButton = {
                    Button(onClick = { 
                        showCompletionDialog = false
                        onPuzzleCompleted(timeElapsed) 
                    }) {
                        Text("Continuar")
                    }
                }
            )
        }
    }
}

@Composable
fun PuzzlePiece(tileValue: Int) {
    if (tileValue == 8) {
        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.2f)))
    } else {
        val row = tileValue / 3
        val col = tileValue % 3

        BoxWithConstraints(modifier = Modifier.fillMaxSize().clipToBounds()) {
            val w = maxWidth
            val h = maxHeight
            
            Image(
                painter = painterResource(id = R.drawable.kkkk_puzzle),
                contentDescription = null,
                modifier = Modifier
                    .size(w * 3, h * 3)
                    .offset {
                        IntOffset(
                            x = -(w * col).roundToPx(),
                            y = -(h * row).roundToPx()
                        )
                    },
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

fun tryMove(index: Int, tiles: MutableList<Int>) {
    val emptyIndex = tiles.indexOf(8)
    val row = index / 3
    val col = index % 3
    val eRow = emptyIndex / 3
    val eCol = emptyIndex % 3

    if (Math.abs(row - eRow) + Math.abs(col - eCol) == 1) {
        Collections.swap(tiles, index, emptyIndex)
    }
}

fun shuffleSolvable(tiles: MutableList<Int>) {
    tiles.clear()
    tiles.addAll(0..8)
    var emptyIndex = 8
    repeat(100) {
        val row = emptyIndex / 3
        val col = emptyIndex % 3
        val neighbors = mutableListOf<Int>()
        if (row > 0) neighbors.add(emptyIndex - 3)
        if (row < 2) neighbors.add(emptyIndex + 3)
        if (col > 0) neighbors.add(emptyIndex - 1)
        if (col < 2) neighbors.add(emptyIndex + 1)
        val moveToIndex = neighbors.random()
        Collections.swap(tiles, emptyIndex, moveToIndex)
        emptyIndex = moveToIndex
    }
}
