package com.oussama_chatri.core.util

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

object FileUtil {

    /**
     * Returns the platform-appropriate user app-data directory:
     * - Windows: `%APPDATA%/WellLogAnalyzer`
     * - Linux/macOS: `~/.local/share/WellLogAnalyzer`
     */
    fun appDataDir(): Path {
        val base = System.getenv("APPDATA")
            ?: "${System.getProperty("user.home")}/.local/share"
        return Paths.get(base, "WellLogAnalyzer").also { it.createDirectories() }
    }

    /** Sub-directory for well profile ProtoBuf files. */
    fun wellProfilesDir(): Path =
        appDataDir().resolve("well_profiles").also { it.createDirectories() }

    /** Sub-directory for simulation result ProtoBuf files. */
    fun simulationResultsDir(): Path =
        appDataDir().resolve("simulation_results").also { it.createDirectories() }

    /** Sub-directory for app settings ProtoBuf. */
    fun settingsDir(): Path =
        appDataDir().resolve("settings").also { it.createDirectories() }

    // ── Native file dialogs (JFileChooser — works on all platforms) ───────────

    /**
     * Opens a JFileChooser "Open File" dialog, filtered by [extensions].
     *
     * Must be called on the AWT EDT — wrap in `withContext(Dispatchers.Main)`
     * from a ViewModel or coroutine scope.
     *
     * @param title       Dialog window title.
     * @param extensions  File extension filters, e.g. `listOf("xlsx", "xls")`.
     * @return The selected [File], or null if the user cancelled.
     */
    fun showOpenDialog(
        title: String = "Open File",
        extensions: List<String> = emptyList()
    ): File? {
        val chooser = JFileChooser().apply {
            dialogTitle = title
            isMultiSelectionEnabled = false
            if (extensions.isNotEmpty()) {
                val description = extensions.joinToString(", ") { ".$it" }
                fileFilter = FileNameExtensionFilter(description, *extensions.toTypedArray())
            }
        }
        val result = chooser.showOpenDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
    }

    /**
     * Opens a JFileChooser "Save File" dialog.
     *
     * Must be called on the AWT EDT.
     *
     * @param title           Dialog window title.
     * @param defaultFileName Suggested filename (including extension).
     * @return The chosen [File], or null if the user cancelled.
     */
    fun showSaveDialog(
        title: String = "Save File",
        defaultFileName: String = "report"
    ): File? {
        val chooser = JFileChooser().apply {
            dialogTitle      = title
            selectedFile     = File(defaultFileName)
        }
        val result = chooser.showSaveDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
    }

    /**
     * Opens a JFileChooser directory chooser dialog.
     */
    fun showDirectoryDialog(title: String = "Select Folder"): File? {
        val chooser = JFileChooser().apply {
            dialogTitle      = title
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        }
        val result = chooser.showOpenDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
    }

    /** Ensures all parent directories for [file] exist. */
    fun ensureParentDirs(file: File) {
        file.parentFile?.mkdirs()
    }

    /** Returns true if the path exists and is a regular file. */
    fun exists(path: String): Boolean = Paths.get(path).exists()

    /**
     * Sanitizes a well name for use as a filename:
     * replaces spaces and special chars with underscores.
     */
    fun sanitizeFileName(name: String): String =
        name.trim()
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .replace(Regex("_+"), "_")
}