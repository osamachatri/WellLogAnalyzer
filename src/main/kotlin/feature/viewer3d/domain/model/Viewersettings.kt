package com.oussama_chatri.feature.viewer3d.domain.model

/**
 * Transient UI state for the 3D viewer — not persisted.
 *
 * [layerVisibility] maps geology layer id → visible flag.
 */
data class ViewerSettings(
    val showWellTube: Boolean = true,
    val showEcdColorMap: Boolean = true,
    val showFormationLabels: Boolean = false,
    val layerVisibility: Map<String, Boolean> = emptyMap(),
    val cameraPreset: CameraPreset = CameraPreset.ISOMETRIC
)

enum class CameraPreset { TOP, SIDE, FRONT, ISOMETRIC }