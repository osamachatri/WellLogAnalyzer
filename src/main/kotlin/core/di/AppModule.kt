package com.oussama_chatri.core.di

import com.oussama_chatri.feature.charts2d.di.Charts2DModule
import com.oussama_chatri.feature.dashboard.di.DashboardModule
import com.oussama_chatri.feature.reports.di.ReportsModule
import com.oussama_chatri.feature.simulation.di.SimulationModule
import com.oussama_chatri.feature.viewer3d.di.Viewer3DModule
import com.oussama_chatri.feature.wellinput.di.WellInputModule
import org.koin.dsl.module

val AppModule = module {
    includes(
        DashboardModule,
        WellInputModule,
        SimulationModule,
        Charts2DModule,
        Viewer3DModule,
        ReportsModule
    )
}