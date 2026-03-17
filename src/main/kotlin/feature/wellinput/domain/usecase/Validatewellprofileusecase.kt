package com.oussama_chatri.feature.wellinput.domain.usecase

import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

/**
 * Validates a [WellProfile] before allowing simulation to run.
 *
 * Collects ALL errors in one pass so the UI can highlight every
 * problematic field simultaneously rather than showing them one by one.
 */
class ValidateWellProfileUseCase {

    /**
     * @param profile The profile to validate.
     * @return A [ValidationResult] containing any discovered errors.
     */
    operator fun invoke(profile: WellProfile): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // ── Well identity ────────────────────────────────────────────────
        if (profile.wellName.isBlank()) {
            errors += ValidationError.WellNameEmpty
        }
        if (profile.totalDepth <= 0.0) {
            errors += ValidationError.TotalDepthInvalid
        }

        // ── Casing ───────────────────────────────────────────────────────
        if (profile.casingOd <= 0.0) {
            errors += ValidationError.CasingOdInvalid
        }
        if (profile.casingId <= 0.0) {
            errors += ValidationError.CasingIdInvalid
        }
        if (profile.casingId >= profile.casingOd && profile.casingOd > 0.0) {
            errors += ValidationError.CasingIdExceedsOd
        }

        // ── Drill string ─────────────────────────────────────────────────
        val ds = profile.drillString
        if (ds.drillPipeOd <= 0.0)  errors += ValidationError.DrillPipeOdInvalid
        if (ds.drillPipeId <= 0.0)  errors += ValidationError.DrillPipeIdInvalid
        if (ds.drillPipeId >= ds.drillPipeOd && ds.drillPipeOd > 0.0) {
            errors += ValidationError.DrillPipeIdExceedsOd
        }
        if (ds.drillCollarOd <= 0.0) errors += ValidationError.DrillCollarOdInvalid
        if (ds.drillCollarLength <= 0.0) errors += ValidationError.DrillCollarLengthInvalid

        // ── Bit parameters ───────────────────────────────────────────────
        val bit = profile.bitParameters
        if (bit.bitSize <= 0.0) {
            errors += ValidationError.BitSizeNotDefined
        }
        if (bit.nozzleCount <= 0) {
            errors += ValidationError.NozzleCountInvalid
        }
        if (bit.nozzleSizes.isEmpty() || bit.nozzleSizes.any { it <= 0.0 }) {
            errors += ValidationError.NozzleSizesInvalid
        }

        // ── Fluid properties ─────────────────────────────────────────────
        val fluid = profile.fluidProperties
        if (fluid.mudWeight <= 0.0) {
            errors += ValidationError.MudWeightInvalid
        }
        if (fluid.flowRate <= 0.0) {
            errors += ValidationError.FlowRateInvalid
        }

        // ── Formation zones ──────────────────────────────────────────────
        if (profile.formationZones.isEmpty()) {
            errors += ValidationError.NoFormationZones
        } else {
            profile.formationZones.forEach { zone ->
                if (zone.topDepth >= zone.bottomDepth) {
                    errors += ValidationError.FormationZoneDepthInvalid(zone.zoneName)
                }
                if (zone.porePressureGradient >= zone.fractureGradient) {
                    errors += ValidationError.FormationWindowInvalid(zone.zoneName)
                }
            }
        }

        // ── Mud weight vs pore pressure ──────────────────────────────────
        val minPorePressure = profile.formationZones.minOfOrNull { it.porePressureGradient }
        if (minPorePressure != null && fluid.mudWeight in 0.0..(minPorePressure - 0.01)) {
            errors += ValidationError.MudWeightBelowPorePressure(fluid.mudWeight, minPorePressure)
        }

        return ValidationResult(errors)
    }
}

/** Immutable result of a validation pass. */
data class ValidationResult(val errors: List<ValidationError>) {
    val isValid: Boolean get() = errors.isEmpty()

    fun hasError(vararg types: Class<out ValidationError>): Boolean =
        errors.any { error -> types.any { it.isInstance(error) } }
}

/** Sealed hierarchy of all possible validation errors. */
sealed class ValidationError(val message: String) {
    data object WellNameEmpty              : ValidationError("Well name is required.")
    data object TotalDepthInvalid          : ValidationError("Total depth must be greater than zero.")
    data object CasingOdInvalid            : ValidationError("Casing OD must be greater than zero.")
    data object CasingIdInvalid            : ValidationError("Casing ID must be greater than zero.")
    data object CasingIdExceedsOd          : ValidationError("Casing ID must be less than Casing OD.")
    data object DrillPipeOdInvalid         : ValidationError("Drill pipe OD must be greater than zero.")
    data object DrillPipeIdInvalid         : ValidationError("Drill pipe ID must be greater than zero.")
    data object DrillPipeIdExceedsOd       : ValidationError("Drill pipe ID must be less than drill pipe OD.")
    data object DrillCollarOdInvalid       : ValidationError("Drill collar OD must be greater than zero.")
    data object DrillCollarLengthInvalid   : ValidationError("Drill collar length must be greater than zero.")
    data object BitSizeNotDefined          : ValidationError("Bit size is not defined.")
    data object NozzleCountInvalid         : ValidationError("Nozzle count must be at least 1.")
    data object NozzleSizesInvalid         : ValidationError("All nozzle sizes must be greater than zero.")
    data object MudWeightInvalid           : ValidationError("Mud weight must be greater than zero.")
    data object FlowRateInvalid            : ValidationError("Flow rate must be greater than zero.")
    data object NoFormationZones           : ValidationError("At least one formation zone is required.")

    data class FormationZoneDepthInvalid(val zone: String)
        : ValidationError("Zone '$zone': top depth must be less than bottom depth.")

    data class FormationWindowInvalid(val zone: String)
        : ValidationError("Zone '$zone': pore pressure must be less than fracture gradient.")

    data class MudWeightBelowPorePressure(val mw: Double, val pp: Double)
        : ValidationError("Mud weight (${String.format("%.2f", mw)} ppg) is below pore pressure gradient (${String.format("%.2f", pp)} ppg).")
}