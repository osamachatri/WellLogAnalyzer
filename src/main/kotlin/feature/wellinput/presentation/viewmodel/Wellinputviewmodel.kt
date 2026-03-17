package com.oussama_chatri.feature.wellinput.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.base.UiState
import com.oussama_chatri.core.util.launchDefault
import com.oussama_chatri.core.util.launchIO
import com.oussama_chatri.feature.wellinput.domain.model.*
import com.oussama_chatri.feature.wellinput.domain.usecase.LoadWellProfileUseCase
import com.oussama_chatri.feature.wellinput.domain.usecase.SaveWellProfileUseCase
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidateWellProfileUseCase
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WellInputViewModel(
    private val saveWellProfileUseCase: SaveWellProfileUseCase,
    private val loadWellProfileUseCase: LoadWellProfileUseCase,
    private val validateWellProfileUseCase: ValidateWellProfileUseCase
) : BaseViewModel() {

    private val _profile = MutableStateFlow(WellProfile.empty())
    val profile: StateFlow<WellProfile> = _profile.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val saveState: StateFlow<UiState<Unit>> = _saveState.asStateFlow()

    private val _validationResult = MutableStateFlow<ValidationResult?>(null)
    val validationResult: StateFlow<ValidationResult?> = _validationResult.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _importState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val importState: StateFlow<UiState<Unit>> = _importState.asStateFlow()

    //  Tab navigation
    fun selectTab(index: Int) { _activeTab.value = index }

    //  Well identity
    fun updateWellName(name: String) {
        _profile.value = _profile.value.copy(wellName = name)
    }

    fun updateTotalDepth(depth: String) {
        _profile.value = _profile.value.copy(totalDepth = depth.toDoubleOrNull() ?: 0.0)
    }

    fun updateCasingOd(value: String) {
        _profile.value = _profile.value.copy(casingOd = value.toDoubleOrNull() ?: 0.0)
    }

    fun updateCasingId(value: String) {
        _profile.value = _profile.value.copy(casingId = value.toDoubleOrNull() ?: 0.0)
    }

    //  Drill string
    fun updateDrillPipeOd(value: String) = updateDs { copy(drillPipeOd = value.toDoubleOrNull() ?: 0.0) }
    fun updateDrillPipeId(value: String) = updateDs { copy(drillPipeId = value.toDoubleOrNull() ?: 0.0) }
    fun updateDrillCollarOd(value: String) = updateDs { copy(drillCollarOd = value.toDoubleOrNull() ?: 0.0) }
    fun updateDrillCollarLength(value: String) = updateDs { copy(drillCollarLength = value.toDoubleOrNull() ?: 0.0) }

    fun addDrillStringSection(section: DrillStringSection) = updateDs {
        copy(sections = sections + section)
    }

    fun updateDrillStringSection(index: Int, section: DrillStringSection) = updateDs {
        copy(sections = sections.toMutableList().also { it[index] = section })
    }

    fun removeDrillStringSection(index: Int) = updateDs {
        copy(sections = sections.filterIndexed { i, _ -> i != index })
    }

    private fun updateDs(block: DrillString.() -> DrillString) {
        _profile.value = _profile.value.copy(drillString = _profile.value.drillString.block())
    }

    //  Bit parameters
    fun updateBitSize(value: String) = updateBit { copy(bitSize = value.toDoubleOrNull() ?: 0.0) }
    fun updateNozzleCount(value: String) = updateBit { copy(nozzleCount = value.toIntOrNull() ?: 0) }

    fun updateNozzleSize(index: Int, value: String) = updateBit {
        val list = nozzleSizes.toMutableList()
        while (list.size <= index) list.add(0.0)
        list[index] = value.toDoubleOrNull() ?: 0.0
        copy(nozzleSizes = list)
    }

    private fun updateBit(block: BitParameters.() -> BitParameters) {
        _profile.value = _profile.value.copy(bitParameters = _profile.value.bitParameters.block())
    }

    //  Fluid properties
    fun updateMudWeight(value: String)       = updateFluid { copy(mudWeight = value.toDoubleOrNull() ?: 0.0) }
    fun updateFlowRate(value: String)        = updateFluid { copy(flowRate = value.toDoubleOrNull() ?: 0.0) }
    fun updateSurfaceTemp(value: String)     = updateFluid { copy(surfaceTemperature = value.toDoubleOrNull() ?: 70.0) }
    fun updateBhtTemp(value: String)         = updateFluid { copy(bottomholeTemperature = value.toDoubleOrNull() ?: 200.0) }
    fun updateRheologyModel(model: RheologyModel) = updateFluid { copy(rheologyModel = model) }
    fun updatePlasticViscosity(value: String) = updateFluid { copy(plasticViscosity = value.toDoubleOrNull() ?: 0.0) }
    fun updateYieldPoint(value: String)      = updateFluid { copy(yieldPoint = value.toDoubleOrNull() ?: 0.0) }
    fun updateFlowBehaviorIndex(value: String) = updateFluid { copy(flowBehaviorIndex = value.toDoubleOrNull() ?: 0.8) }
    fun updateConsistencyIndex(value: String) = updateFluid { copy(consistencyIndex = value.toDoubleOrNull() ?: 0.0) }
    fun updateMudType(type: MudType)         = updateFluid { copy(mudType = type) }
    fun updateSolidsContent(value: String)   = updateFluid { copy(solidsContent = value.toDoubleOrNull() ?: 0.0) }
    fun updatePH(value: String)              = updateFluid { copy(pH = value.toDoubleOrNull() ?: 9.0) }

    private fun updateFluid(block: FluidProperties.() -> FluidProperties) {
        _profile.value = _profile.value.copy(fluidProperties = _profile.value.fluidProperties.block())
    }

    //  Formation zones
    fun addFormationZone() {
        val newZone = FormationZone.empty()
        _profile.value = _profile.value.copy(
            formationZones = _profile.value.formationZones + newZone
        )
    }

    fun updateFormationZone(index: Int, zone: FormationZone) {
        _profile.value = _profile.value.copy(
            formationZones = _profile.value.formationZones.toMutableList().also { it[index] = zone }
        )
    }

    fun removeFormationZone(index: Int) {
        _profile.value = _profile.value.copy(
            formationZones = _profile.value.formationZones.filterIndexed { i, _ -> i != index }
        )
    }

    //  Deviation survey
    fun addSurveyStation() {
        val last = _profile.value.deviationSurvey.lastOrNull()
        val newStation = SurveyStation.empty().copy(
            measuredDepth = (last?.measuredDepth ?: 0.0) + 500.0
        )
        _profile.value = _profile.value.copy(
            deviationSurvey = _profile.value.deviationSurvey + newStation
        )
    }

    fun updateSurveyStation(index: Int, station: SurveyStation) {
        _profile.value = _profile.value.copy(
            deviationSurvey = _profile.value.deviationSurvey.toMutableList().also { it[index] = station }
        )
    }

    fun removeSurveyStation(index: Int) {
        _profile.value = _profile.value.copy(
            deviationSurvey = _profile.value.deviationSurvey.filterIndexed { i, _ -> i != index }
        )
    }

    //  Load / Save / Validate
    fun loadProfile(id: String) {
        viewModelScope.launchIO {
            _saveState.value = UiState.Loading("Loading profile…")
            try {
                val loaded = loadWellProfileUseCase(id)
                if (loaded != null) {
                    _profile.value = loaded
                    _saveState.value = UiState.Idle
                } else {
                    _saveState.value = UiState.Error("Profile not found.")
                }
            } catch (e: Exception) {
                logger.error("Failed to load profile '$id'", e)
                _saveState.value = UiState.Error(e.message ?: "Load failed.")
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launchIO {
            _saveState.value = UiState.Loading("Saving…")
            try {
                saveWellProfileUseCase(_profile.value)
                _saveState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                logger.error("Failed to save profile", e)
                _saveState.value = UiState.Error(e.message ?: "Save failed.")
            }
        }
    }

    fun validate() {
        viewModelScope.launchDefault {
            val result = validateWellProfileUseCase(_profile.value)
            _validationResult.value = result
        }
    }

    fun resetSaveState() { _saveState.value = UiState.Idle }
    fun resetImportState() { _importState.value = UiState.Idle }

    fun newProfile() {
        _profile.value = WellProfile.empty()
        _validationResult.value = null
        _saveState.value = UiState.Idle
        _activeTab.value = 0
    }
}