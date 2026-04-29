package com.memori.app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun HomeMapScreen(onNavigateToTrailStart: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Inicialização do Osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    var hasLocationPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    val kkkkLocation = GeoPoint(-24.4935, -47.8447)
    val mapView = remember { MapView(context) }

    // Sincroniza o ciclo de vida para carregar o mapa corretamente
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(18.0)
                controller.setCenter(kkkkLocation)
                
                overlays.clear()
                val marker = Marker(this)
                marker.position = kkkkLocation
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Conjunto Monumental KKKK"
                overlays.add(marker)

                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                locationOverlay.enableMyLocation()
                overlays.add(locationOverlay)
            }},
            modifier = Modifier.fillMaxSize()
        )

        // Botão de Localização (Posicionado abaixo do header)
        FloatingActionButton(
            onClick = {
                val overlay = mapView.overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()
                overlay?.myLocation?.let { mapView.controller.animateTo(it) }
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 110.dp, end = 16.dp),
            containerColor = Color.White.copy(alpha = 0.9f),
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(Icons.Default.MyLocation, "Onde estou", tint = MaterialTheme.colorScheme.primary)
        }

        // Header Superior (Perfil e Estrelas)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "👦 Usuário",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("10", fontWeight = FontWeight.Bold)
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                }
            }
        }

        // Botão Iniciar Trilha
        Button(
            onClick = onNavigateToTrailStart,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
                .height(56.dp)
                .width(220.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("Iniciar Trilha: KKKK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
