package com.smartwatch.recordatorios.alarm

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.MINUTE
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.repository.DemoDoses
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlarmManager

@RunWith(AndroidJUnit4::class)
class DoseAlarmSchedulerTest {
    private lateinit var graph: TestGraph

    @Before
    fun setUp() {
        graph = TestGraph()
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
    }

    @After
    fun tearDown() = graph.close()

    @Test
    fun `con permiso usa setAlarmClock a la hora de la dosis`() {
        val dose = DemoDoses.create(T0).first()

        assertThat(graph.scheduler.schedule(dose)).isTrue()

        val alarm = graph.shadowAlarms.scheduledAlarms.single()
        assertThat(alarm.alarmClockInfo).isNotNull()
        assertThat(alarm.triggerAtMs).isEqualTo(T0 + MINUTE)
    }

    @Test
    fun `sin permiso programa una alarma inexacta como respaldo`() {
        ShadowAlarmManager.setCanScheduleExactAlarms(false)
        val dose = DemoDoses.create(T0).first()

        assertThat(graph.scheduler.schedule(dose)).isTrue()

        val alarm = graph.shadowAlarms.scheduledAlarms.single()
        assertThat(alarm.alarmClockInfo).isNull()
        assertThat(alarm.triggerAtMs).isEqualTo(T0 + MINUTE)
    }

    @Test
    fun `cada dosis tiene su propia alarma`() {
        assertThat(graph.scheduler.scheduleAll(DemoDoses.create(T0))).isEqualTo(2)

        assertThat(graph.shadowAlarms.scheduledAlarms.map { it.triggerAtMs })
            .containsExactly(T0 + MINUTE, T0 + 2 * MINUTE)
    }

    @Test
    fun `no programa dosis pasadas ni resueltas`() {
        val (first, second) = DemoDoses.create(T0 - 10 * MINUTE)
        val taken = DemoDoses.create(T0).first().copy(status = DoseStatus.TAKEN)

        assertThat(graph.scheduler.schedule(first)).isFalse()
        assertThat(graph.scheduler.schedule(second)).isFalse()
        assertThat(graph.scheduler.schedule(taken)).isFalse()
        assertThat(graph.shadowAlarms.scheduledAlarms).isEmpty()
    }

    @Test
    fun `cancel quita la alarma de esa dosis y deja las demas`() {
        graph.scheduler.scheduleAll(DemoDoses.create(T0))

        graph.scheduler.cancel("demo-1")

        assertThat(graph.shadowAlarms.scheduledAlarms.map { it.triggerAtMs }).containsExactly(T0 + 2 * MINUTE)
    }
}
