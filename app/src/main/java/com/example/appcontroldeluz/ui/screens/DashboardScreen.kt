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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.LocalAppThemeColors
import com.example.appcontroldeluz.ui.theme.PrimaryBlue

@Composable
fun DashboardScreen(
    ledStates: Map<Int, Boolean>,
    ledLabels: Map<Int, String>,
    isLoading: Boolean,
    isSendingCommand: Boolean,
    onToggleLed: (Int, Boolean) -> Unit,
    onToggleAll: (Boolean) -> Unit,
    onRenameLed: (Int, String) -> Unit
) {
    val colors = LocalAppThemeColors.current
    val leds = remember(ledStates, ledLabels) {
        (1..8).map { ledNumber ->
            LedItem(
                number = ledNumber,
                name = ledLabels[ledNumber] ?: "LED $ledNumber",
                isActive = ledStates[ledNumber] == true
            )
        }
    }
    val allLightsOff = leds.all { !it.isActive }

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
                .clickable(enabled = !isSendingCommand) { onToggleAll(allLightsOff) }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Control General", color = colors.onSurfaceVariant, fontSize = 14.sp)
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
                    contentDescription = "Controlar todo",
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
            items(leds.size) { index ->
                val led = leds[index]
                LedCard(
                    led = led,
                    commandEnabled = !isSendingCommand,
                    onToggle = { onToggleLed(led.number, it) },
                    onRename = { onRenameLed(led.number, it) }
                )
            }
        }

        if (isLoading || isSendingCommand) {
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

data class LedItem(
    val number: Int,
    val name: String,
    val isActive: Boolean
)

@Composable
fun LedCard(
    led: LedItem,
    commandEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onRename: (String) -> Unit
) {
    val colors = LocalAppThemeColors.current
    val bgColor = if (led.isActive) colors.surface else colors.surfaceVariant.copy(alpha = 0.8f)
    val iconBgColor = if (led.isActive) PrimaryBlue.copy(alpha = 0.2f) else colors.subtleContainer
    val iconColor = if (led.isActive) PrimaryBlue else colors.onSurfaceVariant
    val subtitleColor = if (led.isActive) PrimaryBlue else colors.onSurfaceVariant
    var renameDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(14.dp),
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
                Icon(Icons.Default.Lightbulb, contentDescription = "LED ${led.number}", tint = iconColor)
            }

            Switch(
                checked = led.isActive,
                enabled = commandEnabled,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = led.name,
                    color = colors.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { renameDialogOpen = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar nombre", tint = colors.onSurfaceVariant)
                }
            }
            Text(
                text = "LED ${led.number} • ${if (led.isActive) "Encendida" else "Todo apagado"}",
                color = subtitleColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (renameDialogOpen) {
        RenameLedDialog(
            ledNumber = led.number,
            initialName = led.name,
            onDismiss = { renameDialogOpen = false },
            onSave = {
                onRename(it)
                renameDialogOpen = false
            }
        )
    }
}

@Composable
private fun RenameLedDialog(
    ledNumber: Int,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar LED $ledNumber") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Ubicación") },
                placeholder = { Text("Ej. Patio frontal") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(name) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
