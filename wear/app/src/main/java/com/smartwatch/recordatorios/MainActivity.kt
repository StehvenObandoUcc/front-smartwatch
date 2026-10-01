package com.smartwatch.recordatorios

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import com.smartwatch.recordatorios.alarm.DoseIntents
import com.smartwatch.recordatorios.alarm.DoseNotifier
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.remote.TokenStore
import com.smartwatch.recordatorios.sync.SyncScheduler
import com.smartwatch.recordatorios.ui.screens.alert.DoseAlertActivity
import com.smartwatch.recordatorios.ui.screens.home.HomeScreen
import com.smartwatch.recordatorios.ui.screens.home.HomeViewModel
import com.smartwatch.recordatorios.ui.screens.pairing.PairingRoute
import com.smartwatch.recordatorios.ui.screens.today.TodayScreen
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var notifier: DoseNotifier

    @Inject lateinit var tokens: TokenStore

    @Inject lateinit var syncScheduler: SyncScheduler

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordatoriosTheme {
                AppScaffold {
                    val paired by tokens.paired.collectAsStateWithLifecycle()
                    if (paired) MainRoute() else PairingRoute()
                }
            }
        }
    }

    @Composable
    private fun MainRoute() {
        var showToday by rememberSaveable { mutableStateOf(false) }
        val next by viewModel.next.collectAsStateWithLifecycle()
        val today by viewModel.today.collectAsStateWithLifecycle()
        val planInfo by viewModel.planInfo.collectAsStateWithLifecycle()
        val exactAllowed by viewModel.exactAlarmsAllowed.collectAsStateWithLifecycle()
        var notificationsAllowed by remember { mutableStateOf(notifier.canPostNotifications()) }
        val notificationLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                notificationsAllowed = granted
            }

        LifecycleResumeEffect(Unit) {
            viewModel.refreshPermissions()
            notificationsAllowed = notifier.canPostNotifications()
            syncScheduler.syncNow()
            onPauseOrDispose { }
        }
        BackHandler(enabled = showToday) { showToday = false }

        if (showToday) {
            TodayScreen(doses = today, onOpenDose = ::openDose)
        } else {
            HomeScreen(
                next = next,
                planInfo = planInfo,
                exactAlarmsAllowed = exactAllowed,
                notificationsAllowed = notificationsAllowed,
                onRequestExactAlarms = ::openExactAlarmSettings,
                onRequestNotifications = { notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                onOpenDose = ::openDose,
                onOpenToday = { showToday = true },
            )
        }
    }

    private fun openDose(dose: DoseEntity) {
        startActivity(Intent(this, DoseAlertActivity::class.java).setData(DoseIntents.uriFor(dose.id)))
    }

    private fun openExactAlarmSettings() {
        val packageUri = Uri.fromParts("package", packageName, null)
        try {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, packageUri))
        } catch (_: ActivityNotFoundException) {
            // Algunos relojes no tienen esa pantalla: se abre la ficha de la app.
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri))
        }
    }
}
