package com.smartwatch.recordatorios.complication

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.smartwatch.recordatorios.MainActivity
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.repository.DoseRepository
import com.smartwatch.recordatorios.tile.dayTileState
import com.smartwatch.recordatorios.ui.theme.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import java.time.ZoneId
import javax.inject.Inject

/**
 * Complication «Próxima toma» para la esfera: la hora de la siguiente dosis y, debajo, el
 * medicamento. Lee Room como el Tile, así que funciona vinculado, en modo local y sin conexión.
 */
@AndroidEntryPoint
class NextDoseComplicationService : SuspendingComplicationDataSourceService() {
    @Inject lateinit var repository: DoseRepository

    override fun getPreviewData(type: ComplicationType): ComplicationData? =
        if (type == ComplicationType.SHORT_TEXT) shortText("20:00", "Losartán") else null

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        if (request.complicationType != ComplicationType.SHORT_TEXT) return null
        val state = dayTileState(repository.observeDoses().first(), System.currentTimeMillis(), ZoneId.systemDefault())
        val next = state.next
        return if (next == null) {
            shortText(getString(R.string.complication_none), null)
        } else {
            shortText(formatTime(next.scheduledAt, DateFormat.is24HourFormat(this)), next.medicationName)
        }
    }

    private fun shortText(
        text: String,
        title: String?,
    ): ShortTextComplicationData {
        val said =
            if (title == null) text else getString(R.string.tile_next_said, "$text $title")
        return ShortTextComplicationData
            .Builder(PlainComplicationText.Builder(text).build(), PlainComplicationText.Builder(said).build())
            .apply { title?.let { setTitle(PlainComplicationText.Builder(it).build()) } }
            .setTapAction(openApp())
            .build()
    }

    private fun openApp(): PendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
}

/** Pide a la esfera que vuelva a leer la complication (tras registrar, posponer o sincronizar). */
fun requestNextDoseComplicationUpdate(context: Context) {
    ComplicationDataSourceUpdateRequester
        .create(context, ComponentName(context, NextDoseComplicationService::class.java))
        .requestUpdateAll()
}
