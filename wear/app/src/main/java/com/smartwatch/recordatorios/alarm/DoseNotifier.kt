package com.smartwatch.recordatorios.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.ui.screens.alert.DoseAlertActivity
import com.smartwatch.recordatorios.ui.theme.formatTime
import com.smartwatch.recordatorios.ui.theme.medicationColor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

/** Alerta de dosis: notificación de alta prioridad con sonido de alarma, vibración y acciones. */
@Singleton
class DoseNotifier
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val manager = NotificationManagerCompat.from(context)

        fun ensureChannel() {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.channel_alarm_name),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = context.getString(R.string.channel_alarm_description)
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                        AudioAttributes
                            .Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build(),
                    )
                    enableVibration(true)
                    vibrationPattern = VIBRATION_PATTERN
                }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        fun canPostNotifications(): Boolean =
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        fun show(dose: DoseEntity) {
            if (!canPostNotifications()) return
            ensureChannel()
            val doseUri = DoseIntents.uriFor(dose.id)

            val alertIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, DoseAlertActivity::class.java).setData(doseUri),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            val builder =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(medicationColor(dose.colorKey).toArgb())
                    .setContentTitle(
                        context.getString(R.string.notification_title, dose.medicationName, dose.doseLabel),
                    ).setContentText(
                        context.getString(
                            R.string.notification_text,
                            context.getString(colorNameRes(dose.colorKey)),
                            formatTime(dose.scheduledAt),
                        ),
                    ).setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setTimeoutAfter(RING_TIMEOUT.toMillis())
                    .setContentIntent(alertIntent)
                    // Abre la pantalla de alerta aunque el reloj esté en reposo.
                    .setFullScreenIntent(alertIntent, true)
                    .addAction(
                        0,
                        context.getString(R.string.action_take),
                        actionIntent(DoseIntents.ACTION_TAKE, dose.id),
                    )

            if (SnoozePolicy.canSnooze(dose.snoozeCount)) {
                builder.addAction(
                    0,
                    context.getString(R.string.action_snooze),
                    actionIntent(DoseIntents.ACTION_SNOOZE, dose.id),
                )
            }

            val notification = builder.build()
            // Repite el sonido hasta que la persona actúe o venza el tiempo.
            notification.flags = notification.flags or android.app.Notification.FLAG_INSISTENT
            @Suppress("MissingPermission") // comprobado en canPostNotifications()
            manager.notify(DoseIntents.notificationId(dose.id), notification)
        }

        fun cancel(doseId: String) {
            manager.cancel(DoseIntents.notificationId(doseId))
        }

        private fun actionIntent(
            action: String,
            doseId: String,
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, DoseActionReceiver::class.java)
                    .setAction(action)
                    .setData(DoseIntents.uriFor(doseId)),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        companion object {
            const val CHANNEL_ID = "dose_alarm"
            private val RING_TIMEOUT: Duration = Duration.ofMinutes(2)
            private val VIBRATION_PATTERN = longArrayOf(0, 800, 400, 800, 400, 800)

            fun colorNameRes(colorKey: String): Int =
                when (colorKey) {
                    "red" -> R.string.color_red
                    "orange" -> R.string.color_orange
                    "yellow" -> R.string.color_yellow
                    "green" -> R.string.color_green
                    "teal" -> R.string.color_teal
                    "purple" -> R.string.color_purple
                    "pink" -> R.string.color_pink
                    else -> R.string.color_blue
                }
        }
    }
