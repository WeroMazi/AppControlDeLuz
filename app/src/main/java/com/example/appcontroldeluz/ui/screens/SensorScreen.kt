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
fun SensorScreen(onBackClick: () -> Unit) {
    var sensorEnabled by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableStateOf(0.5f) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sensor de Movimiento", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Estado Actual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ESTADO ACTUAL", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Movimiento\ndetectado", color = PrimaryBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hace 2 minutos • Pasillo Principal", color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Activar Sensor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Activar sensor", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Automatización habilitada", color = TextGray, fontSize = 14.sp)
                        }
                    }
                    Switch(
                        checked = sensorEnabled,
                        onCheckedChange = { sensorEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apagar después de
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Apagar después de", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("5 min", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 min", color = TextGray, fontSize = 12.sp)
                        Text("5 min", color = TextGray, fontSize = 12.sp)
                        Text("10 min", color = TextGray, fontSize = 12.sp)
                        Text("30 min", color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Luces vinculadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Luces vinculadas", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.clickable { }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Añadir", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinkedLightItem("Techo Pasillo 1", "Intensidad: 80%")
            Spacer(modifier = Modifier.height(12.dp))
            LinkedLightItem("Lámpara Decorativa", "Intensidad: 40%")
            Spacer(modifier = Modifier.height(12.dp))
            LinkedLightItem("Luz de Cortesía", "Intensidad: 20%")
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LinkedLightItem(title: String, subtitle: String) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFBBF24).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFBBF24))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextGray, fontSize = 12.sp)
            }
            IconButton(onClick = { /* Delete */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextGray)
            }
        }
    }
}
