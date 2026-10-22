package com.smartwatch.recordatorios.ui.screens.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwIcon
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Paso guiado tras vincular: los dos permisos sin los que la alarma no suena, cada uno con su estado
 * a la vista (concedido con visto, o el botón para pedirlo). El siguiente que falta es la acción
 * principal; «Listo» cuando están los dos.
 */
@Composable
fun PermissionsScreen(
    notificationsAllowed: Boolean,
    exactAlarmsAllowed: Boolean,
    onRequestNotifications: () -> Unit,
    onRequestExactAlarms: () -> Unit,
    onDone: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    val allGranted = notificationsAllowed && exactAlarmsAllowed
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            edgeItem { BwHeader(stringResource(R.string.permissions_title)) }
            edgeItem(container = true) {
                Permission(
                    label = stringResource(R.string.permissions_notifications),
                    grantedLabel = stringResource(R.string.permissions_notifications_on),
                    granted = notificationsAllowed,
                    primary = true,
                    onRequest = onRequestNotifications,
                )
            }
            edgeItem(container = true) {
                Permission(
                    label = stringResource(R.string.permissions_exact),
                    grantedLabel = stringResource(R.string.permissions_exact_on),
                    granted = exactAlarmsAllowed,
                    primary = notificationsAllowed,
                    onRequest = onRequestExactAlarms,
                )
            }
            edgeItem(container = true) {
                BwButton(
                    label = stringResource(if (allGranted) R.string.permissions_done else R.string.permissions_later),
                    onClick = onDone,
                    primary = allGranted,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun Permission(
    label: String,
    grantedLabel: String,
    granted: Boolean,
    primary: Boolean,
    onRequest: () -> Unit,
) {
    if (granted) {
        Row(
            modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BwIcon(
                R.drawable.ic_check,
                contentDescription = stringResource(R.string.permissions_granted),
                color = BwTheme.colors.success[2],
            )
            BwText(grantedLabel)
        }
    } else {
        BwButton(
            label = stringResource(R.string.permissions_allow_named, label),
            onClick = onRequest,
            primary = primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
