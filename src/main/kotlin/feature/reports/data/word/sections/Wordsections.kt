package com.oussama_chatri.feature.reports.data.word.sections

import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.reports.data.word.WordTemplates
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart

// Cover Page

object CoverPageSection {
    fun build(mdp: MainDocumentPart, config: ReportConfig, profile: WellProfile) {
        WordTemplates.coverPage(
            mdp          = mdp,
            title        = config.reportTitle,
            wellName     = profile.wellName,
            engineerName = config.engineerName.ifBlank { "—" },
            companyName  = config.companyName.ifBlank { "—" },
            date         = config.date
        )
        WordTemplates.pageBreak(mdp)
    }
}

// Executive Summary

object ExecutiveSummarySection {
    fun build(mdp: MainDocumentPart, profile: WellProfile, result: SimulationResult) {
        WordTemplates.h1(mdp, "Executive Summary")

        val overallStatus = if (result.isEcdSafe) "SAFE ✓" else "⚠ WARNING"
        WordTemplates.alertBox(
            mdp  = mdp,
            text = "Overall simulation status: $overallStatus — Well ${profile.wellName}",
            kind = if (result.isEcdSafe) WordTemplates.AlertKind.SUCCESS else WordTemplates.AlertKind.WARNING
        )

        WordTemplates.bodyParagraph(
            mdp  = mdp,
            text = "This report presents the results of a hydraulics simulation performed for well " +
                    "${profile.wellName} to a total depth of ${NumberFormatter.feet(profile.totalDepth)}. " +
                    "The analysis covers annular pressure losses, Equivalent Circulating Density (ECD), " +
                    "bit hydraulics parameters, and formation pressure safety margins."
        )

        WordTemplates.h2(mdp, "Key Results")

        WordTemplates.kpiBlock(
            mdp  = mdp,
            kpis = listOf(
                NumberFormatter.ppg(result.maxEcd)                  to "Max ECD",
                NumberFormatter.psi(result.maxAnnularPressureLoss)  to "Max Annular Pressure Loss",
                NumberFormatter.psi(result.bitPressureDrop)         to "Bit Pressure Drop",
                NumberFormatter.psi(result.totalSurfacePressure)    to "Total Surface Pressure"
            )
        )

        WordTemplates.bodyParagraph(mdp, "")

        WordTemplates.h2(mdp, "Findings")
        WordTemplates.bulletList(
            mdp   = mdp,
            items = listOf(
                "Maximum ECD of ${NumberFormatter.ppg(result.maxEcd)} recorded at total depth.",
                "Total annular pressure loss: ${NumberFormatter.psi(result.maxAnnularPressureLoss)}.",
                "Bit hydraulic horsepower: ${NumberFormatter.hp(result.hydraulicHorsepower)} " +
                        "(HSI = ${NumberFormatter.hsi(result.hsi)}).",
                "Surge pressure (60 ft/min trip speed): ${NumberFormatter.psi(result.surgePressure)}.",
                "ECD safety window: ${if (result.isEcdSafe) "All depth stations within safe limits." else "One or more stations outside safe window — review recommended."}"
            )
        )

        WordTemplates.footerLine(mdp)
        WordTemplates.pageBreak(mdp)
    }
}

// Input Parameters

