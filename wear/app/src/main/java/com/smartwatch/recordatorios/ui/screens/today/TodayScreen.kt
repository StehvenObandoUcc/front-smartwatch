package com.smartwatch.recordatorios.ui.screens.today

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText

/** Las dosis de hoy con su estado. */
@Composable
fun TodayScreen(
    doses: List<DoseEntity>?,
    onOpenDose: (DoseEntity) -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(stringResource(R.string.today_title)) } }
            when {
                doses == null -> Unit
                doses.isEmpty() ->
                    item {
                        Text(
                            text = stringResource(R.string.today_empty),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                else ->
                    doses.forEach { dose ->
                        item(key = dose.id) {
                            Card(onClick = { onOpenDose(dose) }, modifier = Modifier.fillMaxWidth()) {
                                MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey)
                                Text(dose.doseLabel, style = MaterialTheme.typography.bodyMedium)
                                Text(doseStatusText(dose), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
            }
        }
    }
}
