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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appcontroldeluz.ui.screens.*
import com.example.appcontroldeluz.ui.theme.*
import com.example.appcontroldeluz.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return AppViewModel(application = application) as T
                    }
                }
            )
            val darkTheme by appViewModel.isDarkTheme.collectAsState()

            AppControldeluzTheme(darkTheme = darkTheme) {
                var currentScreen by remember { mutableStateOf("login") }
                val isInitializing by appViewModel.isInitializing.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                // Show errors from ViewModel as snackbars
                val error by appViewModel.voiceState.collectAsState()
                LaunchedEffect(error.error) {
                    val err = error.error
                    if (!err.isNullOrEmpty()) {
                        snackbarHostState.showSnackbar(err)
                    }
                }

                if (isInitializing) {
                    LoadingScreen()
                } else if (currentScreen == "login") {
                    LoginScreen(onLoginSuccess = { currentScreen = "dashboard" })
                } else {
                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            // Ocultar barra en el asistente de voz y en Agregar Luces
                            if (currentScreen != "voice_assistant" && currentScreen != "add_lights") {
                                AppBottomBar(
                                    currentScreen = currentScreen,
                                    onNavigate = { currentScreen = it }
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            val lights by appViewModel.lights.collectAsState()
                            val isLoading by appViewModel.isLoading.collectAsState()
                            val sensorStatus by appViewModel.sensorStatus.collectAsState()

                            when (currentScreen) {
                                "dashboard" -> DashboardScreen(
                                    lights = lights,
                                    isLoading = isLoading,
                                    onToggleRoom = { room, state -> appViewModel.controlRoom(room, state) },
                                    onToggleAll = { state -> appViewModel.controlAll(state) },
                                    onRoomClick = { currentScreen = "sensor" }
                                )
                                "schedule" -> ScheduleScreen(onAddClick = { currentScreen = "add_lights" })
                                "sensor" -> SensorScreen(
                                    onBackClick = { currentScreen = "dashboard" },
                                    sensorStatus = sensorStatus,
                                    onSensorEnabledChange = appViewModel::setSensorEnabled,
                                    onRemoveLinkedLight = appViewModel::removeLinkedLightFromSensor
                                )
                                "voice_settings" -> VoiceSettingsScreen(
                                    onBackClick = { currentScreen = "dashboard" },
                                    isDarkTheme = darkTheme,
                                    onThemeChange = appViewModel::setDarkTheme
                                )
                                "voice_assistant" -> VoiceAssistantScreen(
                                    viewModel = appViewModel,
                                    onClose = { currentScreen = "dashboard" }
                                )
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
    val colors = LocalAppThemeColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 18.dp)
    ) {
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = colors.surface.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(Icons.Default.Home, "Casa", currentScreen == "dashboard") { onNavigate("dashboard") }
                BottomNavItem(Icons.Default.Schedule, "Horarios", currentScreen == "schedule") { onNavigate("schedule") }

                Spacer(modifier = Modifier.size(72.dp))

                BottomNavItem(Icons.Default.Sensors, "Sensor", currentScreen == "sensor") { onNavigate("sensor") }
                BottomNavItem(Icons.Default.Settings, "Ajustes", currentScreen == "voice_settings") { onNavigate("voice_settings") }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp)
                .size(76.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .clickable { onNavigate("voice_assistant") },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Mic, contentDescription = "Voz", tint = Color.White, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = LocalAppThemeColors.current
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
            tint = if (isSelected) PrimaryBlue else colors.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Text(label, color = if (isSelected) PrimaryBlue else colors.onSurfaceVariant, fontSize = 10.sp)
    }
}
