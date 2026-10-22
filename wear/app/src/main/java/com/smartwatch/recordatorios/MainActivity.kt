package com.smartwatch.recordatorios

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
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
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.smartwatch.recordatorios.alarm.DoseIntents
import com.smartwatch.recordatorios.alarm.DoseNotifier
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.remote.PairingRepository
import com.smartwatch.recordatorios.data.remote.TokenStore
import com.smartwatch.recordatorios.sync.SyncScheduler
import com.smartwatch.recordatorios.ui.components.BwAppScaffold
import com.smartwatch.recordatorios.ui.components.BwPager
import com.smartwatch.recordatorios.ui.screens.adherence.AdherenceScreen
import com.smartwatch.recordatorios.ui.screens.alert.DoseAlertActivity
import com.smartwatch.recordatorios.ui.screens.chat.ChatScreen
import com.smartwatch.recordatorios.ui.screens.chat.ChatViewModel
import com.smartwatch.recordatorios.ui.screens.home.HomeScreen
import com.smartwatch.recordatorios.ui.screens.home.HomeViewModel
import com.smartwatch.recordatorios.ui.screens.pairing.PairingRoute
import com.smartwatch.recordatorios.ui.screens.permissions.PermissionsScreen
import com.smartwatch.recordatorios.ui.screens.today.TodayScreen
import com.smartwatch.recordatorios.ui.screens.watch.WatchScreen
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var notifier: DoseNotifier

    @Inject lateinit var tokens: TokenStore

    @Inject lateinit var syncScheduler: SyncScheduler

    @Inject lateinit var pairing: PairingRepository

    private val viewModel: HomeViewModel by viewModels()

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordatoriosTheme {
                BwAppScaffold {
                    val paired by tokens.paired.collectAsStateWithLifecycle()
                    if (paired) PairedRoute() else PairingRoute()
                }
            }
        }
    }

    /**
     * Vinculado: primero el paso guiado de permisos (una vez), luego las páginas. Mientras la app está
     * abierta sincroniza cada [FOREGROUND_SYNC], para que un medicamento recién creado en la web llegue
     * en un par de minutos y no al siguiente ciclo de WorkManager.
     */
    @Composable
    private fun PairedRoute() {
        val exactAllowed by viewModel.exactAlarmsAllowed.collectAsStateWithLifecycle()
        var notificationsAllowed by remember { mutableStateOf(notifier.canPostNotifications()) }
        val notificationLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                notificationsAllowed = granted
            }
        val onboarding = remember { getSharedPreferences(ONBOARDING, MODE_PRIVATE) }
        var permissionsSeen by rememberSaveable { mutableStateOf(onboarding.getBoolean(KEY_PERMISSIONS_SEEN, false)) }

        LifecycleResumeEffect(Unit) {
            viewModel.refreshPermissions()
            notificationsAllowed = notifier.canPostNotifications()
            syncScheduler.syncNow()
            val polling =
                lifecycleScope.launch {
                    while (true) {
                        delay(FOREGROUND_SYNC.toMillis())
                        syncScheduler.syncNow()
                    }
                }
            onPauseOrDispose { polling.cancel() }
        }

        val requestNotifications = { notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        if (permissionsSeen) {
            MainRoute(notificationsAllowed, exactAllowed, requestNotifications)
        } else {
            PermissionsScreen(
                notificationsAllowed = notificationsAllowed,
                exactAlarmsAllowed = exactAllowed,
                onRequestNotifications = requestNotifications,
                onRequestExactAlarms = ::openExactAlarmSettings,
                onDone = {
                    onboarding.edit().putBoolean(KEY_PERMISSIONS_SEEN, true).apply()
                    permissionsSeen = true
                },
            )
        }
    }

    @Composable
    private fun MainRoute(
        notificationsAllowed: Boolean,
        exactAllowed: Boolean,
        requestNotifications: () -> Unit,
    ) {
        var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
        // Fuera del when: al volver del chat se sigue en la misma página.
        val pagerState = rememberPagerState(pageCount = { PAGES })
        val chatState by chatViewModel.state.collectAsStateWithLifecycle()
        val speechLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                    ?.let(chatViewModel::ask)
            }
        val next by viewModel.next.collectAsStateWithLifecycle()
        val today by viewModel.today.collectAsStateWithLifecycle()
        val adherence by viewModel.adherence.collectAsStateWithLifecycle()
        val planInfo by viewModel.planInfo.collectAsStateWithLifecycle()
        BackHandler(enabled = screen != Screen.HOME) {
            screen = Screen.HOME
            chatViewModel.reset()
        }

        when (screen) {
            Screen.CHAT ->
                ChatScreen(state = chatState, onAsk = { askByVoice { speechLauncher.launch(it) } })
            Screen.HOME ->
                BwPager(pagerState) { page ->
                    when (page) {
                        0 ->
                            HomeScreen(
                                next = next,
                                planInfo = planInfo,
                                exactAlarmsAllowed = exactAllowed,
                                notificationsAllowed = notificationsAllowed,
                                onRequestExactAlarms = ::openExactAlarmSettings,
                                onRequestNotifications = requestNotifications,
                                onOpenDose = ::openDose,
                                onOpenChat = { screen = Screen.CHAT },
                                onRetryPlan = syncScheduler::syncNow,
                            )
                        1 -> TodayScreen(doses = today, onOpenDose = ::openDose)
                        2 -> AdherenceScreen(summary = adherence)
                        else -> WatchScreen(onUnpair = { lifecycleScope.launch { pairing.unpair() } })
                    }
                }
        }
    }

    private fun askByVoice(launch: (Intent) -> Unit) {
        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.chat_prompt))
        try {
            launch(intent)
        } catch (_: ActivityNotFoundException) {
            chatViewModel.speechUnavailable()
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

/** HOME son las páginas Próxima, Hoy, Adherencia y Reloj. */
private enum class Screen { HOME, CHAT }

private const val PAGES = 4
private const val ONBOARDING = "onboarding"
private const val KEY_PERMISSIONS_SEEN = "permissions_seen"
private val FOREGROUND_SYNC = Duration.ofMinutes(2)
