package com.example.appcontroldeluz

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.appcontroldeluz.ui.screens.*
import com.example.appcontroldeluz.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppControldeluzTheme {
                val context = LocalContext.current
                val requiredPermissions = remember { requiredRuntimePermissions() }
                var permissionCheckTrigger by remember { mutableIntStateOf(0) }
                
                val permissionsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionCheckTrigger++ }
                var requestedOnce by rememberSaveable { mutableStateOf(false) }

                val allPermissionsGranted = remember(requiredPermissions, permissionCheckTrigger) {
                    derivedStateOf {
                        requiredPermissions.all { permission ->
                            ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        }
                    }
                }

                LaunchedEffect(allPermissionsGranted.value) {
                    if (!allPermissionsGranted.value && !requestedOnce) {
                        requestedOnce = true
                        permissionsLauncher.launch(requiredPermissions)
                    }
                }

                if (!allPermissionsGranted.value) {
                    PermissionsRequiredScreen(
                        onRequestPermissions = { permissionsLauncher.launch(requiredPermissions) },
                        onOpenSettings = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                "package:${context.packageName}".toUri()
                            )
                            context.startActivity(intent)
                        }
                    )
                    return@AppControldeluzTheme
                }

                var currentScreen by rememberSaveable { mutableStateOf("login") }

                if (currentScreen == "login") {
                    LoginScreen(onLoginSuccess = { currentScreen = "dashboard" })
                } else {
                    Scaffold(
                        bottomBar = {
                            // Ocultar barra en el asistente de voz y en Agregar Luces
                            if (currentScreen != "voice_assistant" && currentScreen != "add_lights") {
                                AppBottomBar(
                                    currentScreen = currentScreen,
                                    onNavigate = { currentScreen = it }
                                )
                            }
                        },
                        containerColor = BackgroundDark
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
                                "dashboard" -> DashboardScreen(onRoomClick = { currentScreen = "sensor" })
                                "schedule" -> ScheduleScreen(onAddClick = { currentScreen = "add_lights" })
                                "sensor" -> SensorScreen(onBackClick = { currentScreen = "dashboard" })
                                "voice_settings" -> VoiceSettingsScreen(onBackClick = { currentScreen = "dashboard" })
                                "voice_assistant" -> VoiceAssistantScreen(onClose = { currentScreen = "dashboard" })
                                "add_lights" -> AddLightsScreen(onBackClick = { currentScreen = "schedule" })
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun requiredRuntimePermissions(): Array<String> {
    val permissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions += Manifest.permission.BLUETOOTH_SCAN
        permissions += Manifest.permission.BLUETOOTH_CONNECT
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions += Manifest.permission.NEARBY_WIFI_DEVICES
    }

    return permissions.toTypedArray()
}

@Composable
private fun PermissionsRequiredScreen(
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Permisos requeridos",
            tint = PrimaryBlue,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Permisos Requeridos",
            color = Color.White,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Para funcionar correctamente, la app necesita Bluetooth, Wi-Fi y micrófono.",
            color = TextGray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermissions,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Conceder permisos")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenSettings) {
            Text("Abrir ajustes")
        }
    }
}

@Composable
fun AppBottomBar(currentScreen: String, onNavigate: (String) -> Unit) {
    Surface(
        color = Color(0xFF0F1420).copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "Casa", currentScreen == "dashboard") { onNavigate("dashboard") }
            BottomNavItem(Icons.Default.Schedule, "Horarios", currentScreen == "schedule") { onNavigate("schedule") }
            
            // Botón central de voz (Foto 2, 3 y 4)
            Box(
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
                    .clickable { onNavigate("voice_assistant") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Mic, contentDescription = "Voz", tint = Color.White, modifier = Modifier.size(32.dp))
            }

            BottomNavItem(Icons.Default.Sensors, "Sensor", currentScreen == "sensor") { onNavigate("sensor") }
            BottomNavItem(Icons.Default.Settings, "Ajustes", currentScreen == "voice_settings") { onNavigate("voice_settings") }
        }
    }
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            icon, 
            contentDescription = label, 
            tint = if (isSelected) PrimaryBlue else TextGray,
            modifier = Modifier.size(24.dp)
        )
        Text(label, color = if (isSelected) PrimaryBlue else TextGray, fontSize = 10.sp)
    }
}
