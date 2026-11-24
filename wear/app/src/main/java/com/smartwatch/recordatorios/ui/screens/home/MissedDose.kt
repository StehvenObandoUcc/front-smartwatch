package com.smartwatch.recordatorios.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.Tomi
import com.smartwatch.recordatorios.ui.components.TomiMood
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText

/** La última toma de hoy que ya sonó y sigue pendiente (sin posponer a más tarde), o null. */
internal fun missedDose(
    today: List<DoseEntity>,
    now: Long,
): DoseEntity? =
    today
        .filter { it.status == DoseStatus.SCHEDULED && it.nextAlarmAt <= now }
        .maxByOrNull { it.scheduledAt }

/** «¿La tomaste?»: Tomi se preocupa, no regaña; un toque abre la alerta para resolverla. */
@Composable
internal fun MissedDose(
    dose: DoseEntity,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Tomi(mood = TomiMood.WORRIED, width = Sizes.tomiSmall)
            BwText(stringResource(R.string.home_missed_title), style = BwTheme.type.title, textAlign = TextAlign.Center)
            BwText(
                stringResource(R.string.home_missed_body, dose.medicationName, timeText(dose.scheduledAt)),
                color = BwTheme.colors.contentSoft,
                textAlign = TextAlign.Center,
            )
        }
        BwButton(
            label = stringResource(R.string.home_missed_action),
            onClick = onClick,
            primary = true,
            icon = R.drawable.ic_check,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
