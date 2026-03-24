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

    val previousStates = remember { mutableStateListOf<Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

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
                        previousStates.clear()
                        previousStates.addAll(rooms.map { it.isActive })
                        for (i in rooms.indices) {
                            rooms[i] = rooms[i].copy(isActive = false)
                        }
                    } else {
                        if (previousStates.isNotEmpty()) {
                            for (i in rooms.indices) {
                                if (i < previousStates.size) {
                                    rooms[i] = rooms[i].copy(isActive = previousStates[i])
                                }
                            }
                        }
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

        Text("Habitaciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(rooms.size) { index ->
                val room = rooms[index]
                RoomCard(
                    title = room.name,
                    subtitle = room.sub,
                    icon = room.icon,
                    isActive = room.isActive,
                    onToggle = { isActive ->
                        rooms[index] = room.copy(isActive = isActive)
                        if (isActive) allLightsOff = false
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

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(
                1.dp, 
                if (isActive) PrimaryBlue.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f), 
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isActive) PrimaryBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon, 
                        contentDescription = null, 
                        tint = if (isActive) PrimaryBlue else TextGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Switch(
                    checked = isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = TextGray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }
            
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        }
    }
}