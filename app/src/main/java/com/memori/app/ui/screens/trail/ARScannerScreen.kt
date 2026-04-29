package com.memori.app.ui.screens.trail

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun ARScannerScreen(onFinishedAR: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCameraPermission = it
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            // Usamos CameraX para estabilidade total, evitando o erro de sensor do ARCore
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Interface de Scanner
            Box(modifier = Modifier.fillMaxSize()) {
                // Mira de Scanner
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .align(Alignment.Center)
                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 140.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            "Aproxime-se do monumento",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onFinishedAR,
                        modifier = Modifier.height(56.dp).width(240.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Objeto Identificado!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
