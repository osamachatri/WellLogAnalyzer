package com.oussama_chatri.feature.viewer3d.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.util.launchDefault
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.viewer3d.domain.model.CameraPreset
import com.oussama_chatri.feature.viewer3d.domain.model.GeologyLayer
import com.oussama_chatri.feature.viewer3d.domain.model.Trajectory3DPoint
import com.oussama_chatri.feature.viewer3d.domain.model.ViewerSettings
import com.oussama_chatri.feature.viewer3d.domain.model.Wellbore3DModel
import com.oussama_chatri.feature.viewer3d.domain.usecase.BuildGeologyModelUseCase
import com.oussama_chatri.feature.viewer3d.domain.usecase.ComputeWellTrajectoryUseCase
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Viewer3DViewModel(
    private val computeTrajectory: ComputeWellTrajectoryUseCase,
    private val buildGeology: BuildGeologyModelUseCase
) : BaseViewModel() {

    private val _model = MutableStateFlow<Wellbore3DModel?>(null)
    val model: StateFlow<Wellbore3DModel?> = _model.asStateFlow()

    private val _settings = MutableStateFlow(ViewerSettings())
    val settings: StateFlow<ViewerSettings> = _settings.asStateFlow()

    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()

    fun load(profile: WellProfile, result: SimulationResult?) {
        viewModelScope.launchDefault {
            _isBuilding.value = true

            val trajectory = computeTrajectory.execute(profile, result)
            val layers     = buildGeology.execute(profile)

            val minEcd = result?.pressureProfile?.minOfOrNull { it.ecd } ?: 8.0
            val maxEcd = result?.pressureProfile?.maxOfOrNull { it.ecd } ?: 12.0

            // Initialise all layers as visible
            val visibility = layers.associate { it.id to true }

            _model.value = Wellbore3DModel(
                wellName          = profile.wellName,
                totalDepth        = profile.totalDepth,
                trajectoryPoints  = trajectory,
                geologyLayers     = layers,
                maxEcd            = maxEcd,
                minEcd            = minEcd
            )
            _settings.value = _settings.value.copy(layerVisibility = visibility)
            _isBuilding.value = false
        }
    }

    fun toggleLayer(layerId: String) {
        val current = _settings.value.layerVisibility.toMutableMap()
        current[layerId] = !(current[layerId] ?: true)
        _settings.value = _settings.value.copy(layerVisibility = current)
    }

    fun toggleWellTube() {
        _settings.value = _settings.value.copy(showWellTube = !_settings.value.showWellTube)
    }

    fun toggleEcdColorMap() {
        _settings.value = _settings.value.copy(showEcdColorMap = !_settings.value.showEcdColorMap)
    }

    fun toggleFormationLabels() {
        _settings.value = _settings.value.copy(
            showFormationLabels = !_settings.value.showFormationLabels
        )
    }

    fun setCameraPreset(preset: CameraPreset) {
        _settings.value = _settings.value.copy(cameraPreset = preset)
    }
}