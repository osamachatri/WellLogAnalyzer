package com.oussama_chatri.feature.simulation.data.local

import com.oussama_chatri.core.util.FileUtil
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Low-level ProtoBuf file store for [SimulationResultProto] objects.
 *
 * Each result is stored as:
 *   `<appData>/simulation_results/<wellId>/<timestamp>.pb`
 *
 * Multiple results per well are supported so the Dashboard can show history.
 */
class SimulationProtoStore {

    private val logger = LoggerFactory.getLogger(SimulationProtoStore::class.java)

    @OptIn(ExperimentalSerializationApi::class)
    private val proto = ProtoBuf { encodeDefaults = true }

    private fun dirFor(wellId: String): File =
        FileUtil.simulationResultsDir().resolve(wellId).toFile().also { it.mkdirs() }

    private fun fileFor(wellId: String, timestamp: Long): File =
        dirFor(wellId).resolve("$timestamp.pb")

    fun write(result: SimulationResultProto) {
        val file  = fileFor(result.wellId, result.timestamp)
        val bytes = proto.encodeToByteArray(result)
        file.writeBytes(bytes)
        logger.debug("Saved simulation result for well '{}' at timestamp {}", result.wellId, result.timestamp)
    }

    fun readLatest(wellId: String): SimulationResultProto? {
        val dir = dirFor(wellId)
        val file = dir.listFiles { f -> f.extension == "pb" }
            ?.maxByOrNull { it.nameWithoutExtension.toLongOrNull() ?: 0L }
            ?: return null
        return decode(file)
    }

    fun readAll(wellId: String): List<SimulationResultProto> {
        val dir = dirFor(wellId)
        return dir.listFiles { f -> f.extension == "pb" }
            ?.sortedByDescending { it.nameWithoutExtension.toLongOrNull() ?: 0L }
            ?.mapNotNull { decode(it) }
            ?: emptyList()
    }

    fun deleteAll(wellId: String) {
        dirFor(wellId).deleteRecursively()
        logger.debug("Deleted all simulation results for well '{}'", wellId)
    }

    private fun decode(file: File): SimulationResultProto? = try {
        proto.decodeFromByteArray<SimulationResultProto>(file.readBytes())
    } catch (e: Exception) {
        logger.warn("Skipping corrupt simulation file: {}", file.name, e)
        null
    }
}