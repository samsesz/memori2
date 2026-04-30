package com.memori.app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.memori.app.ui.theme.RedPrimary
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun HomeMapScreen(onNavigateToTrailStart: () -> Unit, onNavigateToProfile: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val kkkkLocation = remember { GeoPoint(-24.4935, -47.8447) }
    val mapView = remember { MapView(context) }
    var showTrailDialog by remember { mutableStateOf(false) }

    // Initialization
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(18.0)
        mapView.controller.setCenter(kkkkLocation)
    }

    // Permission handling
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

    // Lifecycle sync
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
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.overlays.clear()
                
                // User Location Overlay
                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), view)
                locationOverlay.enableMyLocation()
                view.overlays.add(locationOverlay)

                // Trail Marker on the Map
                val marker = Marker(view)
                marker.position = kkkkLocation
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                marker.setOnMarkerClickListener { _, _ ->
                    showTrailDialog = true
                    true
                }
                view.overlays.add(marker)
            }
        )

        // FIXED HUD BUTTONS (Fiel ao design solicitado)
        
        // Origami Button (Fixed on screen top right area) - MOVED DOWN
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 24.dp) // De 80.dp para 100.dp
                .size(56.dp)
                .clickable { showTrailDialog = true },
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 6.dp,
            border = BorderStroke(2.dp, RedPrimary)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🏮", fontSize = 28.sp)
            }
        }

        // FAB: Profile (Bottom Center-Right)
        FloatingActionButton(
            onClick = onNavigateToProfile,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 16.dp),
            containerColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD54F)),
                contentAlignment = Alignment.Center
            ) {
                Text("😑", fontSize = 20.sp)
            }
        }

        // FAB: My Location (Top End, but below header) - MOVED DOWN
        FloatingActionButton(
            onClick = {
                val overlay = mapView.overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()
                overlay?.myLocation?.let { mapView.controller.animateTo(it) }
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 180.dp, end = 16.dp), // De 160.dp para 180.dp
            containerColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Default.MyLocation, "Onde estou", tint = RedPrimary)
        }

        // Top Header HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp) // Lowered slightly
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Larissa Tag
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, border = BorderStroke(1.dp, RedPrimary), shadowElevation = 4.dp) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(20.dp).background(Color(0xFFFFD54F), CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(text = "Larissa", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            // Stars Tag
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, border = BorderStroke(1.dp, RedPrimary), shadowElevation = 4.dp) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("10", fontWeight = FontWeight.Bold, color = Color.Gray); Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                }
            }
        }

        // Trail Start Dialog
        if (showTrailDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showTrailDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.width(320.dp).padding(16.dp).clickable(enabled = false) {},
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(modifier = Modifier.padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(modifier = Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(3) { Icon(Icons.Default.StarOutline, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(32.dp)) }
                            }
                            Surface(modifier = Modifier.align(Alignment.TopEnd).offset(x = 10.dp, y = (-10).dp), color = Color(0xFF8BC34A), shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 8.dp)) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Text(text = "Lanternas Brilhantes", color = Color.Gray, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                             Text("🏮", fontSize = 100.sp, modifier = Modifier.alpha(0.3f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Ajude Yumi a encontrar as Lanternas Brilhantes a tempo do evento Tooro Nagashi!", modifier = Modifier.padding(horizontal = 24.dp), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showTrailDialog = false; onNavigateToTrailStart() },
                            modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Jogar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
