package com.smartwatch.recordatorios.tile

import androidx.compose.ui.graphics.toArgb
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.degrees
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Arc
import androidx.wear.protolayout.LayoutElementBuilders.ArcLine
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.FontStyle
import androidx.wear.protolayout.LayoutElementBuilders.Row
import androidx.wear.protolayout.LayoutElementBuilders.Spacer
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.ModifiersBuilders
import com.smartwatch.recordatorios.ui.theme.AppColors
import com.smartwatch.recordatorios.ui.theme.medicationColor

/**
 * «Anillo del día»: el anillo verde de «Tomada» crece con cada dosis registrada hoy; en el centro,
 * «2 de 3 tomadas hoy» y debajo la próxima con la placa de su color junto al nombre. Fondo negro
 * plano y los colores de Recuerda; un toque abre la app.
 */
fun dayTileLayout(
    state: DayTileState,
    texts: DayTileTexts,
    open: ActionBuilders.LaunchAction,
): LayoutElementBuilders.LayoutElement {
    val colors = AppColors
    val content =
        Column
            .Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .addContent(text(texts.count, HERO_SP, colors.content.toArgb(), bold = true))
            .addContent(text(texts.takenToday, SUPPORT_SP, colors.contentSoft.toArgb(), bold = false))
    texts.next?.let { next ->
        content.addContent(Spacer.Builder().setHeight(dp(GAP_DP)).build())
        content.addContent(nextRow(next, state.next?.colorKey))
    }
    return Box
        .Builder()
        .setWidth(expand())
        .setHeight(expand())
        .setModifiers(
            ModifiersBuilders.Modifiers
                .Builder()
                .setClickable(
                    ModifiersBuilders.Clickable
                        .Builder()
                        .setId(CLICK_ID)
                        .setOnClick(open)
                        .build(),
                ).setSemantics(
                    ModifiersBuilders.Semantics
                        .Builder()
                        .setContentDescription(texts.description)
                        .build(),
                ).build(),
        ).addContent(ring(FULL_TURN, colors.track.toArgb()))
        .addContent(ring(FULL_TURN * state.progress, colors.stateTaken.toArgb()))
        .addContent(content.build())
        .build()
}

private fun ring(
    sweep: Float,
    color: Int,
): LayoutElementBuilders.LayoutElement =
    Arc
        .Builder()
        .setAnchorAngle(degrees(0f))
        .setAnchorType(LayoutElementBuilders.ARC_ANCHOR_START)
        .addContent(
            ArcLine
                .Builder()
                .setLength(degrees(sweep))
                .setThickness(dp(RING_DP))
                .setColor(argb(color))
                .build(),
        ).build()

private fun nextRow(
    label: String,
    colorKey: String?,
): LayoutElementBuilders.LayoutElement {
    val row = Row.Builder().setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
    colorKey?.let { key ->
        // La placa del medicamento en su color; el nombre va siempre al lado.
        row.addContent(
            Box
                .Builder()
                .setWidth(dp(PLATE_DP))
                .setHeight(dp(PLATE_DP))
                .setModifiers(
                    ModifiersBuilders.Modifiers
                        .Builder()
                        .setBackground(
                            ModifiersBuilders.Background
                                .Builder()
                                .setColor(argb(medicationColor(key).toArgb()))
                                .setCorner(
                                    ModifiersBuilders.Corner
                                        .Builder()
                                        .setRadius(dp(PLATE_RADIUS_DP))
                                        .build(),
                                ).build(),
                        ).build(),
                ).build(),
        )
        row.addContent(Spacer.Builder().setWidth(dp(GAP_DP)).build())
    }
    row.addContent(text(label, BODY_SP, AppColors.content.toArgb(), bold = true))
    return row.build()
}

private fun text(
    value: String,
    size: Float,
    color: Int,
    bold: Boolean,
): LayoutElementBuilders.LayoutElement =
    Text
        .Builder()
        .setText(value)
        .setMaxLines(1)
        .setFontStyle(
            FontStyle
                .Builder()
                .setSize(sp(size))
                .setColor(argb(color))
                .setWeight(
                    if (bold) LayoutElementBuilders.FONT_WEIGHT_BOLD else LayoutElementBuilders.FONT_WEIGHT_NORMAL,
                ).build(),
        ).build()

private const val CLICK_ID = "open_app"
private const val FULL_TURN = 360f
private const val RING_DP = 8f
private const val HERO_SP = 40f
private const val BODY_SP = 16f
private const val SUPPORT_SP = 14f
private const val GAP_DP = 6f
private const val PLATE_DP = 14f
private const val PLATE_RADIUS_DP = 3f
