package com.example.appcontroldeluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.SurfaceDark
import com.example.appcontroldeluz.ui.theme.TextGray

@Composable
fun AddLightsScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header foto 7
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
                text = "Agregar Nuevas Luces",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Animación/Icono de búsqueda foto 7
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Buscando dispositivos...",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Asegúrate de que tus luces estén\nencendidas y cerca de tu teléfono.",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Stop search */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Text("Detener búsqueda", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Dispositivos Encontrados foto 7
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("DISPOSITIVOS ENCONTRADOS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Badge(containerColor = PrimaryBlue) {
                    Text("2", color = Color.White, modifier = Modifier.padding(2.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DeviceFoundItem("Bombilla Sala 01", "Philips Hue • Bluetooth")
            Spacer(modifier = Modifier.height(12.dp))
            DeviceFoundItem("Luz de Techo", "Yeelight Color • Wi-Fi")

            Spacer(modifier = Modifier.height(40.dp))

            // No ves tu dispositivo foto 7
            Text("¿NO VES TU DISPOSITIVO?", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExtraOptionItem("Escanear QR", Icons.Default.QrCodeScanner, Modifier.weight(1f))
                ExtraOptionItem("Ingresar ID", Icons.Default.Pin, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DeviceFoundItem(name: String, brand: String) {
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TextGray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(brand, color = TextGray, fontSize = 12.sp)
            }
            Button(
                onClick = { /* Add */ },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Agregar", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ExtraOptionItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
