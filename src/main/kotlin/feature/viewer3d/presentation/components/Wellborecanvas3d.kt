package com.oussama_chatri.feature.viewer3d.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.feature.viewer3d.domain.model.CameraPreset
import com.oussama_chatri.feature.viewer3d.domain.model.GeologyLayer
import com.oussama_chatri.feature.viewer3d.domain.model.Trajectory3DPoint
import com.oussama_chatri.feature.viewer3d.domain.model.ViewerSettings
import com.oussama_chatri.feature.viewer3d.domain.model.Wellbore3DModel
import com.oussama_chatri.feature.viewer3d.presentation.util.ecdColor
import com.oussama_chatri.feature.viewer3d.presentation.util.lithologyColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Pure-Compose Canvas 3D viewer.
 *
 * Uses a simple oblique / isometric projection — no OpenGL dependency.
 * The scene is rendered from the selected [CameraPreset] perspective.
 *
 * Gesture support:
 *   - Two-finger pinch  → zoom
 *   - Drag (single touch / mouse click+drag) → rotate azimuth
 */
@Composable
fun WellboreCanvas3D(
    model: Wellbore3DModel,
    settings: ViewerSettings,
    modifier: Modifier = Modifier
) {
    // Rotation in degrees around the vertical axis; starts at 30° for a nice isometric look
    var rotationDeg by remember(settings.cameraPreset) {
        mutableStateOf(presetRotation(settings.cameraPreset))
    }
    var elevationDeg by remember(settings.cameraPreset) {
        mutableStateOf(presetElevation(settings.cameraPreset))
    }
    var zoom by remember { mutableStateOf(1f) }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(NavyDeep)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, pinchZoom, _ ->
                    zoom = (zoom * pinchZoom).coerceIn(0.3f, 4f)
                    rotationDeg  = (rotationDeg  + pan.x * 0.3f) % 360f
                    elevationDeg = (elevationDeg - pan.y * 0.15f).coerceIn(-10f, 85f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width < 1f || size.height < 1f) return@Canvas

            val cx = size.width  / 2f
            val cy = size.height / 2f

            // Scale: fit the total depth into ~60% of canvas height
            val baseScale  = min(size.width, size.height) * 0.55f / model.totalDepth.toFloat()
            val scale      = baseScale * zoom

            val rotRad = Math.toRadians(rotationDeg.toDouble())
            val elvRad = Math.toRadians(elevationDeg.toDouble())

            // Projection helper: world (x=East, y=North, z=TVD↓) → canvas (px, py)
            fun project(wx: Double, wy: Double, wz: Double): Offset {
                // Rotate around vertical (Z) axis
                val rx = wx * cos(rotRad) - wy * sin(rotRad)
                val ry = wx * sin(rotRad) + wy * cos(rotRad)
                val rz = wz

                // Apply elevation tilt
                val px = rx
                val py = rz * cos(elvRad) - ry * sin(elvRad)

                return Offset(
                    x = cx + (px * scale).toFloat(),
                    y = cy + (py * scale).toFloat()
                )
            }

            // Floor grid
            drawGrid(scale, rotRad, elvRad, cx, cy, model)

            // Geology layers — draw back-to-front (deepest first for painter's algorithm)
            val visibleLayers = model.geologyLayers
                .filter { settings.layerVisibility[it.id] != false }
                .sortedByDescending { it.topDepth }

            visibleLayers.forEach { layer ->
                drawGeologyLayer(layer, model, settings, ::project)
            }

            // Well tube
            if (settings.showWellTube && model.trajectoryPoints.size >= 2) {
                drawWellTube(model, settings, ::project)
            }

            // Surface marker (small platform at top)
            val surfacePt = project(0.0, 0.0, 0.0)
            drawRect(
                color   = Color(0xFF607080),
                topLeft = Offset(surfacePt.x - 12f, surfacePt.y - 6f),
                size    = androidx.compose.ui.geometry.Size(24f, 12f)
            )

            // Compass rose (bottom right corner)
            drawCompassRose(rotRad)

            // Formation labels
            if (settings.showFormationLabels) {
                // Labels are composable — skip for canvas; handled by overlay
            }
        }
    }
}

