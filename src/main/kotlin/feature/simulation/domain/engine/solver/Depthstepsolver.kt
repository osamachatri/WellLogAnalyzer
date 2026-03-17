package com.oussama_chatri.feature.simulation.domain.engine.solver

import com.oussama_chatri.feature.simulation.domain.engine.equations.AnnularVelocityCalculator
import com.oussama_chatri.feature.simulation.domain.engine.equations.ECDCalculator
import com.oussama_chatri.feature.simulation.domain.engine.equations.PressureDropCalculator
import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationInput
import com.oussama_chatri.feature.wellinput.domain.model.FormationZone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Walks from surface to TD in [SimulationInput.depthStep] increments,
 * computing hydraulics state at each station and emitting a [PressurePoint].
 *
 * The flow is cold and cancellable — the ViewModel can cancel it to stop the simulation.
 */
object DepthStepSolver {

    fun solve(input: SimulationInput): Flow<PressurePoint> = flow {
        var cumulativeApl = 0.0
        var depth = input.depthStep

        val flowRate   = input.effectiveFlowRate
        val fluid      = input.fluidProperties
        val ds         = input.drillString
        val zones      = input.formationZones

        while (depth <= input.totalDepth + 0.001) {
            val tvd = computeTvd(depth, input)

            // Determine which annular geometry applies at this depth
            val (outerDia, innerDia) = annularGeometryAt(depth, input)

            // Pressure loss for this interval
            val intervalLoss = PressureDropCalculator.annularPressureLoss(
                flowRateGpm          = flowRate,
                casingIdIn           = outerDia,
                pipeOdIn             = innerDia,
                mudWeightPpg         = fluid.mudWeight,
                plasticViscosityCp   = fluid.plasticViscosity,
                yieldPointLb100      = fluid.yieldPoint,
                flowBehaviorIndex    = fluid.flowBehaviorIndex,
                consistencyIndex     = fluid.consistencyIndex,
                rheologyModel        = fluid.rheologyModel,
                intervalLength       = input.depthStep
            )

            cumulativeApl += intervalLoss

            val hydrostatic  = PressureDropCalculator.hydrostaticPressure(fluid.mudWeight, tvd)
            val ecd          = ECDCalculator.ecd(fluid.mudWeight, cumulativeApl, tvd)
            val pp           = ECDCalculator.porePressureGradientAt(depth, zones)
            val fg           = ECDCalculator.fractureGradientAt(depth, zones)

            emit(
                PressurePoint(
                    depth                   = depth,
                    hydrostaticPressure     = hydrostatic,
                    annularPressureLoss     = cumulativeApl,
                    ecd                     = ecd,
                    porePressure            = pp,
                    fractureGradientPressure = fg
                )
            )

            depth += input.depthStep
        }
    }

    // Returns TVD from deviation survey if available, otherwise assumes vertical.
    private fun computeTvd(depth: Double, input: SimulationInput): Double {
        val stations = input.deviationSurvey.sortedBy { it.measuredDepth }
        val station = stations.lastOrNull { it.measuredDepth <= depth }
        return station?.tvd ?: depth
    }

    // Returns (outerDiameter, innerDiameter) of the annulus at [depth]:
    // uses casing ID while inside casing, drill collar OD below.
    private fun annularGeometryAt(depth: Double, input: SimulationInput): Pair<Double, Double> {
        val ds = input.drillString
        // Simple heuristic: drill collar starts in the bottom portion of the drill string
        val collarTopDepth = input.totalDepth - ds.drillCollarLength
        return if (depth >= collarTopDepth) {
            input.casingId to ds.drillCollarOd
        } else {
            input.casingId to ds.drillPipeOd
        }
    }
}