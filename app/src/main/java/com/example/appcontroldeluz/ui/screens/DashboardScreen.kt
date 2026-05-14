package com.example.appcontroldeluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.LocalAppThemeColors
import com.example.appcontroldeluz.ui.theme.PrimaryBlue

@Composable
fun DashboardScreen(
    lights: Map<String, Boolean>,
    isLoading: Boolean,
    onToggleRoom: (String, Boolean) -> Unit,
    onToggleAll: (Boolean) -> Unit,
    onRoomClick: () -> Unit
) {
    val colors = LocalAppThemeColors.current
    val rooms = remember(lights) { mapRooms(lights) }
    val allLightsOff = rooms.all { !it.isActive }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
                color = colors.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.border, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(colors.gradientStart, colors.gradientEnd)
                    )
                )
                .border(
                    1.dp,
                    if (allLightsOff) PrimaryBlue else colors.border,
                    RoundedCornerShape(20.dp)
                )
                .clickable { onToggleAll(allLightsOff) }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ahorro de Energía", color = colors.onSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (allLightsOff) "Encender Todas las Luces" else "Apagar Todas las Luces",
                        color = colors.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    Icons.Rounded.PowerSettingsNew,
                    contentDescription = "Apagar todo",
                    tint = if (allLightsOff) colors.onSurfaceVariant else colors.onBackground,
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
                    subtitle = if (room.isActive) "Encendida" else "Todo apagado",
                    icon = room.icon,
                    isActive = room.isActive,
                    onToggle = { onToggleRoom(room.backendName, it) },
                    onClick = onRoomClick
                )
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = PrimaryBlue,
                trackColor = colors.surfaceVariant
            )
        }
    }
}

data class RoomItem(
    val backendName: String,
    val name: String,
    val icon: ImageVector,
    val isActive: Boolean
)

private fun mapRooms(lights: Map<String, Boolean>): List<RoomItem> {
    return listOf(
        RoomItem("sala", "Sala", Icons.Default.Weekend, lights["sala"] == true),
        RoomItem("cocina", "Cocina", Icons.Default.Restaurant, lights["cocina"] == true),
        RoomItem("dormitorio", "Dormitorio", Icons.Default.Bed, lights["dormitorio"] == true),
        RoomItem("baño", "Baño", Icons.Default.Bathtub, lights["baño"] == true),
        RoomItem("jardin", "Jardín", Icons.AutoMirrored.Filled.DirectionsWalk, lights["jardin"] == true),
        RoomItem("garage", "Garage", Icons.Default.DirectionsCar, lights["garage"] == true)
    )
}

@Composable
fun RoomCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val colors = LocalAppThemeColors.current
    val bgColor = if (isActive) colors.surface else colors.surfaceVariant.copy(alpha = 0.8f)
    val iconBgColor = if (isActive) PrimaryBlue.copy(alpha = 0.2f) else colors.subtleContainer
    val iconColor = if (isActive) PrimaryBlue else colors.onSurfaceVariant
    val subtitleColor = if (isActive) PrimaryBlue else colors.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
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
            Text(title, color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = subtitleColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