private fun DrawScope.drawGrid(
    scale: Float,
    rotRad: Double,
    elvRad: Double,
    cx: Float,
    cy: Float,
    model: Wellbore3DModel
) {
    val halfSize = (model.maxHorizontalReach.coerceAtLeast(500.0) * 1.4).toFloat()
    val step     = halfSize / 4f
    val gridColor = DividerColor.copy(alpha = 0.18f)

    fun proj(wx: Double, wy: Double, wz: Double): Offset {
        val rx = wx * cos(rotRad) - wy * sin(rotRad)
        val ry = wx * sin(rotRad) + wy * cos(rotRad)
        val rz = wz
        val px = rx
        val py = rz * cos(elvRad) - ry * sin(elvRad)
        return Offset(cx + (px * scale).toFloat(), cy + (py * scale).toFloat())
    }

    // Draw a flat grid at z = 0 (surface)
    var g = -halfSize
    while (g <= halfSize) {
        val a = proj(g.toDouble(), -halfSize.toDouble(), 0.0)
        val b = proj(g.toDouble(),  halfSize.toDouble(), 0.0)
        drawLine(gridColor, a, b, 1f)

        val c = proj(-halfSize.toDouble(), g.toDouble(), 0.0)
        val d = proj( halfSize.toDouble(), g.toDouble(), 0.0)
        drawLine(gridColor, c, d, 1f)

        g += step
    }
}

private fun DrawScope.drawGeologyLayer(
    layer: GeologyLayer,
    model: Wellbore3DModel,
    settings: ViewerSettings,
    project: (Double, Double, Double) -> Offset
) {
    val color     = lithologyColor(layer.lithology)
    val halfR     = (model.maxHorizontalReach.coerceAtLeast(800.0) * 1.1)
    val topZ      = layer.topDepth
    val bottomZ   = layer.bottomDepth

    // Draw as a flat ellipse at topDepth (top face of the formation cylinder)
    // Then connect with side lines to create a box-like disc shape
    val segments = 32
    val topRing   = mutableListOf<Offset>()
    val botRing   = mutableListOf<Offset>()

    for (i in 0..segments) {
        val angle = 2.0 * PI * i / segments
        val wx    = halfR * cos(angle)
        val wy    = halfR * sin(angle)
        topRing += project(wx, wy, topZ)
        botRing += project(wx, wy, bottomZ)
    }

    // Top face fill (semi-transparent)
    val topPath = Path()
    topPath.moveTo(topRing.first().x, topRing.first().y)
    topRing.drop(1).forEach { topPath.lineTo(it.x, it.y) }
    topPath.close()
    drawPath(topPath, color.copy(alpha = 0.18f))
    drawPath(topPath, color.copy(alpha = 0.45f), style = Stroke(width = 1f))

    // Side strokes (a few vertical lines to suggest depth)
    listOf(0, segments / 4, segments / 2, 3 * segments / 4).forEach { i ->
        drawLine(color.copy(alpha = 0.25f), topRing[i], botRing[i], 1f)
    }
}

private fun DrawScope.drawWellTube(
    model: Wellbore3DModel,
    settings: ViewerSettings,
    project: (Double, Double, Double) -> Offset
) {
    val pts = model.trajectoryPoints

    for (i in 1 until pts.size) {
        val from = pts[i - 1]
        val to   = pts[i]

        val color = if (settings.showEcdColorMap && from.ecd != null) {
            ecdColor(from.ecd, model.minEcd, model.maxEcd)
        } else {
            Color(0xFFF4A917) // fallback amber
        }

        val p1 = project(from.x, from.y, from.z)
        val p2 = project(to.x, to.y, to.z)

        drawLine(color, p1, p2, strokeWidth = 4f, cap = StrokeCap.Round)
    }

    // TD marker
    val td = pts.last()
    val tdPt = project(td.x, td.y, td.z)
    drawCircle(Color(0xFFE63946), radius = 6f, center = tdPt)
    drawCircle(Color.White, radius = 3f, center = tdPt)
}

private fun DrawScope.drawCompassRose(rotRad: Double) {
    val cx = size.width  - 36f
    val cy = size.height - 36f
    val r  = 18f

    // N arrow
    val northAngle = -(rotRad + PI / 2)
    val nx = cx + (r * cos(northAngle)).toFloat()
    val ny = cy + (r * sin(northAngle)).toFloat()
    drawLine(Color(0xFF4FC3F7), Offset(cx, cy), Offset(nx, ny), 2f)
    drawCircle(Color(0xFF4FC3F7), 3f, Offset(nx, ny))

    // Outer ring
    drawCircle(TextMuted.copy(alpha = 0.3f), r, Offset(cx, cy), style = Stroke(1f))
}

private fun presetRotation(preset: CameraPreset): Float = when (preset) {
    CameraPreset.TOP       -> 45f
    CameraPreset.SIDE      -> 90f
    CameraPreset.FRONT     -> 0f
    CameraPreset.ISOMETRIC -> 30f
}

private fun presetElevation(preset: CameraPreset): Float = when (preset) {
    CameraPreset.TOP       -> 80f
    CameraPreset.SIDE      -> 5f
    CameraPreset.FRONT     -> 5f
    CameraPreset.ISOMETRIC -> 25f
}