package com.smartwatch.recordatorios.ui.components

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.smartwatch.recordatorios.ui.theme.Palette
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.TitleFont

/**
 * Tomi, la mascota: una cápsula con cara que dice el estado de la pantalla. Decorativa para TalkBack
 * (la pantalla ya lo dice con texto). Se anima según el ánimo y se queda quieta si el sistema tiene
 * las animaciones desactivadas.
 */
@Composable
fun Tomi(
    mood: TomiMood,
    modifier: Modifier = Modifier,
    width: Dp = Sizes.tomi,
) {
    val anim = rememberTomiAnimation(mood, motion = !rememberReducedMotion())
    val measurer = rememberTextMeasurer()
    val face = remember(mood) { faces.getValue(mood) }
    Canvas(modifier.width(width).aspectRatio(VIEW_W / VIEW_H)) {
        val k = size.width / VIEW_W
        // Los dibujos van en unidades del lienzo; las letras («z», «?») se miden en píxeles y se
        // colocan fuera de la escala para no agrandarse dos veces.
        val glyph = TextStyle(fontFamily = TitleFont, fontSize = (GLYPH_UNITS * k).toSp(), color = Palette.ink)

        fun at(
            x: Float,
            y: Float,
        ) = Offset((ORIGIN_X + x) * k, (ORIGIN_Y + anim.jump() + y) * k)
        scale(k, k, pivot = Offset.Zero) {
            translate(ORIGIN_X, ORIGIN_Y + anim.jump()) {
                if (mood == TomiMood.RINGING) {
                    translate(-WAVE_AMPLITUDE * anim.wave(), 0f) { strokes(wavesLeft) }
                    translate(WAVE_AMPLITUDE * anim.wave(), 0f) { strokes(wavesRight) }
                }
                rotate(anim.tilt(), pivot = Offset(CENTER_X, CENTER_Y)) {
                    body()
                    face(face, anim.blink())
                }
            }
        }
        when (mood) {
            TomiMood.SLEEPING ->
                drawText(
                    measurer.measure("z", glyph),
                    topLeft = at(Z_X, Z_Y - Z_RISE * anim.drift()),
                    alpha = 1f - anim.drift(),
                )
            TomiMood.LISTENING ->
                scale(anim.pulse(), pivot = at(BUBBLE_X, BUBBLE_BOTTOM)) {
                    scale(k, k, pivot = Offset.Zero) {
                        translate(ORIGIN_X, ORIGIN_Y + anim.jump()) {
                            drawPath(bubble, Palette.white)
                            drawPath(bubble, Palette.ink, style = Stroke(BUBBLE_STROKE, join = StrokeJoin.Round))
                        }
                    }
                    val q = measurer.measure("?", glyph)
                    val center = at(BUBBLE_X + BUBBLE_W / 2, BUBBLE_Y + BUBBLE_H / 2)
                    drawText(q, topLeft = Offset(center.x - q.size.width / 2, center.y - q.size.height / 2))
                }
            else -> Unit
        }
    }
}

/** Rasgos de una cara en unidades del lienzo: trazos (cejas, bocas en arco), ojos rellenos y brillos. */
private class Face(
    strokes: String,
    eyes: String,
    shine: String = "",
) {
    val strokes = svg(strokes)
    val eyes = svg(eyes)
    val shine = svg(shine)
    val eyeLine =
        this.eyes
            .getBounds()
            .center.y
}

private fun svg(data: String): Path = PathParser().parsePathString(data).toPath()

/** Ojos de punto, ojos grandes y sus brillos, ya colocados en la mitad blanca de la cápsula. */
private const val DOT_EYES =
    "M94.75 44.7a4.73 4.73 0 1 0 9.46 0a4.73 4.73 0 1 0 -9.46 0" +
        "M115.39 44.7a4.73 4.73 0 1 0 9.46 0a4.73 4.73 0 1 0 -9.46 0"
private const val BIG_EYES =
    "M93.03 43.84a6.45 6.45 0 1 0 12.9 0a6.45 6.45 0 1 0 -12.9 0" +
        "M113.67 43.84a6.45 6.45 0 1 0 12.9 0a6.45 6.45 0 1 0 -12.9 0"
