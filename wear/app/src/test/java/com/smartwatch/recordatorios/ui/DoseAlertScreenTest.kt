package com.smartwatch.recordatorios.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
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
import org.robolectric.annotation.GraphicsMode

/**
 * Reloj redondo pequeño (192 dp) con fuente grande: el peor caso para que «Tomada» quepa.
 * Gráficos nativos: en el modo por defecto Robolectric no mide el texto de verdad (todo ocupa una línea).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w192dp-h192dp-small-notlong-round-watch-xhdpi-keyshidden-nonav", fontScale = 1.3f)
class DoseAlertScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private var takes = 0
    private val snoozeMinutes = "${SnoozePolicy.DELAY.toMinutes()} min"

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
    fun `Tomada se ve sin desplazarse con un nombre de dos líneas`() {
        show(dose().copy(medicationName = "Hidroclorotiazida con amlodipino"))

        // El nombre ocupa de verdad dos líneas (puede achicarse para no partir una palabra): mide bastante
        // más que la dosis, que va en una línea.
        val name = compose.onNodeWithText("Hidroclorotiazida con amlodipino").getBoundsInRoot()
        val doseLabel = compose.onNodeWithText("50 mg").getBoundsInRoot()
        assertThat((name.bottom - name.top).value).isGreaterThan((doseLabel.bottom - doseLabel.top).value * 1.5f)
        compose.onNodeWithText("Tomada").assertIsDisplayed()
    }

    @Test
    fun `posponer y el contador usan los números de SnoozePolicy`() {
        show(dose(snoozeCount = 1))

        // El estado va arriba; Posponer, al desplazar (a 192 dp con letra grande queda bajo el pliegue).
        compose.onNodeWithText("Pospuesta 1 de ${SnoozePolicy.MAX_SNOOZES}", substring = true).assertExists()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Posponer"))
        compose.onNodeWithText("Posponer").assertExists()
        compose.onNodeWithText(snoozeMinutes).assertExists()
    }

    @Test
    fun `en el tope de pospuestas explica por qué no hay Posponer`() {
        show(dose(snoozeCount = SnoozePolicy.MAX_SNOOZES))

        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Sin más pospuestas"))
        compose.onNodeWithText("Posponer").assertDoesNotExist()
        compose.onNodeWithText("Sin más pospuestas").assertExists()
    }

    @Test
    fun `Tomada registra una vez y muestra la marca de éxito`() {
        show(dose())

        compose.onNodeWithText("Tomada").performClick()

        assertThat(takes).isEqualTo(1)
        compose.onNodeWithText("Losartán").assertDoesNotExist()
        compose.onNodeWithText("Tomada").assertIsDisplayed()
        compose.onNodeWithText("Losartán · 50 mg").assertExists()
        compose.onNodeWithText("Registrada a las", substring = true).assertExists()
    }

    @Test
    fun `posponer muestra cuántas van y cuántas quedan`() {
        show(dose(snoozeCount = 1))

        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Posponer"))
        compose.onNodeWithText("Posponer").performClick()

        compose.onNodeWithText("Pospuesta 2 de ${SnoozePolicy.MAX_SNOOZES}").assertExists()
        compose.onNodeWithText("Suena a las", substring = true).assertExists()
        compose.onNodeWithText("Queda 1 vez").assertExists()
    }

    @Test
    fun `omitir pide confirmar nombrando el medicamento`() {
        show(dose())

        // A 192 dp con fuente grande, Omitir queda bajo el pliegue (Tomada no).
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Omitir"))
        compose.onNodeWithText("Omitir").performClick()

        // La confirmación empieza arriba: aviso y Volver primero; Omitir dosis, desplazando.
        compose.onNodeWithText("¿Omitir Losartán?", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Volver").assertExists()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Omitir dosis"))
        compose.onNodeWithText("Omitir dosis").assertExists()
    }
}
