package com.smartwatch.recordatorios.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.alarm.SnoozePolicy
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.repository.DemoDoses
import com.smartwatch.recordatorios.ui.screens.alert.DoseAlertScreen
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/** Reloj redondo pequeño (192 dp) con fuente grande: el peor caso para que «Tomada» quepa. */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w192dp-h192dp-small-notlong-round-watch-xhdpi-keyshidden-nonav", fontScale = 1.3f)
class DoseAlertScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private var takes = 0
    private val snoozeLabel = "Posponer ${SnoozePolicy.DELAY.toMinutes()} min"

    private fun show(dose: DoseEntity) {
        compose.setContent {
            RecordatoriosTheme {
                DoseAlertScreen(dose = dose, onTake = { takes++ }, onSnooze = {}, onSkip = {})
            }
        }
    }

    private fun dose(snoozeCount: Int = 0) = DemoDoses.create(0).first().copy(snoozeCount = snoozeCount)

    @Test
    fun `Tomada se ve sin desplazarse`() {
        show(dose())

        compose.onNodeWithText("Tomada").assertIsDisplayed()
    }

    @Test
    fun `posponer y el contador usan los números de SnoozePolicy`() {
        show(dose(snoozeCount = 1))

        compose.onNodeWithText(snoozeLabel).assertExists()
        compose.onNodeWithText("Pospuesta 1 de ${SnoozePolicy.MAX_SNOOZES}", substring = true).assertExists()
    }

    @Test
    fun `en el tope de pospuestas explica por qué no hay Posponer`() {
        show(dose(snoozeCount = SnoozePolicy.MAX_SNOOZES))

        compose.onNodeWithText(snoozeLabel).assertDoesNotExist()
        compose.onNodeWithText("Sin más pospuestas").assertExists()
    }

    @Test
    fun `Tomada registra una vez y muestra la marca de éxito`() {
        show(dose())

        compose.onNodeWithText("Tomada").performClick()

        assertThat(takes).isEqualTo(1)
        compose.onNodeWithText("Losartán").assertDoesNotExist()
        compose.onNodeWithText("Tomada").assertIsDisplayed()
    }

    @Test
    fun `omitir pide confirmar nombrando el medicamento`() {
        show(dose())

        // A 192 dp con fuente grande, Omitir queda bajo el pliegue (Tomada no).
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Omitir"))
        compose.onNodeWithText("Omitir").performClick()

        compose.onNodeWithText("¿Omitir Losartán?", substring = true).assertExists()
        compose.onNodeWithText("Volver").assertExists()
        compose.onNodeWithText("Omitir dosis").assertExists()
    }
}
