package com.oussama_chatri.feature.reports.data.word

import com.oussama_chatri.feature.reports.data.word.sections.CoverPageSection
import com.oussama_chatri.feature.reports.data.word.sections.ExecutiveSummarySection
import com.oussama_chatri.feature.reports.data.word.sections.GeologySection
import com.oussama_chatri.feature.reports.data.word.sections.InputParametersSection
import com.oussama_chatri.feature.reports.data.word.sections.SimulationResultsSection
import com.oussama_chatri.feature.reports.domain.model.ExportFormat
import com.oussama_chatri.feature.reports.domain.model.ExportResult
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.model.ReportSection
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.slf4j.LoggerFactory
import java.io.File

class WordReportBuilder {

    private val logger = LoggerFactory.getLogger(WordReportBuilder::class.java)

    fun build(
        config:     ReportConfig,
        profile:    WellProfile,
        result:     SimulationResult,
        outputPath: String
    ): ExportResult {
        return try {
            val pkg = WordprocessingMLPackage.createPackage()
            val mdp = pkg.mainDocumentPart

            if (ReportSection.COVER_PAGE in config.sections) {
                CoverPageSection.build(mdp, config, profile)
            }
            if (ReportSection.EXECUTIVE_SUMMARY in config.sections) {
                ExecutiveSummarySection.build(mdp, profile, result)
            }
            if (ReportSection.INPUT_PARAMETERS in config.sections) {
                InputParametersSection.build(mdp, profile)
            }
            if (ReportSection.SIMULATION_RESULTS in config.sections) {
                SimulationResultsSection.build(mdp, profile, result)
            }
            if (ReportSection.FORMATION_ZONES in config.sections ||
                ReportSection.GEOLOGY_SUMMARY in config.sections) {
                GeologySection.build(mdp, profile)
            }

            val file = File(outputPath)
            file.parentFile?.mkdirs()

            // pkg.save(File) sets up ZipPartStore internally before marshalling
            pkg.save(file)

            logger.info("Word report written to {}", outputPath)
            ExportResult.Success(outputPath, ExportFormat.WORD)

        } catch (e: Exception) {
            logger.error("Word export failed", e)
            ExportResult.Failure("Word export failed: ${e.message}", e)
        }
    }
}