private const val SHINE =
    "M95.7 42.12a2.06 2.06 0 1 0 4.13 0a2.06 2.06 0 1 0 -4.13 0" +
        "M116.34 42.12a2.06 2.06 0 1 0 4.13 0a2.06 2.06 0 1 0 -4.13 0"
private const val OPEN_MOUTH = "M105.07 60.18a4.73 5.59 0 1 0 9.46 0a4.73 5.59 0 1 0 -9.46 0"

private val faces =
    mapOf(
        TomiMood.NEUTRAL to Face("M102.92 58.46L116.68 58.46", DOT_EYES),
        TomiMood.RINGING to Face("", BIG_EYES + OPEN_MOUTH, SHINE),
        TomiMood.HAPPY to
            Face(
                "M93.46 46.42Q99.48 37.82 105.5 46.42M114.1 46.42Q120.12 37.82 126.14 46.42" +
                    "M99.48 55.02Q109.8 67.06 120.12 55.02",
                "",
            ),
        TomiMood.SLEEPING to
            Face(
                "M93.46 43.84Q99.48 49.86 105.5 43.84M114.1 43.84Q120.12 49.86 126.14 43.84" +
                    "M105.5 58.46Q109.8 61.9 114.1 58.46",
                "",
            ),
        TomiMood.WORRIED to
            Face(
                "M92.6 37.82L104.64 33.52M127 37.82L114.96 33.52M104.64 61.9Q109.8 56.74 114.96 61.9",
                "M95.18 47.28a4.3 4.3 0 1 0 8.6 0a4.3 4.3 0 1 0 -8.6 0" +
                    "M115.82 47.28a4.3 4.3 0 1 0 8.6 0a4.3 4.3 0 1 0 -8.6 0",
            ),
        TomiMood.LISTENING to Face("M103.78 58.46Q109.8 62.76 115.82 58.46", BIG_EYES, SHINE),
    )

/** Ondas de sonido a cada lado de la cápsula. */
private val wavesLeft = svg("M14 34Q6 50 14 66M4 28Q-8 50 4 72")
private val wavesRight = svg("M146 34Q154 50 146 66M156 28Q168 50 156 72")

/** Globo de diálogo arriba a la derecha, con su piquito hacia Tomi. */
private val bubble =
    svg("M136 -24H154A8 8 0 0 1 162 -16V-6A8 8 0 0 1 154 2H144L130 10L136 2A8 8 0 0 1 128 -6V-16A8 8 0 0 1 136 -24Z")

private val ink = Palette.ink

/** Cápsula horizontal: mitad salvia, mitad blanca, contorno tinta y la junta en medio. */
private fun DrawScope.body() {
    val shape = RoundRect(BODY_L, BODY_T, BODY_R, BODY_B, CornerRadius(BODY_RADIUS))
    val outline = Path().apply { addRoundRect(shape) }
    drawPath(outline, Palette.white)
    clipPath(outline) { drawRect(Palette.sage, Offset(BODY_L, BODY_T), Size(CENTER_X - BODY_L, BODY_B - BODY_T)) }
    drawLine(ink, Offset(CENTER_X, BODY_T), Offset(CENTER_X, BODY_B), JOINT_W)
    drawPath(outline, ink, style = Stroke(OUTLINE_W))
}

/** La cara va en la mitad blanca, para que la junta no la parta. Al parpadear se aplastan los ojos. */
private fun DrawScope.face(
    face: Face,
    blink: Float,
) {
    strokes(face.strokes)
    scale(1f, blink, pivot = Offset(CENTER_X, face.eyeLine)) { drawPath(face.eyes, ink) }
    drawPath(face.shine, Palette.white)
}

private fun DrawScope.strokes(path: Path) =
    drawPath(path, ink, style = Stroke(FACE_W, cap = StrokeCap.Round, join = StrokeJoin.Round))

/**
 * Valores de la animación como lecturas diferidas: el lienzo los lee al dibujar, así cada fotograma
 * solo redibuja y no recompone.
 */
private class TomiAnimation(
    val wave: () -> Float = { 0f },
    val tilt: () -> Float = { 0f },
    val jump: () -> Float = { 0f },
    val drift: () -> Float = { STILL_DRIFT },
    val pulse: () -> Float = { 1f },
    val blink: () -> Float = { 1f },
)

