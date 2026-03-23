package com.example.appcontroldeluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.SurfaceDark
import com.example.appcontroldeluz.ui.theme.TextGray
import com.example.appcontroldeluz.ui.theme.CardGradientStart
import com.example.appcontroldeluz.ui.theme.CardGradientEnd

@Composable
fun DashboardScreen(onRoomClick: () -> Unit) {
    var allLightsOff by remember { mutableStateOf(false) }
    
    // Grid de Habitaciones con persistencia de estado previo
    val rooms = remember {
        mutableStateListOf(
            RoomItem("Sala", "3 luces encendidas", Icons.Default.Weekend, true),
            RoomItem("Cocina", "Todo apagado", Icons.Default.Restaurant, false),
            RoomItem("Dormitorio", "1 luz encendida", Icons.Default.Bed, true),
            RoomItem("Oficina", "Todo apagado", Icons.Default.Computer, false),
            RoomItem("Baño", "Todo apagado", Icons.Default.Bathtub, false),
            RoomItem("Pasillo", "Todo apagado", Icons.AutoMirrored.Filled.DirectionsWalk, false)
        )
    }

    // Guardamos el estado previo para recuperarlo cuando se desmarque "Apagar Todo"
    val previousStates = remember { mutableStateListOf<Boolean>().apply { 
        addAll(rooms.map { it.isActive }) 
    } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Buenas Noches,\nAlex",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
            
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Tarjeta Principal (Maestro de Apagado)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(CardGradientStart, CardGradientEnd)
                    )
                )
                .border(1.dp, if(allLightsOff) PrimaryBlue else Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                .clickable {
                    allLightsOff = !allLightsOff
                    if (allLightsOff) {
                        // Guardar estados actuales antes de apagar todo
                        previousStates.clear()
                        previousStates.addAll(rooms.map { it.isActive })
                        // Apagar todas
                        rooms.indices.forEach { i -> rooms[i] = rooms[i].copy(isActive = false) }
                    } else {
                        // Restaurar estados previos
                        rooms.indices.forEach { i -> rooms[i] = rooms[i].copy(isActive = previousStates[i]) }
                    }
                }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ahorro de Energía", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if(allLightsOff) "Luces Apagadas" else "Apagar Todas las Luces", 
                        color = Color.White, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    Icons.Rounded.PowerSettingsNew, 
                    contentDescription = "Apagar todo", 
                    tint = if(allLightsOff) PrimaryBlue else Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(rooms.size) { index ->
                val room = rooms[index]
                RoomCard(
                    title = room.name,
                    subtitle = if (room.isActive) room.sub else "Todo apagado",
                    icon = room.icon,
                    isActive = room.isActive,
                    onToggle = { 
                        rooms[index] = room.copy(isActive = it)
                        // Si prendemos una individualmente, quitamos el modo "Apagar todo"
                        if(it) allLightsOff = false
                    },
                    onClick = onRoomClick
                )
            }
        }
    }
}

data class RoomItem(val name: String, val sub: String, val icon: ImageVector, val isActive: Boolean)

@Composable
fun RoomCard(
    title: String, 
    subtitle: String, 
    icon: ImageVector, 
    isActive: Boolean, 
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) SurfaceDark else SurfaceDark.copy(alpha = 0.7f)
    val iconBgColor = if (isActive) PrimaryBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
    val iconColor = if (isActive) PrimaryBlue else TextGray
    val subtitleColor = if (isActive) PrimaryBlue else TextGray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor)
            }
            
            Switch(
                checked = isActive,
                onCheckedChange = { onToggle(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }

        Column {
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = subtitleColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
