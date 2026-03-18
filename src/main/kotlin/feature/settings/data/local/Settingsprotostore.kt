package com.oussama_chatri.feature.settings.data.local

import com.oussama_chatri.core.theme.AppThemeId
import com.oussama_chatri.feature.settings.domain.model.AppSettings
import com.oussama_chatri.feature.settings.domain.model.UnitSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoBuf
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

@Serializable
private data class SettingsProto(
    @ProtoNumber(1) val themeId:           String = AppThemeId.PETROLEUM_DARK.name,
    @ProtoNumber(2) val unitSystem:        String = UnitSystem.API.name,
    @ProtoNumber(3) val decimalPrecision:  Int    = 2,
    @ProtoNumber(4) val defaultExportPath: String = "",
    @ProtoNumber(5) val defaultProjectPath:String = "",
    @ProtoNumber(6) val autoSaveInterval:  Int    = 0
)

class SettingsProtoStore {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val storeFile: File by lazy {
        val os   = System.getProperty("os.name", "").lowercase()
        val base = when {
            os.contains("win") -> File(System.getenv("APPDATA") ?: System.getProperty("user.home"), "WellLogAnalyzer")
            os.contains("mac") -> File(System.getProperty("user.home"), "Library/Application Support/WellLogAnalyzer")
            else               -> File(System.getProperty("user.home"), ".local/share/WellLogAnalyzer")
        }
        File(base, "data").also { it.mkdirs() }.let { File(it, "settings.pb") }
    }

    private val _state = MutableStateFlow(load())

    val flow: StateFlow<AppSettings> = _state.asStateFlow()

    fun read(): AppSettings = _state.value

    fun write(settings: AppSettings) {
        _state.value = settings
        persist(settings)
    }

    private fun load(): AppSettings {
        if (!storeFile.exists()) return AppSettings()
        return try {
            val proto = ProtoBuf.decodeFromByteArray<SettingsProto>(storeFile.readBytes())
            AppSettings(
                themeId           = runCatching { AppThemeId.valueOf(proto.themeId) }.getOrDefault(AppThemeId.PETROLEUM_DARK),
                unitSystem        = runCatching { UnitSystem.valueOf(proto.unitSystem) }.getOrDefault(UnitSystem.API),
                decimalPrecision  = proto.decimalPrecision.coerceIn(0, 6),
                defaultExportPath = proto.defaultExportPath,
                defaultProjectPath= proto.defaultProjectPath,
                autoSaveIntervalMinutes = proto.autoSaveInterval
            )
        } catch (e: Exception) {
            logger.warn("Could not decode settings — using defaults. Reason: ${e.message}")
            AppSettings()
        }
    }

    private fun persist(s: AppSettings) {
        try {
            val proto = SettingsProto(
                themeId           = s.themeId.name,
                unitSystem        = s.unitSystem.name,
                decimalPrecision  = s.decimalPrecision,
                defaultExportPath = s.defaultExportPath,
                defaultProjectPath= s.defaultProjectPath,
                autoSaveInterval  = s.autoSaveIntervalMinutes
            )
            val bytes = ProtoBuf.encodeToByteArray(proto)
            val tmp   = Files.createTempFile(storeFile.parentFile.toPath(), "settings", ".tmp").toFile()
            tmp.writeBytes(bytes)
            tmp.renameTo(storeFile)
            logger.debug("Settings saved: theme=${s.themeId.name}")
        } catch (e: Exception) {
            logger.error("Failed to persist settings", e)
        }
    }
}