package com.oussama_chatri.feature.charts2d.domain.usecase

import androidx.compose.ui.graphics.Color
import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet
import com.oussama_chatri.feature.charts2d.domain.model.ChartSeries
import com.oussama_chatri.feature.charts2d.domain.model.ChartType
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult

/**
 * Converts a [SimulationResult] into a [ChartDataSet] for a given [ChartType].
 * Pure domain logic — no UI or library imports.
 */
class BuildChartDataUseCase {

    fun execute(result: SimulationResult, type: ChartType): ChartDataSet = when (type) {
        ChartType.PRESSURE_DEPTH      -> buildPressureDepth(result)
        ChartType.ECD_PROFILE         -> buildEcdProfile(result)
        ChartType.ANNULAR_VELOCITY    -> buildAnnularVelocity(result)
        ChartType.BIT_HYDRAULICS      -> buildBitHydraulics(result)
        ChartType.COMPONENT_BREAKDOWN -> buildComponentBreakdown(result)
    }

    // Pressure vs. Depth

    private fun buildPressureDepth(result: SimulationResult): ChartDataSet {
        val profile = result.pressureProfile

        val hydrostatic = ChartSeries(
            id     = "hydrostatic",
            label  = "Hydrostatic Pressure",
            color  = Color(0xFFF4A917),
            points = profile.map { it.hydrostaticPressure to it.depth }
        )

        val ecdPressure = ChartSeries(
            id     = "ecd_pressure",
            label  = "ECD Pressure",
            color  = Color(0xFF2EC4B6),
            points = profile.map { it.ecdPressure to it.depth }
        )

        val porePressure = ChartSeries(
            id       = "pore_pressure",
            label    = "Pore Pressure",
            color    = Color(0xFF4FC3F7),
            points   = profile.map { (it.porePressure * 0.052 * it.depth) to it.depth },
            isDashed = true
        )

        val fractureGradient = ChartSeries(
            id       = "frac_gradient",
            label    = "Fracture Gradient",
            color    = Color(0xFFE63946),
            points   = profile.map { (it.fractureGradientPressure * 0.052 * it.depth) to it.depth },
            isDashed = true
        )

        val safeMin = profile.map { (it.porePressure * 0.052 * it.depth) to it.depth }
        val safeMax = profile.map { (it.fractureGradientPressure * 0.052 * it.depth) to it.depth }

        return ChartDataSet(
            type           = ChartType.PRESSURE_DEPTH,
            title          = "Pressure vs. Depth — ${result.wellName}",
            xLabel         = "Pressure (psi)",
            yLabel         = "Depth (ft)",
            series         = listOf(hydrostatic, ecdPressure, porePressure, fractureGradient),
            depthInverted  = true,
            safeWindowMin  = safeMin,
            safeWindowMax  = safeMax
        )
    }

    // ECD vs. Depth

    private fun buildEcdProfile(result: SimulationResult): ChartDataSet {
        val profile = result.pressureProfile

        val ecd = ChartSeries(
            id     = "ecd",
            label  = "ECD",
            color  = Color(0xFF2EC4B6),
            points = profile.map { it.ecd to it.depth }
        )

        val porePressureGrad = ChartSeries(
            id       = "pp_grad",
            label    = "Pore Pressure Gradient",
            color    = Color(0xFF4FC3F7),
            points   = profile.map { it.porePressure to it.depth },
            isDashed = true
        )

        val fracGrad = ChartSeries(
            id       = "frac_grad",
            label    = "Fracture Gradient",
            color    = Color(0xFFE63946),
            points   = profile.map { it.fractureGradientPressure to it.depth },
            isDashed = true
        )

        val safeMin = profile.map { it.porePressure to it.depth }
        val safeMax = profile.map { it.fractureGradientPressure to it.depth }

        return ChartDataSet(
            type          = ChartType.ECD_PROFILE,
            title         = "ECD Profile — ${result.wellName}",
            xLabel        = "Equivalent Density (ppg)",
            yLabel        = "Depth (ft)",
            series        = listOf(ecd, porePressureGrad, fracGrad),
            depthInverted = true,
            safeWindowMin = safeMin,
            safeWindowMax = safeMax
        )
    }

