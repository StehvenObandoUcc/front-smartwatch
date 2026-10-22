package com.smartwatch.recordatorios.ui.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwCard
import com.smartwatch.recordatorios.ui.components.BwEmptyState
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusLabel
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.LARGE_FONT_SCALE
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText

/** Las dosis de hoy con su estado y cuántas van tomadas. */
@Composable
fun TodayScreen(
    doses: List<DoseEntity>?,
    onOpenDose: (DoseEntity) -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            edgeItem { BwHeader(stringResource(R.string.today_title)) }
            if (!doses.isNullOrEmpty()) {
                edgeItem {
                    BwText(
                        text =
                            stringResource(
                                R.string.today_progress,
                                doses.count { it.status == DoseStatus.TAKEN },
                                doses.size,
                            ),
                        style = BwTheme.type.support,
                        color = BwTheme.colors.contentSoft,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            when {
                doses == null -> Unit
                doses.isEmpty() ->
                    edgeItem {
                        BwEmptyState(
                            icon = R.drawable.ic_pill,
                            title = stringResource(R.string.today_empty),
                            body = stringResource(R.string.today_empty_body),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                else ->
                    doses.forEach { dose ->
                        edgeItem(key = dose.id, container = true) {
                            DoseRow(dose, onClick = { onOpenDose(dose) })
                        }
                    }
            }
        }
    }
}

/** Una fila por dosis: la hora a la izquierda, y el medicamento con su estado a la derecha. */
@Composable
private fun DoseRow(
    dose: DoseEntity,
    onClick: () -> Unit,
) {
    BwCard(
        onClick = onClick,
        onClickLabel = stringResource(R.string.open_dose),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth(),
    ) {
        val time = @Composable {
            BwText(timeText(dose.scheduledAt), style = BwTheme.type.label.merge(fontFeatureSettings = "tnum"))
        }
        val details = @Composable {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey, centered = false)
                BwText(
                    text = "${dose.doseLabel} · ${doseStatusLabel(dose)}",
                    style = BwTheme.type.support,
                    color = BwTheme.colors.contentSoft,
                )
            }
        }
        // Con letra grande la hora va encima: a su lado el nombre no cabe y se cortaría.
        if (LocalDensity.current.fontScale >= LARGE_FONT_SCALE) {
            time()
            details()
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                time()
                details()
            }
        }
    }
}
