package com.oussama_chatri.feature.simulation.data.engine

import com.oussama_chatri.feature.simulation.domain.engine.HydraulicsEngine
import com.oussama_chatri.feature.simulation.domain.engine.equations.AnnularVelocityCalculator
import com.oussama_chatri.feature.simulation.domain.engine.equations.BitHydraulicsCalculator
import com.oussama_chatri.feature.simulation.domain.engine.equations.SurgeSwabCalculator
import com.oussama_chatri.feature.simulation.domain.engine.solver.DepthStepSolver
import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationInput
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.model.VelocityProfile
import kotlinx.coroutines.flow.Flow

/**
 * Concrete engine implementation that delegates depth-step iteration
 * to [DepthStepSolver] and bit/surge calculations to their respective calculators.
 */
class HydraulicsEngineImpl : HydraulicsEngine {

    override fun run(input: SimulationInput): Flow<PressurePoint> =
        DepthStepSolver.solve(input)

    override fun buildResult(
        input: SimulationInput,
        pressureProfile: List<PressurePoint>
    ): SimulationResult {
        val fluid    = input.fluidProperties
        val ds       = input.drillString
        val bit      = input.bitParameters
        val flowRate = input.effectiveFlowRate

        val bitHydraulics = BitHydraulicsCalculator.computeAll(
            mudWeightPpg = fluid.mudWeight,
            flowRateGpm  = flowRate,
            bit          = bit
        )

        val surgeSwab = SurgeSwabCalculator.surgeSwabPressure(
            tripSpeedFtMin       = 60.0,
            mudWeightPpg         = fluid.mudWeight,
            casingIdIn           = input.casingId,
            pipeOdIn             = ds.drillPipeOd,
            pipeIdIn             = ds.drillPipeId,
            plasticViscosityCp   = fluid.plasticViscosity,
            yieldPointLb100      = fluid.yieldPoint,
            rheologyModel        = fluid.rheologyModel,
            tvdFt                = input.totalDepth
        )

        val velocityProfile = buildVelocityProfile(input, pressureProfile)

        return SimulationResult(
            wellId                = input.wellId,
            wellName              = input.wellName,
            pressureProfile       = pressureProfile,
            velocityProfile       = velocityProfile,
            bitPressureDrop       = bitHydraulics.bitPressureDrop,
            hydraulicHorsepower   = bitHydraulics.hydraulicHorsepower,
            nozzleVelocity        = bitHydraulics.nozzleVelocity,
            impactForce           = bitHydraulics.impactForce,
            hsi                   = bitHydraulics.hsi,
            surgePressure         = surgeSwab.surgePsi,
            swabPressure          = surgeSwab.swabPsi,
            formationZones        = input.formationZones
        )
    }

    private fun buildVelocityProfile(
        input: SimulationInput,
        pressureProfile: List<PressurePoint>
    ): List<VelocityProfile> {
        val fluid    = input.fluidProperties
        val ds       = input.drillString
        val flowRate = input.effectiveFlowRate

        return pressureProfile.map { point ->
            val collarTopDepth = input.totalDepth - ds.drillCollarLength
            val pipeOd = if (point.depth >= collarTopDepth) ds.drillCollarOd else ds.drillPipeOd

            AnnularVelocityCalculator.compute(
                depth                = point.depth,
                flowRateGpm          = flowRate,
                casingIdIn           = input.casingId,
                pipeOdIn             = pipeOd,
                pipeIdIn             = ds.drillPipeId,
                mudWeightPpg         = fluid.mudWeight,
                plasticViscosityCp   = fluid.plasticViscosity
            )
        }
    }
}