object InputParametersSection {
    fun build(mdp: MainDocumentPart, profile: WellProfile) {
        WordTemplates.h1(mdp, "Input Parameters")

        // 3.1 Drill String
        WordTemplates.h2(mdp, "3.1  Drill String")
        WordTemplates.dataTable(
            mdp     = mdp,
            headers = listOf("Parameter", "Value"),
            rows    = listOf(
                listOf("Well Name",          profile.wellName),
                listOf("Total Depth",        NumberFormatter.feet(profile.totalDepth)),
                listOf("Casing OD",          NumberFormatter.inches(profile.casingOd)),
                listOf("Casing ID",          NumberFormatter.inches(profile.casingId)),
                listOf("Drill Pipe OD",      NumberFormatter.inches(profile.drillString.drillPipeOd)),
                listOf("Drill Pipe ID",      NumberFormatter.inches(profile.drillString.drillPipeId)),
                listOf("Drill Collar OD",    NumberFormatter.inches(profile.drillString.drillCollarOd)),
                listOf("Drill Collar Length",NumberFormatter.feet(profile.drillString.drillCollarLength))
            ),
            colWidths = listOf(3600, 5760)
        )

        if (profile.drillString.sections.isNotEmpty()) {
            WordTemplates.bodyParagraph(mdp, "")
            WordTemplates.caption(mdp, "Drill String Sections")
            WordTemplates.dataTable(
                mdp     = mdp,
                headers = listOf("Section", "OD (in)", "ID (in)", "Length (ft)", "Weight (lb/ft)"),
                rows    = profile.drillString.sections.map { s ->
                    listOf(s.name, "%.3f".format(s.od), "%.3f".format(s.id),
                        "%.0f".format(s.length), "%.2f".format(s.weightPerFoot))
                },
                numericCols = setOf(1, 2, 3, 4)
            )
        }

        // 3.2 Bit Parameters
        WordTemplates.bodyParagraph(mdp, "")
        WordTemplates.h2(mdp, "3.2  Bit Parameters")
        WordTemplates.dataTable(
            mdp     = mdp,
            headers = listOf("Parameter", "Value"),
            rows    = listOf(
                listOf("Bit Size",        NumberFormatter.inches(profile.bitParameters.bitSize)),
                listOf("Nozzle Count",    profile.bitParameters.nozzleCount.toString()),
                listOf("Total Flow Area", NumberFormatter.squareInches(profile.bitParameters.totalFlowArea))
            ) + profile.bitParameters.nozzleSizes.mapIndexed { i, s ->
                listOf("Nozzle ${i+1}", "${s.toInt()} / 32 in")
            },
            colWidths = listOf(3600, 5760)
        )

        // 3.3 Fluid Properties
        WordTemplates.bodyParagraph(mdp, "")
        WordTemplates.h2(mdp, "3.3  Fluid Properties")
        WordTemplates.dataTable(
            mdp     = mdp,
            headers = listOf("Parameter", "Value"),
            rows    = listOf(
                listOf("Mud Weight",          NumberFormatter.ppg(profile.fluidProperties.mudWeight)),
                listOf("Flow Rate",           NumberFormatter.gpm(profile.fluidProperties.flowRate)),
                listOf("Mud Type",            profile.fluidProperties.mudType.displayName),
                listOf("Rheology Model",      profile.fluidProperties.rheologyModel.name),
                listOf("Plastic Viscosity",   NumberFormatter.cP(profile.fluidProperties.plasticViscosity)),
                listOf("Yield Point",         "${NumberFormatter.format(profile.fluidProperties.yieldPoint)} lb/100ft²"),
                listOf("Flow Behavior Index", NumberFormatter.format(profile.fluidProperties.flowBehaviorIndex, 3)),
                listOf("Consistency Index",   "${NumberFormatter.format(profile.fluidProperties.consistencyIndex)} eq.cP"),
                listOf("Solids Content",      NumberFormatter.percent(profile.fluidProperties.solidsContent)),
                listOf("pH",                  NumberFormatter.format(profile.fluidProperties.pH, 1)),
                listOf("Surface Temp",        NumberFormatter.fahrenheit(profile.fluidProperties.surfaceTemperature)),
                listOf("BHT",                 NumberFormatter.fahrenheit(profile.fluidProperties.bottomholeTemperature))
            ),
            colWidths = listOf(3600, 5760)
        )

        WordTemplates.footerLine(mdp)
        WordTemplates.pageBreak(mdp)
    }

    // Extension for dataTable with numeric col set convenience
    private fun WordTemplates.dataTable(
        mdp: MainDocumentPart,
        headers: List<String>,
        rows: List<List<String>>,
        colWidths: List<Int>? = null,
        numericCols: Set<Int> = emptySet()
    ) = dataTable(mdp, headers, rows, colWidths, numericCols)
}

// Simulation Results

