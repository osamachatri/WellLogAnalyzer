package com.oussama_chatri.feature.wellinput.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.base.UiState
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.ConfirmDialog
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.wellinput.domain.model.FluidProperties
import com.oussama_chatri.feature.wellinput.domain.model.Lithology
import com.oussama_chatri.feature.wellinput.domain.model.RheologyModel
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidationError
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidationResult
import com.oussama_chatri.feature.wellinput.presentation.components.*
import com.oussama_chatri.feature.wellinput.presentation.viewmodel.WellInputViewModel
import org.koin.compose.koinInject

private val TABS = listOf(
    "Drill String",
    "Bit Parameters",
    "Fluid Properties",
    "Formation Zones",
    "Deviation Survey"
)

@Composable
fun WellInputScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: WellInputViewModel = koinInject()

    val profile          by viewModel.profile.collectAsState()
    val activeTab        by viewModel.activeTab.collectAsState()
    val saveState        by viewModel.saveState.collectAsState()
    val validationResult by viewModel.validationResult.collectAsState()
    val importState      by viewModel.importState.collectAsState()

    var showNewWellConfirm by remember { mutableStateOf(false) }
    var showSaveSuccess    by remember { mutableStateOf(false) }

    // Show save success banner briefly
    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            showSaveSuccess = true
            kotlinx.coroutines.delay(2000)
            showSaveSuccess = false
            viewModel.resetSaveState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Action bar
        WellInputActionBar(
            wellName       = profile.wellName,
            saveState      = saveState,
            importState    = importState,
            onSave         = { viewModel.saveProfile() },
            onValidate     = { viewModel.validate() },
            onNewWell      = { showNewWellConfirm = true },
            onImportResult = { data ->
                // Map Excel import data into ViewModel updates
                data.wellName?.let    { viewModel.updateWellName(it) }
                data.totalDepth?.let  { viewModel.updateTotalDepth(it.toString()) }
                data.casingOd?.let    { viewModel.updateCasingOd(it.toString()) }
                data.casingId?.let    { viewModel.updateCasingId(it.toString()) }
                data.mudWeight?.let   { viewModel.updateMudWeight(it.toString()) }
                data.flowRate?.let    { viewModel.updateFlowRate(it.toString()) }
                data.plasticViscosity?.let { viewModel.updatePlasticViscosity(it.toString()) }
                data.yieldPoint?.let  { viewModel.updateYieldPoint(it.toString()) }
                data.formations.forEach { row ->
                    viewModel.addFormationZone()
                    val idx = viewModel.profile.value.formationZones.lastIndex
                    val existing = viewModel.profile.value.formationZones[idx]
                    viewModel.updateFormationZone(idx, existing.copy(
                        zoneName             = row["zoneName"] ?: "",
                        topDepth             = row["topDepth"]?.toDoubleOrNull() ?: 0.0,
                        bottomDepth          = row["bottomDepth"]?.toDoubleOrNull() ?: 0.0,
                        porePressureGradient = row["pp"]?.toDoubleOrNull() ?: 8.6,
                        fractureGradient     = row["fg"]?.toDoubleOrNull() ?: 10.0,
                        lithology            = Lithology.entries.firstOrNull {
                            it.displayName.equals(row["lithology"], ignoreCase = true)
                        } ?: com.oussama_chatri.feature.wellinput.domain.model.Lithology.SHALE
                    ))
                }
                data.survey.forEach { row ->
                    viewModel.addSurveyStation()
                    val idx = viewModel.profile.value.deviationSurvey.lastIndex
                    val existing = viewModel.profile.value.deviationSurvey[idx]
                    viewModel.updateSurveyStation(idx, existing.copy(
                        measuredDepth = row["md"]?.toDoubleOrNull()  ?: 0.0,
                        inclination   = row["inc"]?.toDoubleOrNull() ?: 0.0,
                        azimuth       = row["azi"]?.toDoubleOrNull() ?: 0.0
                    ))
                }
            }
        )

        // Save success banner
        AnimatedVisibility(visible = showSaveSuccess, enter = fadeIn(), exit = fadeOut()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealSafe.copy(alpha = 0.12f))
                    .border(0.dp, Color.Transparent)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = TealSafe, modifier = Modifier.size(16.dp))
                Text(
                    "Well profile saved successfully.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TealSafe
                )
            }
        }

        // Tab bar
        WellInputTabBar(
            activeTab = activeTab,
            onTabSelected = { viewModel.selectTab(it) }
        )

        HorizontalDivider(color = DividerColor)

        // Main content
        Row(modifier = Modifier.fillMaxSize()) {
            // Tab content area
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                when (activeTab) {
                    0 -> DrillStringPanel(
                        profile                    = profile,
                        validationResult           = validationResult,
                        onWellNameChange           = viewModel::updateWellName,
                        onTotalDepthChange         = viewModel::updateTotalDepth,
                        onCasingOdChange           = viewModel::updateCasingOd,
                        onCasingIdChange           = viewModel::updateCasingId,
                        onDrillPipeOdChange        = viewModel::updateDrillPipeOd,
                        onDrillPipeIdChange        = viewModel::updateDrillPipeId,
                        onDrillCollarOdChange      = viewModel::updateDrillCollarOd,
                        onDrillCollarLengthChange  = viewModel::updateDrillCollarLength,
                        onAddSection               = viewModel::addDrillStringSection,
                        onUpdateSection            = viewModel::updateDrillStringSection,
                        onRemoveSection            = viewModel::removeDrillStringSection
                    )
                    1 -> BitParametersPanel(
                        bitParameters    = profile.bitParameters,
                        fluidProperties  = profile.fluidProperties,
                        validationResult = validationResult,
                        onBitSizeChange  = viewModel::updateBitSize,
                        onNozzleCountChange = viewModel::updateNozzleCount,
                        onNozzleSizeChange  = viewModel::updateNozzleSize
                    )
                    2 -> FluidPropertiesPanel(
                        fluid                     = profile.fluidProperties,
                        onMudWeightChange         = viewModel::updateMudWeight,
                        onFlowRateChange          = viewModel::updateFlowRate,
                        onSurfaceTempChange       = viewModel::updateSurfaceTemp,
                        onBhtChange               = viewModel::updateBhtTemp,
                        onRheologyModelChange     = viewModel::updateRheologyModel,
                        onPlasticViscosityChange  = viewModel::updatePlasticViscosity,
                        onYieldPointChange        = viewModel::updateYieldPoint,
                        onFlowBehaviorIndexChange = viewModel::updateFlowBehaviorIndex,
                        onConsistencyIndexChange  = viewModel::updateConsistencyIndex,
                        onMudTypeChange           = viewModel::updateMudType,
                        onSolidsContentChange     = viewModel::updateSolidsContent,
                        onPHChange                = viewModel::updatePH
                    )
                    3 -> FormationZoneTable(
                        zones         = profile.formationZones,
                        totalDepth    = profile.totalDepth,
                        onAddZone     = viewModel::addFormationZone,
                        onUpdateZone  = viewModel::updateFormationZone,
                        onRemoveZone  = viewModel::removeFormationZone
                    )
                    4 -> DeviationSurveyPanel(
                        stations         = profile.deviationSurvey,
                        totalDepth       = profile.totalDepth,
                        onAddStation     = viewModel::addSurveyStation,
                        onUpdateStation  = viewModel::updateSurveyStation,
                        onRemoveStation  = viewModel::removeSurveyStation
                    )
                }
            }

            // Validation sidebar
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DividerColor)
            )
            ValidationSidebar(
                validationResult = validationResult,
                onRunSimulation  = { /* navigate to simulation — hoisted to AppState */ }
            )
        }
    }

    // Confirm new well dialog
    if (showNewWellConfirm) {
        ConfirmDialog(
            title         = "New Well Project",
            message       = "Creating a new well will discard all unsaved changes to the current profile. Continue?",
            confirmLabel  = "Create New",
            dismissLabel  = "Cancel",
            isDestructive = true,
            onConfirm     = { viewModel.newProfile(); showNewWellConfirm = false },
            onDismiss     = { showNewWellConfirm = false }
        )
    }
}

