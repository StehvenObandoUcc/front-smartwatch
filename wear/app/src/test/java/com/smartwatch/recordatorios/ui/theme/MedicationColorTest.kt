package com.smartwatch.recordatorios.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class MedicationColorTest {
    @Test
    fun `un 500 de Tailwind va a su tono profundo de la paleta`() {
        assertEquals(Color(0xFF2E7D32), nearestMedicationColor(Color(0xFF22C55E)))
        assertEquals(Color(0xFF1565C0), nearestMedicationColor(Color(0xFF3B82F6)))
        assertEquals(Color(0xFF6A1B9A), nearestMedicationColor(Color(0xFFA855F7)))
    }

    @Test
    fun `un color de la paleta se queda igual`() {
        assertEquals(Color(0xFFC62828), nearestMedicationColor(Color(0xFFC62828)))
    }
}
