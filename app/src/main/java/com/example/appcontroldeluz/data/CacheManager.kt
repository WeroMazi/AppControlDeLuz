package com.example.appcontroldeluz.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val Context.dataStore by preferencesDataStore("app_cache")

class CacheManager(private val context: Context) {
    
    private val memoryCache = ConcurrentHashMap<String, Any>()
    private val CACHE_DURATION = 30 * 60 * 1000L // 30 minutos
    
    companion object {
        private const val USER_CACHE_KEY = "cached_user"
        private const val LIGHTS_CACHE_KEY = "cached_lights"
        private const val ROOMS_CACHE_KEY = "cached_rooms"
        private const val LAST_LOGIN_KEY = "last_login_time"
        private const val AUTO_LOGIN_KEY = "auto_login_enabled"
        private const val DEVICE_SETTINGS_KEY = "device_settings"
    }

    // ============ USUARIO ============
    
    fun getCachedUserFlow(): Flow<CachedUser?> = context.dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(USER_CACHE_KEY)]?.let { json ->
            try {
                Json.decodeFromString<CachedUser>(json)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun cacheUser(user: CachedUser) {
        try {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(USER_CACHE_KEY)] = Json.encodeToString(user)
                preferences[longPreferencesKey(LAST_LOGIN_KEY)] = System.currentTimeMillis()
            }
            memoryCache[USER_CACHE_KEY] = user
        } catch (e: Exception) {
            // Error guardando usuario
        }
    }

    suspend fun clearUserCache() {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(USER_CACHE_KEY))
            preferences.remove(longPreferencesKey(LAST_LOGIN_KEY))
        }
        memoryCache.remove(USER_CACHE_KEY)
    }

    // ============ LUCES ============
    
    fun getCachedLightsFlow(): Flow<List<CachedLight>?> = context.dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(LIGHTS_CACHE_KEY)]?.let { json ->
            try {
                Json.decodeFromString<CachedLightsList>(json).lights
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun cacheLights(lights: List<CachedLight>) {
        try {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(LIGHTS_CACHE_KEY)] = Json.encodeToString(CachedLightsList(lights))
            }
            memoryCache[LIGHTS_CACHE_KEY] = lights
        } catch (e: Exception) {
            // Error al guardar
        }
    }

    suspend fun updateLightCache(lightId: String, isOn: Boolean, brightness: Int = 100) {
        getCachedLights()?.let { lights ->
            val updated = lights.map { light ->
                if (light.id == lightId) {
                    light.copy(isOn = isOn, brightness = brightness)
                } else {
                    light
                }
            }
            cacheLights(updated)
        }
    }

    suspend fun clearLightsCache() {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(LIGHTS_CACHE_KEY))
        }
        memoryCache.remove(LIGHTS_CACHE_KEY)
    }

    // ============ HABITACIONES ============
    
    fun getCachedRoomsFlow(): Flow<List<CachedRoom>?> = context.dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(ROOMS_CACHE_KEY)]?.let { json ->
            try {
                Json.decodeFromString<CachedRoomsList>(json).rooms
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun cacheRooms(rooms: List<CachedRoom>) {
        try {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(ROOMS_CACHE_KEY)] = Json.encodeToString(CachedRoomsList(rooms))
            }
            memoryCache[ROOMS_CACHE_KEY] = rooms
        } catch (e: Exception) {
            // Error al guardar
        }
    }

    suspend fun clearRoomsCache() {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(ROOMS_CACHE_KEY))
        }
        memoryCache.remove(ROOMS_CACHE_KEY)
    }

    // ============ AUTENTICACIÓN ============
    
    suspend fun setAutoLoginEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(AUTO_LOGIN_KEY)] = enabled
        }
    }

    fun getAutoLoginEnabledFlow(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(AUTO_LOGIN_KEY)] ?: false
    }

    fun isLoginCacheValid(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        val lastLogin = preferences[longPreferencesKey(LAST_LOGIN_KEY)] ?: 0
        val currentTime = System.currentTimeMillis()
        (currentTime - lastLogin) < CACHE_DURATION
    }

    // ============ CONFIGURACIÓN DEL DISPOSITIVO ============
    
    suspend fun cacheDeviceSettings(settings: Map<String, String>) {
        context.dataStore.edit { preferences ->
            settings.forEach { (key, value) ->
                preferences[stringPreferencesKey("device_$key")] = value
            }
        }
    }

    fun getDeviceSettingFlow(key: String): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[stringPreferencesKey("device_$key")]
    }

    // ============ MÉTODOS SINCRONOS (DESDE MEMORIA) ============
    
    fun getCachedUserSync(): CachedUser? = memoryCache[USER_CACHE_KEY] as? CachedUser

    fun getCachedLights(): List<CachedLight>? = memoryCache[LIGHTS_CACHE_KEY] as? List<CachedLight>

    fun getCachedRooms(): List<CachedRoom>? = memoryCache[ROOMS_CACHE_KEY] as? List<CachedRoom>

    // ============ LIMPIAR TODO ============
    
    suspend fun clearAllCache() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
        memoryCache.clear()
    }

    fun printCacheStats() {
        println("""
            === CACHE STATISTICS ===
            Memory Cache Size: ${memoryCache.size}
            Memory Cache Keys: ${memoryCache.keys}
            ========================
        """.trimIndent())
    }
}
