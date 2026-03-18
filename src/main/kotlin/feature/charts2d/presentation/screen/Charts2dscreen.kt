package com.oussama_chatri.feature.charts2d.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.charts2d.domain.model.ChartType
import com.oussama_chatri.feature.charts2d.presentation.components.ChartControlPanel
import com.oussama_chatri.feature.charts2d.presentation.components.ChartSelectorTabs
import com.oussama_chatri.feature.charts2d.presentation.components.ChartWithAxisLabels
import com.oussama_chatri.feature.charts2d.presentation.components.DepthLineChart
import com.oussama_chatri.feature.charts2d.presentation.components.HorizontalBarChart
import com.oussama_chatri.feature.charts2d.presentation.components.NoResultsPlaceholder
import com.oussama_chatri.feature.charts2d.presentation.components.ResultsSummaryBar
import com.oussama_chatri.feature.charts2d.presentation.viewmodel.Charts2DViewModel
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import org.koin.compose.koinInject

@Composable
fun Charts2DScreen(
    // The result can be injected from AppState when you wire it up,
    // or passed directly once Dashboard/navigation is done
    simulationResult: SimulationResult? = null,
    modifier: Modifier = Modifier
) {
    val viewModel: Charts2DViewModel = koinInject()

    val activeTab         by viewModel.activeTab.collectAsState()
    val chartData         by viewModel.chartData.collectAsState()
    val result            by viewModel.result.collectAsState()
    val seriesVisibility  by viewModel.seriesVisibility.collectAsState()
    val depthRangeMin     by viewModel.depthRangeMin.collectAsState()
    val depthRangeMax     by viewModel.depthRangeMax.collectAsState()

    // Load result whenever the caller provides one
    LaunchedEffect(simulationResult) {
        simulationResult?.let { viewModel.loadResult(it) }
    }

    val currentResult  = result ?: simulationResult
    val currentDataSet = chartData[activeTab]

    Column(modifier = modifier.fillMaxSize()) {

        // Tab row
        ChartSelectorTabs(
            activeTab     = activeTab,
            onTabSelected = viewModel::selectTab
        )

        HorizontalDivider(color = DividerColor)

        // Summary metrics strip (only when data is present)
        if (currentResult != null) {
            ResultsSummaryBar(
                result   = currentResult,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            HorizontalDivider(color = DividerColor)
        }

        // Main content row: chart area + control sidebar
        Row(modifier = Modifier.fillMaxSize()) {

            // Chart area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                if (currentResult == null || currentDataSet == null) {
                    NoResultsPlaceholder()
                } else {
                    ChartWithAxisLabels(
                        xLabel = currentDataSet.xLabel,
                        yLabel = currentDataSet.yLabel
                    ) {
                        when (activeTab) {
                            ChartType.PRESSURE_DEPTH,
                            ChartType.ECD_PROFILE,
                            ChartType.ANNULAR_VELOCITY -> DepthLineChart(
                                dataSet           = currentDataSet,
                                seriesVisibility  = seriesVisibility,
                                depthFractionMin  = depthRangeMin,
                                depthFractionMax  = depthRangeMax,
                                modifier          = Modifier.fillMaxSize()
                            )

                            ChartType.BIT_HYDRAULICS,
                            ChartType.COMPONENT_BREAKDOWN -> HorizontalBarChart(
                                dataSet          = currentDataSet,
                                seriesVisibility = seriesVisibility,
                                modifier         = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Divider between chart and sidebar
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DividerColor)
            )

            // Control sidebar
            ChartControlPanel(
                dataSet          = currentDataSet,
                result           = currentResult,
                seriesVisibility = seriesVisibility,
                depthRangeMin    = depthRangeMin,
                depthRangeMax    = depthRangeMax,
                onToggleSeries   = viewModel::toggleSeriesVisibility,
                onDepthMinChange = viewModel::setDepthRangeMin,
                onDepthMaxChange = viewModel::setDepthRangeMax,
                onExportPng      = { /* PNG export — stub for Reports feature to implement */ },
                onAddToReport    = { /* Hook for Reports feature */ }
            )
        }
    }
}