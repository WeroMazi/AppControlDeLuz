package com.example.appcontroldeluz.cache

import com.example.appcontroldeluz.AppControldeluzApplication
import com.example.appcontroldeluz.data.CachedLight
import com.example.appcontroldeluz.data.CachedRoom
import com.example.appcontroldeluz.data.CachedUser
import kotlinx.coroutines.launch

/**
 * EJEMPLOS DE USO DEL SISTEMA DE CACHÉ
 * Usa estos ejemplos como referencia en tus pantallas
 */

// ============================================================
// EJEMPLO 1: LOGIN CON CACHÉ
// ============================================================
/*
@Composable
fun LoginScreenExample(onLoginSuccess: () -> Unit) {
    val cacheManager = AppControldeluzApplication.cacheManager
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Button(onClick = {
        scope.launch {
            // Realizar login con tu API
            val isValid = apiLogin(email, password)
            
            if (isValid) {
                // Guardar usuario en caché
                val user = CachedUser(
                    email = email,
                    username = email.substringBefore("@"),
                    isVerified = true
                )
                cacheManager.cacheUser(user)
                
                // Si marcó "recuérdame"
                if (rememberMe) {
                    cacheManager.setAutoLoginEnabled(true)
                }
                
                onLoginSuccess()
            }
        }
    }) {
        Text("Iniciar Sesión")
    }
}
*/

// ============================================================
// EJEMPLO 2: DASHBOARD CARGANDO LUCES DESDE CACHÉ
// ============================================================
/*
@Composable
fun DashboardExample() {
    val cacheManager = AppControldeluzApplication.cacheManager
    var lights by remember { mutableStateOf<List<CachedLight>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        // Primero intenta memoria (instantáneo)
        val lightsFromMemory = cacheManager.getCachedLights()
        if (lightsFromMemory != null) {
            lights = lightsFromMemory
            isLoading = false
        } else {
            // Si no, espera del DataStore (persiste entre sesiones)
            cacheManager.getCachedLightsFlow().collect { cachedLights ->
                cachedLights?.let {
                    lights = it
                    isLoading = false
                }
            }
        }
        
        // Opcionalmente, sincronizar con servidor en background
        fetchLightsFromServer()?.let { serverLights ->
            cacheManager.cacheLights(serverLights)
            lights = serverLights
        }
    }
    
    if (isLoading) {
        LoadingScreen()
    } else {
        LightsGrid(lights = lights)
    }
}
*/

// ============================================================
// EJEMPLO 3: TOGGLE LIGHT (Encender/Apagar)
// ============================================================
/*
@Composable
fun LightToggleExample(lightId: String) {
    val cacheManager = AppControldeluzApplication.cacheManager
    val scope = rememberCoroutineScope()
    var isOn by remember { mutableStateOf(false) }
    
    Switch(
        checked = isOn,
        onCheckedChange = { newState ->
            isOn = newState
            
            // Actualizar en caché inmediatamente
            scope.launch {
                cacheManager.updateLightCache(
                    lightId = lightId,
                    isOn = newState,
                    brightness = 100
                )
                
                // Sincronizar con servidor en background
                updateLightOnServer(lightId, newState)
            }
        }
    )
}
*/

// ============================================================
// EJEMPLO 4: AUTO-LOGIN DESDE CACHÉ
// ============================================================
/*
@Composable
fun SplashScreenExample(onNavigate: (String) -> Unit) {
    val cacheManager = AppControldeluzApplication.cacheManager
    
    LaunchedEffect(Unit) {
        // Verificar si hay sesión válida en caché
        cacheManager.getAutoLoginEnabledFlow().collect { autoLoginEnabled ->
            if (autoLoginEnabled) {
                cacheManager.isLoginCacheValid().collect { isValid ->
                    if (isValid) {
                        // Session válida, ir directo al dashboard
                        onNavigate("dashboard")
                    } else {
                        // Session expirada, ir al login
                        onNavigate("login")
                    }
                }
            } else {
                onNavigate("login")
            }
        }
    }
}
*/

