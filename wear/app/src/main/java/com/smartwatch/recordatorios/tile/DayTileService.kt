package com.smartwatch.recordatorios.tile

import android.content.Context
import android.text.format.DateFormat
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.smartwatch.recordatorios.MainActivity
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.repository.DoseRepository
import com.smartwatch.recordatorios.ui.theme.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import java.time.ZoneId
import javax.inject.Inject

/**
 * Tile «Anillo del día» (se ve al deslizar desde la esfera). Lee las dosis de Room, así que funciona
 * vinculado o solo en el reloj y sin conexión. Se refresca cuando cambian las dosis (ver
 * [requestDayTileUpdate]) y, como red de seguridad, cada [FRESHNESS_MILLIS].
 */
@AndroidEntryPoint
class DayTileService : TileService() {
    @Inject lateinit var repository: DoseRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        scope.future {
            val now = System.currentTimeMillis()
            val state = dayTileState(repository.observeDoses().first(), now, ZoneId.systemDefault())
            val layout = dayTileLayout(state, texts(state), openApp())
            TileBuilders.Tile
                .Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(FRESHNESS_MILLIS)
                .setTileTimeline(TimelineBuilders.Timeline.fromLayoutElement(layout))
                .build()
        }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(
            ResourceBuilders.Resources
                .Builder()
                .setVersion(RESOURCES_VERSION)
                .build(),
        )

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun texts(state: DayTileState): DayTileTexts {
        val count = getString(R.string.tile_count, state.taken, state.total)
        val next =
            state.next?.let {
                val time = formatTime(it.scheduledAt, DateFormat.is24HourFormat(this))
                val label = "$time ${it.medicationName}"
                if (state.nextIsLater) getString(R.string.tile_next_later, label) else label
            }
        val summary =
            if (state.total == 0) getString(R.string.tile_none) else "$count ${getString(R.string.tile_taken_today)}"
        return DayTileTexts(
            count = if (state.total == 0) getString(R.string.tile_none_short) else count,
            takenToday = getString(if (state.total == 0) R.string.tile_none else R.string.tile_taken_today),
            next = next,
            description =
                listOfNotNull(
                    summary,
                    next?.let { getString(R.string.tile_next_said, it) },
                ).joinToString(". "),
        )
    }

    private fun openApp(): ActionBuilders.LaunchAction =
        ActionBuilders.LaunchAction
            .Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity
                    .Builder()
                    .setPackageName(packageName)
                    .setClassName(MainActivity::class.java.name)
                    .build(),
            ).build()

    private companion object {
        const val RESOURCES_VERSION = "1"
        const val FRESHNESS_MILLIS = 15 * 60 * 1000L
    }
}

/** Pide a Wear OS que vuelva a pedir el Tile (tras registrar, posponer o sincronizar). */
fun requestDayTileUpdate(context: Context) {
    TileService.getUpdater(context).requestUpdate(DayTileService::class.java)
}
