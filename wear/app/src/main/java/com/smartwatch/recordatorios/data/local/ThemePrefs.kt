package com.smartwatch.recordatorios.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { DARK, LIGHT }

/** Modo de color elegido dentro de la app (no sigue al sistema); oscuro por defecto. */
@Singleton
class ThemePrefs
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
        private val _mode =
            MutableStateFlow(
                ThemeMode.entries.find { it.name == prefs.getString(KEY_MODE, null) } ?: ThemeMode.DARK,
            )
        val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

        fun toggle() {
            val next = if (_mode.value == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
            prefs.edit().putString(KEY_MODE, next.name).apply()
            _mode.value = next
        }

        private companion object {
            const val KEY_MODE = "mode"
        }
    }
