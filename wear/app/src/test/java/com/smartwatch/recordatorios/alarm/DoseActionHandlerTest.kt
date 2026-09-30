package com.smartwatch.recordatorios.alarm

import android.Manifest
import android.app.NotificationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.MINUTE
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager

@RunWith(AndroidJUnit4::class)
class DoseActionHandlerTest {
    private lateinit var graph: TestGraph
    private lateinit var notifications: NotificationManager

    @Before
    fun setUp() {
        graph = TestGraph()
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
        shadowOf(graph.context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        notifications = graph.context.getSystemService(NotificationManager::class.java)
    }

    @After
    fun tearDown() = graph.close()

    private suspend fun seedAndSchedule() {
        graph.scheduler.scheduleAll(graph.repository.seedDemoDoses())
    }

    @Test
    fun `al sonar muestra la notificacion con Tomada y Posponer`() =
        runTest {
            seedAndSchedule()

            assertThat(graph.firing.fire("demo-1")).isTrue()

            val notification = shadowOf(notifications).allNotifications.single()
            assertThat(notification.channelId).isEqualTo(DoseNotifier.CHANNEL_ID)
            assertThat(notification.fullScreenIntent).isNotNull()
            assertThat(notification.actions.map { it.title.toString() })
                .containsExactly("Tomada", "Posponer 10 min")
                .inOrder()
        }

    @Test
    fun `sin permiso de pantalla completa suena igual, solo con la notificacion`() =
        runTest {
            graph.fullScreenAllowed = false
            seedAndSchedule()

            assertThat(graph.firing.fire("demo-1")).isTrue()

            val notification = shadowOf(notifications).allNotifications.single()
            assertThat(notification.fullScreenIntent).isNull()
            assertThat(notification.channelId).isEqualTo(DoseNotifier.CHANNEL_ID)
        }

    @Test
    fun `tras 3 posposiciones la notificacion ya no ofrece Posponer`() =
        runTest {
            seedAndSchedule()
            repeat(3) { graph.handler.handle("demo-1", DoseAction.SNOOZED) }

            graph.firing.fire("demo-1")

            val notification = shadowOf(notifications).allNotifications.single()
            assertThat(notification.actions.map { it.title.toString() }).containsExactly("Tomada")
        }

    @Test
    fun `no suena una dosis ya tomada`() =
        runTest {
            seedAndSchedule()
            graph.handler.handle("demo-1", DoseAction.TAKEN)

            assertThat(graph.firing.fire("demo-1")).isFalse()
            assertThat(shadowOf(notifications).allNotifications).isEmpty()
        }

    @Test
    fun `tomada cancela alarma y notificacion`() =
        runTest {
            seedAndSchedule()
            graph.firing.fire("demo-1")

            val result = graph.handler.handle("demo-1", DoseAction.TAKEN)

            assertThat(result?.status).isEqualTo(DoseStatus.TAKEN)
            assertThat(shadowOf(notifications).allNotifications).isEmpty()
            assertThat(graph.shadowAlarms.scheduledAlarms.map { it.triggerAtMs }).containsExactly(T0 + 2 * MINUTE)
        }

    @Test
    fun `posponer reprograma la alarma 10 minutos despues`() =
        runTest {
            seedAndSchedule()
            graph.now = T0 + MINUTE

            graph.handler.handle("demo-1", DoseAction.SNOOZED)

            assertThat(graph.shadowAlarms.scheduledAlarms.map { it.triggerAtMs })
                .containsExactly(T0 + 11 * MINUTE, T0 + 2 * MINUTE)
        }

    @Test
    fun `tras reiniciar se reprograman solo las dosis pendientes y futuras`() =
        runTest {
            seedAndSchedule()
            graph.handler.handle("demo-1", DoseAction.TAKEN)
            // Simula el reinicio: el sistema borra todas las alarmas.
            graph.scheduler.cancel("demo-2")
            assertThat(graph.shadowAlarms.scheduledAlarms).isEmpty()

            assertThat(graph.rescheduler.rescheduleAll()).isEqualTo(1)

            val alarm = graph.shadowAlarms.scheduledAlarms.single()
            assertThat(alarm.triggerAtMs).isEqualTo(T0 + 2 * MINUTE)
            assertThat(alarm.alarmClockInfo).isNotNull()
        }
}
