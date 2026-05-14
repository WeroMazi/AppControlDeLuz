package com.example.appcontroldeluz.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcontroldeluz.analytics.AppTelemetry
import com.example.appcontroldeluz.data.model.SensorStatus
import com.example.appcontroldeluz.data.remote.ApiClient
import com.example.appcontroldeluz.data.config.HomeAutomationConfig
import com.example.appcontroldeluz.data.repository.LightsRepository
import com.example.appcontroldeluz.data.repository.SensorsRepository
import com.example.appcontroldeluz.utils.NetworkUtils
import android.content.SharedPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class VoiceUiState(
    val listening: Boolean = false,
    val processing: Boolean = false,
    val lastMessage: String = "",
    val lastTranscription: String = "",
    val error: String? = null
)

class AppViewModel(
    application: Application,
    private val repository: LightsRepository = LightsRepository(ApiClient.service),
    private val sensorsRepository: SensorsRepository = SensorsRepository(ApiClient.service),
    private val cacheStore: LightCacheStore = AndroidLightCacheStore(application)
) : AndroidViewModel(application) {

    private val settingsStore: SharedPreferences = application.getSharedPreferences("app_settings", 0)

    private val _lights = MutableStateFlow(
        mapOf(
            "sala" to false,
            "cocina" to false,
            "dormitorio" to false,
            "baño" to false,
            "jardin" to false,
            "garage" to false
        )
    )
    val lights: StateFlow<Map<String, Boolean>> = _lights.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()

    private val _voiceState = MutableStateFlow(VoiceUiState())
    val voiceState: StateFlow<VoiceUiState> = _voiceState.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(settingsStore.getBoolean("dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _sensorStatus = MutableStateFlow(
        SensorStatus(
            enabled = true,
            linkedLights = HomeAutomationConfig.defaultSensorLinkedLights
        )
    )
    val sensorStatus: StateFlow<SensorStatus> = _sensorStatus.asStateFlow()

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _isOnlineMode = MutableStateFlow(true)
    val isOnlineMode: StateFlow<Boolean> = _isOnlineMode.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000) // Mostrar animación por 2 segundos
            checkNetworkConnectivity()
            loadInitialData()
            _isInitializing.value = false
        }
    }

    private fun checkNetworkConnectivity() {
        _isNetworkAvailable.value = NetworkUtils.isNetworkAvailable(getApplication())
        _isOnlineMode.value = _isNetworkAvailable.value
    }

    fun refreshNetworkStatus() {
        checkNetworkConnectivity()
    }

    private suspend fun loadInitialData() {
        _isLoading.value = true
        // Load cached values first
        val cached = loadCachedLights()
        if (cached != null) {
            _lights.value = cached
        }

        repository.getLights()
            .onSuccess { remoteLights ->
                val normalized = repository.ensureAllRooms(remoteLights)
                _lights.value = normalized
                persistLights(normalized)
            }
            .onFailure { error ->
                _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo cargar el estado de luces")
            }

        sensorsRepository.getSensorStatus()
            .onSuccess { status ->
                _sensorStatus.value = status
            }
            .onFailure { error ->
                _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo cargar el estado del sensor")
            }

        _isLoading.value = false
    }

    private fun persistLights(map: Map<String, Boolean>) {
        map.forEach { (k, v) ->
            cacheStore.putBoolean("light_$k", v)
        }
    }

    private fun loadCachedLights(): Map<String, Boolean>? {
        // if no keys exist, return null
        if (!cacheStore.contains("light_sala")) return null
        return mapOf(
            "sala" to cacheStore.getBoolean("light_sala", false),
            "cocina" to cacheStore.getBoolean("light_cocina", false),
            "dormitorio" to cacheStore.getBoolean("light_dormitorio", false),
            "baño" to cacheStore.getBoolean("light_baño", false),
            "jardin" to cacheStore.getBoolean("light_jardin", false),
            "garage" to cacheStore.getBoolean("light_garage", false)
        )
    }

    fun refreshLights() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLights()
                .onSuccess { remoteLights ->
                    _lights.value = repository.ensureAllRooms(remoteLights)
                }
                .onFailure { error ->
                    _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo cargar el estado de luces")
                    AppTelemetry.recordException(error, "refreshLights failed")
                }
            _isLoading.value = false
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        if (_isDarkTheme.value == enabled) return
        _isDarkTheme.value = enabled
        settingsStore.edit().putBoolean("dark_theme", enabled).apply()
    }

    fun controlRoom(roomLabel: String, state: Boolean) {
        val room = repository.toCanonicalRoom(roomLabel)
        val previousLights = _lights.value
        _lights.value = previousLights.toMutableMap().apply { put(room, state) }
        AppTelemetry.logEvent(getApplication(), "toggle_room") {
            putString("room", room)
            putBoolean("state", state)
        }
        // persist optimistically
        persistLights(_lights.value)
        viewModelScope.launch {
            repository.controlLight(room, state)
                .onSuccess { updatedLights ->
                    _lights.value = repository.ensureAllRooms(updatedLights)
                    persistLights(_lights.value)
                }
                .onFailure { error ->
                    _lights.value = previousLights
                    persistLights(previousLights)
                    _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo ejecutar el comando")
                    AppTelemetry.recordException(error, "controlRoom failed room=\$room state=\$state")
                }
        }
    }

    fun controlAll(state: Boolean) {
        val previousLights = _lights.value
        _lights.value = _lights.value.mapValues { state }
        AppTelemetry.logEvent(getApplication(), "toggle_all") {
            putBoolean("state", state)
        }
        persistLights(_lights.value)
        viewModelScope.launch {
            repository.controlLight("todas", state)
                .onSuccess { updatedLights ->
                    _lights.value = repository.ensureAllRooms(updatedLights)
                    persistLights(_lights.value)
                }
                .onFailure { error ->
                    _lights.value = previousLights
                    persistLights(previousLights)
                    _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo cambiar todas las luces")
                    AppTelemetry.recordException(error, "controlAll failed state=\$state")
                }
        }
    }

    fun onVoiceRecordingStarted() {
        _voiceState.value = VoiceUiState(listening = true, processing = false)
    }

    fun onVoiceRecordingStopped() {
        _voiceState.value = _voiceState.value.copy(listening = false, processing = true, error = null)
    }

    fun processVoiceAudio(audioFile: File) {
        viewModelScope.launch {
            repository.processVoiceCommand(audioFile)
                .onSuccess { response ->
                    AppTelemetry.logEvent(getApplication(), "voice_command") {
                        putString("message", response.message)
                        putBoolean("executed", response.commandExecuted)
                    }
                    val normalizedLights = response.lights?.let(repository::ensureAllRooms)
                    if (normalizedLights != null) {
                        _lights.value = normalizedLights
                    }

                    _voiceState.value = VoiceUiState(
                        listening = false,
                        processing = false,
                        lastMessage = response.message,
                        lastTranscription = response.transcription,
                        error = if (response.commandExecuted) null else response.message
                    )
                }
                .onFailure { error ->
                    _voiceState.value = VoiceUiState(
                        listening = false,
                        processing = false,
                        error = error.message ?: "No se pudo procesar el comando de voz"
                    )
                    AppTelemetry.recordException(error, "processVoiceAudio failed")
                }
        }
    }

    fun clearVoiceMessage() {
        _voiceState.value = _voiceState.value.copy(lastMessage = "", lastTranscription = "", error = null)
    }

    fun setSensorEnabled(enabled: Boolean) {
        val previous = _sensorStatus.value
        _sensorStatus.value = previous.copy(enabled = enabled)

        viewModelScope.launch {
            sensorsRepository.setSensorEnabled(enabled)
                .onSuccess { updated ->
                    _sensorStatus.value = updated
                }
                .onFailure { error ->
                    _sensorStatus.value = previous
                    _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo actualizar el sensor")
                    AppTelemetry.recordException(error, "setSensorEnabled failed enabled=\$enabled")
                }
        }
    }

    fun removeLinkedLightFromSensor(lightId: String) {
        val previous = _sensorStatus.value
        _sensorStatus.value = previous.copy(
            linkedLights = previous.linkedLights.filterNot { it.id == lightId }
        )

        viewModelScope.launch {
            sensorsRepository.removeLinkedLight(lightId)
                .onSuccess { updated ->
                    _sensorStatus.value = updated
                }
                .onFailure { error ->
                    _sensorStatus.value = previous
                    _voiceState.value = _voiceState.value.copy(error = error.message ?: "No se pudo desvincular la luz")
                    AppTelemetry.recordException(error, "removeLinkedLightFromSensor failed lightId=\$lightId")
                }
        }
    }
}

interface LightCacheStore {
    fun contains(key: String): Boolean
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
}

class AndroidLightCacheStore(application: Application) : LightCacheStore {
    private val prefs: SharedPreferences = application.getSharedPreferences("lights_cache", 0)

    override fun contains(key: String): Boolean = prefs.contains(key)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)

    override fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
}

class MemoryLightCacheStore : LightCacheStore {
    private val values = mutableMapOf<String, Any>()

    override fun contains(key: String): Boolean = values.containsKey(key)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = values[key] as? Boolean ?: defaultValue

    override fun putBoolean(key: String, value: Boolean) {
        values[key] = value
    }

    override fun getInt(key: String, defaultValue: Int): Int = values[key] as? Int ?: defaultValue

    override fun putInt(key: String, value: Int) {
        values[key] = value
    }
}
