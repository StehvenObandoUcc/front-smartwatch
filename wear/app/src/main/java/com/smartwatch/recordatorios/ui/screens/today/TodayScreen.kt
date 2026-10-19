package com.smartwatch.recordatorios.ui.screens.today

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwCard
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.theme.BwTheme

/** Las dosis de hoy con su estado y cuántas van tomadas. */
@Composable
fun TodayScreen(
    doses: List<DoseEntity>?,
    onOpenDose: (DoseEntity) -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { BwHeader(stringResource(R.string.today_title)) }
            if (!doses.isNullOrEmpty()) {
                item {
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
                    item {
                        BwText(
                            text = stringResource(R.string.today_empty),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                else ->
                    doses.forEach { dose ->
                        item(key = dose.id) {
                            BwCard(
                                onClick = { onOpenDose(dose) },
                                onClickLabel = stringResource(R.string.open_dose),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey)
                                BwText(dose.doseLabel)
                                BwText(doseStatusText(dose))
                            }
                        }
                    }
            }
        }
    }
}
