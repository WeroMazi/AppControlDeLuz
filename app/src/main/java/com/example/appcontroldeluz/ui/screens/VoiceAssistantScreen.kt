package com.example.appcontroldeluz.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.SurfaceDark
import com.example.appcontroldeluz.ui.theme.TextGray

@Composable
fun VoiceAssistantScreen(onClose: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "voiceInfinite")
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "textAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark.copy(alpha = 0.95f))
            .padding(24.dp)
    ) {
        // Efecto de brillo central animado
        val glowSize by infiniteTransition.animateValue(
            initialValue = 250.dp,
            targetValue = 350.dp,
            typeConverter = Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "glowSize"
        )

        Box(
            modifier = Modifier
                .size(glowSize)
                .align(Alignment.Center)
                .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape)
                .blur(80.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Escuchando...",
                color = Color.White.copy(alpha = textAlpha),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ADELANTE, ESTOY LISTO",
                color = PrimaryBlue.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Waveform animado
            Row(
                modifier = Modifier.height(120.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Generamos 7 barras con animaciones desfasadas
                repeat(7) { index ->
                    AnimatedWaveBar(index)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("Prueba a decir...", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            VoiceHintItem("Enciende las luces de la sala", Icons.Default.Lightbulb)
            Spacer(modifier = Modifier.height(12.dp))
            VoiceHintItem("Enciende el dormitorio", Icons.Default.Palette)
            Spacer(modifier = Modifier.height(12.dp))
            VoiceHintItem("Apagar todo", Icons.Default.Bedtime)

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onClose) {
                Text("Cancelar", color = Color.White.copy(alpha = 0.5f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnimatedWaveBar(index: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "barTransition$index")
    
    // Altura base segun la posicion (mas alta al centro)
    val baseHeight = when(index) {
        0, 6 -> 20.dp
        1, 5 -> 45.dp
        2, 4 -> 75.dp
        else -> 100.dp
    }

    val height by infiniteTransition.animateValue(
        initialValue = baseHeight * 0.4f,
        targetValue = baseHeight,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 400 + (index * 50), 
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "barHeight$index"
    )

    Box(
        modifier = Modifier
            .width(8.dp)
            .height(height)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryBlue.copy(alpha = 0.4f), PrimaryBlue)
                )
            )
    )
}

@Composable
fun VoiceHintItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(alpha = 0.2f))
        }
    }
}
