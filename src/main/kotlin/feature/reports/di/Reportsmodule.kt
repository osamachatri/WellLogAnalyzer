package com.oussama_chatri.feature.reports.di

import com.oussama_chatri.feature.reports.data.excel.ExcelReportBuilder
import com.oussama_chatri.feature.reports.data.repository.ReportRepositoryImpl
import com.oussama_chatri.feature.reports.data.word.WordReportBuilder
import com.oussama_chatri.feature.reports.domain.repository.ReportRepository
import com.oussama_chatri.feature.reports.domain.usecase.ExportExcelReportUseCase
import com.oussama_chatri.feature.reports.domain.usecase.ExportWordReportUseCase
import com.oussama_chatri.feature.reports.presentation.viewmodel.ReportViewModel
import org.koin.dsl.module

val ReportsModule = module {

    single { ExcelReportBuilder() }
    single { WordReportBuilder() }

    single<ReportRepository> {
        ReportRepositoryImpl(
            excelBuilder = get(),
            wordBuilder  = get()
        )
    }

    factory { ExportExcelReportUseCase(get()) }
    factory { ExportWordReportUseCase(get()) }

    factory {
        ReportViewModel(
            exportExcel = get(),
            exportWord  = get()
        )
    }
}