// ============================================================
// EJEMPLO 5: CACHÉ DE HABITACIONES
// ============================================================
/*
@Composable
fun RoomsListExample() {
    val cacheManager = AppControldeluzApplication.cacheManager
    var rooms by remember { mutableStateOf<List<CachedRoom>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // Cargar habitaciones
    LaunchedEffect(Unit) {
        val cachedRooms = cacheManager.getCachedRooms()
        if (cachedRooms != null) {
            rooms = cachedRooms
        } else {
            // Obtener del servidor
            val serverRooms = fetchRoomsFromServer()
            cacheManager.cacheRooms(serverRooms)
            rooms = serverRooms
        }
    }
    
    LazyColumn {
        items(rooms) { room ->
            RoomItem(room)
        }
    }
}
*/

// ============================================================
// EJEMPLO 6: SINCRONIZAR DATOS DEL SERVIDOR
// ============================================================
/*
fun syncWithServer(cacheManager: CacheManager) {
    // Obtener datos del servidor
    val serverLights = fetchLightsFromServer()
    val serverRooms = fetchRoomsFromServer()
    val serverUser = fetchUserFromServer()
    
    // Guardar en caché (actualiza automáticamente)
    scope.launch {
        cacheManager.cacheLights(serverLights)
        cacheManager.cacheRooms(serverRooms)
        serverUser?.let { cacheManager.cacheUser(it) }
    }
}
*/

// ============================================================
// EJEMPLO 7: AJUSTES CON CACHÉ
// ============================================================
/*
@Composable
fun SettingsExample(cacheManager: CacheManager) {
    val scope = rememberCoroutineScope()
    var brightness by remember { mutableStateOf(100) }
    var colorTemp by remember { mutableStateOf(4000) }
    
    Slider(
        value = brightness.toFloat(),
        onValueChange = { newValue ->
            brightness = newValue.toInt()
            scope.launch {
                // Guardar en configuración
                cacheManager.cacheDeviceSettings(mapOf(
                    "brightness" to brightness.toString(),
                    "colorTemp" to colorTemp.toString()
                ))
            }
        }
    )
}
*/

// ============================================================
// EJEMPLO 8: LOGOUT (Limpiar Caché)
// ============================================================
/*
fun logoutExample(cacheManager: CacheManager, scope: CoroutineScope) {
    scope.launch {
        // Opción 1: Limpiar todo
        cacheManager.clearAllCache()
        
        // Opción 2: Limpiar solo usuario
        // cacheManager.clearUserCache()
        
        // Desactivar auto-login
        cacheManager.setAutoLoginEnabled(false)
        
        // Navegar al login
        navigateTo("login")
    }
}
*/

// ============================================================
// EJEMPLO 9: MONITOREAR CAMBIOS EN CACHÉ
// ============================================================
/*
@Composable
fun MonitorCacheExample() {
    val cacheManager = AppControldeluzApplication.cacheManager
    
    // Monitorear cambios en luces
    LaunchedEffect(Unit) {
        cacheManager.getCachedLightsFlow().collect { lights ->
            println("Luces en caché actualizadas: ${lights?.size}")
        }
    }
    
    // Monitorear cambios en usuario
    LaunchedEffect(Unit) {
        cacheManager.getCachedUserFlow().collect { user ->
            if (user != null) {
                println("Usuario en caché: ${user.email}")
            }
        }
    }
}
*/

// ============================================================
// EJEMPLO 10: LIMPIEZA PERIÓDICA DE CACHÉ
// ============================================================
/*
fun setupCacheCleanup(context: Context, cacheManager: CacheManager) {
    // Ejecutar cada 1 hora
    val interval = 60 * 60 * 1000L
    
    Timer().scheduleAtFixedRate(
        initialDelay = interval,
        period = interval
    ) {
        runBlocking {
            // Verificar si caché expiró y limpiar
            cacheManager.clearAllCache()
            println("Caché limpiado por expiración")
        }
    }
}
*/

// Funciones auxiliares (reemplaza con tus llamadas reales al servidor)
suspend fun apiLogin(email: String, password: String): Boolean {
    // Implementar llamada a tu API
    return true
}

suspend fun fetchLightsFromServer(): List<CachedLight>? {
    // Implementar llamada a tu API
    return null
}

suspend fun fetchRoomsFromServer(): List<CachedRoom>? {
    // Implementar llamada a tu API
    return null
}

suspend fun fetchUserFromServer(): CachedUser? {
    // Implementar llamada a tu API
    return null
}

suspend fun updateLightOnServer(lightId: String, isOn: Boolean) {
    // Implementar llamada a tu API
}
