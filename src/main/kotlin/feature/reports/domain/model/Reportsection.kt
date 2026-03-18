package com.oussama_chatri.feature.reports.domain.model

enum class ReportSection(val displayName: String) {
    COVER_PAGE("Cover Page"),
    EXECUTIVE_SUMMARY("Executive Summary"),
    INPUT_PARAMETERS("Input Parameters"),
    SIMULATION_RESULTS("Simulation Results"),
    PRESSURE_CHARTS("Pressure Charts"),
    ECD_PROFILE("ECD Profile"),
    FORMATION_ZONES("Formation Zones"),
    GEOLOGY_SUMMARY("Geology Summary"),
    APPENDIX_RAW_DATA("Appendix: Raw Data")
}