object SimulationResultsSection {
    fun build(mdp: MainDocumentPart, profile: WellProfile, result: SimulationResult) {
        WordTemplates.h1(mdp, "Simulation Results")

        WordTemplates.alertBox(
            mdp  = mdp,
            text = "ECD Status: ${if (result.isEcdSafe) "All depth points within safe window ✓" else "⚠ One or more points exceed safe window"}",
            kind = if (result.isEcdSafe) WordTemplates.AlertKind.SUCCESS else WordTemplates.AlertKind.WARNING
        )

        WordTemplates.bodyParagraph(mdp, "")

        WordTemplates.h2(mdp, "4.1  Summary Metrics")
        WordTemplates.kpiBlock(
            mdp  = mdp,
            kpis = listOf(
                NumberFormatter.ppg(result.maxEcd)                  to "Max ECD",
                NumberFormatter.psi(result.maxAnnularPressureLoss)  to "Max APL",
                NumberFormatter.psi(result.bitPressureDrop)         to "Bit ΔP",
                NumberFormatter.psi(result.totalSurfacePressure)    to "Surface Pressure"
            )
        )

        WordTemplates.bodyParagraph(mdp, "")
        WordTemplates.kpiBlock(
            mdp  = mdp,
            kpis = listOf(
                NumberFormatter.hp(result.hydraulicHorsepower)             to "HHP at Bit",
                "${String.format("%.1f", result.nozzleVelocity)} ft/s"     to "Nozzle Velocity",
                NumberFormatter.lbf(result.impactForce)                    to "Impact Force",
                NumberFormatter.hsi(result.hsi)                            to "HSI"
            )
        )

        WordTemplates.bodyParagraph(mdp, "")

        WordTemplates.h2(mdp, "4.2  Pressure Profile (sampled)")
        // Show every 5th point to keep Word doc reasonable
        val sampled = result.pressureProfile.filterIndexed { idx, _ -> idx % 5 == 0 }
        WordTemplates.dataTable(
            mdp        = mdp,
            headers    = listOf("Depth (ft)", "Hydrostatic (psi)", "APL (psi)", "ECD (ppg)", "Status"),
            rows       = sampled.map { p ->
                listOf(
                    "${p.depth.toInt()}",
                    String.format("%.1f", p.hydrostaticPressure),
                    String.format("%.1f", p.annularPressureLoss),
                    String.format("%.3f", p.ecd),
                    if (p.isEcdSafe) "SAFE" else "WARNING"
                )
            },
            statusCols = setOf(4)
        )

        WordTemplates.bodyParagraph(mdp, "")
        WordTemplates.h2(mdp, "4.3  Surge & Swab")
        WordTemplates.dataTable(
            mdp     = mdp,
            headers = listOf("Parameter", "Value"),
            rows    = listOf(
                listOf("Surge Pressure", NumberFormatter.psi(result.surgePressure)),
                listOf("Swab Pressure",  NumberFormatter.psi(result.swabPressure)),
                listOf("Trip Speed",     "60 ft/min (standard assumption)")
            )
        )

        WordTemplates.footerLine(mdp)
        WordTemplates.pageBreak(mdp)
    }
}

//Geology Section

object GeologySection {
    fun build(mdp: MainDocumentPart, profile: WellProfile) {
        WordTemplates.h1(mdp, "Formation Geology")

        WordTemplates.bodyParagraph(
            mdp  = mdp,
            text = "The following formation zones were defined for well ${profile.wellName}. " +
                    "Pore pressure and fracture gradient values govern the safe ECD operating window at each depth interval."
        )

        WordTemplates.bodyParagraph(mdp, "")

        WordTemplates.dataTable(
            mdp     = mdp,
            headers = listOf("Zone", "Top (ft)", "Bottom (ft)", "PP Grad (ppg)", "FG Grad (ppg)", "Lithology"),
            rows    = profile.formationZones.map { z ->
                listOf(
                    z.zoneName,
                    z.topDepth.toInt().toString(),
                    z.bottomDepth.toInt().toString(),
                    String.format("%.2f", z.porePressureGradient),
                    String.format("%.2f", z.fractureGradient),
                    z.lithology.displayName
                )
            }
        )

        WordTemplates.bodyParagraph(mdp, "")

        val minWindow = profile.formationZones.minOfOrNull { it.fractureGradient - it.porePressureGradient } ?: 0.0
        WordTemplates.alertBox(
            mdp  = mdp,
            text = "Narrowest operating window: ${String.format("%.2f", minWindow)} ppg " +
                    "— ensure ECD remains within limits at all times.",
            kind = when {
                minWindow < 1.0 -> WordTemplates.AlertKind.DANGER
                minWindow < 2.0 -> WordTemplates.AlertKind.WARNING
                else            -> WordTemplates.AlertKind.INFO
            }
        )

        WordTemplates.footerLine(mdp)
    }
}