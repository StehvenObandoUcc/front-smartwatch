package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun RecordatoriosTheme(content: @Composable () -> Unit) {
    // Wear Material 3 ya usa tipografía grande y alto contraste sobre fondo negro.
    MaterialTheme(content = content)
}
