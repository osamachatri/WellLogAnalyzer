package com.oussama_chatri.feature.viewer3d.domain.model

import com.oussama_chatri.feature.wellinput.domain.model.Lithology

/**
 * A single geological formation layer as used by the 3D viewer.
 * Derived from [com.oussama_chatri.feature.wellinput.domain.model.FormationZone].
 */
data class GeologyLayer(
    val id: String,
    val name: String,
    val topDepth: Double,
    val bottomDepth: Double,
    val lithology: Lithology,
    val isVisible: Boolean = true
) {
    val thickness: Double get() = bottomDepth - topDepth
}