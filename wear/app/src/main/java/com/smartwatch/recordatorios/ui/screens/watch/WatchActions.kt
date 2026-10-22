package com.smartwatch.recordatorios.ui.screens.watch

/** Lo que muestra la página Reloj y lo que puede hacer el usuario desde ella. */
class WatchActions(
    /** Crear un medicamento en el reloj; null si no aplica (vinculado, los carga el panel web). */
    val onAdd: (() -> Unit)?,
    val onRemove: (String) -> Unit,
    val onPair: () -> Unit,
    val onUnpair: () -> Unit,
)
