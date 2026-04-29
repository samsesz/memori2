package com.memori.app.ui.screens.trail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R
import kotlinx.coroutines.delay
import java.util.Collections

@Composable
fun PuzzleScreen(onPuzzleCompleted: (Int) -> Unit) {
    var timeElapsed by remember { mutableIntStateOf(0) }
    var puzzleFinished by remember { mutableStateOf(false) }

    // Estado das peças (0 a 8, onde 8 é o espaço vazio)
    val tiles = remember {
        mutableStateListOf<Int>().apply {
            addAll(0..8)
            shuffleSolvable(this)
        }
    }

    // Verifica se o puzzle foi resolvido (ordem 0 a 8)
    LaunchedEffect(tiles.toList()) {
        if (tiles.toList() == (0..8).toList() && !puzzleFinished && timeElapsed > 0) {
            puzzleFinished = true
            delay(1000)
            onPuzzleCompleted(timeElapsed)
        }
    }

    // Cronômetro
    LaunchedEffect(puzzleFinished) {
        while (!puzzleFinished) {
            delay(1000)
            timeElapsed++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Reconstrua o KKKK",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Display
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = "⏱️ ${timeElapsed}s",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Grade do Puzzle 3x3
        Surface(
            modifier = Modifier
                .size(310.dp)
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(4.dp),
                userScrollEnabled = false
            ) {
                itemsIndexed(tiles) { index, tileValue ->
                    PuzzleTile(
                        tileValue = tileValue,
                        onClick = {
                            if (!puzzleFinished) {
                                moveTile(index, tiles)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (puzzleFinished) {
            Text(
                text = "🎉 Excelente! Puzzle Resolvido!",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        } else {
            Button(
                onClick = { shuffleSolvable(tiles) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Embaralhar / Reiniciar")
            }
        }
    }
}

@Composable
fun PuzzleTile(tileValue: Int, onClick: () -> Unit) {
    if (tileValue == 8) {
        // Espaço vazio
        Box(
            modifier = Modifier
                .padding(2.dp)
                .aspectRatio(1f)
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
        )
    } else {
        val row = tileValue / 3
        val col = tileValue % 3

        Box(
            modifier = Modifier
                .padding(2.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize().clipToBounds()) {
                val tileWidth = maxWidth
                val tileHeight = maxHeight

                // IMPORTANTE: Adicione sua imagem 'kkkk_puzzle.jpg' em res/drawable
                Image(
                    painter = painterResource(id = R.drawable.kkkk_puzzle),
                    contentDescription = "Peça do quebra-cabeça",
                    modifier = Modifier
                        .size(tileWidth * 3, tileHeight * 3)
                        .offset(
                            x = -(tileWidth * col),
                            y = -(tileHeight * row)
                        ),
                    contentScale = ContentScale.FillBounds
                )
            }
            
            // Pequeno número auxiliar
            Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 4.dp),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text(
                    text = (tileValue + 1).toString(),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// Lógica de movimentação (apenas se estiver adjacente ao espaço vazio)
fun moveTile(index: Int, tiles: MutableList<Int>) {
    val emptyIndex = tiles.indexOf(8)
    val row = index / 3
    val col = index % 3
    val emptyRow = emptyIndex / 3
    val emptyCol = emptyIndex % 3

    val isNeighbor = (Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
            (Math.abs(col - emptyCol) == 1 && row == emptyRow)

    if (isNeighbor) {
        Collections.swap(tiles, index, emptyIndex)
    }
}

// Garante que o puzzle seja resolvível simulando movimentos aleatórios a partir do fim
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
