package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.ThemeMode
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

/** Cada preview se dibuja en los dos modos. */
private class ThemeModes : PreviewParameterProvider<ThemeMode> {
    override val values = ThemeMode.entries.asSequence()
}

@Composable
private fun PreviewFrame(
    mode: ThemeMode,
    content: @Composable ColumnScope.() -> Unit,
) {
    RecordatoriosTheme(mode) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(BwTheme.colors.background)
                    .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

@WearPreviewDevices
@Composable
private fun ButtonsPreview(
    @PreviewParameter(ThemeModes::class) mode: ThemeMode,
) = PreviewFrame(mode) {
    BwHeader("Inicio")
    BwButton("Tomada", onClick = {}, primary = true, modifier = Modifier.fillMaxWidth())
    BwButton("Tema: Oscuro", onClick = {}, icon = R.drawable.ic_theme, modifier = Modifier.fillMaxWidth())
}

@WearPreviewDevices
@Composable
private fun CardPreview(
    @PreviewParameter(ThemeModes::class) mode: ThemeMode,
) = PreviewFrame(mode) {
    BwCard(onClick = {}, modifier = Modifier.fillMaxWidth()) {
        BwText("Próxima dosis", style = BwTheme.type.support)
        MedicationLabel(name = "Losartán", colorKey = "blue")
        BwText("08:00", style = BwTheme.type.title)
    }
}

@WearPreviewDevices
@Composable
private fun BannerPreview(
    @PreviewParameter(ThemeModes::class) mode: ThemeMode,
) = PreviewFrame(mode) {
    BwBanner(
        icon = R.drawable.ic_warning,
        iconDescription = "Aviso",
        title = "Alarmas desactivadas",
        body = "La alarma puede sonar tarde",
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@WearPreviewDevices
@Composable
private fun MedicationColorsPreview(
    @PreviewParameter(ThemeModes::class) mode: ThemeMode,
) = PreviewFrame(mode) {
    listOf("red", "orange", "yellow", "green", "teal", "blue", "purple", "pink").forEach {
        MedicationLabel(name = it, colorKey = it)
    }
}

@WearPreviewDevices
@Composable
private fun FeedbackPreview(
    @PreviewParameter(ThemeModes::class) mode: ThemeMode,
) = PreviewFrame(mode) {
    BwProgress()
    BwSuccessMark("Tomada")
}