    // Annular Velocity vs. Depth

    private fun buildAnnularVelocity(result: SimulationResult): ChartDataSet {
        val velocity = ChartSeries(
            id     = "annular_velocity",
            label  = "Annular Velocity",
            color  = Color(0xFFF4A917),
            points = result.velocityProfile.map { it.annularVelocity to it.depth }
        )

        val pipeVelocity = ChartSeries(
            id     = "pipe_velocity",
            label  = "Pipe Velocity",
            color  = Color(0xFF4FC3F7),
            points = result.velocityProfile.map { it.pipeVelocity to it.depth }
        )

        // Minimum transport velocity reference line: 150 ft/min is commonly cited
        val minTransport = ChartSeries(
            id       = "min_transport",
            label    = "Min. Transport (150 ft/min)",
            color    = Color(0xFFE63946),
            points   = result.velocityProfile.map { 150.0 to it.depth },
            isDashed = true
        )

        return ChartDataSet(
            type          = ChartType.ANNULAR_VELOCITY,
            title         = "Annular Velocity — ${result.wellName}",
            xLabel        = "Velocity (ft/min)",
            yLabel        = "Depth (ft)",
            series        = listOf(velocity, pipeVelocity, minTransport),
            depthInverted = true
        )
    }

    // Bit Hydraulics Bar Chart

    private fun buildBitHydraulics(result: SimulationResult): ChartDataSet {
        // Encode bar chart as single-point series; the chart composable interprets
        // these specially when type == BIT_HYDRAULICS.
        val bars = listOf(
            ChartSeries(
                id     = "bit_pressure_drop",
                label  = "Bit ΔP (psi)",
                color  = Color(0xFFF4A917),
                points = listOf(result.bitPressureDrop to 0.0)
            ),
            ChartSeries(
                id     = "hhp",
                label  = "HHP (hp)",
                color  = Color(0xFF2EC4B6),
                points = listOf(result.hydraulicHorsepower to 0.0)
            ),
            ChartSeries(
                id     = "impact_force",
                label  = "Impact Force (lbf)",
                color  = Color(0xFF4FC3F7),
                points = listOf(result.impactForce to 0.0)
            ),
            ChartSeries(
                id     = "nozzle_velocity",
                label  = "Nozzle Velocity (ft/s)",
                color  = Color(0xFFE63946),
                points = listOf(result.nozzleVelocity to 0.0)
            )
        )

        return ChartDataSet(
            type   = ChartType.BIT_HYDRAULICS,
            title  = "Bit Hydraulics — ${result.wellName}",
            xLabel = "Metric",
            yLabel = "Value",
            series = bars
        )
    }

    // Pressure Component Breakdown

    private fun buildComponentBreakdown(result: SimulationResult): ChartDataSet {
        val hydrostatic    = result.hydrostaticAtTd
        val annularLoss    = result.maxAnnularPressureLoss
        val bitDrop        = result.bitPressureDrop
        val surgePressure  = result.surgePressure

        val components = listOf(
            ChartSeries(
                id     = "hydrostatic",
                label  = "Hydrostatic",
                color  = Color(0xFFF4A917),
                points = listOf(hydrostatic to 0.0)
            ),
            ChartSeries(
                id     = "annular_loss",
                label  = "Annular Loss",
                color  = Color(0xFF2EC4B6),
                points = listOf(annularLoss to 0.0)
            ),
            ChartSeries(
                id     = "bit_drop",
                label  = "Bit Pressure Drop",
                color  = Color(0xFF4FC3F7),
                points = listOf(bitDrop to 0.0)
            ),
            ChartSeries(
                id     = "surge",
                label  = "Surge",
                color  = Color(0xFFE63946),
                points = listOf(surgePressure to 0.0)
            )
        )

        return ChartDataSet(
            type   = ChartType.COMPONENT_BREAKDOWN,
            title  = "Pressure Component Breakdown — ${result.wellName}",
            xLabel = "Component",
            yLabel = "Pressure (psi)",
            series = components
        )
    }
}