/** Solo corre la animación del ánimo que se ve; sin movimiento, ninguna (ni pide fotogramas). */
@Composable
private fun rememberTomiAnimation(
    mood: TomiMood,
    motion: Boolean,
): TomiAnimation {
    // Feliz: un salto al aparecer.
    val jump = remember(mood) { Animatable(0f) }
    LaunchedEffect(mood, motion) {
        if (motion && mood == TomiMood.HAPPY) {
            jump.animateTo(-JUMP, tween(JUMP_UP_MS, easing = FastOutSlowInEasing))
            jump.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    if (!motion) return remember { TomiAnimation() }
    return when (mood) {
        TomiMood.HAPPY -> remember(jump) { TomiAnimation(jump = { jump.value }) }
        TomiMood.RINGING -> {
            val loop = rememberInfiniteTransition(label = "tomi")
            val wave = loop.animateFloat(-1f, 1f, reverse(WAVE_MS), label = "wave")
            val tilt = loop.animateFloat(-TILT_DEG, TILT_DEG, reverse(TILT_MS), label = "tilt")
            remember(wave, tilt) { TomiAnimation(wave = { wave.value }, tilt = { tilt.value }) }
        }
        TomiMood.SLEEPING -> {
            val drift =
                rememberInfiniteTransition(label = "tomi").animateFloat(
                    0f,
                    1f,
                    infiniteRepeatable(tween(DRIFT_MS, easing = LinearEasing), RepeatMode.Restart),
                    label = "drift",
                )
            remember(drift) { TomiAnimation(drift = { drift.value }) }
        }
        TomiMood.LISTENING -> {
            val pulse =
                rememberInfiniteTransition(label = "tomi").animateFloat(
                    1f,
                    PULSE_MAX,
                    reverse(PULSE_MS),
                    label = "pulse",
                )
            remember(pulse) { TomiAnimation(pulse = { pulse.value }) }
        }
        TomiMood.NEUTRAL, TomiMood.WORRIED -> {
            val blink =
                rememberInfiniteTransition(label = "tomi").animateFloat(
                    1f,
                    1f,
                    infiniteRepeatable(
                        keyframes {
                            durationMillis = BLINK_CYCLE_MS
                            1f at BLINK_START_MS
                            BLINK_CLOSED at BLINK_START_MS + BLINK_HALF_MS
                            1f at BLINK_START_MS + 2 * BLINK_HALF_MS
                        },
                    ),
                    label = "blink",
                )
            remember(blink) { TomiAnimation(blink = { blink.value }) }
        }
    }
}

private fun reverse(millis: Int) =
    infiniteRepeatable(tween<Float>(millis, easing = FastOutSlowInEasing), RepeatMode.Reverse)

/** Con «Quitar animaciones» del sistema la escala de animaciones es 0. */
@Composable
private fun rememberReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/** Lienzo en unidades propias: la cápsula ocupa 116 × 60 y deja sitio a ondas, «z» y globo. */
private const val VIEW_W = 176f
private const val VIEW_H = 112f
private const val ORIGIN_X = 8f
private const val ORIGIN_Y = 26f

private const val BODY_L = 22f
private const val BODY_T = 20f
private const val BODY_R = 138f
private const val BODY_B = 80f
private const val BODY_RADIUS = 30f
private const val CENTER_X = 80f
private const val CENTER_Y = 50f
private const val OUTLINE_W = 5.5f
private const val JOINT_W = 3f
private const val FACE_W = 4.3f

private const val WAVE_AMPLITUDE = 3f
private const val GLYPH_UNITS = 30f
private const val Z_X = 136f
private const val Z_Y = -8f
private const val Z_RISE = 16f
private const val STILL_DRIFT = 0.2f
private const val BUBBLE_X = 128f
private const val BUBBLE_Y = -24f
private const val BUBBLE_W = 34f
private const val BUBBLE_H = 26f
private const val BUBBLE_BOTTOM = 10f
private const val BUBBLE_STROKE = 3f

private const val WAVE_MS = 250
private const val TILT_MS = 240
private const val TILT_DEG = 4f
private const val DRIFT_MS = 2400
private const val PULSE_MS = 600
private const val PULSE_MAX = 1.08f
private const val JUMP = 10f
private const val JUMP_UP_MS = 180
private const val BLINK_CYCLE_MS = 3600
private const val BLINK_START_MS = 3300
private const val BLINK_HALF_MS = 90
private const val BLINK_CLOSED = 0.1f
