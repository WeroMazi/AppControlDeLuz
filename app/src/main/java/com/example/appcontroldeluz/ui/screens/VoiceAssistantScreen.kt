package com.example.appcontroldeluz.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.appcontroldeluz.backend.audio.WavAudioRecorder
import com.example.appcontroldeluz.ui.theme.LocalAppThemeColors
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.viewmodel.AppViewModel
import java.io.File

@Composable
fun VoiceAssistantScreen(
    viewModel: AppViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppThemeColors.current
    val voiceState by viewModel.voiceState.collectAsState()
    val recorder = remember { WavAudioRecorder() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.wav")
            recorder.start(file)
            viewModel.onVoiceRecordingStarted()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.stop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "voiceInfinite")
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textAlpha"
    )

    val glowSizeFloat by infiniteTransition.animateFloat(
        initialValue = 250f,
        targetValue = 350f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowSize"
    )
    val glowSize = glowSizeFloat.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background.copy(alpha = 0.98f))
            .padding(24.dp)
    ) {
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
                    onClick = {
                        recorder.stop()
                        viewModel.clearVoiceMessage()
                        onClose()
                    },
                    modifier = Modifier.background(colors.subtleContainer, CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.onBackground)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = when {
                    voiceState.processing -> "Procesando..."
                    voiceState.listening -> "Escuchando..."
                    else -> "Asistente de Voz"
                },
                color = colors.onBackground.copy(alpha = textAlpha),
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

            Row(
                modifier = Modifier.height(120.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(7) { index ->
                    AnimatedWaveBar(index)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(if (voiceState.listening) Color(0xFFEF5350) else PrimaryBlue)
                    .clickable(enabled = !voiceState.processing) {
                        if (voiceState.listening) {
                            viewModel.onVoiceRecordingStopped()
                            val file = recorder.stop()
                            if (file != null && file.exists() && file.length() > 44) {
                                viewModel.processVoiceAudio(file)
                            } else {
                                viewModel.clearVoiceMessage()
                            }
                        } else {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.wav")
                                recorder.start(file)
                                viewModel.onVoiceRecordingStarted()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (voiceState.listening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (voiceState.processing) {
                CircularProgressIndicator(color = PrimaryBlue)
            }

            if (voiceState.lastTranscription.isNotBlank()) {
                Text(
                    text = "Texto: ${voiceState.lastTranscription}",
                    color = colors.onBackground.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            if (voiceState.lastMessage.isNotBlank()) {
                Text(
                    text = voiceState.lastMessage,
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!voiceState.error.isNullOrBlank()) {
                Text(
                    text = voiceState.error ?: "",
                    color = Color(0xFFFF8A80),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("Prueba a decir...", color = colors.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            VoiceHintItem("Enciende las luces de la sala", Icons.Default.Lightbulb)
            Spacer(modifier = Modifier.height(12.dp))
            VoiceHintItem("Enciende el dormitorio", Icons.Default.Palette)
            Spacer(modifier = Modifier.height(12.dp))
            VoiceHintItem("Apaga todas las luces", Icons.Default.Bedtime)

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = {
                recorder.stop()
                viewModel.clearVoiceMessage()
                onClose()
            }) {
                Text("Cancelar", color = colors.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnimatedWaveBar(index: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "barTransition$index")

    val baseHeightFloat = when (index) {
        0, 6 -> 20f
        1, 5 -> 45f
        2, 4 -> 75f
        else -> 100f
    }

    val heightFloat by infiniteTransition.animateFloat(
        initialValue = baseHeightFloat * 0.4f,
        targetValue = baseHeightFloat,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 400 + (index * 50),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight$index"
    )
    val height = heightFloat.dp

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
fun VoiceHintItem(text: String, icon: ImageVector) {
    val colors = LocalAppThemeColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.subtleContainer)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text, color = colors.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = colors.onSurfaceVariant.copy(alpha = 0.4f))
        }
    }
}
