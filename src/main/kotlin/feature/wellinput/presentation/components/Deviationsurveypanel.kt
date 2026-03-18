package com.oussama_chatri.feature.wellinput.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.LabeledTextField
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.wellinput.domain.model.SurveyStation
import kotlin.math.*

/**
 * Deviation Survey tab — editable station table on the left,
 * 2D well-path canvas preview on the right.
 */
@Composable
fun DeviationSurveyPanel(
    stations: List<SurveyStation>,
    totalDepth: Double,
    onAddStation: () -> Unit,
    onUpdateStation: (Int, SurveyStation) -> Unit,
    onRemoveStation: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left: survey table
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionCard(title = "Directional Survey Stations") {
                SurveyTableHeader()
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))

                if (stations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No survey stations. Add the first station below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                } else {
                    stations.forEachIndexed { index, station ->
                        SurveyStationRow(
                            index    = index,
                            station  = station,
                            onChange = { updated -> onUpdateStation(index, updated) },
                            onDelete = { onRemoveStation(index) }
                        )
                        if (index < stations.lastIndex) {
                            HorizontalDivider(color = DividerColor.copy(alpha = 0.3f))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick  = onAddStation,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Survey Station")
                }
            }
        }

        // Right: well path preview
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
        ) {
            SectionCard(title = "Well Path Preview") {
                WellPathCanvas(
                    stations = stations,
                    modifier = Modifier.fillMaxWidth().height(340.dp)
                )

                Spacer(Modifier.height(12.dp))

                // Stats
                val maxInc   = stations.maxOfOrNull { it.inclination } ?: 0.0
                val maxEast  = stations.mapNotNull { it.easting }.maxOfOrNull { abs(it) } ?: 0.0
                val maxNorth = stations.mapNotNull { it.northing }.maxOfOrNull { abs(it) } ?: 0.0
                val horizReach = sqrt(maxEast.pow(2) + maxNorth.pow(2))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip("Max Inc: ${String.format("%.1f", maxInc)}°", Modifier.weight(1f))
                    StatChip("H-Reach: ${String.format("%.0f", horizReach)} ft", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SurveyTableHeader() {
    val cols = listOf(
        "Stn" to 0.06f,
        "MD (ft)" to 0.16f,
        "Inc (°)" to 0.14f,
        "Azi (°)" to 0.14f,
        "TVD (ft)" to 0.16f,
        "N (ft)" to 0.14f,
        "E (ft)" to 0.14f,
        "" to 0.06f
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        cols.forEach { (label, weight) ->
            Text(
                text     = label,
                style    = MaterialTheme.typography.labelSmall,
                color    = TextSecondary,
                modifier = Modifier.weight(weight)
            )
        }
    }
}

@Composable
private fun SurveyStationRow(
    index: Int,
    station: SurveyStation,
    onChange: (SurveyStation) -> Unit,
    onDelete: () -> Unit
) {
    var md  by remember(station.id) { mutableStateOf(if (station.measuredDepth == 0.0) "" else station.measuredDepth.toString()) }
    var inc by remember(station.id) { mutableStateOf(if (station.inclination == 0.0) "" else station.inclination.toString()) }
    var azi by remember(station.id) { mutableStateOf(if (station.azimuth == 0.0) "" else station.azimuth.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Station #
        Text(
            text     = "${index + 1}",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextMuted,
            modifier = Modifier.weight(0.06f)
        )

        // MD (editable)
        CompactTextField(
            value         = md,
            onValueChange = {
                md = it
                onChange(station.copy(measuredDepth = it.toDoubleOrNull() ?: station.measuredDepth))
            },
            placeholder   = "0",
            modifier      = Modifier.weight(0.16f)
        )
        // Inc (editable)
        CompactTextField(
            value         = inc,
            onValueChange = {
                inc = it
                onChange(station.copy(inclination = it.toDoubleOrNull() ?: station.inclination))
            },
            placeholder   = "0.0",
            modifier      = Modifier.weight(0.14f)
        )
        // Azi (editable)
        CompactTextField(
            value         = azi,
            onValueChange = {
                azi = it
                onChange(station.copy(azimuth = it.toDoubleOrNull() ?: station.azimuth))
            },
            placeholder   = "0.0",
            modifier      = Modifier.weight(0.14f)
        )

        // Calculated: TVD
        Text(
            text     = station.tvd?.let { String.format("%.0f", it) } ?: "—",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextMuted,
            modifier = Modifier.weight(0.16f)
        )
        Text(
            text     = station.northing?.let { String.format("%.0f", it) } ?: "—",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextMuted,
            modifier = Modifier.weight(0.14f)
        )
        Text(
            text     = station.easting?.let { String.format("%.0f", it) } ?: "—",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextMuted,
            modifier = Modifier.weight(0.14f)
        )

        // Delete
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).weight(0.06f)) {
            Icon(Icons.Default.Delete, null, tint = CoralDanger.copy(alpha = 0.6f), modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
private fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = {
            Text(placeholder, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        },
        singleLine    = true,
        textStyle     = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AmberGold.copy(alpha = 0.7f),
            unfocusedBorderColor = DividerColor,
            cursorColor          = AmberGold
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        modifier      = modifier.height(42.dp)
    )
}

@Composable
private fun WellPathCanvas(
    stations: List<SurveyStation>,
    modifier: Modifier = Modifier
) {
    // ── Capture @Composable colors before Canvas ──
    val colorBg      = NavyDeep
    val colorDivider = DividerColor
    val colorTeal    = TealSafe
    val colorAmber   = AmberGold
    val colorDanger  = CoralDanger
    val colorMuted   = TextMuted

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(colorBg)
    ) {
        if (stations.size < 2) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Add at least 2 stations\nto preview the well path",
                    style     = MaterialTheme.typography.bodySmall,
                    color     = colorMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            return@Box
        }

        val points = computeMinimumCurvaturePoints(stations)
        if (points.isEmpty()) return@Box

        val maxTvd   = points.maxOf { it.second }.coerceAtLeast(1.0)
        val maxHoriz = points.maxOf { it.first  }.coerceAtLeast(1.0)

        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val w = size.width
            val h = size.height

            // Grid
            for (i in 1..4) {
                val y = h * i / 4f
                drawLine(colorDivider.copy(alpha = 0.25f), Offset(0f, y), Offset(w, y), 1f)
            }
            for (i in 1..4) {
                val x = w * i / 4f
                drawLine(colorDivider.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, h), 1f)
            }

            // Axes
            drawLine(colorDivider.copy(alpha = 0.5f), Offset(0f, 0f), Offset(0f, h), 1.5f)
            drawLine(colorDivider.copy(alpha = 0.5f), Offset(0f, 0f), Offset(w, 0f), 1.5f)

            // Well path with gradient stroke
            points.forEachIndexed { i, _ ->
                if (i == 0) return@forEachIndexed
                val fraction = i.toFloat() / points.lastIndex
                val color    = lerpColor(colorTeal, colorAmber, fraction)
                val (h1, t1) = points[i - 1]
                val (h2, t2) = points[i]
                drawLine(
                    color       = color,
                    start       = Offset((h1 / maxHoriz * w).toFloat(), (t1 / maxTvd * h).toFloat()),
                    end         = Offset((h2 / maxHoriz * w).toFloat(), (t2 / maxTvd * h).toFloat()),
                    strokeWidth = 3f
                )
            }

            // Surface dot
            drawCircle(colorAmber, radius = 5f, center = Offset(0f, 0f))

            // TD dot
            val (lastH, lastT) = points.last()
            drawCircle(
                colorDanger,
                radius = 5f,
                center = Offset((lastH / maxHoriz * w).toFloat(), (lastT / maxTvd * h).toFloat())
            )
        }
    }
}


/** Simple colour lerp for gradient path colouring. */
private fun lerpColor(start: Color, end: Color, t: Float): Color {
    val f = t.coerceIn(0f, 1f)
    return Color(
        red   = start.red   + (end.red   - start.red)   * f,
        green = start.green + (end.green - start.green) * f,
        blue  = start.blue  + (end.blue  - start.blue)  * f
    )
}

/**
 * Minimum-curvature method to convert MD/Inc/Azi → (horizontal departure, TVD) pairs.
 * Returns a list of (horizontalDeparture, tvd) for each survey station.
 */
private fun computeMinimumCurvaturePoints(stations: List<SurveyStation>): List<Pair<Double, Double>> {
    if (stations.isEmpty()) return emptyList()
    val result = mutableListOf(0.0 to 0.0)
    var tvd     = 0.0
    var northing = 0.0
    var easting  = 0.0

    for (i in 1 until stations.size) {
        val prev = stations[i - 1]
        val curr = stations[i]
        val dMd  = curr.measuredDepth - prev.measuredDepth
        if (dMd <= 0) { result += (result.last().first to result.last().second); continue }

        val i1 = Math.toRadians(prev.inclination)
        val i2 = Math.toRadians(curr.inclination)
        val a1 = Math.toRadians(prev.azimuth)
        val a2 = Math.toRadians(curr.azimuth)

        val beta = acos(
            (cos(i2 - i1) - (1 - cos(a2 - a1)) * sin(i1) * sin(i2)).coerceIn(-1.0, 1.0)
        )
        val rf   = if (beta == 0.0) 1.0 else (2.0 / beta) * tan(beta / 2.0)

        tvd     += dMd / 2.0 * (cos(i1) + cos(i2)) * rf
        northing += dMd / 2.0 * (sin(i1) * cos(a1) + sin(i2) * cos(a2)) * rf
        easting  += dMd / 2.0 * (sin(i1) * sin(a1) + sin(i2) * sin(a2)) * rf

        val horiz = sqrt(northing.pow(2) + easting.pow(2))
        result   += horiz to tvd
    }
    return result
}

@Composable
private fun StatChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(CardElevated)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}