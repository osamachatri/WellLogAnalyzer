package com.oussama_chatri.feature.simulation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.model.SimulationStatus
import com.oussama_chatri.feature.simulation.presentation.components.ResultsSummaryCard
import com.oussama_chatri.feature.simulation.presentation.components.SimulationControlPanel
import com.oussama_chatri.feature.simulation.presentation.components.SimulationLogPanel
import com.oussama_chatri.feature.simulation.presentation.components.SimulationProgressBar
import com.oussama_chatri.feature.simulation.presentation.viewmodel.SimulationViewModel
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.koin.java.KoinJavaComponent.get

@Composable
fun SimulationScreen(
    profileToLoad: WellProfile?,
    onSimulationComplete: (SimulationResult) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: SimulationViewModel = remember { get(SimulationViewModel::class.java) }

    LaunchedEffect(profileToLoad) {
        profileToLoad?.let { viewModel.loadProfile(it) }
    }

    val status           by viewModel.status.collectAsState()
    val logLines         by viewModel.logLines.collectAsState()
    val liveProfile      by viewModel.liveProfile.collectAsState()
    val depthStep        by viewModel.depthStep.collectAsState()
    val flowRateOverride by viewModel.flowRateOverride.collectAsState()
    val activeProfile    by viewModel.activeProfile.collectAsState()

    // Notify AppState once — not on every recomposition
    LaunchedEffect(status) {
        if (status is SimulationStatus.Done) {
            onSimulationComplete((status as SimulationStatus.Done).result)
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SimulationControlPanel(
            profile                  = activeProfile,
            status                   = status,
            depthStep                = depthStep,
            flowRateOverride         = flowRateOverride,
            onDepthStepChange        = viewModel::updateDepthStep,
            onFlowRateOverrideChange = viewModel::updateFlowRateOverride,
            onRun                    = viewModel::runSimulation,
            onStop                   = viewModel::stopSimulation,
            onReset                  = viewModel::reset
        )

        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(DividerColor))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
        ) {
            when (val s = status) {
                is SimulationStatus.Idle -> IdleStateContent(hasProfile = activeProfile != null)

                is SimulationStatus.Running -> Column(
                    modifier            = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SimulationProgressBar(
                        status      = s,
                        liveProfile = liveProfile
                    )
                }

                is SimulationStatus.Done -> Column(
                    modifier            = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint     = TealSafe,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text  = "Simulation Complete — ${s.result.wellName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TealSafe
                            )
                        }

                        // Quick navigation to charts — saves the user an extra click
                        OutlinedButton(
                            onClick = { onSimulationComplete(s.result) },
                            colors  = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                            border  = androidx.compose.foundation.BorderStroke(
                                1.dp, AmberGold.copy(alpha = 0.6f)
                            )
                        ) {
                            Icon(
                                Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("View Charts", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    ResultsSummaryCard(result = s.result)
                }

                is SimulationStatus.Failed -> Column(
                    modifier            = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint     = CoralDanger,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text  = "Simulation Failed",
                        style = MaterialTheme.typography.titleMedium,
                        color = CoralDanger
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = s.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = viewModel::reset,
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = CoralDanger),
                        border  = androidx.compose.foundation.BorderStroke(
                            1.dp, CoralDanger.copy(alpha = 0.6f)
                        )
                    ) {
                        Text("Reset")
                    }
                }
            }
        }

        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(DividerColor))

        SimulationLogPanel(logLines = logLines)
    }
}

@Composable
private fun IdleStateContent(hasProfile: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text  = if (hasProfile) "Ready to Simulate" else "No Well Profile Loaded",
                style = MaterialTheme.typography.headlineSmall,
                color = if (hasProfile) TextSecondary else CoralDanger
            )
            Text(
                text  = if (hasProfile)
                    "Configure the run parameters on the left, then press Run Simulation."
                else
                    "Go to Well Input, fill in your drilling parameters, and validate before running.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}