package com.smartwatch.recordatorios.ui.theme

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

/** La hora sigue el idioma de la app: «8:00 p. m.» en español y «8:00 PM» en inglés. */
@RunWith(AndroidJUnit4::class)
class FormattersTest {
    private val original = Locale.getDefault()
    private val eightPm =
        LocalDate
            .of(2026, 11, 4)
            .atTime(20, 0)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

    @After
    fun restore() = Locale.setDefault(original)

    @Test
    fun `en ingles la hora de 12 h dice PM`() {
        Locale.setDefault(Locale.US)

        assertThat(formatTime(eightPm, is24Hour = false, zone = ZoneOffset.UTC)).contains("PM")
        assertThat(formatLocalTime("20:00", is24Hour = false)).contains("PM")
    }

    @Test
    fun `en espanol la hora de 12 h no dice PM`() {
        Locale.setDefault(Locale.forLanguageTag("es-CO"))

        val text = formatTime(eightPm, is24Hour = false, zone = ZoneOffset.UTC)

        assertThat(text).startsWith("8:00")
        assertThat(text).doesNotContain("PM")
        assertThat(formatLocalTime("20:00", is24Hour = true)).isEqualTo("20:00")
    }
}
