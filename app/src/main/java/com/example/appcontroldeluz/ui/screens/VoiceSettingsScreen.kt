package com.example.appcontroldeluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.SurfaceDark
import com.example.appcontroldeluz.ui.theme.TextGray

@Composable
fun VoiceSettingsScreen(onBackClick: () -> Unit) {
    var wakeWordEnabled by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Control por Voz",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Mic Calibration
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Toca para calibrar el micrófono", color = TextGray, fontSize = 14.sp)
            }

            // Detección
            Text("DETECCIÓN", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Escuchar \"Hey Home\"", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Activa el asistente manos libres.", color = TextGray, fontSize = 12.sp)
                    }
                    Switch(
                        checked = wakeWordEnabled,
                        onCheckedChange = { wakeWordEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Voz del Asistente (Simplificado a una sola opción)
            Text("VOZ DEL ASISTENTE", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Nova", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nova es la voz femenina predeterminada, optimizada para una claridad y naturalidad excepcional.",
                color = TextGray, 
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Accesos Directos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ACCESOS DIRECTOS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("+ Nuevo", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { })
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            ShortcutItem("Buenas noches", "Apagar todas las luces • Cerrar puertas", Icons.Default.Nightlight, Color(0xFF6366F1))
            Spacer(modifier = Modifier.height(12.dp))
            ShortcutItem("Modo Enfoque", "Office Lights 100% Cool White", Icons.Default.WbSunny, Color(0xFFF59E0B))
            Spacer(modifier = Modifier.height(12.dp))
            ShortcutItem("Modo Cine", "Bajar intensidad al 10%", Icons.Default.Movie, Color(0xFFE11D48))
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ShortcutItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("\"$title\"", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(subtitle, color = TextGray, fontSize = 12.sp)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGray)
        }
    }
}
