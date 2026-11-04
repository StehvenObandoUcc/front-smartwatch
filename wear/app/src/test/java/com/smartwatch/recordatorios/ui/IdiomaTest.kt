package com.smartwatch.recordatorios.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.screens.chat.speechLocale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/** La app existe en español (por defecto) y en inglés; cualquier otro idioma cae en español. */
@RunWith(AndroidJUnit4::class)
class IdiomaTest {
    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    @Config(qualifiers = "es")
    fun `en espanol los textos y la voz son en espanol`() {
        assertThat(context.getString(R.string.status_taken)).isEqualTo("Tomada")
        assertThat(speechLocale().language).isEqualTo("es")
    }

    @Test
    @Config(qualifiers = "en")
    fun `en ingles los textos y la voz son en ingles`() {
        assertThat(context.getString(R.string.status_taken)).isEqualTo("Taken")
        assertThat(speechLocale().language).isEqualTo("en")
    }

    @Test
    @Config(qualifiers = "fr")
    fun `otro idioma cae en espanol`() {
        assertThat(context.getString(R.string.status_taken)).isEqualTo("Tomada")
        assertThat(speechLocale().language).isEqualTo("es")
    }
}