// Action bar
@Composable
private fun WellInputActionBar(
    wellName: String,
    saveState: UiState<Unit>,
    importState: UiState<Unit>,
    onSave: () -> Unit,
    onValidate: () -> Unit,
    onNewWell: () -> Unit,
    onImportResult: (com.oussama_chatri.feature.wellinput.presentation.components.ExcelImportData) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Well name display
        Text(
            text  = if (wellName.isBlank()) "Untitled Well" else "Well Input — $wellName",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // New Well
            OutlinedButton(
                onClick = onNewWell,
                colors  = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("New", style = MaterialTheme.typography.labelMedium)
            }

            // Import from Excel
            ImportFromExcelButton(
                importState    = importState,
                onImportResult = onImportResult,
                onResetState   = {}
            )

            // Validate
            OutlinedButton(
                onClick = onValidate,
                colors  = ButtonDefaults.outlinedButtonColors(contentColor = TealSafe),
                border  = androidx.compose.foundation.BorderStroke(1.dp, TealSafe.copy(alpha = 0.6f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Validate", style = MaterialTheme.typography.labelMedium)
            }

            // Save
            Button(
                onClick = onSave,
                enabled = saveState !is UiState.Loading,
                colors  = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = NavyDeep),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                if (saveState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(14.dp),
                        color       = NavyDeep,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(4.dp))
                Text("Save", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// Tab bar
@Composable
private fun WellInputTabBar(
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TABS.forEachIndexed { index, label ->
            val isActive = activeTab == index
            Column(
                modifier = Modifier
                    .then(if (!isActive) Modifier else Modifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = { onTabSelected(index) },
                    colors  = ButtonDefaults.textButtonColors(
                        contentColor = if (isActive) AmberGold else TextSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text  = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                // Active indicator
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(if (isActive) 40.dp else 0.dp)
                        .background(if (isActive) AmberGold else Color.Transparent)
                        .clip(MaterialTheme.shapes.extraSmall)
                )
            }
        }
    }
}

// Validation sidebar
@Composable
private fun ValidationSidebar(
    validationResult: ValidationResult?,
    onRunSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text  = "Validation Status",
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondary
        )

        if (validationResult == null) {
            Text(
                text  = "Press \"Validate\" to check your inputs before running the simulation.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        } else {
            // Define checklist items by error class
            val checks = listOf(
                "Well name set"            to validationResult.errors.none { it is ValidationError.WellNameEmpty },
                "Total depth > 0"          to validationResult.errors.none { it is ValidationError.TotalDepthInvalid },
                "Casing defined"           to validationResult.errors.none { it is ValidationError.CasingOdInvalid || it is ValidationError.CasingIdInvalid },
                "Drill string complete"    to validationResult.errors.none {
                    it is ValidationError.DrillPipeOdInvalid || it is ValidationError.DrillPipeIdInvalid ||
                            it is ValidationError.DrillCollarOdInvalid || it is ValidationError.DrillCollarLengthInvalid
                },
                "Bit size defined"         to validationResult.errors.none { it is ValidationError.BitSizeNotDefined },
                "Nozzles configured"       to validationResult.errors.none { it is ValidationError.NozzleCountInvalid || it is ValidationError.NozzleSizesInvalid },
                "Mud weight set"           to validationResult.errors.none { it is ValidationError.MudWeightInvalid },
                "Flow rate set"            to validationResult.errors.none { it is ValidationError.FlowRateInvalid },
                "Formation zones defined"  to validationResult.errors.none { it is ValidationError.NoFormationZones },
                "Mud weight ≥ pore pressure" to validationResult.errors.none { it is ValidationError.MudWeightBelowPorePressure }
            )

            checks.forEach { (label, passed) ->
                ValidationCheckItem(label = label, passed = passed)
            }

            // Additional error messages
            val specificErrors = validationResult.errors.filterIsInstance<ValidationError.FormationZoneDepthInvalid>() +
                    validationResult.errors.filterIsInstance<ValidationError.FormationWindowInvalid>() +
                    validationResult.errors.filterIsInstance<ValidationError.MudWeightBelowPorePressure>()

            if (specificErrors.isNotEmpty()) {
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))
                specificErrors.forEach { error ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            tint     = AmberWarning,
                            modifier = Modifier.size(14.dp).padding(top = 1.dp)
                        )
                        Text(
                            text  = error.message,
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberWarning
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Run Simulation button
            val canRun = validationResult.isValid
            Button(
                onClick  = onRunSimulation,
                enabled  = canRun,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = AmberGold,
                    contentColor           = NavyDeep,
                    disabledContainerColor = CardElevated,
                    disabledContentColor   = TextMuted
                )
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Run Simulation →", style = MaterialTheme.typography.labelMedium)
            }

            if (!canRun) {
                Text(
                    text  = "${validationResult.errors.size} error(s) must be resolved before running.",
                    style = MaterialTheme.typography.labelSmall,
                    color = CoralDanger
                )
            }
        }
    }
}

@Composable
private fun ValidationCheckItem(label: String, passed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint     = if (passed) TealSafe else CoralDanger,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (passed) MaterialTheme.colorScheme.onSurface else CoralDanger
        )
    }
}