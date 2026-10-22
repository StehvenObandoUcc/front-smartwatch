package com.smartwatch.recordatorios.ui

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.ui.screens.medication.AddMedicationScreen
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav")
class AddMedicationScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private var saved: Triple<String, String, List<LocalTime>>? = null

    private fun show() {
        compose.setContent {
            RecordatoriosTheme {
                AddMedicationScreen(saving = false) { name, dose, times -> saved = Triple(name, dose, times) }
            }
        }
    }

    private fun tapSave() {
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Guardar"))
        compose.onNodeWithText("Guardar").performClick()
    }

    @Test
    fun `sin nombre no guarda`() {
        show()

        tapSave()

        assertThat(saved).isNull()
    }

    @Test
    fun `guarda el nombre, la dosis por defecto y la hora ajustada con +`() {
        show()
        compose.onNodeWithContentDescription("Nombre").performTextInput("Losartán")
        compose.onNodeWithContentDescription("Más tarde").performClick()
        compose.onNodeWithContentDescription("Más tarde").performClick()

        tapSave()

        assertThat(saved).isEqualTo(Triple("Losartán", "1 dosis", listOf(LocalTime.of(8, 30))))
    }

    @Test
    fun `otra hora añade una toma 12 horas despues`() {
        show()
        compose.onNodeWithContentDescription("Nombre").performTextInput("Metformina")
        compose.onNodeWithContentDescription("Dosis (p. ej. 1 tableta)").performTextInput("850 mg")
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Otra hora"))
        compose.onNodeWithText("Otra hora").performClick()

        tapSave()

        assertThat(saved?.third).containsExactly(LocalTime.of(8, 0), LocalTime.of(20, 0)).inOrder()
        assertThat(saved?.second).isEqualTo("850 mg")
    }
}
