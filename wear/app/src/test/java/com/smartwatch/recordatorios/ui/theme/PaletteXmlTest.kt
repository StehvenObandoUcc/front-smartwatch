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
        assertThat(context.getColor(R.color.screen)).isEqualTo(Palette.screen.toArgb())
        assertThat(context.getColor(R.color.sage)).isEqualTo(Palette.sage.toArgb())
        assertThat(context.getColor(R.color.ink)).isEqualTo(Palette.ink.toArgb())
        assertThat(context.getColor(R.color.tile_track)).isEqualTo(TileColors.track.toArgb())
        assertThat(context.getColor(R.color.med_blue)).isEqualTo(Palette.medBlue.toArgb())
    }

    @Test
    fun `el fondo de la app es el primitivo de pantalla`() {
        assertThat(BwTheme.colors.background).isEqualTo(Palette.screen)
    }
}
