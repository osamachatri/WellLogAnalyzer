package com.oussama_chatri.feature.reports.domain.model

/**
 * All user-provided metadata and section selections for a single report export.
 */
data class ReportConfig(
    val reportTitle: String,
    val engineerName: String,
    val companyName: String,
    val date: String,
    val sections: Set<ReportSection> = ReportSection.entries.toSet(),
    val includeRawData: Boolean = false
) {
    companion object {
        fun default(wellName: String = "") = ReportConfig(
            reportTitle  = "Hydraulics Analysis Report${if (wellName.isNotBlank()) " — $wellName" else ""}",
            engineerName = "",
            companyName  = "",
            date         = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        )
    }
}