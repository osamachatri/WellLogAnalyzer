package com.oussama_chatri.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val FADE_IN_MS       = 600
private const val LOGO_RISE_MS     = 700
private const val RING_EXPAND_MS   = 900
private const val TEXT_FADE_MS     = 500
private const val TAGLINE_FADE_MS  = 400
private const val BAR_FILL_MS      = 1_400
private const val HOLD_MS          = 400L
private const val TOTAL_MS         = 3_600L

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        started = true
        delay(TOTAL_MS)
        onFinished()
    }


    // Background fade
    val bgAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(FADE_IN_MS, easing = LinearEasing),
        label         = "bgAlpha"
    )

    // Logo / icon rise + fade
    val logoAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(LOGO_RISE_MS, delayMillis = 200, easing = FastOutSlowInEasing),
        label         = "logoAlpha"
    )
    val logoOffsetY by animateFloatAsState(
        targetValue   = if (started) 0f else 40f,
        animationSpec = tween(LOGO_RISE_MS, delayMillis = 200, easing = FastOutSlowInEasing),
        label         = "logoOffsetY"
    )

    // Spinning outer ring
    val ringRotation by animateFloatAsState(
        targetValue   = if (started) 360f else 0f,
        animationSpec = tween(RING_EXPAND_MS * 3, easing = LinearEasing),
        label         = "ringRotation"
    )
    val ringScale by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(RING_EXPAND_MS, delayMillis = 100, easing = FastOutSlowInEasing),
        label         = "ringScale"
    )

    // App name
    val titleAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(TEXT_FADE_MS, delayMillis = 700, easing = LinearEasing),
        label         = "titleAlpha"
    )
    val titleOffsetY by animateFloatAsState(
        targetValue   = if (started) 0f else 20f,
        animationSpec = tween(TEXT_FADE_MS + 100, delayMillis = 700, easing = FastOutSlowInEasing),
        label         = "titleOffsetY"
    )

    // Tagline
    val taglineAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(TAGLINE_FADE_MS, delayMillis = 1_100, easing = LinearEasing),
        label         = "taglineAlpha"
    )

    // Progress bar
    val barProgress by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(BAR_FILL_MS, delayMillis = 1_400, easing = FastOutSlowInEasing),
        label         = "barProgress"
    )

    // Indefinite slow rotation for the dashed orbit ring
    val infiniteRotation by rememberInfiniteTransition(label = "orbit")
        .animateFloat(
            initialValue   = 0f,
            targetValue    = 360f,
            animationSpec  = infiniteRepeatable(
                animation  = tween(4_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label          = "orbitRot"
        )

    // Colors
    val navy   = Color(0xFF0D1B2A)
    val slate  = Color(0xFF1A2535)
    val amber  = Color(0xFFF4A917)
    val teal   = Color(0xFF2EC4B6)
    val muted  = Color(0xFF4A5568)
    val white  = Color(0xFFFFFFFF)
    val coral  = Color(0xFFE63946)

    // Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(bgAlpha)
            .background(
                Brush.radialGradient(
                    colors  = listOf(Color(0xFF162336), navy),
                    radius  = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // Decorative background grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = white.copy(alpha = 0.025f)
            val cols = 12
            val rows = 8
            for (c in 0..cols) {
                val x = size.width * c / cols
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
            }
            for (r in 0..rows) {
                val y = size.height * r / rows
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
            }
        }

        // Center column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.fillMaxSize()
        ) {

            Spacer(Modifier.weight(1f))

            // Logo canvas — drill-bit ring + wellbore icon
            Box(
                modifier          = Modifier
                    .size(160.dp)
                    .offset(y = logoOffsetY.dp)
                    .alpha(logoAlpha),
                contentAlignment  = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx     = size.width  / 2f
                    val cy     = size.height / 2f
                    val outer  = size.width  / 2f * ringScale
                    val inner  = outer * 0.55f
                    val mid    = outer * 0.75f

                    // ── Outer dashed orbit ring (slow spin) ──
                    val dashCount = 24
                    for (i in 0 until dashCount) {
                        val angle  = (i * 360f / dashCount + infiniteRotation) * PI.toFloat() / 180f
                        val start  = Offset(cx + (outer - 6f) * cos(angle), cy + (outer - 6f) * sin(angle))
                        val end    = Offset(cx + outer * cos(angle),         cy + outer * sin(angle))
                        val alpha  = if (i % 2 == 0) 0.6f else 0.15f
                        drawLine(teal.copy(alpha = alpha), start, end, strokeWidth = 3f, cap = StrokeCap.Round)
                    }

                    // ── Amber ring (rotates with ringRotation) ──
                    val sweepRot = ringRotation * PI.toFloat() / 180f
                    drawArc(
                        color      = amber,
                        startAngle = ringRotation - 30f,
                        sweepAngle = 260f,
                        useCenter  = false,
                        style      = Stroke(width = 3f, cap = StrokeCap.Round),
                        topLeft    = androidx.compose.ui.geometry.Offset(cx - mid, cy - mid),
                        size       = androidx.compose.ui.geometry.Size(mid * 2, mid * 2)
                    )

                    // ── Wellbore tube (vertical center line) ──
                    drawLine(
                        brush       = Brush.verticalGradient(
                            colors  = listOf(teal, amber, coral),
                            startY  = cy - inner * 0.8f,
                            endY    = cy + inner * 0.8f
                        ),
                        start       = Offset(cx, cy - inner * 0.8f),
                        end         = Offset(cx, cy + inner * 0.8f),
                        strokeWidth = 5f,
                        cap         = StrokeCap.Round
                    )

                    // ── Inner filled circle ──
                    drawCircle(
                        brush  = Brush.radialGradient(
                            colors  = listOf(Color(0xFF1E3050), navy),
                            center  = Offset(cx, cy),
                            radius  = inner * 0.85f
                        ),
                        radius = inner * 0.85f,
                        center = Offset(cx, cy)
                    )

                    // ── Nozzle dots around the inner ring ──
                    val nozzles = 6
                    for (i in 0 until nozzles) {
                        val angle  = (i * 360f / nozzles + ringRotation * 0.5f) * PI.toFloat() / 180f
                        val dotX   = cx + inner * cos(angle)
                        val dotY   = cy + inner * sin(angle)
                        drawCircle(amber, radius = 4f, center = Offset(dotX, dotY))
                    }

                    // ── Center amber dot ──
                    drawCircle(amber, radius = 6f, center = Offset(cx, cy))
                    drawCircle(navy,  radius = 3f, center = Offset(cx, cy))
                }
            }

            Spacer(Modifier.height(32.dp))

            // App name
            Text(
                text       = "WellLog",
                fontSize   = 42.sp,
                fontWeight = FontWeight.Bold,
                color      = white,
                letterSpacing = 2.sp,
                modifier   = Modifier
                    .offset(y = titleOffsetY.dp)
                    .alpha(titleAlpha)
            )
            Text(
                text       = "ANALYZER",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = amber,
                letterSpacing = 8.sp,
                modifier   = Modifier
                    .offset(y = titleOffsetY.dp)
                    .alpha(titleAlpha)
            )

            Spacer(Modifier.height(12.dp))

            // Tagline
            Text(
                text      = "Hydraulics Simulation  ·  3D Wellbore Viewer  ·  Engineering Reports",
                fontSize  = 11.sp,
                color     = muted,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .alpha(taglineAlpha)
                    .padding(horizontal = 48.dp)
            )

            Spacer(Modifier.weight(1f))

            // Progress bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.padding(bottom = 48.dp)
            ) {
                // Bar track
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(2.dp)
                        .background(muted.copy(alpha = 0.3f))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val fillW = size.width * barProgress
                        // Glowing fill
                        drawRect(
                            brush   = Brush.horizontalGradient(
                                colors  = listOf(teal, amber),
                                startX  = 0f,
                                endX    = fillW.coerceAtLeast(1f)
                            ),
                            size    = androidx.compose.ui.geometry.Size(fillW, size.height)
                        )
                        // Leading glow dot
                        if (fillW > 4f) {
                            drawCircle(
                                color  = amber.copy(alpha = 0.9f),
                                radius = 4f,
                                center = Offset(fillW, size.height / 2f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Version label
                Text(
                    text     = "v1.0.0  ·  Powered by Jetpack Compose Desktop",
                    fontSize = 10.sp,
                    color    = muted.copy(alpha = 0.6f),
                    modifier = Modifier.alpha(taglineAlpha)
                )
            }
        }

        // Corner decoration — compass rose hint
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .alpha(taglineAlpha * 0.4f)
        ) {
            val cx = size.width  / 2f
            val cy = size.height / 2f
            val r  = size.minDimension / 2f
            drawCircle(teal.copy(alpha = 0.3f), r, Offset(cx, cy), style = Stroke(1f))
            for (i in 0 until 4) {
                val a = (i * 90f + infiniteRotation * 0.2f) * PI.toFloat() / 180f
                drawLine(
                    color       = teal.copy(alpha = 0.6f),
                    start       = Offset(cx, cy),
                    end         = Offset(cx + r * cos(a), cy + r * sin(a)),
                    strokeWidth = 1.5f
                )
            }
        }

        // "PETROLEUM ENGINEERING" vertical side text
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .alpha(taglineAlpha * 0.35f)
        ) {
            Text(
                text     = "P  E  T  R  O  L  E  U  M    E  N  G  I  N  E  E  R  I  N  G",
                fontSize = 9.sp,
                color    = muted,
                modifier = Modifier
                    .wrapContentSize()
                    .let {
                        // rotate text 90° counter-clockwise
                        it.graphicsLayer { rotationZ = -90f }
                    }
            )
        }
    }
}