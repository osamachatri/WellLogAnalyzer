package com.oussama_chatri.feature.viewer3d.domain.usecase

import com.oussama_chatri.feature.viewer3d.domain.model.GeologyLayer
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

/**
 * Converts the formation zones from a [WellProfile] into [GeologyLayer] objects
 * ready for the 3D renderer, preserving order and lithology.
 */
class BuildGeologyModelUseCase {

    fun execute(profile: WellProfile): List<GeologyLayer> =
        profile.formationZones.map { zone ->
            GeologyLayer(
                id          = zone.id,
                name        = zone.zoneName,
                topDepth    = zone.topDepth,
                bottomDepth = zone.bottomDepth,
                lithology   = zone.lithology,
                isVisible   = true
            )
        }
}