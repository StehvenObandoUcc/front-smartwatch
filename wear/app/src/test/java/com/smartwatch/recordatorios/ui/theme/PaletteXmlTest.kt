package com.smartwatch.recordatorios.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.R
import org.junit.Test
import org.junit.runner.RunWith

/** colors.xml repite algunos primitivos para el XML: aquí se vigila que no se desincronicen. */
@RunWith(AndroidJUnit4::class)
class PaletteXmlTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `los colores del XML son los de la paleta`() {
        assertThat(context.getColor(R.color.petrol_900)).isEqualTo(Palette.petrol900.toArgb())
        assertThat(context.getColor(R.color.cream)).isEqualTo(Palette.cream.toArgb())
        assertThat(context.getColor(R.color.sand)).isEqualTo(Palette.sand.toArgb())
        assertThat(context.getColor(R.color.mint)).isEqualTo(Palette.mint.toArgb())
        assertThat(context.getColor(R.color.track)).isEqualTo(BwTheme.colors.track.toArgb())
        assertThat(context.getColor(R.color.med_blue)).isEqualTo(Palette.medBlue.toArgb())
    }

    @Test
    fun `el fondo de la app es el primitivo petróleo`() {
        assertThat(BwTheme.colors.background).isEqualTo(Palette.petrol900)
    }
}
