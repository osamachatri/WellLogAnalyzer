package com.oussama_chatri.feature.reports.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.util.launchIO
import com.oussama_chatri.core.util.withMain
import com.oussama_chatri.feature.reports.domain.model.ExportFormat
import com.oussama_chatri.feature.reports.domain.model.ExportResult
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.model.ReportSection
import com.oussama_chatri.feature.reports.domain.usecase.ExportExcelReportUseCase
import com.oussama_chatri.feature.reports.domain.usecase.ExportWordReportUseCase
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReportViewModel(
    private val exportExcel: ExportExcelReportUseCase,
    private val exportWord:  ExportWordReportUseCase
) : BaseViewModel() {

    private val _config = MutableStateFlow(ReportConfig.default())
    val config: StateFlow<ReportConfig> = _config.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    // Config updates

    fun setProfile(profile: WellProfile) {
        _config.value = _config.value.copy(
            reportTitle = "Hydraulics Analysis Report — ${profile.wellName}"
        )
    }

    fun updateTitle(v: String)        { _config.value = _config.value.copy(reportTitle  = v) }
    fun updateEngineer(v: String)     { _config.value = _config.value.copy(engineerName = v) }
    fun updateCompany(v: String)      { _config.value = _config.value.copy(companyName  = v) }
    fun updateDate(v: String)         { _config.value = _config.value.copy(date         = v) }

    fun toggleSection(section: ReportSection) {
        val current = _config.value.sections.toMutableSet()
        if (section in current) current.remove(section) else current.add(section)
        _config.value = _config.value.copy(sections = current)
    }

    fun selectAllSections()  { _config.value = _config.value.copy(sections = ReportSection.entries.toSet()) }
    fun clearAllSections()   { _config.value = _config.value.copy(sections = emptySet()) }

    // Export actions

    fun exportExcel(profile: WellProfile, result: SimulationResult, outputPath: String? = null) {
        if (_exportState.value is ExportState.Exporting) return
        viewModelScope.launchIO {
            withMain { _exportState.value = ExportState.Exporting(ExportFormat.EXCEL) }
            val out = exportExcel.execute(_config.value, profile, result, outputPath)
            withMain { _exportState.value = out.toState() }
        }
    }

    fun exportWord(profile: WellProfile, result: SimulationResult, outputPath: String? = null) {
        if (_exportState.value is ExportState.Exporting) return
        viewModelScope.launchIO {
            withMain { _exportState.value = ExportState.Exporting(ExportFormat.WORD) }
            val out = exportWord.execute(_config.value, profile, result, outputPath)
            withMain { _exportState.value = out.toState() }
        }
    }

    fun exportBoth(profile: WellProfile, result: SimulationResult) {
        if (_exportState.value is ExportState.Exporting) return
        viewModelScope.launchIO {
            withMain { _exportState.value = ExportState.Exporting(ExportFormat.EXCEL) }
            val xResult = exportExcel.execute(_config.value, profile, result)
            if (xResult is ExportResult.Failure) {
                withMain { _exportState.value = ExportState.Failed(xResult.message) }
                return@launchIO
            }
            withMain { _exportState.value = ExportState.Exporting(ExportFormat.WORD) }
            val wResult = exportWord.execute(_config.value, profile, result)
            withMain {
                _exportState.value = when (wResult) {
                    is ExportResult.Success -> ExportState.BothDone(
                        excelPath = (xResult as ExportResult.Success).filePath,
                        wordPath  = wResult.filePath
                    )
                    is ExportResult.Failure -> ExportState.Failed(wResult.message)
                }
            }
        }
    }

    fun dismissResult() { _exportState.value = ExportState.Idle }

    private fun ExportResult.toState(): ExportState = when (this) {
        is ExportResult.Success -> ExportState.Done(filePath, format)
        is ExportResult.Failure -> ExportState.Failed(message)
    }
}

// UI state sealed class

sealed class ExportState {
    data object Idle : ExportState()
    data class Exporting(val format: ExportFormat) : ExportState()
    data class Done(val filePath: String, val format: ExportFormat) : ExportState()
    data class BothDone(val excelPath: String, val wordPath: String) : ExportState()
    data class Failed(val message: String) : ExportState()
}