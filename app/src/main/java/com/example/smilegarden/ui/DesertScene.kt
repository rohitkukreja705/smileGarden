package com.example.smilegarden.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Drawn when no smile is detected: warm sky, sand, dunes and a couple of
 * cacti. [alpha] is driven by (1 - bloomProgress) so it fades out as the
 * sunflower scene fades in.
 */
@Composable
fun DesertScene(alpha: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.alpha(alpha.coerceIn(0f, 1f))) {
        val w = size.width
        val h = size.height

        // Sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFCE38A), Color(0xFFF38181)),
                startY = 0f,
                endY = h * 0.65f
            ),
            size = Size(w, h * 0.65f)
        )

        // Sun
        drawCircle(
            color = Color(0xFFFFF1B0),
            radius = w * 0.12f,
            center = Offset(w * 0.78f, h * 0.18f)
        )

        // Sand base
        drawRect(
            color = Color(0xFFE3B778),
            topLeft = Offset(0f, h * 0.62f),
            size = Size(w, h * 0.38f)
        )

        // Dune ridge
        val dune = Path().apply {
            moveTo(0f, h * 0.72f)
            quadraticBezierTo(w * 0.25f, h * 0.65f, w * 0.5f, h * 0.74f)
            quadraticBezierTo(w * 0.75f, h * 0.82f, w, h * 0.7f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(dune, color = Color(0xFFD7A368))

        drawCactus(x = w * 0.18f, baseY = h * 0.95f, scale = w * 0.012f)
        drawCactus(x = w * 0.62f, baseY = h * 0.97f, scale = w * 0.009f)
    }
}

private fun DrawScope.drawCactus(x: Float, baseY: Float, scale: Float) {
    val color = Color(0xFF5B7553)
    drawRoundRect(
        color = color,
        topLeft = Offset(x - scale * 2, baseY - scale * 14),
        size = Size(scale * 4, scale * 14),
        cornerRadius = CornerRadius(scale * 2)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(x - scale * 6, baseY - scale * 10),
        size = Size(scale * 4, scale * 6),
        cornerRadius = CornerRadius(scale * 1.5f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(x + scale * 2, baseY - scale * 9),
        size = Size(scale * 4, scale * 6),
        cornerRadius = CornerRadius(scale * 1.5f)
    )
}
