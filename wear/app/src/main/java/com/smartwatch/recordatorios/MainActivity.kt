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
import com.smartwatch.recordatorios.data.remote.DeviceSession
import com.smartwatch.recordatorios.data.remote.PairingRepository
import com.smartwatch.recordatorios.data.remote.TokenStore
import com.smartwatch.recordatorios.data.repository.LocalPlanRepository
import com.smartwatch.recordatorios.sync.SyncScheduler
import com.smartwatch.recordatorios.ui.components.BwAppScaffold
import com.smartwatch.recordatorios.ui.components.BwPager
import com.smartwatch.recordatorios.ui.screens.adherence.AdherenceScreen
import com.smartwatch.recordatorios.ui.screens.alert.DoseAlertActivity
import com.smartwatch.recordatorios.ui.screens.chat.ChatScreen
import com.smartwatch.recordatorios.ui.screens.chat.ChatViewModel
import com.smartwatch.recordatorios.ui.screens.chat.speechLocale
import com.smartwatch.recordatorios.ui.screens.home.HomeScreen
import com.smartwatch.recordatorios.ui.screens.home.HomeViewModel
import com.smartwatch.recordatorios.ui.screens.medication.AddMedicationScreen
import com.smartwatch.recordatorios.ui.screens.pairing.PairingRoute
import com.smartwatch.recordatorios.ui.screens.permissions.PermissionsScreen
import com.smartwatch.recordatorios.ui.screens.today.TodayScreen
import com.smartwatch.recordatorios.ui.screens.watch.WatchActions
import com.smartwatch.recordatorios.ui.screens.watch.WatchScreen
import com.smartwatch.recordatorios.ui.screens.welcome.WelcomeScreen
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

    @Inject lateinit var localPlan: LocalPlanRepository

    @Inject lateinit var deviceSession: DeviceSession

    private val viewModel: HomeViewModel by viewModels()

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordatoriosTheme {
                BwAppScaffold {
                    StartRoute()
                }
            }
        }
    }

    /**
     * Arranque: vinculado → la app; si no, lo que eligió el usuario la primera vez (usar el reloj solo
     * o vincular). Sin elección todavía, la bienvenida con las dos opciones.
     */
    @Composable
    private fun StartRoute() {
        val paired by tokens.paired.collectAsStateWithLifecycle()
        val localMedications by localPlan.medications.collectAsStateWithLifecycle(emptyList())
        val onboarding = remember { getSharedPreferences(ONBOARDING, MODE_PRIVATE) }
        var mode by rememberSaveable { mutableStateOf(onboarding.getString(KEY_MODE, null)) }
        val choose = { next: String? ->
            onboarding.edit().putString(KEY_MODE, next).apply()
            mode = next
        }
        // Al salir de Vincular (o al desvincular) se vuelve al modo local si hay medicamentos aquí.
        val leavePairing = { choose(if (localMedications.isEmpty()) null else MODE_LOCAL) }
        when {
            paired -> AppRoute(paired = true, onPair = {}, onUnpaired = leavePairing)
            mode == MODE_PAIR -> {
                BackHandler { leavePairing() }
                PairingRoute()
            }
            mode == MODE_LOCAL -> AppRoute(paired = false, onPair = { choose(MODE_PAIR) }, onUnpaired = {})
            else -> WelcomeScreen(onUseLocally = { choose(MODE_LOCAL) }, onPair = { choose(MODE_PAIR) })
        }
    }

    /**
     * La app (vinculada o solo en el reloj): primero el paso guiado de permisos (una vez), luego las
     * páginas. Al volver a primer plano completa las dosis locales (también sin red) y, mientras está
     * abierta, sincroniza cada [FOREGROUND_SYNC] para que lo creado en la web llegue en un par de minutos.
     */
    @Composable
    private fun AppRoute(
        paired: Boolean,
        onPair: () -> Unit,
        onUnpaired: () -> Unit,
    ) {
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
            lifecycleScope.launch { localPlan.refill() }
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
            MainRoute(paired, notificationsAllowed, exactAllowed, requestNotifications, onPair, onUnpaired)
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
        paired: Boolean,
        notificationsAllowed: Boolean,
        exactAllowed: Boolean,
        requestNotifications: () -> Unit,
        onPair: () -> Unit,
        onUnpaired: () -> Unit,
    ) {
        var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
        var saving by remember { mutableStateOf(false) }
        // Sin vincular no hay adherencia (la calcula el backend): esa página no aparece.
        val pages = if (paired) PAIRED_PAGES else LOCAL_PAGES
        // Fuera del when: al volver del chat se sigue en la misma página.
        val pagerState = rememberPagerState(pageCount = { pages.size })
        val localMedications by localPlan.medications.collectAsStateWithLifecycle(emptyList())
        val patientName by deviceSession.patientName.collectAsStateWithLifecycle()
        // Crear en el reloj solo sin vincular: vinculado, los medicamentos los carga el panel web.
        val onAdd: (() -> Unit)? = if (paired) null else ({ screen = Screen.ADD })
        val chatState by chatViewModel.state.collectAsStateWithLifecycle()
        val speechLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val heard = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                chatViewModel.ask(heard.orEmpty())
            }
        val next by viewModel.next.collectAsStateWithLifecycle()
        val today by viewModel.today.collectAsStateWithLifecycle()
        val adherence by viewModel.adherence.collectAsStateWithLifecycle()
        val planInfo by viewModel.planInfo.collectAsStateWithLifecycle()
        BackHandler(enabled = screen != Screen.HOME && !saving) {
            screen = Screen.HOME
            chatViewModel.reset()
        }

        when (screen) {
            Screen.CHAT ->
                ChatScreen(
                    state = chatState,
                    onAsk = { askByVoice { speechLauncher.launch(it) } },
                    onConfirm = chatViewModel::confirm,
                    onDecline = chatViewModel::reset,
                )
            Screen.ADD ->
                AddMedicationScreen(saving = saving) { name, dose, times ->
                    saving = true
                    lifecycleScope.launch {
                        localPlan.add(name, dose, times)
                        saving = false
                        screen = Screen.HOME
                    }
                }
            Screen.HOME ->
                BwPager(pagerState) { page ->
                    when (pages[page]) {
                        Page.NEXT ->
                            HomeScreen(
                                next = next,
                                planInfo = if (paired) planInfo else null,
                                exactAlarmsAllowed = exactAllowed,
                                notificationsAllowed = notificationsAllowed,
                                onRequestExactAlarms = ::openExactAlarmSettings,
                                onRequestNotifications = requestNotifications,
                                onOpenDose = ::openDose,
                                // El asistente necesita el backend: solo vinculado.
                                onOpenChat = if (paired) ({ screen = Screen.CHAT }) else null,
                                onRetryPlan = syncScheduler::syncNow,
                                onAddMedication = onAdd,
                                today = today.orEmpty(),
                            )
                        Page.TODAY -> TodayScreen(doses = today, onOpenDose = ::openDose)
                        Page.ADHERENCE -> AdherenceScreen(summary = adherence)
                        Page.WATCH ->
                            WatchScreen(
                                paired = paired,
                                medications = localMedications,
                                patientName = patientName,
                                actions =
                                    WatchActions(
                                        onAdd = onAdd,
                                        onRemove = { id -> lifecycleScope.launch { localPlan.remove(id) } },
                                        onPair = onPair,
                                        onUnpair = {
                                            lifecycleScope.launch {
                                                pairing.unpair()
                                                onUnpaired()
                                            }
                                        },
                                    ),
                            )
                    }
                }
        }
    }

    private fun askByVoice(launch: (Intent) -> Unit) {
        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.chat_prompt))
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLocale().toLanguageTag())
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

/** HOME son las páginas ([Page]); CHAT y ADD, pantallas encima. */
private enum class Screen { HOME, CHAT, ADD }

private enum class Page { NEXT, TODAY, ADHERENCE, WATCH }

private val PAIRED_PAGES = listOf(Page.NEXT, Page.TODAY, Page.ADHERENCE, Page.WATCH)
private val LOCAL_PAGES = listOf(Page.NEXT, Page.TODAY, Page.WATCH)
private const val KEY_MODE = "mode"
private const val MODE_LOCAL = "local"
private const val MODE_PAIR = "pair"
private const val ONBOARDING = "onboarding"
private const val KEY_PERMISSIONS_SEEN = "permissions_seen"
private val FOREGROUND_SYNC = Duration.ofMinutes(2)
