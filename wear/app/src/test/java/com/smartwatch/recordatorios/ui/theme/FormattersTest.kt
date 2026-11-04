package com.smartwatch.recordatorios.ui.theme

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

/** La hora sale en español aunque el reloj esté en inglés: el resto de la app solo existe en español. */
@RunWith(AndroidJUnit4::class)
class FormattersTest {
    private val original = Locale.getDefault()

    @After
    fun restore() = Locale.setDefault(original)

    @Test
    fun `con el reloj en ingles la hora de 12 h dice p m y no PM`() {
        Locale.setDefault(Locale.US)
        val at =
            LocalDate
                .of(2026, 11, 4)
                .atTime(20, 0)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()

        val text = formatTime(at, is24Hour = false, zone = ZoneOffset.UTC)

        assertThat(text).startsWith("8:00")
        assertThat(text).doesNotContain("PM")
        assertThat(formatLocalTime("20:00", is24Hour = false)).doesNotContain("PM")
        assertThat(formatLocalTime("20:00", is24Hour = true)).isEqualTo("20:00")
    }
}
