package com.example.smilegarden.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin

private data class FlowerSpec(val xFraction: Float, val heightFraction: Float, val phase: Float)

private val flowerSpecs = listOf(
    FlowerSpec(0.15f, 0.85f, 0.0f),
    FlowerSpec(0.32f, 1.00f, 0.6f),
    FlowerSpec(0.50f, 0.78f, 1.2f),
    FlowerSpec(0.68f, 0.95f, 1.8f),
    FlowerSpec(0.85f, 0.82f, 2.4f),
)

/**
 * Drawn when a smile is detected: a field of sunflowers grows from the
 * ground and opens. [bloomProgress] (0..1) drives both the overall fade-in
 * and each flower's stem height / petal opening, so the field genuinely
 * "blooms" rather than just appearing.
 */
@Composable
fun SunflowerScene(bloomProgress: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sway")
    val sway by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "swayAngle"
    )

    Canvas(modifier = modifier.alpha(bloomProgress.coerceIn(0f, 1f))) {
        val w = size.width
        val h = size.height

        // Sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF8EC9F0), Color(0xFFD7F2E3)),
                startY = 0f,
                endY = h * 0.7f
            ),
            size = Size(w, h * 0.7f)
        )

        // Sun
        drawCircle(
            color = Color(0xFFFFE066),
            radius = w * 0.1f,
            center = Offset(w * 0.18f, h * 0.15f)
        )

        // Grass
        drawRect(
            color = Color(0xFF7CB342),
            topLeft = Offset(0f, h * 0.68f),
            size = Size(w, h * 0.32f)
        )
        drawRect(
            color = Color(0xFF689F38),
            topLeft = Offset(0f, h * 0.9f),
            size = Size(w, h * 0.1f)
        )

        flowerSpecs.forEach { spec ->
            drawSunflower(
                x = w * spec.xFraction,
                groundY = h * 0.92f,
                maxHeight = h * 0.32f * spec.heightFraction,
                growth = bloomProgress,
                swayAngle = sway + spec.phase
            )
        }
    }
}

private fun DrawScope.drawSunflower(
    x: Float,
    groundY: Float,
    maxHeight: Float,
    growth: Float,
    swayAngle: Float
) {
    if (growth <= 0.02f) return

    val stemHeight = maxHeight * growth
    val swayOffset = sin(swayAngle) * (stemHeight * 0.05f)
    val topX = x + swayOffset
    val topY = groundY - stemHeight

    // Stem
    drawLine(
        color = Color(0xFF558B2F),
        start = Offset(x, groundY),
        end = Offset(topX, topY),
        strokeWidth = (maxHeight * 0.035f).coerceAtLeast(2f)
    )

    // Leaf
    drawOval(
        color = Color(0xFF689F38),
        topLeft = Offset(x - maxHeight * 0.18f, groundY - stemHeight * 0.5f),
        size = Size(maxHeight * 0.16f, maxHeight * 0.08f)
    )

    // Bloom opens only once the stem has grown past ~30%
    val petalProgress = ((growth - 0.3f) / 0.7f).coerceIn(0f, 1f)
    if (petalProgress <= 0f) return

    val flowerRadius = maxHeight * 0.16f * petalProgress
    val petalCount = 12
    repeat(petalCount) { i ->
        val angle = (2 * Math.PI / petalCount) * i
        val px = topX + (flowerRadius * 1.6f) * cos(angle).toFloat()
        val py = topY + (flowerRadius * 1.6f) * sin(angle).toFloat()
        drawOval(
            color = Color(0xFFFFC107),
            topLeft = Offset(px - flowerRadius * 0.5f, py - flowerRadius * 0.3f),
            size = Size(flowerRadius, flowerRadius * 0.6f)
        )
    }

    // Center
    drawCircle(
        color = Color(0xFF6D4C28),
        radius = flowerRadius * 0.9f,
        center = Offset(topX, topY)
    )
}
