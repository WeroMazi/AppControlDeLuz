package com.example.appcontroldeluz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.screens.*
import com.example.appcontroldeluz.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppControldeluzTheme {
                var currentScreen by remember { mutableStateOf("login") }

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
