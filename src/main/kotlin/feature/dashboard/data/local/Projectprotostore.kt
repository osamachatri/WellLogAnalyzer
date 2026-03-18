package com.oussama_chatri.feature.dashboard.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

class ProjectProtoStore {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val storeFile: File by lazy {
        val dir = resolveAppDataDir()
        dir.mkdirs()
        File(dir, "projects.pb")
    }

    private val _state = MutableStateFlow<ProjectListProto>(ProjectListProto())

    init {
        _state.value = load()
    }

    fun observe(): Flow<ProjectListProto> = _state.asStateFlow()

    fun read(): ProjectListProto = _state.value

    fun write(list: ProjectListProto) {
        _state.value = list
        persist(list)
    }

    private fun load(): ProjectListProto {
        if (!storeFile.exists()) return ProjectListProto()
        return try {
            ProtoBuf.decodeFromByteArray(storeFile.readBytes())
        } catch (e: Exception) {
            logger.warn("Could not decode projects store — resetting. Reason: ${e.message}")
            ProjectListProto()
        }
    }

    private fun persist(list: ProjectListProto) {
        try {
            val bytes = ProtoBuf.encodeToByteArray(list)
            // Atomic write via temp file
            val tmp = Files.createTempFile(storeFile.parentFile.toPath(), "projects", ".tmp").toFile()
            tmp.writeBytes(bytes)
            tmp.renameTo(storeFile)
            logger.debug("Saved ${list.projects.size} project(s) to ${storeFile.absolutePath}")
        } catch (e: Exception) {
            logger.error("Failed to persist projects store", e)
        }
    }

    private fun resolveAppDataDir(): File {
        val os = System.getProperty("os.name", "").lowercase()
        val base = when {
            os.contains("win") -> File(System.getenv("APPDATA") ?: System.getProperty("user.home"), "WellLogAnalyzer")
            os.contains("mac") -> File(System.getProperty("user.home"), "Library/Application Support/WellLogAnalyzer")
            else               -> File(System.getProperty("user.home"), ".local/share/WellLogAnalyzer")
        }
        return File(base, "data")
    }
}