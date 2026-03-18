package com.oussama_chatri.feature.dashboard.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.core.ui.components.ConfirmDialog
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.presentation.components.*
import com.oussama_chatri.feature.dashboard.presentation.viewmodel.DashboardViewModel
import org.koin.compose.koinInject

@Composable
fun DashboardScreen(
    onNavigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DashboardViewModel = koinInject()

    val projects       by viewModel.projects.collectAsState()
    val isLoading      by viewModel.isLoading.collectAsState()
    val confirmDeleteId by viewModel.confirmDeleteId.collectAsState()

    // Confirm delete dialog
    confirmDeleteId?.let { id ->
        val name = projects.find { it.id == id }?.wellName ?: id
        ConfirmDialog(
            title         = "Delete Project",
            message       = "Delete \"$name\"? This cannot be undone.",
            confirmLabel  = "Delete",
            dismissLabel  = "Cancel",
            isDestructive = true,
            onConfirm     = viewModel::confirmDelete,
            onDismiss     = viewModel::cancelDelete
        )
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator(color = com.oussama_chatri.core.theme.AmberGold)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Stat cards
        QuickStatsRow(projects = projects)

        // Middle row
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recent projects table
            RecentRunsTable(
                projects = projects,
                onOpen   = { id ->
                    // Navigate to Well Input with the selected profile loaded
                    // The actual profile loading is handled by AppState which holds
                    // the active profile. Dashboard only triggers navigation.
                    onNavigateTo(Route.WellInput)
                },
                onDelete = { id -> viewModel.requestDelete(id) },
                modifier = Modifier.weight(0.62f).heightIn(min = 300.dp)
            )

            // Right column: Quick Actions + Activity log
            Column(
                modifier            = Modifier.weight(0.38f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionsCard(
                    onNewWell        = { onNavigateTo(Route.WellInput) },
                    onOpenLastReport = { onNavigateTo(Route.Reports) }
                )
                ActivityLogCard(
                    projects = projects,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Simulations mini bar chart
        SimulationsBarChart(
            projects = projects,
            modifier = Modifier.fillMaxWidth()
        )
    }
}