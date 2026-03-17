package com.oussama_chatri.feature.wellinput.data.local

import com.oussama_chatri.core.util.FileUtil
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Low-level ProtoBuf file store for [WellProfileProto] objects.
 *
 * Each profile is saved as a separate binary file:
 *   `<appData>/well_profiles/<id>.pb`
 *
 * This class is an implementation detail of the data layer.
 * The repository is the only caller.
 */
class WellProfileProtoStore {

    private val logger = LoggerFactory.getLogger(WellProfileProtoStore::class.java)
    @OptIn(ExperimentalSerializationApi::class)
    private val proto  = ProtoBuf { encodeDefaults = true }

    private fun fileFor(id: String): File =
        FileUtil.wellProfilesDir().resolve("$id.pb").toFile()

    /**
     * Write [profileProto] to disk. Creates the file if it does not exist.
     */
    fun write(profileProto: WellProfileProto) {
        val file  = fileFor(profileProto.id)
        val bytes = proto.encodeToByteArray(profileProto)
        file.writeBytes(bytes)
        logger.debug("Saved well profile '{}' ({} bytes)", profileProto.id, bytes.size)
    }

    /**
     * Read and decode a profile by [id].
     * @return The decoded proto, or null if the file does not exist or fails to decode.
     */
    fun read(id: String): WellProfileProto? {
        val file = fileFor(id)
        if (!file.exists()) return null
        return try {
            proto.decodeFromByteArray<WellProfileProto>(file.readBytes())
        } catch (e: Exception) {
            logger.error("Failed to decode well profile '{}'", id, e)
            null
        }
    }

    /**
     * Read all profiles in the well_profiles directory.
     */
    fun readAll(): List<WellProfileProto> {
        val dir = FileUtil.wellProfilesDir().toFile()
        return dir.listFiles { f -> f.extension == "pb" }
            ?.mapNotNull { file ->
                try {
                    proto.decodeFromByteArray<WellProfileProto>(file.readBytes())
                } catch (e: Exception) {
                    logger.warn("Skipping corrupt profile file: {}", file.name, e)
                    null
                }
            }
            ?: emptyList()
    }

    /**
     * Delete the profile file for [id]. No-ops if file does not exist.
     */
    fun delete(id: String) {
        val file = fileFor(id)
        if (file.exists()) {
            file.delete()
            logger.debug("Deleted well profile '{}'", id)
        }
    }
}