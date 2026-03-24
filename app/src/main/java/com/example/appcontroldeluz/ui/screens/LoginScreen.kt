package com.example.appcontroldeluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcontroldeluz.AppControldeluzApplication
import com.example.appcontroldeluz.data.CachedUser
import com.example.appcontroldeluz.ui.theme.PrimaryBlue
import com.example.appcontroldeluz.ui.theme.BackgroundDark
import com.example.appcontroldeluz.ui.theme.TextGray
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val cacheManager = AppControldeluzApplication.cacheManager
    val scope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Verificar si hay sesión en caché válida
    LaunchedEffect(Unit) {
        val autoLoginEnabled = cacheManager.getAutoLoginEnabledFlow().first()
        if (autoLoginEnabled) {
            val isCacheValid = cacheManager.isLoginCacheValid().first()
            if (isCacheValid) {
                onLoginSuccess()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A2234),
                        BackgroundDark
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-200).dp)
                .background(PrimaryBlue.copy(alpha = 0.2f), shape = CircleShape)
                .blur(100.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Logo",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(50.dp)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .background(PrimaryBlue, CircleShape)
                        .border(2.dp, BackgroundDark, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "LumaControl",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Bienvenido de nuevo, ilumina tu mundo.",
                color = TextGray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (showError) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            errorMessage,
                            color = Color(0xFFC62828),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Correo Electrónico",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("nombre@ejemplo.com", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color.Gray)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Contraseña",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            enabled = !isLoading
                        ) {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            Icon(icon, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Prueba: demo@luma.com / 123456",
                    color = TextGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isLoading) { rememberMe = !rememberMe },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            enabled = !isLoading,
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryBlue,
                                uncheckedColor = Color.White.copy(alpha = 0.3f),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            "Recuérdame",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable(enabled = !isLoading) { /* Handle forgot password */ }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.length >= 6) {
                            isLoading = true
                            showError = false
                            
                            scope.launch {
                                try {
                                    val isValid = validateCredentials(email, password)
                                    
                                    if (isValid) {
                                        val user = CachedUser(
                                            email = email,
                                            username = email.substringBefore("@"),
                                            isVerified = true
                                        )
                                        cacheManager.cacheUser(user)
                                        
                                        if (rememberMe) {
                                            cacheManager.setAutoLoginEnabled(true)
                                        }
                                        
                                        isLoading = false
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = "Usuario o contraseña incorrectos"
                                        showError = true
                                        isLoading = false
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                    showError = true
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMessage = if (email.isEmpty()) "Ingresa un correo" else "La contraseña debe tener al menos 6 caracteres"
                            showError = true
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun validateCredentials(email: String, password: String): Boolean {
    val normalizedEmail = email.trim().lowercase()
    val normalizedPassword = password.trim()

    val testLogin = normalizedEmail == "demo@luma.com" && normalizedPassword == "123456"
    val basicValidation = normalizedEmail.contains("@") && normalizedPassword.length >= 6

    return testLogin || basicValidation
}