package com.oussama_chatri.feature.viewer3d.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.viewer3d.presentation.components.CameraControlPanel
import com.oussama_chatri.feature.viewer3d.presentation.components.ECDColorScaleBar
import com.oussama_chatri.feature.viewer3d.presentation.components.GeologyLayerLegend
import com.oussama_chatri.feature.viewer3d.presentation.components.LayerVisibilityPanel
import com.oussama_chatri.feature.viewer3d.presentation.components.WellboreCanvas3D
import com.oussama_chatri.feature.viewer3d.presentation.viewmodel.Viewer3DViewModel
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.koin.compose.koinInject

@Composable
fun Viewer3DScreen(
    profile: WellProfile?,
    simulationResult: SimulationResult?,
    modifier: Modifier = Modifier
) {
    val viewModel: Viewer3DViewModel = koinInject()

    val model      by viewModel.model.collectAsState()
    val settings   by viewModel.settings.collectAsState()
    val isBuilding by viewModel.isBuilding.collectAsState()

    // Build the 3D model whenever profile or result changes
    LaunchedEffect(profile, simulationResult) {
        profile?.let { viewModel.load(it, simulationResult) }
    }

    Box(modifier = modifier.fillMaxSize().background(NavyDeep)) {

        when {
            profile == null -> {
                // Nothing to show yet
                NoProfilePlaceholder()
            }

            isBuilding -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(40.dp))
                        Text(
                            text  = "Building 3D model…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            model != null -> {
                val m = model!!

                // Main 3D viewport — fills everything, panels float on top
                WellboreCanvas3D(
                    model    = m,
                    settings = settings,
                    modifier = Modifier.fillMaxSize()
                )

                // Floating left panel: layer toggles
                LayerVisibilityPanel(
                    layers           = m.geologyLayers,
                    settings         = settings,
                    onToggleLayer    = viewModel::toggleLayer,
                    onToggleWellTube = viewModel::toggleWellTube,
                    onToggleEcdMap   = viewModel::toggleEcdColorMap,
                    onToggleLabels   = viewModel::toggleFormationLabels,
                    modifier         = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .width(180.dp)
                )

                // Floating right panel: ECD scale + depth legend
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (simulationResult != null) {
                        ECDColorScaleBar(
                            minEcd       = m.minEcd,
                            maxEcd       = m.maxEcd,
                            currentMaxEcd = simulationResult.maxEcd
                        )
                    }

                    GeologyLayerLegend(
                        model    = m,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(190.dp)
                    )
                }

                // Floating bottom bar: camera presets
                CameraControlPanel(
                    activePreset = settings.cameraPreset,
                    onPreset     = viewModel::setCameraPreset,
                    modifier     = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun NoProfilePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector        = Icons.Default.ViewInAr,
                contentDescription = null,
                tint               = DividerColor,
                modifier           = Modifier.size(64.dp)
            )
            Text(
                text     = "No Well Profile Loaded",
                style    = MaterialTheme.typography.headlineSmall,
                color    = TextSecondary,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text  = "Complete Well Input and run a simulation to view the 3D model.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}