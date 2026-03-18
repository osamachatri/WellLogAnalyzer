package com.oussama_chatri.feature.reports.domain.model

sealed class ExportResult {
    data class Success(val filePath: String, val format: ExportFormat) : ExportResult()
    data class Failure(val message: String, val cause: Throwable? = null) : ExportResult()
}

enum class ExportFormat(val extension: String, val displayName: String) {
    EXCEL("xlsx", "Excel Workbook"),
    WORD("docx", "Word Document")
}