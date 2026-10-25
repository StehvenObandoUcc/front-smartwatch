package com.smartwatch.recordatorios.ui.screens.adherence

import com.smartwatch.recordatorios.data.remote.AdherenceDay

/** Cómo terminó un día: todas tomadas, alguna no tomada o sin dosis vencidas. */
enum class DayResult { ALL_TAKEN, SOME_MISSED, NO_DOSES }

fun dayResult(day: AdherenceDay): DayResult =
    when {
        day.total == 0 -> DayResult.NO_DOSES
        day.taken == day.total -> DayResult.ALL_TAKEN
        else -> DayResult.SOME_MISSED
    }
