package com.oussama_chatri.feature.charts2d.data.mapper

import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet
import com.oussama_chatri.feature.charts2d.domain.model.ChartType
import com.oussama_chatri.feature.charts2d.domain.usecase.BuildChartDataUseCase
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult

/**
 * Thin data-layer wrapper that delegates to [BuildChartDataUseCase].
 * Exists so the data layer can intercept/transform if needed (e.g., unit conversion later).
 */
object SimResultToChartMapper {

    private val useCase = BuildChartDataUseCase()

    fun map(result: SimulationResult, type: ChartType): ChartDataSet =
        useCase.execute(result, type)

    fun mapAll(result: SimulationResult): Map<ChartType, ChartDataSet> =
        ChartType.entries.associateWith { useCase.execute(result, it) }
}