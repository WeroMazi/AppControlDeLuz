package com.example.appcontroldeluz.data

import kotlinx.serialization.Serializable

@Serializable
data class CachedUser(
    val email: String,
    val username: String,
    val isVerified: Boolean = false
)

@Serializable
data class CachedLight(
    val id: String,
    val name: String,
    val room: String,
    val isOn: Boolean,
    val brightness: Int = 100,
    val colorTemp: Int = 4000 // Temperatura de color en Kelvin
)

@Serializable
data class CachedRoom(
    val id: String,
    val name: String,
    val icon: String,
    val lightsCount: Int,
    val isAnyLightOn: Boolean
)

@Serializable
data class CacheMetadata(
    val lastUpdated: Long,
    val expiresAt: Long,
    val version: Int = 1
)

// Wrappers para listas (necesarios para serialización)
@Serializable
data class CachedLightsList(val lights: List<CachedLight>)

@Serializable
data class CachedRoomsList(val rooms: List<CachedRoom>)
