package com.example.appcontroldeluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.SurfaceDark
import com.example.appcontroldeluz.ui.theme.TextGray

@Composable
fun ScheduleScreen(onAddClick: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header exacto foto 3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Navegación manejada por el menú inferior */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D7CF3))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Programación", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryBlue)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Próxima Rutina foto 3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("PRÓXIMA RUTINA", color = Color(0xFF2D7CF3), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("23:00", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PM", color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Modo Noche: Apagar todo", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Icon(Icons.Default.Nightlight, contentDescription = null, tint = Color(0xFF2D7CF3), modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tus Rutinas foto 3
            Text("TUS RUTINAS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            RoutineItem("Despertar", "08:00 AM • Luces dormitorio", Icons.Default.WbSunny, Color(0xFFD97706), Color(0xFFFEF3C7).copy(alpha = 0.1f), true)
            Spacer(modifier = Modifier.height(16.dp))
            RoutineItem("Noche", "11:00 PM • Apagar todo", Icons.Default.Nightlight, Color(0xFF4F46E5), Color(0xFFE0E7FF).copy(alpha = 0.1f), true)
            Spacer(modifier = Modifier.height(16.dp))
            RoutineItem("Lectura", "04:00 PM • Luces cálidas", Icons.Default.Coffee, TextGray, Color.White.copy(alpha = 0.05f), false)
            
            // Espacio extra para asegurar que el scroll se note
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RoutineItem(title: String, subtitle: String, icon: ImageVector, iconColor: Color, iconBgColor: Color, isActive: Boolean) {
    var isChecked by remember { mutableStateOf(isActive) }
    
    val alpha = if (isChecked) 1f else 0.6f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark.copy(alpha = alpha))
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
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor.copy(alpha = alpha))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White.copy(alpha = alpha), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = TextGray.copy(alpha = alpha), fontSize = 14.sp)
                }
            }
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
    }
}
