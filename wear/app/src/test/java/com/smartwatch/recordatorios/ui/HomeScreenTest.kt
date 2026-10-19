package com.smartwatch.recordatorios.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.data.remote.PlanInfo
import com.smartwatch.recordatorios.data.remote.PlanStatus
import com.smartwatch.recordatorios.data.repository.DemoDoses
import com.smartwatch.recordatorios.ui.screens.home.HomeScreen
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav")
class HomeScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private var exactRequests = 0
    private var notificationRequests = 0

    private fun show(
        exact: Boolean,
        notifications: Boolean,
    ) {
        compose.setContent {
            RecordatoriosTheme {
                HomeScreen(
                    next = DemoDoses.create(0).first(),
                    planInfo = PlanInfo(PlanStatus.OK, 0),
                    exactAlarmsAllowed = exact,
                    notificationsAllowed = notifications,
                    onRequestExactAlarms = { exactRequests++ },
                    onRequestNotifications = { notificationRequests++ },
                    onOpenDose = {},
                    onOpenToday = {},
                    onOpenChat = {},
                )
            }
        }
    }

    @Test
    fun `sin alarmas exactas muestra el aviso y abre el permiso`() {
        show(exact = false, notifications = true)

        compose.onNodeWithText("Alarmas desactivadas").assertIsDisplayed().performClick()

        assertThat(exactRequests).isEqualTo(1)
        compose.onNodeWithText("Avisos desactivados").assertDoesNotExist()
    }

    @Test
    fun `sin notificaciones muestra el aviso y pide el permiso`() {
        show(exact = true, notifications = false)

        compose.onNodeWithText("Avisos desactivados").assertIsDisplayed().performClick()

        assertThat(notificationRequests).isEqualTo(1)
    }

    @Test
    fun `con todos los permisos no hay avisos`() {
        show(exact = true, notifications = true)

        compose.onNodeWithText("Alarmas desactivadas").assertDoesNotExist()
        compose.onNodeWithText("Avisos desactivados").assertDoesNotExist()
        compose.onNodeWithText("Losartán").assertIsDisplayed()
    }
}
