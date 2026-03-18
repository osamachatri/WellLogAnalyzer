package com.oussama_chatri.feature.charts2d.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.util.launchDefault
import com.oussama_chatri.feature.charts2d.data.mapper.SimResultToChartMapper
import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet
import com.oussama_chatri.feature.charts2d.domain.model.ChartType
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Charts2DViewModel : BaseViewModel() {

    private val _activeTab = MutableStateFlow(ChartType.PRESSURE_DEPTH)
    val activeTab: StateFlow<ChartType> = _activeTab.asStateFlow()

    private val _chartData = MutableStateFlow<Map<ChartType, ChartDataSet>>(emptyMap())
    val chartData: StateFlow<Map<ChartType, ChartDataSet>> = _chartData.asStateFlow()

    private val _result = MutableStateFlow<SimulationResult?>(null)
    val result: StateFlow<SimulationResult?> = _result.asStateFlow()

    // Depth range filter — values are 0f..1f fractions of totalDepth
    private val _depthRangeMin = MutableStateFlow(0f)
    val depthRangeMin: StateFlow<Float> = _depthRangeMin.asStateFlow()

    private val _depthRangeMax = MutableStateFlow(1f)
    val depthRangeMax: StateFlow<Float> = _depthRangeMax.asStateFlow()

    // Per-series visibility toggles: seriesId → visible
    private val _seriesVisibility = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val seriesVisibility: StateFlow<Map<String, Boolean>> = _seriesVisibility.asStateFlow()

    fun loadResult(simulationResult: SimulationResult) {
        _result.value = simulationResult
        viewModelScope.launchDefault {
            val maps = SimResultToChartMapper.mapAll(simulationResult)
            _chartData.value = maps
            // Default: all series visible
            val visibility = maps.values
                .flatMap { it.series }
                .associate { it.id to true }
            _seriesVisibility.value = visibility
        }
    }

    fun selectTab(type: ChartType) {
        _activeTab.value = type
    }

    fun setDepthRangeMin(value: Float) {
        _depthRangeMin.value = value.coerceIn(0f, _depthRangeMax.value - 0.05f)
    }

    fun setDepthRangeMax(value: Float) {
        _depthRangeMax.value = value.coerceIn(_depthRangeMin.value + 0.05f, 1f)
    }

    fun toggleSeriesVisibility(seriesId: String) {
        _seriesVisibility.value = _seriesVisibility.value.toMutableMap().also {
            it[seriesId] = !(it[seriesId] ?: true)
        }
    }
}