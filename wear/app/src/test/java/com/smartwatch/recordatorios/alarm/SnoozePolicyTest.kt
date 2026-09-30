package com.smartwatch.recordatorios.alarm

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SnoozePolicyTest {
    @Test
    fun `permite posponer hasta 3 veces`() {
        assertThat(SnoozePolicy.canSnooze(0)).isTrue()
        assertThat(SnoozePolicy.canSnooze(2)).isTrue()
        assertThat(SnoozePolicy.canSnooze(3)).isFalse()
    }

    @Test
    fun `pospone 10 minutos`() {
        assertThat(SnoozePolicy.nextAlarmAt(1_000L)).isEqualTo(1_000L + 10 * 60_000L)
    }
}
