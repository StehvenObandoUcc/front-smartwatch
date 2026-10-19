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
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

@Composable
private fun PreviewFrame(content: @Composable ColumnScope.() -> Unit) {
    RecordatoriosTheme {
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
private fun ButtonsPreview() =
    PreviewFrame {
        BwHeader("Inicio")
        BwButton("Tomada", onClick = {}, primary = true, modifier = Modifier.fillMaxWidth())
        BwButton("Ver las de hoy", onClick = {}, modifier = Modifier.fillMaxWidth())
    }

@WearPreviewDevices
@Composable
private fun CardPreview() =
    PreviewFrame {
        BwCard(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            BwText("Próxima dosis", style = BwTheme.type.support)
            MedicationLabel(name = "Losartán", colorKey = "blue")
            BwText("08:00", style = BwTheme.type.title)
        }
    }

@WearPreviewDevices
@Composable
private fun BannerPreview() =
    PreviewFrame {
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
private fun MedicationColorsPreview() =
    PreviewFrame {
        listOf("red", "orange", "yellow", "green", "teal", "blue", "purple", "pink").forEach {
            MedicationLabel(name = it, colorKey = it)
        }
    }

@WearPreviewDevices
@Composable
private fun FeedbackPreview() =
    PreviewFrame {
        BwProgress()
        BwSuccessMark("Tomada")
    }
