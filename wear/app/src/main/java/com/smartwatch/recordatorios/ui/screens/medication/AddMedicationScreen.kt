package com.smartwatch.recordatorios.ui.screens.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwActionPill
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Nuevo medicamento en el reloj: nombre y dosis con el teclado del sistema (que también deja dictar)
 * y las horas con botones grandes de −/+ (de 15 en 15 min). Suena todos los días. Hasta [MAX_TIMES] horas.
 */
@Composable
fun AddMedicationScreen(
    saving: Boolean,
    onSave: (name: String, dose: String, times: List<LocalTime>) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var dose by rememberSaveable { mutableStateOf("") }
    // Minutos desde las 00:00, para que rememberSaveable los guarde sin un Saver propio.
    val times = rememberSaveable { mutableStateListOf(DEFAULT_MINUTES) }
    val listState = rememberTransformingLazyColumnState()
    val canSave = name.isNotBlank() && !saving

    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            edgeItem { BwHeader(stringResource(R.string.add_title)) }
            edgeItem(container = true) {
                Field(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = stringResource(R.string.add_name),
                    imeAction = ImeAction.Next,
                )
            }
            edgeItem(container = true) {
                Field(
                    value = dose,
                    onValueChange = { dose = it },
                    placeholder = stringResource(R.string.add_dose),
                    imeAction = ImeAction.Done,
                )
            }
            edgeItem { Caption(stringResource(R.string.add_times)) }
            times.forEachIndexed { index, minutes ->
                edgeItem(key = "time-$index") {
                    TimeStepper(
                        minutes = minutes,
                        onChange = { times[index] = it.mod(MINUTES_PER_DAY) },
                        onRemove = if (times.size > 1) ({ times.removeAt(index) }) else null,
                    )
                }
            }
            if (times.size < MAX_TIMES) {
                edgeItem {
                    BwActionPill(
                        label = stringResource(R.string.add_time),
                        icon = R.drawable.ic_snooze,
                        onClick = { times.add((times.last() + HOURS_BETWEEN * MINUTES_PER_HOUR).mod(MINUTES_PER_DAY)) },
                        quiet = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            edgeItem { Caption(stringResource(R.string.add_daily)) }
            edgeItem(container = true) {
                BwButton(
                    label = stringResource(if (saving) R.string.add_saving else R.string.add_save),
                    onClick = {
                        if (canSave) {
                            onSave(
                                name,
                                dose.ifBlank { DEFAULT_DOSE },
                                times.map {
                                    LocalTime.of(
                                        it / MINUTES_PER_HOUR,
                                        it % MINUTES_PER_HOUR,
                                    )
                                },
                            )
                        }
                    },
                    primary = canSave,
                    icon = R.drawable.ic_check,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** Campo de texto del tema: al tocarlo aparece el teclado del reloj (con dictado). */
@Composable
private fun Field(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction,
) {
    val colors = BwTheme.colors
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = BwTheme.type.body.merge(color = colors.content, textAlign = TextAlign.Center),
        cursorBrush = SolidColor(colors.info),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = imeAction),
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = Sizes.buttonHeight)
                .clip(BwTheme.shapes.pill)
                .background(colors.surface)
                .border(Sizes.outline, colors.outline, BwTheme.shapes.pill)
                .semantics { contentDescription = placeholder },
        decorationBox = { inner ->
            Box(Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md), contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    BwText(placeholder, color = colors.contentSoft, textAlign = TextAlign.Center)
                }
                inner()
            }
        },
    )
}

/** Una hora con −/+ a los lados (botones de 48 dp) y, si hay más de una, Quitar debajo. */
@Composable
private fun TimeStepper(
    minutes: Int,
    onChange: (Int) -> Unit,
    onRemove: (() -> Unit)?,
) {
    val at =
        LocalDate
            .now()
            .atTime(minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
        ) {
            StepButton(stringResource(R.string.add_earlier), "−") { onChange(minutes - STEP_MINUTES) }
            BwText(timeText(at), style = BwTheme.type.title.merge(fontFeatureSettings = "tnum"))
            StepButton(stringResource(R.string.add_later), "+") { onChange(minutes + STEP_MINUTES) }
        }
        onRemove?.let { remove ->
            Box(
                modifier =
                    Modifier
                        .heightIn(min = Sizes.touchMin)
                        .clip(BwTheme.shapes.pill)
                        .clickable(role = Role.Button, onClick = remove)
                        .padding(horizontal = Spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                BwText(
                    text = stringResource(R.string.add_remove_time),
                    style = BwTheme.type.support,
                    color = BwTheme.colors.contentSoft,
                )
            }
        }
    }
}

@Composable
private fun StepButton(
    description: String,
    symbol: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(Sizes.touchMin)
                .clip(BwTheme.shapes.pill)
                .background(BwTheme.colors.surface)
                .clickable(role = Role.Button, onClick = onClick)
                .clearAndSetSemantics {
                    contentDescription = description
                    role = Role.Button
                },
        contentAlignment = Alignment.Center,
    ) {
        BwText(symbol, style = BwTheme.type.title)
    }
}

@Composable
private fun Caption(text: String) {
    BwText(
        text = text,
        style = BwTheme.type.support,
        color = BwTheme.colors.contentSoft,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

private const val MINUTES_PER_HOUR = 60
private const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR
private const val DEFAULT_MINUTES = 8 * MINUTES_PER_HOUR
private const val STEP_MINUTES = 15
private const val HOURS_BETWEEN = 12
private const val MAX_TIMES = 4
private const val DEFAULT_DOSE = "1 dosis"
